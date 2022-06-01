import React from 'react';
import ReactDOM from 'react-dom';

// STARTS HERE
import * as AWS from 'aws-sdk';
import { ConfigurationOptions } from 'aws-sdk';
import {fetchData} from './AWSFunctions';




const fetchDataFormDynamoDb = () => {
    const configuration: ConfigurationOptions = {
        region: 'eu-central-1',
        endpoint: 'http://a2mr3kdz48c7g8-ats.iot.eu-central-1.amazonaws.com',
        secretAccessKey: 'AKIATT53DCKUKE73QKVO',
        accessKeyId: '0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE'
    }

    AWS.config.update(configuration);
    console.log("Trying to fetch data from database table...")
    fetchData('wx_data')
}


const DBData = () => {



    return (
        <div className="dbdata">

            <div className="message">Helooo!</div>
           <button onClick= {fetchDataFormDynamoDb}>Fetch data</button>

        </div>
    );










};

export default DBData;