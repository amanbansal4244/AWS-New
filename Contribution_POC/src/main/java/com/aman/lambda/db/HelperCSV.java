package com.aman.lambda.db;

import java.text.ParseException;

import com.amazonaws.services.dynamodbv2.document.Item;

/**
 * The Class Helper and this helps to whatever columns we want to insert into DynamoDB.
 * This class helps you parse CSV file.
 * 
 */
public class HelperCSV {

	/**
	 * Parses the it.
	 *
	 * @param nextLine
	 *            the next line
	 * @return the item
	 * @throws ParseException
	 *             the parse exception
	 */

	public Item parseIt(String[] nextLine) throws ParseException {

		Item newItem = new Item();

		/* def_spcification table */
		String Id = nextLine[0];
		System.out.println("IdddddddddD:"+ Id);
		String specification_category;
		String specification_name;
		String specification_value;
		String is_ancillary1;
		String RECORD_STATUS_ID;

		if (nextLine[1] != null && !nextLine[1].isEmpty()) {
			
			specification_category = nextLine[1];
		} else {
			specification_category = " ";
		}
		if (nextLine[2] != null && !nextLine[2].isEmpty()) {
			specification_name = nextLine[2];
		} else {
			specification_name = " ";
		}
		if (nextLine[3] != null && !nextLine[3].isEmpty()) {
			specification_value = nextLine[3];
		} else {
			specification_value = " ";
		}
		if (nextLine[4] != null && !nextLine[4].isEmpty()) {
			is_ancillary1 = nextLine[4];
		} else {
			is_ancillary1 = " ";
		}
		
		if (nextLine[5] != null && !nextLine[5].isEmpty()) {
			RECORD_STATUS_ID = nextLine[5];
		} else {
			RECORD_STATUS_ID = " ";
		}

		newItem.withPrimaryKey("Id", Id); // Here, we are defining 'Id' will be PK.
		newItem.withString("specification_category", specification_category);
		newItem.withString("specification_name", specification_name);
		newItem.withString("specification_value", specification_value);
		newItem.withString("aman", is_ancillary1); // aman will be column name in DB
		newItem.withString("RECORD_STATUS_ID", RECORD_STATUS_ID);

		return newItem;
	}
}
