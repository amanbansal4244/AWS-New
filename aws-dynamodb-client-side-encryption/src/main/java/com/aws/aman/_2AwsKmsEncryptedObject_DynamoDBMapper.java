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
public class _2AwsKmsEncryptedObject_DynamoDBMapper implements RequestHandler<Object, String> {
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	static DynamoDB dynamoDB = new DynamoDB(client);

	//static String tableName = "ProductCatalog1";
	final String cmkArn = "arn:aws:kms:us-east-1:658803210908:key/b22e78fc-a039-4a34-bb67-05597265f2c9";
	final String region = "us-east-1";

	@Override
	public String handleRequest(Object obj, Context arg1) {
		encryptRecord(cmkArn, region);

		return "successfully executed";
	}

	public static void encryptRecord(final String cmkArn, final String region) {

		/*
		 * Step 1: Create the Direct KMS Provider Create an instance of the AWS KMS
		 * client with the specified region. Then, use the client instance to create an
		 * instance of the Direct KMS Provider with your preferred CMK.
		 * 
		 * This example uses the Amazon Resource Name (ARN) to identify the CMK, but you
		 * can use any valid CMK identifier.
		 */
		// Set up our configuration and clients
		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
		final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(region).build();
		final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, cmkArn);

		/*
		 * Step 2: Create the DynamoDB Encryptor and DynamoDB Mapper Use the Direct KMS
		 * Provider that you created in the previous step to create an instance of the
		 * DynamoDB Encryptor. You need to instantiate the lower-level DynamoDB
		 * Encryptor to use the DynamoDB Mapper.
		 * 
		 * Next, create an instance of your DynamoDB database and a mapper
		 * configuration, and use them to create an instance of the DynamoDB Mapper.
		 * 
		 * Important: When using the DynamoDBMapper to add or edit signed (or encrypted
		 * and signed) items, configure it to use a save behavior, such as PUT, that
		 * includes all attributes, as shown in the following example. Otherwise, you
		 * might not be able to decrypt your data.
		 */
		// Encryptor creation
		final DynamoDBEncryptor encryptor = DynamoDBEncryptor.getInstance(cmp);
		// Mapper Creation
		// Please note the use of SaveBehavior.PUT (SaveBehavior.CLOBBER works as well).
		// Omitting this can result in data-corruption.
		DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.PUT).build();
		DynamoDBMapper mapper = new DynamoDBMapper(ddb, mapperConfig, new AttributeEncryptor(encryptor));

		
		
		/*Step 3: Define your DynamoDB table : See 'DataPoJo.java'
		 * 
		 * Step 4: Encrypt and save a table item Now, when you create a table item and
		 * use the DynamoDB Mapper to save it, the item is automatically encrypted and
		 * signed before it is added to the table.
		 * 
		 * This example defines a table item called record. Before it is saved in the
		 * table, its attributes are encrypted and signed based on the annotations in
		 * the DataPoJo class. In this case, all attributes except for
		 * PartitionAttribute, SortAttribute, and LeaveMe are encrypted and signed.
		 * PartitionAttribute and SortAttributes are only signed. The LeaveMe attribute
		 * is not encrypted or signed.
		 * 
		 * To encrypt and sign the record item, and then add it to the ExampleTable,
		 * call the save method of the DynamoDBMapper class. Because your DynamoDB
		 * Mapper is configured to use the PUT save behavior, the item replaces any item
		 * with the same primary keys, instead of updating it. This ensures that the
		 * signatures match and you can decrypt the item when you get it from the table.
		 */
		// Sample object to be encrypted
		DataPoJo record = new DataPoJo();
		record.setPartitionAttribute("is this");
		record.setSortAttribute(55);
		record.setExample("data");
		record.setSomeNumbers(99);
		record.setSomeBinary(new byte[] { 0x00, 0x01, 0x02 });
		record.setLeaveMe("alone");
		
		System.out.println("Plaintext Record: " + record);
		
		// Save the item to the DynamoDB table
		mapper.save(record);

	}

}
