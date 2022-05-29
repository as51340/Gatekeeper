package com.example.gatekeeperapp;

import android.os.AsyncTask;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import com.amazonaws.mobileconnectors.dynamodbv2.document.internal.KeyDescription;
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

      /*  GetAllItemsAsyncTask g = new  GetAllItemsAsyncTask();
        try {
            g.get();
            System.out.println("Here");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }


    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void,  Map<String, KeyDescription>> {
        @Override
        protected Map<String, KeyDescription> doInBackground(Void... params) {
            Log.d("DynamoDB_fail_test", "access");
           DatabaseAccess databaseAccess = DatabaseAccess.getInstance(MainActivity.this);
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