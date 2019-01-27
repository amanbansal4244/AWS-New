package com.aws.aman;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionContext;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.EncryptionFlags;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Example showing use of AWS KMS CMP with record encryption functions directly.
 */
public class _1AwsKmsDecryptedItem_DynamoDBDecryptor implements RequestHandler<Object, String> {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	//First create the table name 'ProductCatalog1' from AWS console with PK 'partition_attribute' as per this example.
	static String tableName = "ProductCatalog1";
	final String cmkArn = "arn:aws:kms:us-east-1:658803210908:key/b22e78fc-a039-4a34-bb67-05597265f2c9";
	final String region = "us-east-1";

	@Override
	public String handleRequest(Object obj, Context arg1) {
		try {
			encryptRecord(tableName, cmkArn, region);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return "successfully executed";
	}

	private static void encryptRecord(String tableName, String cmkArn, String region) throws GeneralSecurityException {

		final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(region).build();
		final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, cmkArn);

		final String partitionKeyName = "partition_attribute";
		final String sortKeyName = "sort_attribute";
		final Map<String, AttributeValue> record = new HashMap<>();
		record.put(partitionKeyName, new AttributeValue().withS("is this"));
		record.put(sortKeyName, new AttributeValue().withN("55"));
		record.put("example", new AttributeValue().withS("data"));
		record.put("some numbers", new AttributeValue().withN("99"));
		record.put("and some binary", new AttributeValue().withB(ByteBuffer.wrap(new byte[] { 0x00, 0x01, 0x02 })));
		record.put("leave me", new AttributeValue().withS("alone")); // We want to ignore this attribute

		final DynamoDBEncryptor encryptor = DynamoDBEncryptor.getInstance(cmp);


		final EncryptionContext encryptionContext = new EncryptionContext.Builder().withTableName(tableName)
				.withHashKeyName(partitionKeyName).withRangeKeyName(sortKeyName).build();

		final EnumSet<EncryptionFlags> signOnly = EnumSet.of(EncryptionFlags.SIGN);
		final EnumSet<EncryptionFlags> encryptAndSign = EnumSet.of(EncryptionFlags.ENCRYPT, EncryptionFlags.SIGN);
		final Map<String, Set<EncryptionFlags>> actions = new HashMap<>();
		for (final String attributeName : record.keySet()) {
			switch (attributeName) {
			case partitionKeyName: // fall through
			case sortKeyName:
				actions.put(attributeName, signOnly);
				break;
			case "leave me":
				break;
			default:
				actions.put(attributeName, encryptAndSign);
				break;
			}
		}
		
		final Map<String, AttributeValue> encrypted_record = encryptor.encryptRecord(record, actions,
				encryptionContext);
		
		/*
		 * Step 7: Decrypt the item:
		 * 
		 *  Decryption is identical. We'll pretend that we retrieved the record from DynamoDB.
		 */
	    final Map<String, AttributeValue> decrypted_record = encryptor.decryptRecord(encrypted_record, actions, encryptionContext);
	    System.out.println("Decrypted Record: " + decrypted_record);
		 
	}
}
