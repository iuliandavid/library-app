/**
 * 
 */
package com.library.app.order.services;

import javax.ejb.Local;

import com.library.app.book.exception.BookNotFoundException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.FilterValidationException;
import com.library.app.order.exception.OrderNotFoundException;
import com.library.app.order.model.Order;
import com.library.app.order.model.filter.OrderFilter;
import com.library.app.user.exception.UserNotFoundException;

/**
 * The contract for the {@link Order} services
 * 
 * It will not be a remote call, that's why it'a annotated as {@link Local}
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Local
public interface OrderServices {

	Order add(Order order) throws FieldNotValidException, UserNotFoundException, BookNotFoundException;

	void update(Order order) throws FieldNotValidException,
			OrderNotFoundException;

	Order findById(Long id) throws BookNotFoundException;

	PaginatedData<Order> findByFilter(OrderFilter orderFilter) throws FilterValidationException;
}
