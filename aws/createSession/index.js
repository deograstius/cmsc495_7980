let AWS = require('aws-sdk');
let dynamodb = new AWS.DynamoDB({ apiVersion: '2012-08-10' });
let uuid = require('uuid');
exports.handler = async(event, context) => {
    // Load the message passed into the Lambda function into a JSON object 
    var eventText = JSON.stringify(event, null, 2);

    // For Event Debugging
    console.log("Received event:", eventText);

    const deviceId = event.deviceId ? event.deviceId : null;

    if (deviceId == null) {
        console.log('Cannot Create Session: No Device Id');
        const response = {
            statusCode: 500,
            body: JSON.stringify('No Device Id Provided'),
        };
        return response;
    } else {
        let params = {
            TableName: process.env.TABLE_NAME,
            Key: {
                "deviceId": { "S": deviceId }
            }
        };
        let device = await dynamodb.getItem(params).promise().then(data => {
            return data.Item;
        }).catch(err => {
            console.log("DynamoDb Error: " + err);
            return new Error("DynamoDB Error");
        });


        if (device != {} && device != null) {
            // Set device status to active
            let user = device.user.S;

            // If no user has been assigned log Warning
            if (user == '' || user == null) {
                console.warn('Device instance: ' + deviceId + ' reporting with no user');
                return new Error("No User Associated to Device")
            }


            let sessionId = device.session.S;
            let sessionParams = {
                TableName: process.env.SESSION_TABLE,
                Key: {
                    "sessionId": { S: sessionId }
                }
            };


            let session = dynamodb.getItem(sessionParams).promise().then(data => {
                return data.Item;
            }).catch(err => {
                console.log("DynamoDb Error: " + err);
                return new Error("DynamoDB Error");
            });

            if (session == '' || session == null) {
                console.warn('Device instance: ' + deviceId + ' reporting to incorrect session');
                return new Error("No Session Associated to Device");
            }

            // Notify EC2 Instance 

            // Create new Record Instance
            let recordId = uuid.v4();
            let recordFlag = false;
            let recordParams = {
                TableName: process.env.RECORD_TABLE,
                Item: {
                    'recordId': { "S": recordId }
                }
            };
            let recordCreate = dynamodb.putItem(recordParams).promise().then(data => {
                console.log('Record instance created: ' + recordId);
            }).catch(err => {
                console.warn("Dynamo Error: " + err);
                recordFlag = true;
            });

            if (recordFlag) {
                return new Error("Record could not be created!");
            }

            // Associate Record to Session Id
            let updateParams = {
                TableName: process.env.SESSION_TABLE,
                ExpressionAttributeNames: {
                    "#rec": "record",
                },
                ExpressionAttributeValues: {
                    ":val": {
                        S: recordId
                    }
                },
                Key: {
                    'sessionId': { "S": sessionId }
                },
                UpdateExpression: "SET #rec = :val"
            };

            let sessionUpdate = await dynamodb.updateItem(updateParams).promise()
                .then(data => {
                    console.log("Session assigned record: " + recordId);
                })
                .catch(err => {
                    console.warn("Dynamo Update Error: " + err)
                    return new Error('Record could not be assigned');
                });


            // Set Device to be Active 
            let deviceUpdate = {
                TableName: process.env.TABLE_NAME,
                ExpressionAttributeNames: {
                    "#status": "deviceStatus",
                },
                ExpressionAttributeValues: {
                    ":val": {
                        S: 'active'
                    }
                },
                Key: {
                    'deviceId': deviceId
                },
                UpdateExpression: "SET #status = :val"
            };

            let deviceUpdateReq = await dynamodb.updateItem(updateParams).promise()
                .then(data => {
                    console.log("Device Status set to 'active'");
                })
                .catch(err => {
                    console.warn("Dynamo Update Error: " + err)

                    return new Error("Could not set device to active");
                });


            const response = {
                statusCode: 200,
                body: JSON.stringify({ recordId: recordId }),
            };
            return response;
        }



        // If device cannot be found
        const response = {
            statusCode: 500,
            body: JSON.stringify('No Device Could be Found'),
        };
        return response;

    }
};