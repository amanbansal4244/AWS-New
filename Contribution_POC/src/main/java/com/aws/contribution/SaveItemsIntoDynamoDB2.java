package com.aws.contribution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.aman.lambda.db.HelperCSV;
import com.aman.lambda.pojo.parameter.Report;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaFunction;
import com.amazonaws.services.lambda.invoke.LambdaSerializationException;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import au.com.bytecode.opencsv.CSVReader;

public class SaveItemsIntoDynamoDB2 implements RequestStreamHandler {
	private static final AmazonS3 S3_CLIENT = AmazonS3ClientBuilder.defaultClient();
	AWSLambda awsLambda = AWSLambdaClientBuilder.standard().withRegion(Regions.US_EAST_1)
			/* .withCredentials( new AWSStaticCredentialsProvider( awsCreds ) ) */.build();
	/** Provide the AWS region which your DynamoDB table is hosted. */
	private static final Region AWS_REGION = Region.getRegion(Regions.US_EAST_1);
	private static final String CLIENT_REGION = "us-east-1";
	private static AmazonDynamoDB DYNAMO_DB_CLIENT = AmazonDynamoDBClientBuilder.standard().build();
	private static DynamoDB DYNAMO_DB = new DynamoDB(DYNAMO_DB_CLIENT);
	/** The DynamoDB table name. */
	private static final String DYNAMO_TABLE_NAME = "def_specification";

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		JSONObject JSONObjectResult = null;
		try {
			System.out.println("amannnn");
			/*int line;
			while((line = inputStream.read()) != -1)
	        {
	            //System.out.println("line:" + line);
	        }*/
			
			 getJSONInputStream(inputStream);
			
			
			
			//List<Item> itemList = convertIntoItemList(inputStream);
			//saveItems(itemList);
			/*Iterator<String> keys = JSONObjectResult.keys();
			while(keys.hasNext()) {
			    String key = keys.next();
			    System.out.println("keyyy: "+key);
			    if (JSONObjectResult.get(key) instanceof JSONObject) {
			    		System.out.println("O/p stream:"+JSONObjectResult.getString(key) );
			    }
			}*/
			
			System.out.println("JSONObjectResult:"+ JSONObjectResult);
		} catch (JSONException e) {
			System.out.println("JSONException occured:" + e);
			e.printStackTrace();
		}
	}

	
	
	private List<Item> convertIntoItemList(InputStream inputStream) throws IOException, JSONException {
		String input = IOUtils.toString(inputStream);
		StringBuilder responseStrBuilder = new StringBuilder();
		StringReader input2 = new StringReader(input);
		System.out.println("input1:" + input);
		JSONObject jsonObject = null;
		int k = 0;
		List<Item> itemList = new ArrayList<Item>();
		while ((k = input2.read()) != -1) {
			responseStrBuilder.append((char) k);
		}
			System.out.println("responseStrBuilder:"+ responseStrBuilder);
			System.out.println("responseStrBuilder.toString():"+ responseStrBuilder.toString());
			jsonObject = new JSONObject(responseStrBuilder.toString());
			System.out.println("responseStrBuilder:"+ responseStrBuilder);
			System.out.println("jsonObject:"+ jsonObject);
			Item item = new Item()
				    .withPrimaryKey("specification_id", "specification_id")
				    .withString("specification_category", "Bicycle 123")
				    .withString("specification_name", "123 description")
				    .withString("specification_value", "Hybrid")
				    .withString("is_ancillary", "Brand-Company C");
			
			System.out.println("item:" + item);
			itemList.add(item);
		
		/*System.out.println("responseStrBuilder:" + responseStrBuilder);
		JSONObject result = null;
		try {
			result = new JSONObject(responseStrBuilder.toString());
			System.out.println("result:" + result);
		} catch (JSONException e) {
			System.out.println("JSONException:" + e);
			e.printStackTrace();
		}*/
		return itemList;
	}
	
	/*private String getJSONInputStream(InputStream input) throws IOException {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	      System.out.println("reader:" + reader);
	      String data = "";
	      String line;
	      while ((line = reader.readLine()) != null) {
	    	  System.out.println("line:" + line);
	                data += line;
	      }
	      System.out.println("data:" + data);
	      return data;
	  }*/
	
	private JSONObject getJSONInputStream(InputStream inputStream) throws IOException, JSONException {
		String input = IOUtils.toString(inputStream);
		StringBuilder responseStrBuilder = new StringBuilder();
		StringReader input2 = new StringReader(input);
		System.out.println("input1:" + input);
		int k = 0;
		while ((k = input2.read()) != -1) {
			responseStrBuilder.append((char) k);

		}
		System.out.println("responseStrBuilder:" + responseStrBuilder);
		JSONObject JSONObjectResult = null;
		try {
			JSONObjectResult = new JSONObject(responseStrBuilder.toString());
			Iterator<String> keys = JSONObjectResult.keys();
			while(keys.hasNext()) {
			    String key = keys.next();
			    System.out.println("keyyy: "+key);
			    if (JSONObjectResult.get(key) instanceof JSONObject) {
			    		System.out.println("O/p stream:"+JSONObjectResult.getString(key) );
			    }
			}
			//System.out.println("result:" + result);
		} catch (JSONException e) {
			System.out.println("JSONException:" + e);
			e.printStackTrace();
		}
		return JSONObjectResult;
	}
	
	protected static void saveItems(List<Item> itemList) {
		// AmazonDynamoDB dynamoDBClient = new AmazonDynamoDBClient();
		// dynamoDBClient.setRegion(AWS_REGION);
		// DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

		TableWriteItems tableWriteItems = new TableWriteItems(DYNAMO_TABLE_NAME);
		/*
		 * Item is nothing, it is record in DyanmoDB.
		 */
		for (List<Item> partition : Lists.partition(itemList, 25)) {
			tableWriteItems.withItemsToPut(partition);
			BatchWriteItemOutcome outcome = DYNAMO_DB.batchWriteItem(tableWriteItems);
			// Thread.sleep(1000);
			// logger.log("Insert data after 1 milisecond");
			// logger.log("I am inside for loop");
			System.out.println("I am inside for loop");
			do {
				// Check for unprocessed keys which could happen if you exceed provisioned
				// throughput
				Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
				if (outcome.getUnprocessedItems().size() > 0) {
					// logger.log("Retrieving the unprocessed "
					// +String.valueOf(outcome.getUnprocessedItems().size()) + " items."+"i=====");
					System.out.println("Retrieving the unprocessed "
							+ String.valueOf(outcome.getUnprocessedItems().size()) + " items." + "i=====");
					outcome = DYNAMO_DB.batchWriteItemUnprocessed(unprocessedItems);
				}
			} while (outcome.getUnprocessedItems().size() > 0);
		}
	}

}