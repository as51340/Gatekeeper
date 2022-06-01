import React from 'react';
import ReactDOM from 'react-dom';

// STARTS HERE
import * as AWS from 'aws-sdk';
import { ConfigurationOptions, credentials } from 'aws-sdk';
import {fetchData} from './AWSFunctions';

const fetchDataFromDynamoDb = () => {

    AWS.config.update({
        IdentityPoolId: 'eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd',
        region: "eu-central-1",
        accessKeyId: '0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE',
        secretAccessKey: 'AKIATT53DCKUKE73QKVO',
    });

    console.log("Trying to fetch data from database table..." );
    fetchData('wx_data');
}

const DBData = () => {

    return (
        <div className="dbdata">

            <div className="message">Helooo!</div>
           <button onClick= {fetchDataFromDynamoDb}>Fetch data</button>

        </div>
    );
};

export default DBData;