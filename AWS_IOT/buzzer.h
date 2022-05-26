// Change this depending on where you put your piezo buzzer
const int TONE_OUTPUT_PIN = 27;

// The ESP32 has 16 channels which can generate 16 independent waveforms
// We'll just choose PWM channel 0 here
const int TONE_PWM_CHANNEL = 0; 

void buzzer_setup() {
  // ledcAttachPin(uint8_t pin, uint8_t channel);
  ledcAttachPin(TONE_OUTPUT_PIN, TONE_PWM_CHANNEL);
  Serial.println("buzzer ready!");
}

void buzzer_loop() {
  // Plays the middle C scale
  ledcWriteNote(TONE_PWM_CHANNEL, NOTE_C, 4);
  delay(250);
  ledcWriteTone(TONE_PWM_CHANNEL, 800);
  delay(250);
}
