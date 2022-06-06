package com.example.gatekeeperapp.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gatekeeperapp.R;

import java.util.ArrayList;

public class LogsItemAdapter extends ArrayAdapter<LogItem> {

    // invoke the suitable constructor of the ArrayAdapter class
    public LogsItemAdapter(@NonNull Context context, ArrayList<LogItem> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.logs_item, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        LogItem logItem = getItem(position);


        TextView date = currentItemView.findViewById(R.id.date);
        date.setText(logItem.getDate());

        TextView time = currentItemView.findViewById(R.id.time);
        time.setText(logItem.getTime());

        // then return the recyclable view
        return currentItemView;
    }
}
