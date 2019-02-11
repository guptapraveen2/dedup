package com.marketo.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;

import com.marketo.BaseTest;


/**
 * Test the functionality of {@code JsonCompare}.
 *
 * @author PraveenG
 */
public class JsonCompareTest extends BaseTest
{
	@Test
	public void testCompareRecords() throws Exception
	{
		final String record1 = "  {\n" +
						 "    \"_id\": \"jkj238238jdsnfsj23\",\n" +
						 "    \"email\": \"foo@bar.com\",\n" +
						 "    \"firstName\": \"John\",\n" +
						 "    \"lastName\": \"Smith\",\n" +
						 "    \"address\": \"123 Street St\",\n" +
						 "    \"entryDate\": \"2014-05-07T17:30:20+00:00\"\n" +
						 "  }";

		final String record2 = "  {\n" +
						 "    \"_id\": \"edu45238jdsnfsj23\",\n" +
						 "    \"email\": \"mae@bar.com\",\n" +
						 "    \"firstName\": \"Ted\",\n" +
						 "    \"lastName\": \"Masters\",\n" +
						 "    \"address\": \"44 North Hampton St\",\n" +
						 "    \"entryDate\": \"2014-05-07T17:31:20+00:00\"\n" +
						 "  }";

		final String record3 = "  {\n" +
							   "    \"_id\": \"jkj238238jdsnfsj23\",\n" +
							   "    \"email\": \"mae@bar.com\",\n" +
							   "    \"firstName\": \"John\",\n" +
							   "    \"lastName\": \"Smith\",\n" +
							   "    \"address\": \"888 Mayberry St\",\n" +
							   "    \"entryDate\": \"2014-05-07T17:33:20+00:00\"\n" +
							   "  }";

		final JSONParser parser = new JSONParser();
		final JSONObject object1 = (JSONObject) parser.parse(record1);
		final JSONObject object2 = (JSONObject) parser.parse(record2);
		final JSONObject object3 = (JSONObject) parser.parse(record3);

		JSONObject applicable = JsonCompare.compareRecords(object1, object2);
		Assert.assertEquals("Unexpected object", object2, applicable);

		applicable = JsonCompare.compareRecords(object1, object2, object3);
		Assert.assertEquals("Unexpected object", object3, applicable);
	}
}
