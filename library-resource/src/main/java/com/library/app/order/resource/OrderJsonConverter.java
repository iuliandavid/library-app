/**
 * 
 */
package com.library.app.order.resource;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.book.model.Book;
import com.library.app.book.resource.BookJsonConverter;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.common.json.EntityJsonConverter;
import com.library.app.common.json.JsonReader;
import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderHistoryEntry;
import com.library.app.order.model.OrderItem;
import com.library.app.user.model.Customer;
import com.library.app.user.resource.UserJsonConverter;

/**
 * Class for converting a JSON string into a {@link Order}
 * Since this is an utility class we will need only one instance
 * so it will annotated with {@link ApplicationScoped}
 * 
 * @author iulian
 *
 */
@ApplicationScoped
public class OrderJsonConverter implements EntityJsonConverter<Order> {

	@Inject
	AuthorJsonConverter authorJsonConverter;

	@Inject
	CategoryJsonConverter categoryJsonConverter;

	@Inject
	UserJsonConverter userJsonConverter;

	@Inject
	BookJsonConverter bookJsonConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Order convertFrom(final String json) {
		final JsonObject jsonObject = JsonReader.readAsJsonObject(json);

		final Order order = new Order();
		order.setId(JsonReader.getLongOrNull(jsonObject, "id"));
		final String createdAt = JsonReader.getStringOrNull(jsonObject, "createdAt");
		order.setCreatedAt(DateUtils.getAsDateTime(createdAt));
		order.setCustomer((Customer) userJsonConverter.convertFrom(JsonReader.getStringOrNull(jsonObject, "customer")));

		final JsonArray itemsArray = JsonReader.readAsJsonArray(JsonReader.getStringOrNull(jsonObject, "items"));
		if (itemsArray != null) {
			itemsArray.forEach(item -> {
				final JsonObject bookJson = item.getAsJsonObject();
				final Book book = new Book(JsonReader.getLongOrNull(bookJson, "id"));
				final Integer quantity = JsonReader.getIntegerOrNull(bookJson, "quantity");
				order.addItem(book, quantity);
			});
		}
		final JsonArray historyEntries = jsonObject.getAsJsonArray("historyEntries");
		if (historyEntries != null) {
			historyEntries.forEach(historyEntry -> {
				order.getHistoryEntries().add(convertJsonToHistoryEntry(historyEntry));
			});
		}
		return order;
	}

	/**
	 * @param historyEntry
	 * @return
	 */
	private OrderHistoryEntry convertJsonToHistoryEntry(final JsonElement historyEntry) {
		final JsonObject jsonHistoryEntry = historyEntry.getAsJsonObject();
		final String entryCreatedAt = JsonReader.getStringOrNull(jsonHistoryEntry, "createdAt");
		final OrderHistoryEntry entry = new OrderHistoryEntry();
		entry.setCreatedAt(DateUtils.getAsDateTime(entryCreatedAt));
		entry.setStatus(OrderStatus.valueOf(JsonReader.getStringOrNull(jsonHistoryEntry, "status")));
		return entry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonElement convertToJsonElement(final Order order) {
		return getOrderAsJsonElement(order, true);
	}

	private JsonElement getOrderAsJsonElement(final Order order, final boolean addItemsAndHistory) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", order.getId());
		jsonObject.addProperty("createdAt", DateUtils.formatDateTime(order.getCreatedAt()));
		jsonObject.add("customer", getCustomerAsJsonElement(order.getCustomer()));
		if (addItemsAndHistory) {
			final JsonArray items = new JsonArray();
			order.getItems().forEach(item -> items.add(getOrderItemAsJsonElement(item)));
			jsonObject.add("items", items);

			final JsonArray historyItems = new JsonArray();
			order.getHistoryEntries()
					.forEach(historyEntry -> historyItems.add(getHistoryEntryAsJsonElement(historyEntry)));
			jsonObject.add("historyEntries", historyItems);
		}
		return jsonObject;
	}

	private JsonElement getHistoryEntryAsJsonElement(final OrderHistoryEntry entry) {
		final JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("createdAt", DateUtils.formatDateTime(entry.getCreatedAt()));
		jsonItem.addProperty("status", entry.getStatus().name());
		return jsonItem;
	}

	/**
	 * @param item
	 * @return
	 */
	private JsonObject getOrderItemAsJsonElement(final OrderItem item) {
		final JsonObject jsonItem = new JsonObject();
		jsonItem.addProperty("bookId", item.getBook().getId());
		jsonItem.addProperty("quantity", item.getQuantity());
		return jsonItem;
	}

	private JsonElement getCustomerAsJsonElement(final Customer customer) {
		final JsonObject jsonCustomer = new JsonObject();
		jsonCustomer.addProperty("id", customer.getId());
		jsonCustomer.addProperty("name", customer.getName());
		return jsonCustomer;
	}

	@Override
	public JsonElement convertToJsonElement(final List<Order> orders) {
		final JsonArray jsonArray = new JsonArray();
		for (final Order order : orders) {
			jsonArray.add(getOrderAsJsonElement(order, false));
		}
		return jsonArray;
	}

}
