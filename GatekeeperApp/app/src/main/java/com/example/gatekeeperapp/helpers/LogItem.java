package com.example.gatekeeperapp.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.sql.Timestamp;

public class LogItem implements Parcelable {

    private String txtDate;

    private String txtTime;

    private Timestamp t_timestamp;

    // create constructor to set the values for all the parameters of the each single view
    public LogItem(String date, String time, Timestamp timestamp) {
        txtDate = date;
        txtTime = time;
        t_timestamp = timestamp;
    }

    protected LogItem(Parcel in) {
        txtDate = in.readString();
        txtTime = in.readString();
    }

    public static final Creator<LogItem> CREATOR = new Creator<LogItem>() {
        @Override
        public LogItem createFromParcel(Parcel in) {
            return new LogItem(in);
        }

        @Override
        public LogItem[] newArray(int size) {
            return new LogItem[size];
        }
    };

    public String getDate() {
        return txtDate;
    }

    public String getTime() {
        return txtTime;
    }

    public Timestamp getTimestamp() {
        return t_timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txtDate);
        dest.writeString(txtTime);
    }
}