package com.example.gatekeeperapp.database;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.example.gatekeeperapp.helpers.LogItem;

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DatabaseAccess {

    /**
     * The Amazon Cognito POOL_ID to use for authentication and authorization.
     */
    private final String COGNITO_POOL_ID = "eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd";

    /**
     * The AWS Region that corresponds to the POOL_ID above
     */
    private final Regions COGNITO_REGION = Regions.EU_CENTRAL_1;

    /**
     * The name of the DynamoDB table used to store data.  If using AWS Mobile Hub, then note
     * that this is the "long name" of the table, as specified in the Resources section of
     * the console.  This should be defined with the Notes schema.
     */
    private final String DYNAMODB_TABLE = "wx_data";

    /**
     * The Android calling context
     */
    private Context context;

    /**
     * The Cognito Credentials Provider
     */
    private CognitoCachingCredentialsProvider credentialsProvider;

    /**
     * A connection to the DynamoDD service
     */
    private AmazonDynamoDBClient dbClient;

    /**
     * A reference to the DynamoDB table used to store data
     */
    private Table dbTable;

    /**
     * This class is a singleton - storage for the current instance.
     */
    private static volatile DatabaseAccess instance;


    public ArrayList<LogItem> DBlogs;
    private Object object;


    /**
     * Creates a new DatabaseAccess instance.
     *
     * @param context the calling context
     */
    public DatabaseAccess(Context context) {
        this.context = context;

        // Create a new credentials provider

        credentialsProvider = new CognitoCachingCredentialsProvider(context, COGNITO_POOL_ID, COGNITO_REGION);
        Log.d("DynamoDB_fail_test", "a");

        dbClient = new AmazonDynamoDBClient(credentialsProvider);

        Log.d("DynamoDB_fail_test", "endpoint > " + dbClient.getEndpoint());

        // Create a table reference

        dbClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));

        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);

        Log.d("DynamoDB_fail_test", "yes");

    }

    public AmazonDynamoDBClient getDbClient() {
        return dbClient;
    }

    /**
     * Singleton pattern - retrieve an instance of the DatabaseAccess
     */
    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }


    public void init_database() {
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // context
                COGNITO_POOL_ID, // Identity Pool ID
                COGNITO_REGION // Region
        );
    }


    /**
     * Retrieve all the data from the database
     *
     * @return the list of detected
     */

    public Object[] getAllLogs() {

        Integer counter = 0;

        // fetch only rows with "trigger" value
        Map<String, AttributeValue> innerExpresssion =
                new HashMap<String, AttributeValue>();
        innerExpresssion.put("value", new AttributeValue().withS("trigger"));

        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val_msg", new AttributeValue().withM(innerExpresssion));


        ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withFilterExpression("message = :val_msg")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withLimit(50);
        ScanResult result = getDbClient().scan(scanRequest);

        DBlogs = new ArrayList<LogItem>();
        LogItem tmpLogItem;
        Long sampleTime;

        for (Map<String, AttributeValue> item : result.getItems()) {
            sampleTime = Long.valueOf(item.get("sample_time").getN());
            Timestamp timestamp = new Timestamp(sampleTime);
            Date date = new Date(timestamp.getTime());
            if (checkDayBefore24(date)) {
                counter++;
            }

            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat f1 = new SimpleDateFormat("hh:mm:ss");
            String dateTxt = f.format(date);
            String timeTxt = f1.format(date);
            tmpLogItem = new LogItem(dateTxt, timeTxt, timestamp);

            DBlogs.add(tmpLogItem);
        }

        Collections.sort(DBlogs, new Comparator<LogItem>() {
            @Override
            public int compare(LogItem o1, LogItem o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        return new Object[]{DBlogs, counter};
    }




    public boolean checkDayBefore24(Date date) {
        DateTime dateTime = new DateTime(date); // Convert java.util.Date to Joda-Time DateTime.
        return dateTime.isBefore(DateTime.now().minusDays(1));
    }
}

