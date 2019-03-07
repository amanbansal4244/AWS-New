package com.aws.contribution;

import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.common.collect.Lists;

public class SaveItemsIntoDynamoDB1 implements RequestHandler<List<Item>, Object> {
	/** Provide the AWS region which your DynamoDB table is hosted. */
	private static final Region AWS_REGION = Region.getRegion(Regions.US_EAST_1);
	private static final String CLIENT_REGION = "us-east-1";
	private static AmazonDynamoDB DYNAMO_DB_CLIENT = AmazonDynamoDBClientBuilder.standard().build();
	private static DynamoDB DYNAMO_DB = new DynamoDB(DYNAMO_DB_CLIENT);
	/** The DynamoDB table name. */
	private static final String DYNAMO_TABLE_NAME = "def_specification";

	@Override
	public Object handleRequest(List<Item> itemList, Context context) {
		//context.getLogger().log("Input Function [" + context.getFunctionName() + "], Parameters [" + parameters + "]");
	
		saveItems(itemList);
		return null;
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
