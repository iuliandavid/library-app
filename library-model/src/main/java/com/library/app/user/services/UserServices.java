package com.library.app.user.services;

import javax.ejb.Local;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;

/**
 * The contract for the {@link User} services
 * 
 * It will not be a remote call, that's why it'a annotated as {@link Local}
 * 
 * @User Iulian David david.iulian@gmail.com
 *
 */
@Local
public interface UserServices {

	User add(User user) throws FieldNotValidException, UserNotFoundException;

	void update(User user) throws FieldNotValidException, UserNotFoundException;

	User findById(long id);

	PaginatedData<User> findByFilter(UserFilter userFilter);

	void updatePassword(long id, String password);
}
