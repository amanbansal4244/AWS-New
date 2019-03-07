package com.aws.contribution;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class UpdateDynamoDbTable {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	static String tableName = "aman";
	static Table table = dynamoDB.getTable(tableName);

	/**
	 * 
	 * @param Id
	 *            PK of the table
	 * @param updatedNewValue
	 *            value to be updated
	 */
	public static void updateColumnValueBasedOnPK(String Id, String updatedNewValue , String newIndexValue) {

		/***
		 * Key of this map is the column name of the DB Id -> PK of the table
		 * specification_name -> Column name of DB
		 * RECORD_STATUS_ID -> Column name of DB
		 ***/
		Map<String, AttributeValue> attributeValues = new HashMap<>();
		attributeValues.put("Id", new AttributeValue().withS(Id));
		attributeValues.put("specification_name", new AttributeValue().withS(updatedNewValue));
		attributeValues.put("RECORD_STATUS_ID", new AttributeValue().withS(updatedNewValue));

		UpdateItemRequest updateItemRequest = new UpdateItemRequest()
				.withTableName(tableName)
				.addKeyEntry("Id", new AttributeValue().withS(Id))
				.addAttributeUpdatesEntry("specification_name", 
						new AttributeValueUpdate().withValue(new AttributeValue().withS(updatedNewValue)))
				.addAttributeUpdatesEntry("RECORD_STATUS_ID", 
						new AttributeValueUpdate().withValue(new AttributeValue().withS(newIndexValue)));
		
		UpdateItemResult updateItemResult = client.updateItem(updateItemRequest);
		System.out.println("Printing item after update to new attribute...");
		System.out.println(updateItemResult);
	}

}
