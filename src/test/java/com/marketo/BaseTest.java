package com.marketo;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.marketo.write.Log;


/**
 * BaseTest Description.
 *
 * @author PraveenG
 */
public class BaseTest
{
	protected static String pathToResouceDir ;

	@BeforeClass
	public static void setup()
	{
		pathToResouceDir = System.getProperty("user.dir") + "/src/test/resources/";
		Log.setFileName(pathToResouceDir + "change.log");
	}

	@AfterClass
	public static void tearDown() throws IOException
	{
		Log.getLogger().close();
	}
}
