
#include "secrets.h"
#include "buzzer.h"
#include "sensor.h"

#include <WiFiClientSecure.h>
#include <MQTTClient.h> //MQTT Library Source: https://github.com/256dpi/arduino-mqtt
#include <ArduinoJson.h> //ArduinoJson Library Source: https://github.com/bblanchon/ArduinoJson
#include "WiFi.h"

// MQTT topics for the device
#define AWS_IOT_PUBLISH_TOPIC   "sensor/esp32"
#define AWS_IOT_SUBSCRIBE_TOPIC "actuator/esp32/data"
#define AWS_IOT_DISABLE_TOPIC   "actuator/esp32/mode"

WiFiClientSecure wifi_client = WiFiClientSecure();
MQTTClient mqtt_client = MQTTClient(256); //256 indicates the maximum size for packets being published and received.

uint32_t t1;

extern const char THINGNAME[];

extern const char WIFI_SSID[];
extern const char WIFI_PASSWORD[];
extern const char AWS_IOT_ENDPOINT[];

extern const char AWS_CERT_CA[] PROGMEM;
extern const char AWS_CERT_CRT[] PROGMEM;

// Device Private Key
extern const char AWS_CERT_PRIVATE[] PROGMEM;

enum state{listening, ringing, standby};

static state current_state = listening;

void connectAWS()
{
  //Begin WiFi in station mode
  WiFi.mode(WIFI_STA); 
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.println("Connecting to Wi-Fi");

  //Wait for WiFi connection
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  // Configure wifi_client with the correct certificates and keys
  wifi_client.setCACert(AWS_CERT_CA);
  wifi_client.setCertificate(AWS_CERT_CRT);
  wifi_client.setPrivateKey(AWS_CERT_PRIVATE);

  //Connect to AWS IOT Broker. 8883 is the port used for MQTT
  mqtt_client.begin(AWS_IOT_ENDPOINT, 8883, wifi_client);

  //Set action to be taken on incoming messages
  mqtt_client.onMessage(incomingMessageHandler);

  Serial.print("Connecting to AWS IOT");

  //Wait for connection to AWS IoT
  while (!mqtt_client.connect(THINGNAME)) {
    Serial.print(".");
    delay(100);
  }
  Serial.println();

  if(!mqtt_client.connected()){
    Serial.println("AWS IoT Timeout!");
    return;
  }

  //Subscribe to a topic
  mqtt_client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC);
  mqtt_client.subscribe(AWS_IOT_DISABLE_TOPIC);

  Serial.println("AWS IoT Connected!");
}

void publishMessage(String topic, String msg)
{
  //Create a JSON document of size 200 bytes, and populate it
  //See https://arduinojson.org/
  StaticJsonDocument<200> doc;
  doc["elapsed_time"] = millis() - t1;
  doc["value"] = msg;
  char jsonBuffer[512];
  serializeJson(doc, jsonBuffer); // print to mqtt_client

  //Publish to the topic
  mqtt_client.publish(topic, jsonBuffer);
  Serial.println("Sent a message");
}

void incomingMessageHandler(String &topic, String &payload) {
  Serial.println("Message received!");
  Serial.println("Topic: " + topic);
  Serial.println("Payload: " + payload);

  // deserialise payload
  StaticJsonDocument<200> doc;
  deserializeJson(doc, payload);

  String msg = doc["message"];


  switch(current_state)
  {
    case listening:
      if (topic == AWS_IOT_SUBSCRIBE_TOPIC && msg == "trigger")
      {
        current_state = ringing;
      }
      else
      {
        if (msg == "disable")
        {
          current_state = standby;
        }
      }
      break;
    case ringing:
      if (topic == AWS_IOT_SUBSCRIBE_TOPIC && msg == "acknowledge")
      {
        current_state = listening;
      }
      else
      {
        if (msg == "disable")
        {
          current_state = standby;
        }
      }
      break;
    case standby:
      if (topic == AWS_IOT_DISABLE_TOPIC && msg == "enable")
      {
        current_state = listening;
      }
      break;
  }

  //  StaticJsonDocument<200> doc;
  //  deserializeJson(doc, payload);
  //  const char* message = doc["message"];
}

void setup() {
  Serial.begin(115200);
  t1 = millis();
  connectAWS();
  buzzer_setup();
  sensor_setup();
  Serial.println("Setup finished...");
}

void loop() {
  mqtt_client.loop();
  switch (current_state)
  {
    case listening:
      if (sensor_loop())
      {
        publishMessage(AWS_IOT_PUBLISH_TOPIC, "trigger");
      }
      break;
    case ringing:
      buzzer_loop();
      break;
    case standby:
      delay(500);
      break;
  }
}
