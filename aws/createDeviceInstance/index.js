let AWS = require('aws-sdk');
let dynamodb = new AWS.DynamoDB({ apiVersion: '2012-08-10' });
let uuid = require('uuid');
exports.handler = async(event, context) => {
    // Load the message passed into the Lambda function into a JSON object 
    var eventText = JSON.stringify(event, null, 2);

    // For Event Debugging
    console.log("Received event:", eventText);

    const deviceId = uuid.v4();
    const status = event.deviceStatus ? event.deviceStatus : null;
    const serialNumber = event.serialNumber ? event.serialNumber : null;

    if (status == null) {
        console.log('Cannot Create Deivce: No Status');
        const response = {
            statusCode: 500,
            body: JSON.stringify('No Status Provided'),
        };
        return response;
    } else if (serialNumber == null) {
        console.log('Cannot Create Deivce: No Serial Number');
        const response = {
            statusCode: 500,
            body: JSON.stringify('No Serial Number Provided'),
        };
        return response;

    } else {
        let dbParams = {
            TableName: process.env.TABLE_NAME,
            Item: {
                'deviceId': { 'S': deviceId },
                'status': { 'S': status },
                'serialNumber': { 'S': serialNumber }
            }
        };

        console.log('Writing to DynamoDB' + JSON.stringify(dbParams));
        let dbErr = false;

        await dynamodb.putItem(dbParams).promise().then(data => {
            console.log('Sucsess:' + JSON.stringify(data));
            const response = {
                statusCode: 200,
                body: JSON.stringify({ 'deviceId': deviceId }),
            };
            return response;
        }).catch(err => {
            console.log(err);
            const response = {
                statusCode: 500,
                body: JSON.stringify('Could not write to DB'),
            };
            return response;
        });


        // Create Associated Session instance 
        let sessionId = uuid.v4();
        let sessionParams = {
            TableName: process.env.SESSION_TABLE,
            Item: {
                'sessionId': { 'S': sessionId },
                'device': { 'S': deviceId }
            }
        };

        await dynamodb.putItem(sessionParams).promise().then(data => {
            console.log("Session Created: " + sessionId);
        }).catch(err => {
            console.log(err);
        })

    }
};