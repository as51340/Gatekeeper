package com.example.gatekeeperapp;

import android.content.ClipData;
import android.os.AsyncTask;
import android.os.Bundle;

import com.amazonaws.auth.policy.actions.AutoScalingActions;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import com.amazonaws.mobileconnectors.dynamodbv2.document.internal.KeyDescription;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.gatekeeperapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void,  Map<String, KeyDescription>> {
        @Override
        protected Map<String, KeyDescription> doInBackground(Void... params) {
            Log.d("DynamoDB_fail_test", "access");
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MainActivity.this);

          //  Log.d("All_memos", databaseAccess.getAllMemos().toString());

            return databaseAccess.getAllMemos();

        }

        @Override
        protected void onPostExecute( Map<String, KeyDescription> documents) {
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