/**
 * 
 */
package com.library.app.common.json;

import com.google.gson.JsonObject;
import com.library.app.common.model.OperationResult;

/**
 * Writes JSON from the OperationResult
 * 
 * @author iulian
 *
 */
public class OperationResultJsonWriter {

	private OperationResultJsonWriter() {
	}

	public static String toJson(final OperationResult operationResult) {
		return JsonWriter.writeToString(getJsonObject(operationResult));
	}

	private static Object getJsonObject(final OperationResult operationResult) {
		if (operationResult.isSuccess()) {
			return getJsonSucces(operationResult);
		} else {
			return getJsonError(operationResult);
		}
	}

	private static Object getJsonSucces(final OperationResult operationResult) {
		return operationResult.getEntity();
	}

	private static Object getJsonError(final OperationResult operationResult) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("errorIdentification", operationResult.getErrorIdentification());
		jsonObject.addProperty("errorDescription", operationResult.getErrorDescription());
		return jsonObject;
	}
}
