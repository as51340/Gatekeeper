import React, {useEffect, useState} from 'react';
import ReactDOM from 'react-dom';

import * as AWS from 'aws-sdk';
import "./DBData.css";
import Time from './Time'

const DBData = () => {
    const [data, setData] = useState();


    const docClient = new AWS.DynamoDB.DocumentClient({
        region: "eu-central-1",
        IdentityPoolId: 'eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd',
        secretAccessKey: '0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE',
        accessKeyId: 'AKIATT53DCKUKE73QKVO',
    });


    useEffect(() =>{
        console.log("Trying to fetch data from database table...");


        let params = {
            TableName: "wx_data",
        }

        docClient.scan(params, function (err, fetchedData) {
            if (!err) {
                console.log("Fetched data: " + JSON.stringify(fetchedData));
                JSON.stringify(fetchedData);
                setData(fetchedData);

                // return data
            } else {
                console.log("Err: " + err.message + ", params: " + params.TableName);
            }

        });

    }, []);

    return (
        <div className="card">

            <div className="title">Gatekeeper App</div>

            {data?.Items.map((item) => (
                <div key={item.sample_time} className="card-wrapper">

                    <span >

                    <Time className="time" alarmDate={ new Date(item.sample_time).toUTCString().split(' ').slice(0, 5).join(' ')}></Time>

                    </span>


                    {JSON.stringify(item.message)!= undefined ?

                        <span className = "alarm-wrapper">

                            {JSON.stringify(item.message).includes("nontriggered") ?

                                <span className="off"> OFF </span> :

                                <span className="on"> ON </span>
                            }

                        </span>


                        :

                        <span> </span>
                    }


                </div>

            ))}

        </div>
    );
    
};

export default DBData;