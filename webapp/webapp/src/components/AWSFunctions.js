import * as AWS from 'aws-sdk'

const docClient = new AWS.DynamoDB.DocumentClient()

export const fetchData = (tableName) => {
    console.log("Here....")
    var params = {
        TableName: "wx_data",
       // region: "eu-central-1"
    }

    docClient.scan(params, function (err, data) {
        if (!err) {
            console.log("Data: " + data);
        } else {
            console.log("Err: " + err.message);
        }
    })
}