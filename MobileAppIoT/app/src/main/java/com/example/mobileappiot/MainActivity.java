package com.example.mobileappiot;

import androidx.appcompat.app.AppCompatActivity;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
//import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
//import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
//import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
//import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.S3Exception;
//import software.amazon.awssdk.services.s3.S3Client;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.s3.model.CreateBucketRequest;

import android.os.Bundle;



import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


  Regions clientRegion = Regions.US_WEST_2;
 // S3Client s3 = S3Client.builder().region(region).build();
    //AmazonS3Client
    AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
     @Override
     public AWSCredentials getCredentials() {
         return new BasicAWSCredentials("AKIATT53DCKUKE73QKVO", "0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE");
     }

     @Override
     public void refresh() {

     }
 };

   // BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATT53DCKUKE73QKVO", "0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE");
   //AWSIotClient awsIotClient = (AWSIotClient) AWSIotClient.builder().withRegion(Regions.US_WEST_2).build();

    AmazonS3 s3Client = AmazonS3Client.builder().withCredentials(awsCredentialsProvider).withRegion(clientRegion).build();






}