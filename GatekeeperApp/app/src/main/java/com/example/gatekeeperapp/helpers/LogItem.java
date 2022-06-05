package com.example.gatekeeperapp.helpers;

import java.sql.Time;
import java.sql.Timestamp;

public class LogItem {

    private String txtDate;

    private String txtTime;

    private Timestamp t_timestamp;

    // create constructor to set the values for all the parameters of the each single view
    public LogItem(String date, String time, Timestamp timestamp) {
        txtDate = date;
        txtTime = time;
        t_timestamp = timestamp;
    }

    public String getDate() {
        return txtDate;
    }

    public String getTime() {
        return txtTime;
    }

    public Timestamp getTimestamp() {
        return t_timestamp;
    }
}