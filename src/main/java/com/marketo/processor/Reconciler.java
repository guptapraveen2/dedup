package com.marketo.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.marketo.utils.Constants;
import com.marketo.utils.JsonCompare;
import com.marketo.utils.JsonConfig;
import com.marketo.write.Log;


/**
 * This class is used to reconciled the de-duplicate the records.
 *
 * @author Praveen Gupta
 */
public final class Reconciler
{
	/**
	 * Map contains the records based on the record ID.
	 * <pre>
	 * DEVELOPER NOTE: [Praveen Gupta - 2019/02/09]:
	 * Approach1: Given the problem, records have two unique keys (ID, email), so if we are storing
	 * the records in the list, then for each new record whole need to scan to make sure no duplicate record available.
	 * This makes the O(n) time complexity for searching and O(n) for traversing each record,
	 * so total problem time complexity is O(n*n).
	 *</pre>
	 * <pre>
	 * Approach 2: Using the two maps are providing the O(1) time complexity for searching the record based on ID and email.
	 * First map store the key as ID and value as Object, another map contains the key as email and value as ID.
	 * so total problem time complexity is O(n*1)= O(n)
	 * </pre>
	 */
	private final HashMap<String, JSONObject> idToJSONObjectMap = new HashMap<>();

	/**
	 * Map contains the record ID based on the email Id.
	 */
	private final HashMap<String, String> emailToIdMap = new HashMap<>();


