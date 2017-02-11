package com.library.app.commontests.order;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.ArgumentMatcher;

import com.library.app.order.model.Order;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class OrderArgumentMatcher implements ArgumentMatcher<Order> {
	private Order expected;

	public static Order orderEq(final Order expected) {
		return argThat(new OrderArgumentMatcher(expected));
	}

	public OrderArgumentMatcher(final Order expected) {
		this.expected = expected;
	}

	@Override
	public boolean matches(final Order actual) {

		assertThat(actual.getId(), is(equalTo(expected.getId())));
		assertThat(actual.getCustomer(), is(equalTo(expected.getCustomer())));

		assertThat(actual.getItems(), is(equalTo(expected.getItems())));

		assertThat(actual.getTotal(), is(equalTo(expected.getTotal())));
		assertThat(actual.getHistoryEntries(), is(equalTo(expected.getHistoryEntries())));
		assertThat(actual.getCurrentStatus(), is(equalTo(expected.getCurrentStatus())));

		return true;
	}

}
