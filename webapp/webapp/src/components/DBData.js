import React, {useState} from 'react';
import ReactDOM from 'react-dom';

import * as AWS from 'aws-sdk';


const DBData = () => {
    const [data, setData] = useState();

    const docClient = new AWS.DynamoDB.DocumentClient({
        region: "eu-central-1",
        IdentityPoolId: 'eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd',
        secretAccessKey: '0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE',
        accessKeyId: 'AKIATT53DCKUKE73QKVO',
    });



    const fetchDataFromDynamoDb = (tableName) => {
        console.log("Trying to fetch data from database table...");

        let params = {
            TableName: tableName,
        }

        docClient.scan(params, function (err, fetchedData) {
            if (!err) {
                console.log("Fetched data: " + JSON.stringify(fetchedData));
                JSON.stringify(fetchedData);
                setData(JSON.stringify(fetchedData));
                return data
            } else {
                console.log("Err: " + err.message + ", params: " + params.TableName);
            }

        });

    }

    return (
        <div className="dbdata">

            <div className="message">Gatekeeper App</div>
            {fetchDataFromDynamoDb("wx_data")}
            <div className="message">{JSON.stringify(data)}</div>



        </div>
    );
    
};

export default DBData;