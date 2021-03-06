package com.example.gatekeeperapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.example.gatekeeperapp.database.DatabaseAccess;
import com.example.gatekeeperapp.databinding.ActivityMainBinding;
import com.example.gatekeeperapp.helpers.LogItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
    private static final Integer ALARM_CHANNEL_ID = 5;
    //private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    // --- Constants to modify per your configuration ---

    // IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2mr3kdz48c7g8-ats.iot.eu-central-1.amazonaws.com";
    ;
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd";
    // Name of the AWS IoT policy to attach to a newly created certificate
    private static final String AWS_IOT_POLICY_NAME = "Esp32Policy";
    ;

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.EU_CENTRAL_1;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "iot_keystore";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "password";
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "default";
    // message to be sent to actuator when user shuts down SCREAMING alarm
    private static final String ALARM_DOWN = "{ \"message\": \"acknowledge\"} ";
    // message to be sent to actuator when user shuts down SCREAMING alarm
    private static final String ALARM_UP = "{ \"message\": \"trigger\"} ";
    // message to be sent to actuator when user TURNS OFF  alarm
    private static final String ALARM_OFF = "{ \"message\": \"disable\"} ";
    // message to be sent to actuator when user TURNS ON  alarm
    private static final String ALARM_ON = "{ \"message\": \"enable\"} ";


    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;

    String ALARM_STATE;
    boolean ALARM_STATE_ON = false;

    ArrayList<LogItem> logs;

    String clientId;
    String topicData;
    String topicMode;
    String keystorePath;
    String keystoreName;
    String keystorePassword;
    String sensorReadings;
    Integer NUMDETECTED = -1;
    boolean PUBLISH_ACK = false; //if notification is triggered, first initialise (connect) then publish

    TextView numDetectedTV;


    KeyStore clientKeyStore = null;
    String certificateId;
    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        clientId = "MobApp";
        topicData = "actuator/esp32/data";
        topicMode = "actuator/esp32/mode";
        sensorReadings = "sensor/esp32";


        //aws stuff
        init_database();
        init_publish_subscribe();
        connectIoT();
        //app stuff
        createNotificationChannel();
        try {
            ALARM_STATE_ON = checkAlarm();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Log.d("ALARM", String.valueOf(checkAlarm()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initCards();

        Intent current = getIntent();
        boolean silenceAlarm = current.getBooleanExtra("silenceAlarm", false);
        if (silenceAlarm) {
            PUBLISH_ACK = true;
        }
        Log.i("ALARM_STATE", ALARM_STATE + String.valueOf(ALARM_STATE_ON));


    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOG_TAG, "inside HandlerClass instance of mHandler...");
            Object obj = msg.obj;
            int arg1 = msg.arg1;
            boolean flag = (arg1 == 0);
            Log.d(LOG_TAG, "Successful?: " + flag);
            super.handleMessage(msg);
        }
    };

    private class DeliveryCallback implements AWSIotMqttMessageDeliveryCallback {
        /**
         * Callback interface to be implemented by application.
         *
         * @param status   New status of the publish message.
         * @param userData User defined data which was passed in as part of the publish call.
         */
        @Override
        public void statusChanged(MessageDeliveryStatus status, Object userData) {
            Log.d(LOG_TAG, "inside statusChanged method in DeliveryCallback...");
            Message msg = Message.obtain();
            msg.arg1 = (status == MessageDeliveryStatus.Success) ? 0 : 1;
            msg.obj = (StringBuffer) userData;
            mHandler.sendMessage(msg);
            if (status == MessageDeliveryStatus.Fail) {
                Log.d(LOG_TAG, "Delivery Failed");
                return;
            }
            Log.d(LOG_TAG, "Delivery Success");
        }
    }

    private void publish(String topic, String msg) {
        Log.d(LOG_TAG, "*********************************************************8");
        Log.d(LOG_TAG, "publishing to topic: " + topic + " msg: " + msg);
        try {
            DeliveryCallback callback = new DeliveryCallback();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(" success ");
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS1, callback, stringBuffer);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Publish error.", e);
        }

    }

    private void subscribe(String topic) {

        Log.d(LOG_TAG, " subscribing to topic: " + topic);


        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS1,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");

                                        JSONObject obj = new JSONObject(message);
                                        boolean isConfirmation = obj.has("message");

                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        Log.d(LOG_TAG, "IsConfirmation " + isConfirmation);


                                        // No need to do anything
                                        if(isConfirmation) return;

                                        //check again if alarm is on
                                        try {
                                            ALARM_STATE_ON = checkAlarm();
                                            Log.d("ALARM_STATE", String.valueOf(ALARM_STATE_ON));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        //Detection
                                        if (ALARM_STATE_ON) {
                                            publish(topicData, ALARM_UP); //trigger alarm
                                            runAlarmNotify();
                                        }

                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }

                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }

    }

    void init_database() {
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );
    }

    private void connectIoT() {
        Log.d(LOG_TAG, "clientId = " + clientId);
        String d_tag = "MainActivity.connectIoT";
        try {
            mqttManager.connect(clientKeyStore, (status, throwable) -> {
                Log.d(LOG_TAG, "Status = " + status);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                            Log.d(d_tag, "Connected");
                            subscribe(sensorReadings);
                            if (PUBLISH_ACK) {
                                publish(topicData,ALARM_DOWN);
                                PUBLISH_ACK=false;

                            }

                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {
                                Log.d(d_tag, "Connection error.", throwable);
                            }
                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {
                                Log.d(d_tag, "Connection error.", throwable);
                            }
                        } else {
                            Log.d(d_tag, "Disconnected");

                        }
                    }
                });
            });
        } catch (final Exception e) {
            Log.d(d_tag, "Connection error.", e);
        }
    }


    private void init_publish_subscribe() {
        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        mqttManager.setAutoReconnect(true);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(30);


        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
        Log.d("KeystorePath", keystorePath);
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;


        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);


                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.",
                                e);
                    }
                }
            }).start();
        }


    }


    /**
     * inflates custom card layouts into card views
     **/
    private void initCards() {
        CardView alarmCard = findViewById(R.id.alarm_state_card);
        View child1 = LayoutInflater.from(this).inflate(R.layout.alarm_state_card_layout, alarmCard, false);
        alarmCard.addView(child1);
        SwitchCompat alarmSwitch;
        ImageView bellImg;

        alarmSwitch = child1.findViewById(R.id.alarmSwitch);
        bellImg = child1.findViewById(R.id.icon_alarm);
        bellImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAlarmNotify();
            }
        });
        alarmSwitch.setChecked(ALARM_STATE_ON);
        if (ALARM_STATE_ON) {
            alarmSwitch.setText("ON");
            alarmSwitch.setTextColor(Color.GREEN);
        } else {
            alarmSwitch.setText("OFF");
            alarmSwitch.setTextColor(Color.RED);

        }
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //user wants alarm on
                    publish(topicMode, ALARM_ON);
                    buttonView.setText("ON");
                    buttonView.setTextColor(Color.GREEN);

                    SharedPreferences sharedPref = getSharedPreferences("GatekeeperData", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("alarmState", "ON");
                    editor.commit();


                } else {
                    publish(topicMode, ALARM_OFF);
                    buttonView.setText("OFF");
                    buttonView.setTextColor(Color.RED);

                    SharedPreferences sharedPref = getSharedPreferences("GatekeeperData", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("alarmState", "OFF");
                    editor.commit();

                }
            }
        });




        CardView logsCard = findViewById(R.id.logs_card);
        View logChild = LayoutInflater.from(this).inflate(R.layout.logs_card_layout, logsCard, false);
        logsCard.addView(logChild);
        numDetectedTV = logChild.findViewById(R.id.logs_detected_num);

        logChild.findViewById(R.id.link_logs_full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogsLookupActivity.class);
                intent.putParcelableArrayListExtra("logs",logs);
                startActivity(intent);
            }
        });


        CardView settingsCard = findViewById(R.id.alaram_settings_card);
        View child2 = LayoutInflater.from(this).inflate(R.layout.settings_card_layout, settingsCard, false);
        settingsCard.addView(child2);


        TextView gotosettings = child2.findViewById(R.id.link_settings);
        gotosettings.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnspecifiedImmutableFlag")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        CardView websiteCard = findViewById(R.id.website_card);
        View c = LayoutInflater.from(this).inflate(R.layout.website_card_layout, websiteCard, false);
        websiteCard.addView(c);
        TextView websiteLink = c.findViewById(R.id.link_website);
        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Website is not deployed yet :)", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void runAlarmNotify() {

        Intent fullScreenIntent = new Intent(getApplicationContext(), FullScreenAlert.class);
        PendingIntent fullScreenPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {

            fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), String.valueOf(ALARM_CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_alarm_icon_50)
                .setContentTitle("ALARM TRIGGERED")
                .setContentText("Movement is detected on position \"Glavni ulaz\"!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(fullScreenPendingIntent, true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(ALARM_CHANNEL_ID, builder.build());


    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GatekeeperAlar,";
            String description = "GatekeeperAlarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(String.valueOf(ALARM_CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private Boolean checkAlarm() throws ParseException {


        SharedPreferences sharedPref = getSharedPreferences("GatekeeperData", 0);
        String fromTime = sharedPref.getString("fromTime", "");
        String toTime = sharedPref.getString("toTime", "");
        //ALARM STATE
        ALARM_STATE = sharedPref.getString("alarmState", "OFF");
        if (ALARM_STATE.equals("ON")) {
            ALARM_STATE_ON = true;
        } else return false;


        // check time
        Long dateNow = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeNow = dateFormat.format(dateNow);

        return isWithingSH(fromTime, timeNow, toTime);
    }

    public static boolean isWithingSH(String startSH, String now, String stopSH) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm", Locale.GERMAN);
            Date startTime = parser.parse(startSH);
            Date endTime = parser.parse(stopSH);
            Date nowTime = parser.parse(now);

            if (startTime.before(endTime)) {
                return startTime.before(nowTime) && endTime.after(nowTime);
            } else {
                return nowTime.after(startTime);
            }
        } catch (java.text.ParseException e) {
            return false;
        }
    }


    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void,Object[]> {
        @Override
        protected Object[] doInBackground(Void... params) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            return databaseAccess.getAllLogs();

        }

        @Override
        protected void onPostExecute(Object[] res) {
            if (res != null) {
                logs=(ArrayList<LogItem>) res[0];
                NUMDETECTED = (Integer) res[1];
                numDetectedTV.setText(String.valueOf(NUMDETECTED));

                Log.d(LOG_TAG, "Logs: Retrieval success!");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        GetAllItemsAsyncTask task = new GetAllItemsAsyncTask();
        task.execute();
    }


}
