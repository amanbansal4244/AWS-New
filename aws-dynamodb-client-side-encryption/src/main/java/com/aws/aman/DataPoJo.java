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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;

/*
 Define your DynamoDB table
Next, define your DynamoDB table. Use annotations to specify the attribute actions. 
This example creates a DynamoDB table, ExampleTable, and a DataPoJo class that represents table items.

In this sample table, the primary key attributes will be signed but not encrypted. 
This applies to the partition_attribute, which is annotated with @DynamoDBHashKey,
 and the sort_attribute, which is annotated with @DynamoDBRangeKey.

Attributes that are annotated with @DynamoDBAttribute, such as some numbers,
 will be encrypted and signed. The exceptions are attributes that use the @DoNotEncrypt (sign only) 
 or @DoNotTouch (do not encrypt or sign) encryption annotations defined by the DynamoDB Encryption Client. 
 For example, because the leave me attribute has a @DoNotTouch annotation, it will not be encrypted or signed.

 */

//First create the table name 'ProductCatalog1' from AWS console with PK 'partition_attribute' as per this example.
@DynamoDBTable(tableName = "ProductCatalog3")
public final class DataPoJo {
	private String partitionAttribute; // This will be PK.
	private int sortAttribute;
	private String example;
	private long someNumbers;
	private byte[] someBinary;
	private String leaveMe;

	// 'partition_attribute' will be the name of PK.
	@DynamoDBHashKey(attributeName = "partition_attribute")
	public String getPartitionAttribute() {
		return partitionAttribute;
	}

	public void setPartitionAttribute(String partitionAttribute) {
		this.partitionAttribute = partitionAttribute;
	}

	@DynamoDBRangeKey(attributeName = "sort_attribute")
	public int getSortAttribute() {
		return sortAttribute;
	}

	public void setSortAttribute(int sortAttribute) {
		this.sortAttribute = sortAttribute;
	}

	@DynamoDBAttribute(attributeName = "example")
	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	@DynamoDBAttribute(attributeName = "some numbers")
	public long getSomeNumbers() {
		return someNumbers;
	}

	public void setSomeNumbers(long someNumbers) {
		this.someNumbers = someNumbers;
	}

	@DynamoDBAttribute(attributeName = "and some binary")
	public byte[] getSomeBinary() {
		return someBinary;
	}

	public void setSomeBinary(byte[] someBinary) {
		this.someBinary = someBinary;
	}

	@DynamoDBAttribute(attributeName = "leave me")
	@DoNotTouch
	public String getLeaveMe() {
		return leaveMe;
	}

	public void setLeaveMe(String leaveMe) {
		this.leaveMe = leaveMe;
	}

	@Override
	public String toString() {
		return "DataPoJo [partitionAttribute=" + partitionAttribute + ", sortAttribute=" + sortAttribute + ", example="
				+ example + ", someNumbers=" + someNumbers + ", someBinary=" + Arrays.toString(someBinary)
				+ ", leaveMe=" + leaveMe + "]";
	}

}
