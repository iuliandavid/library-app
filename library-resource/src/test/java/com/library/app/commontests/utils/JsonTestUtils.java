/**
 * 
 */
package com.library.app.commontests.utils;

import java.io.InputStream;
import java.util.Scanner;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Helper class for comparing JSONS
 * 
 * @author iulian
 *
 */
public class JsonTestUtils {

	public static final String BASE_JOSN_PATH = "json/";

	public static String readJsonFile(final String relativePath) {
		final InputStream is = JsonTestUtils.class.getClassLoader().getResourceAsStream(BASE_JOSN_PATH + relativePath);
		try (Scanner s = new Scanner(is)) {
			return s.useDelimiter("\\A").hasNext() ? s.next() : "";
		}
	}

	public static void assertJsonMatchesExpectedJson(final String actualJson, final String expectedJson) {
		try {
			JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
		} catch (final JSONException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
