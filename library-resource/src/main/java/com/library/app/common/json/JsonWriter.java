/**
 * 
 */
package com.library.app.common.json;

import com.google.gson.Gson;

/**
 * @author iulian
 *
 */
public class JsonWriter {

	public static String writeToString(final Object object) {
		if (object == null) {
			return "";
		}
		return new Gson().toJson(object);
	}
}
