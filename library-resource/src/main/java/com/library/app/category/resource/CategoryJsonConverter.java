/**
 * 
 */
package com.library.app.category.resource;

import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;

/**
 * Class for converting a JSON string into a Category
 * 
 * @author iulian
 *
 */
public class CategoryJsonConverter {

	public Category convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Category category = new Category();
		category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return category;
	}
}
