package com.example.gatekeeperapp.database;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.internal.KeyDescription;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

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

     ;
    /**
     * Creates a new DatabaseAccess instance.
     * @param context the calling context
     */
    private DatabaseAccess(Context context) {
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

    /**
     * create a new alarm in the database

     */
    public void create() {


        Document d= new Document();
        d.put("sample_time", 4848);
        d.put("device_data",111);
        dbTable.putItem(d);




    }

    /**
     * Update an existing memo in the database
     * @param memo the memo to save
     */
    public void update(Document memo) {
        Document document = dbTable.updateItem(memo, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }

    /**
     * Delete an existing memo in the database
     * @param memo the memo to delete
     */
    public void delete(Document memo) {
        dbTable.deleteItem(memo.get("userId").asPrimitive(), memo.get("noteId").asPrimitive());
    }

    /**
     * Retrieve a memo by noteId from the database
     * @param noteId the ID of the note
     * @return the related document
     */
    public Document getMemoById(String noteId) {
        return dbTable.getItem(new Primitive(credentialsProvider.getCachedIdentityId()), new Primitive(noteId));
    }

    /**
     * Retrieve all the alarms from the database
     * @return the list of alarms
     */

    public Map<String, KeyDescription> getAllMemos() {

        Map<String, KeyDescription> fml = dbTable.getKeys();

        create();
        ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE);
        ScanResult result = getDbClient().scan(scanRequest);


        for (Map<String, AttributeValue> item : result.getItems()){
            Log.d("Items_in_dbTable " , item.toString());
        }



        return fml;
    }



}
