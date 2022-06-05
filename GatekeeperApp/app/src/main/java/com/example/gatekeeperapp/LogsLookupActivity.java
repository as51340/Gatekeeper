package com.example.gatekeeperapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gatekeeperapp.database.DatabaseAccess;
import com.example.gatekeeperapp.helpers.LogItem;
import com.example.gatekeeperapp.helpers.LogsItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class LogsLookupActivity extends AppCompatActivity {
    ArrayList<LogItem> logs;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogsLookupActivity.this.setTitle("Logs - triggered sensor");
        setContentView(R.layout.logs_lookup_layout);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }






    private void populateList(ArrayList<LogItem> logs) {
        ListAdapter numbersArrayAdapter = new LogsItemAdapter(this,logs );

        // create the instance of the ListView to set the numbersViewAdapter
        ListView numbersListView = findViewById(R.id.logs_list);

        // set the numbersViewAdapter for ListView
        numbersListView.setAdapter(numbersArrayAdapter);
    }




    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void, ArrayList<LogItem>> {
        @Override
        protected ArrayList<LogItem> doInBackground(Void... params) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(LogsLookupActivity.this);
            return databaseAccess.getAllLogs();

        }

        @Override
        protected void onPostExecute(ArrayList<LogItem> logs) {
            if (logs != null) {
                populateList(logs);
                Log.d("MainActivity-Logs", "Retrieval success!");
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
    }

}




