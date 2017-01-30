/**
 * 
 */
package com.library.app.user.services.impl;

import static com.library.app.common.ValidationUtils.*;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.library.app.common.ValidationUtils;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.user.exception.UserExistentException;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;
import com.library.app.user.repository.UserRepository;
import com.library.app.user.services.UserServices;

/**
 * Since there is a pool of {@link Stateless} ejbs is more useful and scalable to use the {@link Stateless} instead of
 * {@link Stateful} EJB
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
public class UserServicesImpl implements UserServices {

	/** Will be used by container's own Validator implementation **/
	@Inject
	public Validator validator;

	@Inject
	public UserRepository userRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#add(com.library.app.user.model.User)
	 */
	@Override
	public User add(final User user) throws FieldNotValidException, UserNotFoundException {
		ValidationUtils.validateEntityFields(validator, user);
		return userRepository.add(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#update(com.library.app.user.model.User)
	 */
	@Override
	public void update(final User user) throws FieldNotValidException, UserNotFoundException {
		validateEntityFields(validator, user);

		if (!userRepository.existsById(user.getId())) {
			throw new UserNotFoundException();
		}

		if (userRepository.alreadyExists(user)) {
			throw new UserExistentException();
		}
		userRepository.update(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#findById(long)
	 */
	@Override
	public User findById(final long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#findByFilter(com.library.app.user.model.filter.UserFilter)
	 */
	@Override
	public PaginatedData<User> findByFilter(final UserFilter userFilter) {
		// TODO Auto-generated method stub
		return null;
	}

}
