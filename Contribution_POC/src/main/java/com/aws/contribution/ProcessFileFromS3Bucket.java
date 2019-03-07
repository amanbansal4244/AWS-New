package com.aws.contribution;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.aman.lambda.pojo.parameter.Parameters;
import com.aman.lambda.pojo.parameter.Report;
import com.amazonaws.AmazonServiceException;
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
public class ProcessFileFromS3Bucket implements RequestHandler<S3Event, Parameters> {

	private static final String ARN_AWS_STATE_MACHINE_CONTRIBUTION_POC = "arn:aws:states:us-east-1:658803210908:stateMachine:Contribution-POC";
	private static final AWSStepFunctions AWS_STEP_FUNCTION_CLIENT = AWSStepFunctionsClientBuilder.defaultClient();

	@Override
	public Parameters handleRequest(S3Event s3event, Context context) {
		long startTime = System.currentTimeMillis();
		Report statusReport = new Report();
		LambdaLogger logger = context.getLogger();

		logger.log("Lambda Function Started");
		logger.log("I am inside lambda function");

		S3EventNotificationRecord record = s3event.getRecords().get(0);
		String s3BucketName = record.getS3().getBucket().getName();
		String uploadedFileName = record.getS3().getObject().getKey().replace('+', ' ');

		Parameters parameters = new Parameters(s3BucketName, uploadedFileName);

		try {
			uploadedFileName = URLDecoder.decode(uploadedFileName, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			System.out.println("UnsupportedEncodingException Occurred:" + uee);
			uee.printStackTrace();
		}

		System.out.println("S3 Event Receivedddddd: " + s3BucketName + "/" + uploadedFileName);

		StartExecutionRequest startExecutionRequest = new StartExecutionRequest();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		startExecutionRequest.setStateMachineArn(ARN_AWS_STATE_MACHINE_CONTRIBUTION_POC);
		try {
			startExecutionRequest.setInput(objectMapper.writeValueAsString(parameters)); // this helps to convert string into JSON format.
		} catch (JsonProcessingException e) {
			throw new AmazonServiceException("Error in [" + context.getFunctionName() + "]", e);
		}

		parameters.setStartExecutionRequest(startExecutionRequest);

		context.getLogger().log("Step Function [" + startExecutionRequest.getStateMachineArn()
				+ "] will be called with [" + startExecutionRequest.getInput() + "]");

		StartExecutionResult result = AWS_STEP_FUNCTION_CLIENT.startExecution(startExecutionRequest);

		context.getLogger()
				.log("Output Function [" + context.getFunctionName() + "], Result [" + result.toString() + "]");

		System.out.println("Load finish in " + String.valueOf(System.currentTimeMillis() - startTime) + "ms");
		System.out.println("Input Function in In invoke class[" + context.getFunctionName() + "], Parameters ["
				+ parameters + "]");

		return parameters;
	}
}
