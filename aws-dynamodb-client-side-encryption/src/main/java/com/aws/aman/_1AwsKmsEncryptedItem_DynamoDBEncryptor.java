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
public class _1AwsKmsEncryptedItem_DynamoDBEncryptor implements RequestHandler<Object, String> {

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

		/*
		 * Step 1: Create the Direct KMS Provider:
		 * 
		 * Create an instance of the AWS KMS
		 * client with the specified region. Then, use the client instance to create an
		 * instance of the Direct KMS Provider with your preferred CMK.
		 * 
		 * Set up our configuration and clients. All of this is thread-safe and can be
		 * reused across calls.
		 * 
		 * Provider Configuration.
		 */

		final AWSKMS kms = AWSKMSClientBuilder.standard().withRegion(region).build();
		final DirectKmsMaterialProvider cmp = new DirectKmsMaterialProvider(kms, cmkArn);

		/*
		 * Step 2: Create an item:
		 * 
		 * This example defines a record HashMap that represents
		 * a sample table item. Sample record to be encrypted
		 */
		final String partitionKeyName = "partition_attribute"; // this is PK.
		final String sortKeyName = "sort_attribute";
		final Map<String, AttributeValue> record = new HashMap<>();
		record.put(partitionKeyName, new AttributeValue().withS("is this"));
		record.put(sortKeyName, new AttributeValue().withN("55"));
		record.put("example", new AttributeValue().withS("data"));
		record.put("some numbers", new AttributeValue().withN("99"));
		record.put("and some binary", new AttributeValue().withB(ByteBuffer.wrap(new byte[] { 0x00, 0x01, 0x02 })));
		record.put("leave me", new AttributeValue().withS("alone")); // We want to ignore this attribute

		/*
		 * Step 3: Create a DynamoDBEncryptor: Encryptor creation.
		 * 
		 * Create an instance of the
		 * DynamoDBEncryptor with the Direct KMS Provider.
		 */

		final DynamoDBEncryptor encryptor = DynamoDBEncryptor.getInstance(cmp);

		/*
		 * Step 4: Create a DynamoDB encryption context:
		 * 
		 * The DynamoDB encryption context
		 * contains information about the table structure and how it is encrypted and
		 * signed. If you use the DynamoDBMapper, the AttributeEncryptor creates the
		 * encryption context for you.
		 * 
		 * Information about the context of our data (normally just Table information).
		 */

		final EncryptionContext encryptionContext = new EncryptionContext.Builder().withTableName(tableName)
				.withHashKeyName(partitionKeyName).withRangeKeyName(sortKeyName).build();

		/*
		 * Step 5: Create the attribute actions object:
		 * 
		 * Attribute actions determine which
		 * attributes of the item are encrypted and signed, which are only signed, and
		 * which are not encrypted or signed.
		 * 
		 * In Java, to specify attribute actions, you create a HashMap of attribute name
		 * and EncryptionFlags value pairs.
		 * 
		 * For example, the following Java code creates an actions HashMap that encrypts
		 * and signs all attributes in the record item, except for the partition key and
		 * sort key attributes, which are signed, but not encrypted, and the test
		 * attribute, which is not signed or encrypted.
		 * 
		 * 
		 * Describe what actions need to be taken for each attribute.
		 */
		final EnumSet<EncryptionFlags> signOnly = EnumSet.of(EncryptionFlags.SIGN);
		final EnumSet<EncryptionFlags> encryptAndSign = EnumSet.of(EncryptionFlags.ENCRYPT, EncryptionFlags.SIGN);
		final Map<String, Set<EncryptionFlags>> actions = new HashMap<>();
		for (final String attributeName : record.keySet()) {
			switch (attributeName) {
			case partitionKeyName: // fall through
			case sortKeyName:
				// Partition and sort keys must not be encrypted but should be signed
				actions.put(attributeName, signOnly);
				break;
			case "leave me":
				// For this example, we are neither signing nor encrypting this field
				break;
			default:
				// We want to encrypt and sign everything else
				actions.put(attributeName, encryptAndSign);
				break;
			}
		}
		// End set-up

		/*
		 * Step 6: Encrypt and sign the item:
		 * 
		 * To encrypt and sign the table item, call
		 * the encryptRecord method on the instance of the DynamoDBEncryptor. Specify
		 * the table item (record), the attribute actions (actions), and the encryption
		 * context (encryptionContext).
		 */
		
		final Map<String, AttributeValue> encrypted_record = encryptor.encryptRecord(record, actions,
				encryptionContext);
		
		System.out.println("Plaintext Record: " + record);
		System.out.println("Encrypted Record: " + encrypted_record);
		
		
		/*
		 * Step 7: Put the item in the DynamoDB table:
		 * Finally, put the encrypted and signed item in the DynamoDB table.
		 */
		final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		ddb.putItem(tableName, encrypted_record);

		
		/*
		 *  Decryption is identical. We'll pretend that we retrieved the record from DynamoDB.
		 */
	    final Map<String, AttributeValue> decrypted_record = encryptor.decryptRecord(encrypted_record, actions, encryptionContext);
	    System.out.println("Decrypted Record: " + decrypted_record);
		 
	}
}
