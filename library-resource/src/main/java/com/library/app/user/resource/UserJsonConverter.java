/**
 * 
 */
package com.library.app.user.resource;

import javax.enterprise.context.ApplicationScoped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;
import com.library.app.common.utils.DateUtils;
import com.library.app.user.model.Customer;
import com.library.app.user.model.Employee;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;

/**
 * Class for converting a JSON string into a {@link User}
 * Since this is an utility class we will need only one instance
 * so it will annotated with {@link ApplicationScoped}
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class UserJsonConverter implements EntityJsonConverter<User> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public User convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final User author = getUserInstance(jsonObject);
		author.setName(JsonReader.getStringOrNull(jsonObject, "name"));
		author.setEmail(JsonReader.getStringOrNull(jsonObject, "email"));
		author.setPassword(JsonReader.getStringOrNull(jsonObject, "password"));
		return author;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement convertToJsonElement(final User user) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getId());
		jsonObject.addProperty("name", user.getName());
		jsonObject.addProperty("email", user.getEmail());
		jsonObject.addProperty("type", user.getUserType().toString());

		final JsonArray roles = new JsonArray();
		user.getRoles().forEach(role -> roles.add(new JsonPrimitive(role.toString())));
		jsonObject.add("roles", roles);
		jsonObject.addProperty("createdAt", DateUtils.formatDateTime(user.getCreatedAt()));

		return jsonObject;
	}

	private User getUserInstance(final JsonObject userJson) {
		final UserType userType = UserType.valueOf(JsonReader.getStringOrNull(userJson, "type"));
		if (UserType.EMPLOYEE == userType) {
			return new Employee();
		}
		return new Customer();
	}
}
