/**
 * 
 */
package com.library.app.category.resource;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

/**
 * Class for converting a JSON string into a {@link Category}
 * Since this is an utility class we will need only one instance
 * so it will annotated with {@link ApplicationScoped}
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class CategoryJsonConverter implements EntityJsonConverter<Category> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Category convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Category category = new Category();
		category.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return category;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement convertToJsonElement(final Category category) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", category.getId());
		jsonObject.addProperty("name", category.getName());
		return jsonObject;
	}

}
