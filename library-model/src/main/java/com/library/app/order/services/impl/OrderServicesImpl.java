/**
 * 
 */
package com.library.app.order.services.impl;

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Validator;
import javax.ws.rs.core.SecurityContext;

import com.library.app.book.model.Book;
import com.library.app.book.services.BookServices;
import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.FilterValidationException;
import com.library.app.common.utils.ValidationUtils;
import com.library.app.logaudit.interceptor.Auditable;
import com.library.app.logaudit.interceptor.LogAuditInterceptor;
import com.library.app.logaudit.model.LogAudit.Action;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.exception.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderItem;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.user.model.Customer;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;

/**
 * @author iulian
 *
 */
@Stateless
@Interceptors(LogAuditInterceptor.class)
public class OrderServicesImpl implements OrderServices {

	@Inject
	OrderRepository orderRepository;

	@Inject
	Validator validator;

	@Inject
	BookServices bookServices;

	@Inject
	UserServices userServices;

	@Resource
	SessionContext sessionContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#add(com.library.app.order.model.Order)
	 */
	@Override
	@Auditable(action = Action.ADD)
	public Order add(final Order order) {
		checkCustomerAndSetItOnOrder(order);
		checkBooksForItemsAndSetThem(order);

		order.setInitialStatus();
		order.calculateTotal();

		ValidationUtils.validateEntityFields(validator, order);
		return orderRepository.add(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#findById(java.lang.Long)
	 */
	@Override
	public Order findById(final Long id) throws OrderNotFoundException {
		final Order order = orderRepository.findById(id);
		if (order == null) {
			throw new OrderNotFoundException();
		}
		return order;
	}

	@Override
	@Auditable(action = Action.UPDATE)
	public void updateStatus(final Long id, final OrderStatus newStatus) {

		final Order order = orderRepository.findById(id);
		if (order == null) {
			throw new OrderNotFoundException();
		}
		if (newStatus == OrderStatus.DELIVERED) {
			if (!sessionContext.isCallerInRole(Roles.EMPLOYEE.name())) {
				throw new UserNotAuthorizedException();
			}
		}

		if (newStatus == OrderStatus.CANCELLED) {
			if (sessionContext.isCallerInRole(Roles.CUSTOMER.name())) {

				if (!sessionContext.getCallerPrincipal().getName().equals(order.getCustomer().getEmail())) {
					throw new UserNotAuthorizedException();
				}
			}
		}
		try {
			order.addHistoryEntry(newStatus);
		} catch (final IllegalArgumentException e) {
			throw new OrderStatusCannotBeChangedException(e.getMessage());
		}

		order.setCurrentStatus(newStatus);

		orderRepository.update(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#findByFilter(com.library.app.order.model.filter.OrderFilter)
	 */
	@Override
	public PaginatedData<Order> findByFilter(final OrderFilter orderFilter) throws FilterValidationException {
		return orderRepository.findByFilter(orderFilter);
	}

	/**
	 * Based on the fact that {@link SecurityContext} returns
	 * as {@link Principal} name an unique identifier of the logged user,
	 * that unique identifier will be the email in our case
	 * 
	 * @param id
	 */
	private void checkCustomerAndSetItOnOrder(final Order order) {
		final String email = sessionContext.getCallerPrincipal().getName();
		final User loggerUser = userServices.findByEmail(email);
		order.setCustomer((Customer) loggerUser);

	}

	private void checkBooksForItemsAndSetThem(final Order order) {
		for (final OrderItem item : order.getItems()) {
			if (item.getBook() != null) {
				final Book book = bookServices.findById(item.getBook().getId());
				item.setBook(book);
			}
		}
	}

}
