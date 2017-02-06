/**
 * 
 */
package com.library.app.order.services.impl;

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;
import javax.ws.rs.core.SecurityContext;

import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.services.BookServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.FilterValidationException;
import com.library.app.common.utils.ValidationUtils;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.model.Order;
import com.library.app.order.model.OrderItem;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.user.model.Customer;
import com.library.app.user.model.User;
import com.library.app.user.services.UserServices;

/**
 * @author iulian
 *
 */
@Stateless
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
	public Order add(final Order order) {
		checkCustomerAndSetItOnOrder(order);
		checkBooksForItemsAndSetThem(order);

		ValidationUtils.validateEntityFields(validator, order);
		return orderRepository.add(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#update(com.library.app.order.model.Order)
	 */
	@Override
	public void update(final Order order) throws FieldNotValidException, OrderNotFoundException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#findById(java.lang.Long)
	 */
	@Override
	public Order findById(final Long id) throws BookNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.order.services.OrderServices#findByFilter(com.library.app.order.model.filter.OrderFilter)
	 */
	@Override
	public PaginatedData<Order> findByFilter(final OrderFilter orderFilter) throws FilterValidationException {
		// TODO Auto-generated method stub
		return null;
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
