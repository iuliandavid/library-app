/**
 * 
 */
package com.library.app.book.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.model.Author;
import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;

/**
 * Class for converting a JSON string into a {@link Book}
 * Since this is an utility class we will need only one instance
 * so it will annotated with {@link ApplicationScoped}
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class BookJsonConverter implements EntityJsonConverter<Book> {

	@Inject
	AuthorJsonConverter authorJsonConverter;

	@Inject
	CategoryJsonConverter categoryJsonConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Book convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Book book = new Book();
		book.setTitle(JsonReader.getStringOrNull(jsonObject, "title"));
		book.setDescription(JsonReader.getStringOrNull(jsonObject, "description"));
		book.setPrice(JsonReader.getDoubleOrNull(jsonObject, "price"));

		final Category category = new Category();
		category.setId(JsonReader.getLongOrNull(jsonObject, "categoryId"));
		book.setCategory(category);

		final JsonArray authorsArray = jsonObject.getAsJsonArray("authorsIds");
		if (authorsArray != null) {
			authorsArray.forEach(authorJson -> {
				final Author author = new Author();
				author.setId(authorJson.getAsLong());
				book.addAuthor(author);
			});
		}
		return book;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement convertToJsonElement(final Book book) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", book.getId());
		jsonObject.addProperty("title", book.getTitle());
		jsonObject.addProperty("description", book.getDescription());
		jsonObject.addProperty("price", book.getPrice());
		jsonObject.add("category", categoryJsonConverter.convertToJsonElement(book.getCategory()));
		jsonObject.add("authors", authorJsonConverter.convertToJsonElement(book.getAuthors()));
		return jsonObject;
	}

}
