/**
 * 
 */
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
	private Order expectedOder;

	/**
	 * @param expectedOrder
	 */
	public OrderArgumentMatcher(final Order expectedOrder) {
		this.expectedOder = expectedOrder;
	}

	public static Order orderEq(final Order expectedOrder) {
		return argThat(new OrderArgumentMatcher(expectedOrder));
	}

	@Override
	public boolean matches(final Order actualOrder) {
		assertThat(actualOrder.getId(), is(equalTo(expectedOder.getId())));
		assertThat(actualOrder.getCustomer(), is(equalTo(expectedOder.getCustomer())));

		assertThat(actualOrder.getItems(), is(equalTo(expectedOder.getItems())));

		assertThat(actualOrder.getTotal(), is(equalTo(expectedOder.getTotal())));
		assertThat(actualOrder.getHistoryEntries(), is(equalTo(expectedOder.getHistoryEntries())));
		assertThat(actualOrder.getCurrentStatus(), is(equalTo(expectedOder.getCurrentStatus())));
		return true;
	}

}
