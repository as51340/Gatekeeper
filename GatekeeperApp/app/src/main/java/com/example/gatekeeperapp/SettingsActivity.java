package com.example.gatekeeperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    EditText editText_from, editText_to;
    TimePickerDialog picker;
    AppCompatButton saveBtn;
    String startTimeTxt, endTimeTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();


        editText_from = findViewById(R.id.et_time_from);
        editText_from.setInputType(InputType.TYPE_NULL);
        editText_to = findViewById(R.id.et_time_to);
        editText_to.setInputType(InputType.TYPE_NULL);

        saveBtn = findViewById(R.id.save_settings);


        editText_from.setOnClickListener(clockListener);
        editText_to.setOnClickListener(clockListener);
        saveBtn.setOnClickListener(saveListener);


        //get saved times if exists
        SharedPreferences sharedPref = getSharedPreferences("GatekeeperData", 0);
        startTimeTxt = sharedPref.getString("fromTime", "");
        endTimeTxt = sharedPref.getString("toTime", "");

        if (startTimeTxt != null && endTimeTxt != null) {
            editText_from.setText(startTimeTxt);
            editText_to.setText(endTimeTxt);

        }


    }


    private final View.OnClickListener clockListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText etext = (EditText) v;


            final Calendar cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            // time picker dialog
            picker = new TimePickerDialog(SettingsActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                            etext.setText(sHour + ":" + sMinute);
                        }
                    }, hour, minutes, true);
            picker.show();
        }
    };

    private final View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String startTimeTxt, endTimeTxt;
            Date startTime = null, endTime = null;


            startTimeTxt = editText_from.getText().toString();
            endTimeTxt = editText_to.getText().toString();

            SimpleDateFormat parser = new SimpleDateFormat("HH:mm", Locale.UK);
            try {
                startTime = parser.parse(startTimeTxt);
                endTime = parser.parse(endTimeTxt);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Create object of SharedPreferences.
            SharedPreferences sharedPref = getSharedPreferences("GatekeeperData", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("fromTime", startTimeTxt);
            editor.putString("toTime", endTimeTxt);

            Log.d("SettingsActivity", " Times: " + startTimeTxt + " " + endTimeTxt);
            //commit edits
            editor.commit();

            finish();


        }
    };
}