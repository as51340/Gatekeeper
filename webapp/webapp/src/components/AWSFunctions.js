import * as AWS from 'aws-sdk'

const docClient = new AWS.DynamoDB.DocumentClient({region: "eu-central-1",
    IdentityPoolId: 'eu-central-1:9426c50a-6fa8-4bbc-951a-c09c395122cd',
    secretAccessKey: '0nQwzBfv9jV6nwgd5lReWgsdqiz98xWLIf99ynTE',
    accessKeyId: 'AKIATT53DCKUKE73QKVO',
});

export const fetchData = (tableName) => {

    var params = {
        TableName: tableName,
    }

    docClient.scan(params, function (err, data) {
        if (!err) {
            console.log("Data: " + JSON.stringify(data));
        } else {
            console.log("Err: " + err.message + ", params: " + params.TableName);
        }
    });
}