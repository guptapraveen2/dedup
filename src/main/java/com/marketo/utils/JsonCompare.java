package com.marketo.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

import com.marketo.write.Log;


/**
 * JSON compare provides the various helper methods to compare the {@link JSONObject} based the {@code entryDate}
 *
 * @author Praveen Gupta
 */
public final class JsonCompare
{
	/**
	 *Compare the given records based on the entry date.
	 *
	 * @param record1 Represent the latest Json Object. Must not be {@code null}.
	 * @param record2 Represent the Json Object. Must not be {@code null}.
	 *
	 * @return the newest date record, if both are same then return the latest one.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public static JSONObject compareRecords(final JSONObject record1, final JSONObject record2) throws IOException
	{
		try
		{
			final Date date1 = new SimpleDateFormat(Constants.DATE_FORMAT).parse((String) record1.get(Constants.ENTRY_DATE));
			final Date date2 = new SimpleDateFormat(Constants.DATE_FORMAT).parse((String) record2.get(Constants.ENTRY_DATE));
			Log.getLogger().write(String.format("Date comparison between first %s and second %s records. %n", date1, date2));

			if (date1.compareTo(date2) > 0)
			{
				return record1;
			}
			else if (date1.compareTo(date2) < 0)
			{
				return record2;
			}
			else
			{
				Log.getLogger().write("Dates are the same in both records, considering the last record.\n");
				return record1;
			}
		}
		catch (final ParseException e)
		{
			Log.getLogger().write("Date format is not correct.");
		}

		return null;
	}

	/**
	 *Compare the given records based on the entry date.
	 *
	 * @param record1 Represent the latest Json Object. Must not be {@code null}.
	 * @param record2 Represent the Json Object. Must not be {@code null}.
	 * @param record3 Represent the Json Object. Must not be {@code null}.
	 *
	 * @return the newest date record, if all are same then return the latest one.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public static JSONObject compareRecords(final JSONObject record1, final JSONObject record2, final JSONObject record3) throws IOException
	{
		JSONObject result = compareRecords(record2, record3);
		result = result != null ? compareRecords(record1, result) : record1;
		return result;
	}
}
