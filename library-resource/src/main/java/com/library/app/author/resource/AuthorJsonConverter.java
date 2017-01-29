/**
 * 
 */
package com.library.app.author.resource;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

/**
 * Class for converting a JSON string into a {@link Author}
 * Since this is an utility class we will need only one instance
 * so it will annotated with {@link ApplicationScoped}
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class AuthorJsonConverter implements EntityJsonConverter<Author> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Author convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Author author = new Author();
		author.setName(JsonReader.getStringOrNull(jsonObject, "name"));

		return author;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement convertToJsonElement(final Author author) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", author.getId());
		jsonObject.addProperty("name", author.getName());
		return jsonObject;
	}

}
