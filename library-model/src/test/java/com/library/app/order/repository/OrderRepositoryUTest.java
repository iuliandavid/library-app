/**
 * 
 */
package com.library.app.order.repository;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.book.BookForTestsRepository.normalizeDependencies;
import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static com.library.app.commontests.order.OrderForTestsRepository.*;
import static com.library.app.commontests.order.OrderForTestsRepository.normalizeDependencies;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.book.model.Book;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.common.utils.DateUtils;
import com.library.app.commontests.db.TestBaseRepository;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderItem;
import com.library.app.order.model.filter.OrderFilter;

/**
 * Unit testing for CategoryRepository
 * 
 * @author iulian
 *
 */
public class OrderRepositoryUTest extends TestBaseRepository {

	private OrderRepository orderRepository;

	@Before
	public void initTestCase() {

		initializeTestDB();
		orderRepository = new OrderRepository();
		orderRepository.em = em;

		loadBooks();
	}

	@After
	public void setDownTestCase() {
		closeEntityManager();
	}

	@Test
	public void addAnOrderAndFindIt() {
		final Long orderedId = dbCommandExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderDelivered(), em)).getId();
		});

		assertThat(orderedId, is(notNullValue()));
		final Order orderAdded = orderRepository.findById(orderedId);
		assertActualOrderWithExpectedOrder(orderAdded, normalizeDependencies(orderDelivered(), em));
	}

	@Test
	public void findOrderByIdNotFound() {
		final Order order = orderRepository.findById(999L);
		assertThat(order, is(nullValue()));
	}

	@Test
	public void updateOrder() {
		final Long orderedId = dbCommandExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderReserved(), em)).getId();
		});
		assertThat(orderedId, is(notNullValue()));
		final Order orderAdded = orderRepository.findById(orderedId);
		assertThat(orderAdded.getHistoryEntries().size(), is(equalTo(1)));
		assertThat(orderAdded.getCurrentStatus(), is(equalTo(OrderStatus.RESERVED)));

		orderAdded.addHistoryEntry(OrderStatus.DELIVERED);
		dbCommandExecutor.executeCommand(() -> {
			orderRepository.update(orderAdded);
			return null;
		});

		final Order orderAfterUpdate = orderRepository.findById(orderedId);
		assertThat(orderAfterUpdate.getHistoryEntries().size(), is(equalTo(2)));
		assertThat(orderAfterUpdate.getCurrentStatus(), is(equalTo(OrderStatus.DELIVERED)));

	}

	@Test
	public void existsById() {
		final Long orderedId = dbCommandExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderReserved(), em)).getId();
		});

		assertThat(orderedId, is(notNullValue()));

		assertThat(orderRepository.existsById(orderedId), is(equalTo(true)));
		assertThat(orderRepository.existsById(999L), is(equalTo(false)));
	}

	@Test
	public void findByFilterNoFilter() {
		loadForFindByFilter();
		final OrderFilter filter = new OrderFilter();

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(3)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()),
				is(equalTo("2017-02-05T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()),
				is(equalTo("2017-02-04T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(2).getCreatedAt()),
				is(equalTo("2017-02-03T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByStatus() {
		loadForFindByFilter();
		final OrderFilter filter = new OrderFilter();
		filter.setStatus(OrderStatus.RESERVED);

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(1)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()),
				is(equalTo("2017-02-03T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByCustomerIOrderingByCreationAscending() {
		loadForFindByFilter();
		final OrderFilter filter = new OrderFilter();
		filter.setCustomerId(normalizeDependencies(orderDelivered(), em).getCustomer().getId());
		filter.setPaginationData(new PaginationData(0, 10, "createdAt", OrderMode.ASCENDING));

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()),
				is(equalTo("2017-02-04T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()),
				is(equalTo("2017-02-05T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByDate() {
		loadForFindByFilter();
		final OrderFilter filter = new OrderFilter();
		filter.setStartDate(DateUtils.getAsDateTime("2017-02-04T10:10:21Z"));
		filter.setEndDate(DateUtils.getAsDateTime("2017-02-05T10:10:21Z"));

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()),
				is(equalTo("2017-02-05T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()),
				is(equalTo("2017-02-04T10:10:21Z")));
	}

	private void loadForFindByFilter() {
		final Order order1 = normalizeDependencies(orderReserved(), em);
		orderCreatedAt(order1, "2017-02-03T10:10:21Z");

		final Order order2 = normalizeDependencies(orderDelivered(), em);
		orderCreatedAt(order2, "2017-02-04T10:10:21Z");

		final Order order3 = normalizeDependencies(orderDelivered(), em);
		orderCreatedAt(order3, "2017-02-05T10:10:21Z");

		dbCommandExecutor.executeCommand(() -> {
			orderRepository.add(order1);
			orderRepository.add(order2);
			orderRepository.add(order3);
			return null;
		});
	}

	private void assertActualOrderWithExpectedOrder(final Order actualOrder, final Order expectedOrder) {
		assertThat(expectedOrder.getCreatedAt(), is(notNullValue()));
		assertThat(actualOrder.getCustomer(), is(equalTo(expectedOrder.getCustomer())));
		assertThat(actualOrder.getItems().size(), is(equalTo(expectedOrder.getItems().size())));
		for (final OrderItem actualItem : actualOrder.getItems()) {
			final OrderItem expectedItem = findItemByBook(expectedOrder, actualItem.getBook());
			assertThat(actualItem.getBook().getTitle(), is(equalTo(expectedItem.getBook().getTitle())));
			assertThat(actualItem.getPrice(), is(equalTo(expectedItem.getPrice())));
			assertThat(actualItem.getQuantity(), is(equalTo(expectedItem.getQuantity())));
		}

		assertThat(actualOrder.getTotal(), is(equalTo(expectedOrder.getTotal())));
		assertThat(actualOrder.getCurrentStatus(), is(equalTo(expectedOrder.getCurrentStatus())));
		assertThat(actualOrder.getHistoryEntries().size(), is(equalTo(expectedOrder.getHistoryEntries().size())));
		for (int i = 0; i < actualOrder.getHistoryEntries().size(); i++) {
			assertThat(actualOrder.getHistoryEntries(), is(equalTo(expectedOrder.getHistoryEntries())));
		}
	}

	private OrderItem findItemByBook(final Order order, final Book book) {
		final Optional<OrderItem> orderItem = order.getItems().stream()
				.filter(item -> item.getBook().getTitle().equals(book.getTitle())).findFirst();
		if (orderItem.isPresent()) {
			return orderItem.get();
		}
		return null;
	}

	private void loadBooks() {
		dbCommandExecutor.executeCommand(() -> {
			allUsers().forEach(em::persist);
			allCategories().forEach(em::persist);
			allAuthors().forEach(em::persist);
			allBooks().forEach((book) -> em.persist(normalizeDependencies(book, em)));
			return null;
		});
	}

}
