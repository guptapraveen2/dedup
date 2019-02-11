package com.marketo.processor;

import java.util.Collection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;

import com.marketo.BaseTest;


/**
 * Test the functionality of {@code Reconciler}.
 *
 * @author PraveenG
 */
public class ReconcilerTest extends BaseTest
{
	@Test
	public void testperformDupsReconcile() throws Exception
	{
		final String inputFile = pathToResouceDir + "leads.json";
		final Reconciler reconciler = new Reconciler();

		final Collection<JSONObject> results =  reconciler.performDupsReconcile(inputFile);
		Assert.assertEquals("Unexpected object count.", 5, results.size());
	}


	@Test
	public void testMultiRecordUpdate() throws Exception
	{
		final Reconciler reconciler = new Reconciler();
		final Collection<JSONObject> results = reconciler.performDupsReconcile(pathToResouceDir + "leadsDupsAtIdAndEmail.json");
		Assert.assertFalse(results.isEmpty());
		Assert.assertEquals("Unexpected object count.", 1, results.size());
		final String outPutJson = "{\n" +
								  "    \"_id\": \"jkj238238jdsnfsj23\",\n" +
								  "    \"email\": \"mae@bar.com\",\n" +
								  "    \"firstName\": \"John\",\n" +
								  "    \"lastName\": \"Smith\",\n" +
								  "    \"address\": \"888 Mayberry St\",\n" +
								  "    \"entryDate\": \"2014-05-07T17:33:20+00:00\"\n" +
								  "  }";

		final JSONObject output = (JSONObject) new JSONParser().parse(outPutJson);
		Assert.assertEquals("Unexpected object", output, results.iterator().next());
	}
}
