package com.aws.contribution;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.aman.lambda.pojo.parameter.Parameters;
import com.aman.lambda.pojo.parameter.Report;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
* When a new snapshot is uploaded to S3, this function is triggered. It
* activates a Step Function to further process the new snapshot.
* 
* STEP_MACHINE_ARN should be specified as an Environment variable for this Lambda Function.
* 
* Part of the Smart Security Camera project.
* 
* @author mark.west
*/
/**
 * The Class LambdaFunctionHandler. This application loads GZIPped CSV file to
 * DynamoDB using AWS Lambda function.
 * 
 */
public class SaveItemsIntoDynamoDB implements RequestHandler<List<Item>, Parameters> {

	private static final String ARN_AWS_STATE_MACHINE_CONTRIBUTION_POC = "arn:aws:states:us-east-1:658803210908:stateMachine:Contribution-POC";
	private static final AWSStepFunctions AWS_STEP_FUNCTION_CLIENT = AWSStepFunctionsClientBuilder.defaultClient();
	@Override
	public Parameters handleRequest(List<Item> list, Context arg1) {

		System.out.println("aman");
		return null;
	}

	
}
