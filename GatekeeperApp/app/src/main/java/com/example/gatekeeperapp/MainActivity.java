package com.example.gatekeeperapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.internal.KeyDescription;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
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


    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;


    String clientId;
    String topicData;
    String topicMode;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;
    CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        clientId =  "MyNewESP32";
        topicData = "actuator/esp32/data";
        topicMode = "actuator/esp32/mode";


        init_database();
        init_publish_subscribe();
        connectIoT();
        initCards();

    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOG_TAG,"inside HandlerClass instance of mHandler...");
            Object obj = msg.obj;
            int arg1 = msg.arg1;
            boolean flag = (arg1==0);
            Log.d(LOG_TAG,"Successful?: "+  flag);
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
            Log.d(LOG_TAG,"inside statusChanged method in DeliveryCallback...");
            Message msg = Message.obtain();
            msg.arg1 = (status == MessageDeliveryStatus.Success) ? 0 : 1;
            msg.obj = (StringBuffer) userData;
            mHandler.sendMessage(msg);
            if (status == MessageDeliveryStatus.Fail) {
                Log.d(LOG_TAG,"Delivery Failed");
                return;
            }
            Log.d(LOG_TAG,"Delivery Success");
        }
    }
    private void publish(String topic, String msg){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "publishing to topic: " + topic + "msg: "+ jsonObject.toString());
        try {
            DeliveryCallback callback = new DeliveryCallback();
            StringBuffer stringBuffer= new StringBuffer();
            stringBuffer.append(" success ");
            //mqttManager.publishData(jsonObject.toString().getBytes(StandardCharsets.UTF_8), topic, AWSIotMqttQos.QOS0,callback,stringBuffer);
            mqttManager.publishString("hellllllllllooooooo", "loptica/hh", AWSIotMqttQos.QOS1,callback,stringBuffer);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Publish error.", e);
        }

    }

    private void subscribe(String topic) {

        Log.d(LOG_TAG, " subscribing to topic: " + topic);

        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);


                                    } catch (UnsupportedEncodingException e) {
                                        Log.d(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }

                    });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Subscription error.", e);
        }

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
                            Log.d("d_tag", "Connected");
                            //subscribe(topicData);
                            publish(topicData, "none");



                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {
                                Log.d(d_tag, "Connection error.", throwable);
                            }
                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {
                                Log.d(d_tag, "Connection error.", throwable);
                            }
                        } else {
                            Log.d("d_tag", "Disconnected");
                            ;

                        }
                    }
                });
            });
        } catch (final Exception e) {
            Log.d(d_tag, "Connection error.", e);
        }
    }

    private void init_database() {
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );
    }

    private void init_publish_subscribe() {
        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);
        mqttManager.setAutoReconnect(false);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(20);



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
        alarmCard.setOnClickListener(v -> {
            Log.d("MainActivity", String.valueOf(v.getId()==R.id.alarm_state_card));
        });

        CardView settingsCard = findViewById(R.id.alaram_settings_card);
        View child2 = LayoutInflater.from(this).inflate(R.layout.settings_card_layout, settingsCard, false);
        settingsCard.addView(child2);



        CardView logsCard = findViewById(R.id.logs_card);
        View d = LayoutInflater.from(this).inflate(R.layout.logs_card_layout, logsCard, false);
        logsCard.addView(d);

        CardView websiteCard = findViewById(R.id.website_card);
        View c = LayoutInflater.from(this).inflate(R.layout.website_card_layout, websiteCard, false);
        websiteCard.addView(c);


    }


    //TODO: smjesti donji kod u prikladni file

    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void, Map<String, KeyDescription>> {
        @Override
        protected Map<String, KeyDescription> doInBackground(Void... params) {
            Log.d("DynamoDB_fail_test", "access");
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MainActivity.this);

            //  Log.d("All_memos", databaseAccess.getAllMemos().toString());

            return databaseAccess.getAllMemos();

        }

        @Override
        protected void onPostExecute(Map<String, KeyDescription> documents) {
            if (documents != null) {
                // populateMemoList(documents);
                Log.d("post", "pporukaZaPost");
            }
        }
    }

    /**
     * Lifecycle method - called when the app is resumed (including the first-time start)
     */
    @Override
    protected void onResume() {
        super.onResume();
        GetAllItemsAsyncTask task = new GetAllItemsAsyncTask();
        task.execute();
        Log.d("jj", "hdi");
    }

}