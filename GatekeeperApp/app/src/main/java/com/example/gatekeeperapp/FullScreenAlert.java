package com.example.gatekeeperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import java.util.Objects;

public class FullScreenAlert extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_full_screen_alert);

        //set flags so activity is showed when phone is off (on lock screen)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Button button = findViewById(R.id.silence_alarm_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenAlert.this, MainActivity.class);
                intent.putExtra("silenceAlarm",true);
                startActivity(intent);

            }
        });
    }



}