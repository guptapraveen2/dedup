package com.marketo;

import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import com.marketo.utils.Constants;
import com.marketo.utils.JsonConfig;


/**
 * ApplicationIntegrationTest Description.
 *
 * @author PraveenG
 */
public final class ApplicationIntegrationTest extends BaseTest
{
	@Test
	public void testPerformDupsReconcile() throws Exception
	{
		final String inputFile = pathToResouceDir + "leads.json";
		final String outPutFile = pathToResouceDir + "output.json";
		Application.performDupsReconcile(inputFile, outPutFile);
		final JSONArray results =  JsonConfig.getJSONArray(outPutFile, Constants.LEADS);
		Assert.assertEquals("Unexpected object count.", 5, results.size());
	}
}
