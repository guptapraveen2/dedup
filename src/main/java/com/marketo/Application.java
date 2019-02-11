package com.marketo;

import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.marketo.processor.Reconciler;
import com.marketo.utils.JsonConfig;
import com.marketo.write.Log;


/**
 * Represent the application starting point. It accept three files, input file to parse the json,
 * output file to reconcile the dups record and change log file.
 */
public class Application
{
	public static void main(String[] args) throws IOException
	{
		final Scanner input = new Scanner(System.in);
		System.out.print("Please enter input filename:");
		final String inputFile = input.nextLine();
		System.out.print("Please enter output filename [output.json]:");
		final String outputFile = input.nextLine();
		System.out.print("Please enter log filename [change.log]:");
		Log.setFileName(input.nextLine());

		performDupsReconcile(inputFile, outputFile);
	}


	/**
	 * Perform reconcile duplicate records from the given input file.
	 *
	 * @param inputFile name of the input file.
	 * @param outputFile name of the output file, default name is output.json.
	 *
	 * @throws IOException if error occurred during I/O operation.
	 */
	protected static void performDupsReconcile(final String inputFile, final String outputFile) throws IOException
	{
		try
		{
			Log.getLogger().write(String.format("Parsing the given input file: %s %n", inputFile));

			final Collection<JSONObject> results = new Reconciler().performDupsReconcile(inputFile);
			if (!results.isEmpty())
			{
				Log.getLogger().write("Final output collection records: \n");
				JsonConfig.output(outputFile, results);
			}
			else
			{
				Log.getLogger().write("The result is a null value.\n");
			}
		}
		catch (final IOException ex)
		{
			Log.getLogger().write("Something went wrong while reading the input file...\n");
		}
		finally
		{
			Log.getLogger().close();
		}
	}
}
