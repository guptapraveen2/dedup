package com.marketo.utils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.marketo.write.Log;


/**
 * JsonConfig is handling the all the parsing operation from file to JSON Object.
 *
 * @author Praveen Gupta
 */
public final class JsonConfig
{
	/**
	 * Provide the JSON Array object based on the input tag name and file.
	 *
	 * @param inputFile represent the input file name. Must not be {@code null}.
	 * @param tagName represent the tag. Must not be {@code null}.
	 *
	 * @return the JSONArray object.
	 *
	 * @throws IOException if error occurred during I/O operation.
	 * @throws ParseException if error occurred while parsing JSON objects in the file.
	 */
	public static JSONArray getJSONArray(final String inputFile, final String tagName) throws IOException, ParseException
	{
		return (JSONArray) getJSONObject(inputFile).get(tagName);
	}


	/**
	 * Provide the duplicated reconciled output record in same format as input.
	 *
	 * @param outPutFileName name of the output file.
	 * @param objects list of duplicated reconciled records.
	 *
	 * @throws IOException if error occurred during I/O operation.
	 */
	public static void output(final String outPutFileName, final Collection<JSONObject> objects) throws IOException
	{
		final String fileName = outPutFileName.isEmpty()? "output.json": outPutFileName;
		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
		{
			final JSONObject leads = new JSONObject();
			leads.put(Constants.LEADS, objects);
			leads.writeJSONString(writer);
			Log.getLogger().write(leads.toJSONString());
		}
	}


	/**
	 * Provide the JSONObject object based on the input file.
	 *
	 * @param inpFileName name of the input file.
	 *
	 * @return the JSONObject object.
	 *
	 * @throws IOException if error occurred during I/O operation.
	 * @throws ParseException if error occurred while parsing JSON objects in the file.
	 */
	private static JSONObject getJSONObject(final String inpFileName) throws IOException, ParseException
	{
		final JSONParser parser = new JSONParser();
		try (final FileReader fr = new FileReader(inpFileName))
		{
			final Object records = parser.parse(fr);
			return (JSONObject) records;
		}
	}
}
