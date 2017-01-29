/**
 * 
 */
package com.library.app.common.json;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.common.model.PaginatedData;

/**
 * @author iulian
 *
 */
public interface EntityJsonConverter<T> {

	/**
	 * Takes a JSON string and returns an object of type T
	 * 
	 * @param json
	 * @return
	 */
	public T convertFrom(final String json);

	/**
	 * Converts a {@link List} of entities into a {@link JsonArray}
	 * 
	 * @param entities
	 * @return
	 */
	public default JsonElement convertToJsonElement(final List<T> entities) {
		final JsonArray jsonArray = new JsonArray();
		entities.forEach(entity -> jsonArray.add(convertToJsonElement(entity)));
		return jsonArray;
	}

	/**
	 * Converts an object to a {@link JsonElement}
	 * 
	 * @param entity
	 * @return
	 */
	public JsonElement convertToJsonElement(T entity);

	/**
	 * {@inheritDoc}
	 */
	public default JsonElement convertToJsonElement(final PaginatedData<T> paginationData) {
		final JsonObject jsonObject = new JsonObject();
		final JsonObject totalRecordsElement = new JsonObject();
		totalRecordsElement.addProperty("totalRecords", paginationData.getNumberOfRows());
		jsonObject.add("paging", totalRecordsElement);
		jsonObject.add("entries", convertToJsonElement(paginationData.getRows()));
		return jsonObject;
	}

}
