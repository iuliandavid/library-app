/**
 * 
 */
package com.library.app.order.services.impl;

import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.order.OrderArgumentMatcher.*;
import static com.library.app.commontests.order.OrderForTestsRepository.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Arrays;

import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.author.services.AuthorServices;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.services.BookServices;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.PaginatedData;
import com.library.app.commontests.category.CategoryForTestsRepository;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;

/**
 * @author iulian
 *
 */
@Stateful
public class OrderServicesUTest {

	private static Validator validator;
	private OrderServices orderServices;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CategoryServices categoryServices;

	@Mock
	private BookServices bookServices;

	@Mock
	private AuthorServices authorServices;

	@Mock
	private UserServices userServices;

	@Mock
	private CategoryForTestsRepository categoryForTestsRepository;

	@Mock
	private SessionContext sessionContext;

	private static final String LOGGED_EMAIL = "anyemail@domain.com";

	@BeforeClass
	public static void initTestClass() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);

		orderServices = new OrderServicesImpl();

		((OrderServicesImpl) orderServices).validator = validator;
		((OrderServicesImpl) orderServices).orderRepository = orderRepository;
		((OrderServicesImpl) orderServices).bookServices = bookServices;
		((OrderServicesImpl) orderServices).userServices = userServices;
		((OrderServicesImpl) orderServices).sessionContext = sessionContext;

		setUpLoggedEmail(LOGGED_EMAIL, Roles.ADMINISTRATOR);
	}

	private void setUpLoggedEmail(final String email, final Roles userRole) {
		reset(sessionContext);

		final Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(email);

		when(sessionContext.getCallerPrincipal()).thenReturn(principal);
		when(sessionContext.isCallerInRole(Roles.EMPLOYEE.name())).thenReturn(userRole == Roles.EMPLOYEE);
		when(sessionContext.isCallerInRole(Roles.CUSTOMER.name())).thenReturn(userRole == Roles.CUSTOMER);
	}

	@Test(expected = UserNotFoundException.class)
	public void addOrderWithInexistentCustomer() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenThrow(new UserNotFoundException());

		orderServices.add(orderReserved());
	}

	@Test(expected = BookNotFoundException.class)
	public void addOrderWithInexistentBook() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById((Long) any())).thenThrow(new BookNotFoundException());

		orderServices.add(orderReserved());
	}

	@Test
	public void addOrderWithNullQuantityInOneItem() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById(1l)).thenReturn(bookWithId(designPatterns(), 1L));
		when(bookServices.findById(2l)).thenReturn(bookWithId(refactoring(), 2L));

		final Order order = orderReserved();
		order.getItems().iterator().next().setQuantity(null);

		addOrderWithInvalidField(order, "items[].quantity");
	}

	@Test
	public void addOrderWithNullBookInOneItem() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());

		final Order order = orderReserved();
		order.getItems().iterator().next().setBook(null);

		addOrderWithInvalidField(order, "items[].book");
	}

	@Test
	public void addValidOrder() {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById(1l)).thenReturn(bookWithId(designPatterns(), 1L));
		when(bookServices.findById(2l)).thenReturn(bookWithId(refactoring(), 2L));
		when(orderRepository.add(orderEq(orderReserved()))).thenReturn(orderWithId(orderReserved(), 1l));

		final Order order = new Order();
		order.setItems(orderReserved().getItems());

		final Long id = orderServices.add(order).getId();
		assertThat(id, is(notNullValue()));
	}

	@Test(expected = OrderNotFoundException.class)
	public void findOrderByIdNotFound() {
		when(orderRepository.findById(1L)).thenReturn(null);
		orderServices.findById(1l);
	}

	@Test
	public void findOrderById() {
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		final Order order = orderServices.findById(1l);
		assertThat(order, is(notNullValue()));
	}

	@Test(expected = OrderNotFoundException.class)
	public void updateStatusOrderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(null);
		orderServices.updateStatus(1l, OrderStatus.DELIVERED);
	}

	@Test(expected = OrderStatusCannotBeChangedException.class)
	public void updateStatusInvalidStatus() {
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		orderServices.updateStatus(1l, OrderStatus.RESERVED);
	}

	@Test(expected = UserNotAuthorizedException.class)
	public void updateStatusDeliveredAsNotEmployee() {
		setUpLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		orderServices.updateStatus(1l, OrderStatus.DELIVERED);
	}

	@Test
	public void updateStatusDeliveredAsEmployee() {
		setUpLoggedEmail(LOGGED_EMAIL, Roles.EMPLOYEE);
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		orderServices.updateStatus(1l, OrderStatus.DELIVERED);
	}

	@Test(expected = UserNotAuthorizedException.class)
	public void updateStatusCancelledAsCustomerNotTheOrderCustomer() {
		setUpLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		orderServices.updateStatus(1l, OrderStatus.CANCELLED);
	}

	@Test
	public void updateStatusCancelledAsCustomerTheOrderCustomer() {
		setUpLoggedEmail(orderReserved().getCustomer().getEmail(), Roles.CUSTOMER);
		when(orderRepository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1l));
		orderServices.updateStatus(1l, OrderStatus.CANCELLED);

		final Order expectedOrder = orderWithId(orderReserved(), 1l);
		expectedOrder.addHistoryEntry(OrderStatus.CANCELLED);
		verify(orderRepository).update(orderEq(expectedOrder));
	}

	@Test
	public void findByFilter() {
		final PaginatedData<Order> orders = new PaginatedData<>(2, Arrays.asList(orderReserved(), orderDelivered()));
		when(orderRepository.findByFilter((OrderFilter) any())).thenReturn(orders);
		final PaginatedData<Order> ordersReturned = orderServices.findByFilter(new OrderFilter());
		assertThat(ordersReturned.getNumberOfRows(), is(equalTo(2)));
		assertThat(ordersReturned.getRows().size(), is(equalTo(2)));
	}

	private void addOrderWithInvalidField(final Order order, final String invalidField) {
		try {
			orderServices.add(order);
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo(invalidField)));
		}
	}
}
