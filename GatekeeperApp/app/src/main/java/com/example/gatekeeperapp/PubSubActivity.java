package com.example.gatekeeperapp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;

import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class PubSubActivity<subscribeReq> {
    private static final String ACCESS_KEY = "AKIATT53DCKUKE73QKVO";
    private static final String SECRET_KEY = "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";
    //publish to a Topic

    // Create a client
    BasicAWSCredentials creds = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
    AmazonSNSClient service = new AmazonSNSClient(creds);


    public AmazonSNSClient getService() {
        return service;
    }

    // Create a topic
    CreateTopicRequest createReq = new CreateTopicRequest()
            .withName("MyTopic");
    CreateTopicResult createRes = service.createTopic(createReq);

    // Publish to a topic
    PublishRequest publishReq = new PublishRequest()
            .withTopicArn(createRes.getTopicArn())
            .withMessage("Example notification sent at " + new Date());
    PublishResult pubRes = service.publish(publishReq);

    //subscribe to a Topic

    // Subscribe to topic
    String address;

    {
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    SubscribeRequest subscribeReq = new SubscribeRequest()
            .withTopicArn(createRes.getTopicArn())
            .withProtocol("http")
            .withEndpoint("http://" + address + ":" + "port");


}