	/**
	 * Parse the given input file and provide the de-duplicate the records.
	 *
	 * @param inputFileName given input json file.
	 *
	 * @return list of de-duplicate the records, otherwise empty list. Will never return {@code null}.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public Collection<JSONObject> performDupsReconcile(final String inputFileName) throws IOException
	{
		JSONArray records = null;
		if (inputFileName.isEmpty())
		{
			Log.getLogger().write("Input file name is invalid. \n");
			return Collections.emptyList();
		}
		try
		{
			records = JsonConfig.getJSONArray(inputFileName, Constants.LEADS);
		}
		catch (final ParseException exp)
		{
			Log.getLogger().write("Error occurred while parsing JSON objects in the file.\n");
		}

		if (records == null)
		{
			Log.getLogger().write(String.format("There is no leads tag available in given file: %s %n", inputFileName));
			return Collections.emptyList();
		}
		reconciledLeads(records);
		return idToJSONObjectMap.values();
	}


	/**
	 * This method reconciled the duplicate records based on the certain conditions.
	 * 1. The data from the newest date should be preferred.
	 * 2. duplicate IDs count as dups. Duplicate emails count as dups. Both must be unique in our dataset. Duplicate values elsewhere do not count as dups.
	 * 3. If the dates are identical the data from the record provided last in the list should be preferred.
	 *
	 * @param records A Json Array object.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	private void reconciledLeads(final JSONArray records) throws IOException
	{
		for (final Object rec : records)
		{
			final JSONObject newRecord = (JSONObject) rec;
			final String recordId = (String) newRecord.get(Constants.RECORD_ID);
			final String recordEmail = (String) newRecord.get(Constants.EMAIL);

			Log.getLogger().write(String.format("New Record with id: %s  email: %s %n", recordId, recordEmail));

			final List<JSONObject> existingRecords = getDuplicateRecords(recordId, recordEmail);

			if (existingRecords.isEmpty())
			{
				// New record is not exist in the output collection.
				Log.getLogger().write("New record added in the output record collection. \n");
				idToJSONObjectMap.put(recordId, newRecord);
				emailToIdMap.put(recordEmail, recordId);
			}
			else if (existingRecords.size() == 1)
			{
				// Record found in the output collection based on new id or email id.
				singleRecordUpdate(newRecord, existingRecords.get(0));
			}
			else if (existingRecords.size() == 2)
			{
				// DEVELOPER NOTE: [Praveen - 2019/02/09]: This is the corner case when two different record founds for new record ID and new email.
				multiRecordUpdate(newRecord, existingRecords);
			}
		}
		Log.getLogger().write("Output collection is ready. \n");
	}


	/**
	 * Update the single record based on the new record.
	 *
	 * @param newRecord The new record.
	 * @param existingRecord The existing record.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	private void singleRecordUpdate(final JSONObject newRecord, final JSONObject existingRecord) throws IOException
	{
		final String existingID = (String) existingRecord.get(Constants.RECORD_ID);
		final String existingEmail = (String) existingRecord.get(Constants.EMAIL);

		Log.getLogger().write(String.format("Output record collection contains the duplicate record with id: %s  email: %s %n",
											existingID, existingEmail));

		final JSONObject applicableObject = JsonCompare.compareRecords(newRecord, existingRecord);
		if (newRecord.equals(applicableObject))
		{
			// new record is applicable because it contains latest date or last in the list.
			updateRecord(existingRecord, newRecord);
		}
		else
		{
			Log.getLogger().write("Existing Output record has the latest date, ignoring the new record.");
		}
	}


	/**
	 * Update the list record based on the new record.
	 *
	 * @param newRecord The new record.
	 * @param existingRecords list of existing records.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	private void multiRecordUpdate(final JSONObject newRecord, final List<JSONObject> existingRecords) throws IOException
	{
		final JSONObject record2 = existingRecords.get(0);
		final JSONObject record3 = existingRecords.get(1);

		final String record2Id = (String) record2.get(Constants.RECORD_ID);
		final String record2Email = (String) record2.get(Constants.EMAIL);

		final String record3Id = (String) record3.get(Constants.RECORD_ID);
		final String record3Email = (String) record3.get(Constants.EMAIL);

		Log.getLogger().write("Two duplicate records found is output collection. \n");
		Log.getLogger().write(String.format(" id: %s  email: %s %n", record2Id, record2Email));
		Log.getLogger().write(String.format(" id: %s  email: %s %n", record3Id, record3Email));

		final JSONObject applicableObject = JsonCompare.compareRecords(newRecord, record2, record3);

		if (newRecord.equals(applicableObject))
		{
			// new record is applicable because it contains latest date or last in the list.
			// remove the both old records from both maps, there is possibility both records are not present in both map based on their Id and email, but
			// there no harm to remove.
			emailToIdMap.remove(record2Email);
			idToJSONObjectMap.remove(record2Id);

			emailToIdMap.remove(record3Email);
			idToJSONObjectMap.remove(record3Id);

			Log.getLogger().write(String.format("Updating the existing record id: %s  email: %s %n", record2Id, record2Email));
			Log.getLogger().writeDiff(record2, newRecord);
			Log.getLogger().write(String.format("Updating the existing record id: %s  email: %s %n", record3Id, record3Email));
			Log.getLogger().writeDiff(record3, newRecord);

			final String recordId = (String) newRecord.get(Constants.RECORD_ID);
			final String recordEmail = (String) newRecord.get(Constants.EMAIL);

			idToJSONObjectMap.put(recordId, newRecord);
			emailToIdMap.put(recordEmail, recordId);
		}
		else if (record2.equals(applicableObject))
		{
			// Applicable record is record2, so ignore new record and remove the record3.
			updateRecord(record3, record2);
			Log.getLogger().write("Existing Output collection record has the latest date, ignoring the new record.");

		}
		else if (record3.equals(applicableObject))
		{
			// Applicable record is record3, so ignore new record and remove the record2.
			updateRecord(record2, record3);
			Log.getLogger().write("Existing Output collection record has the latest date, ignoring the new record.");
		}
	}


	/**
	 * Update the old record based on the new record in output collection.
	 *
	 * @param oldRecord out-dated record.
	 * @param newRecord new record.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	private void updateRecord(final JSONObject oldRecord, final JSONObject newRecord) throws IOException
	{
		final String newRecordId = (String) newRecord.get(Constants.RECORD_ID);
		final String newEmailId = (String) newRecord.get(Constants.EMAIL);
		final String oldRecordId = (String) oldRecord.get(Constants.RECORD_ID);
		final String oldEmailId = (String) oldRecord.get(Constants.EMAIL);

		Log.getLogger().write(String.format("Update the existing record recordId: %s  email: %s with new record recordId: %s email: %s  %n",
											oldRecordId, oldEmailId, newRecordId, newEmailId));
		idToJSONObjectMap.remove(oldRecordId);
		emailToIdMap.remove(oldEmailId);

		Log.getLogger().writeDiff(oldRecord, newRecord);

		idToJSONObjectMap.put(newRecordId, newRecord);
		emailToIdMap.put(newEmailId, newRecordId);
	}


	/**
	 * Provide the list of duplicate records based on the given record ID and record email.
	 *
	 * @param recordId record is used to find the existing record the from the output collection.
	 * @param recordEmail recordEmail is used to find the existing record the from the output collection.
	 *
	 * @return list of duplicate records if found otherwise empty list. Will never be {@code null}.
	 */
	private List<JSONObject> getDuplicateRecords(final String recordId, final String recordEmail)
	{
		final List<JSONObject> existingRecords = new ArrayList<>(2);
		final JSONObject objectFromIdMap = idToJSONObjectMap.get(recordId);
		final String recordIdFromEmailMap = emailToIdMap.get(recordEmail);

		if (objectFromIdMap != null)
		{
			existingRecords.add(objectFromIdMap);
		}

		if (recordIdFromEmailMap != null && !recordIdFromEmailMap.equals(recordId))
		{
			existingRecords.add(idToJSONObjectMap.get(recordIdFromEmailMap));
		}

		return existingRecords;
	}
}
