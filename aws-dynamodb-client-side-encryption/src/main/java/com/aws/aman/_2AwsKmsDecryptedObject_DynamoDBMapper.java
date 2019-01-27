package com.aws.aman;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotTouch;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * This demonstrates how to use the {@link DynamoDBMapper} with the
 * {@link AttributeEncryptor} to encrypt your data. Before you can use this you
 * need to set up a table called "ExampleTable" to hold the encrypted data.
 */
public class _2AwsKmsDecryptedObject_DynamoDBMapper implements RequestHandler<Object, String> {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	final String cmkArn = "arn:aws:kms:us-east-1:658803210908:key/b22e78fc-a039-4a34-bb67-05597265f2c9";
	final String region = "us-east-1";
	static String tableName = "ProductCatalog3";
	
	@Override
	public String handleRequest(Object obj, Context arg1) {
		decryptRecord(cmkArn, region);

		return "successfully executed";
	}

	public static void decryptRecord(final String cmkArn, final String region) {

		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
		final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(region).build();
		final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, cmkArn);
		final DynamoDBEncryptor encryptor = DynamoDBEncryptor.getInstance(cmp);

		DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.PUT).build();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb, mapperConfig, new AttributeEncryptor(encryptor));

		/*
		 * Retrieve the encrypted item (directly without decrypting) from DynamoDB so we can see it in our example.
		 */
		final Map<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put("partition_attribute", new AttributeValue().withS("is this"));
		itemKey.put("sort_attribute", new AttributeValue().withN("55"));
		
		System.out.println("Encrypted Record: " + ddb.getItem(tableName, itemKey).getItem());

		// Retrieve (and Decrypt) it from DynamoDB
		DataPoJo decrypted_record = mapper.load(DataPoJo.class, "is this", 55);
		System.out.println("Decrypted Record: " + decrypted_record);
		
	}

}
