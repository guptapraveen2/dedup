package com.marketo.write;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONObject;


/**
 * Log is providing the facility to the capture the information of each meaning full event in the change.log file.
 *
 * @author PraveenG
 */
public final class Log
{
	/**
	 * Singleton instance of Log.
	 */
	private static Log s_log;

	/**
	 * Represent the change log file name.
	 */
	private static String s_fileName = "change.log";

	/**
	 * Instance of Buffered writer.
	 */
	private final BufferedWriter logs;


	/**
	 * Construct log object.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	private Log() throws IOException
	{
		final FileWriter log = new FileWriter(s_fileName);
		logs = new BufferedWriter(log);
	}


	/**
	 * Providing the log instance.
	 *
	 * @return the singleton instance of log object.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public static Log getLogger() throws IOException
	{
		if (s_log == null)
		{
			s_log = new Log();
		}
		return s_log;
	}


	/**
	 * Set the log file name.
	 *
	 * @param fileName file that the change log.
	 */
	public static void setFileName(final String fileName)
	{
		if (!fileName.isEmpty())
		{
			s_fileName = fileName;
		}
	}


	/**
	 * Writes a string.
	 *
	 * @param str String to be written
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public void write(final String str) throws IOException
	{
		logs.write(str);
	}


	/**
	 * Closing the stream.
	 *
	 * @throws IOException if something wrong in closing the steam.
	 */
	public void close() throws IOException
	{
		logs.close();
	}


	/**
	 * Print the value of each field if change.
	 *
	 * @param oldObject old JSON object.
	 * @param newObject new JSON object.
	 *
	 * @throws IOException if something wrong in writing the log file.
	 */
	public void writeDiff(final JSONObject oldObject, final JSONObject newObject) throws IOException
	{
		logs.write("---------------------------------------------------. \n");
		logs.write("List of updated fields from an old value to new value. \n");
		for (final Iterator iterator = oldObject.keySet().iterator(); iterator.hasNext(); )
		{
			final Object key = iterator.next();
			if (!oldObject.get(key).equals(newObject.get(key)))
			{
				logs.write(String.format("Updating field  %s: from %s to %s %n", key, oldObject.get(key), newObject.get(key)));
			}
		}
		logs.write("---------------------------------------------------. \n");
	}
}
