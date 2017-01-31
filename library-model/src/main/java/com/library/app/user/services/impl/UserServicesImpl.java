/**
 * 
 */
package com.library.app.user.services.impl;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.PasswordUtils;
import com.library.app.common.utils.ValidationUtils;
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
		validateUser(user);
		user.setPassword(PasswordUtils.encryptPassword(user.getPassword()));
		return userRepository.add(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#update(com.library.app.user.model.User)
	 */
	@Override
	public void update(final User user) throws FieldNotValidException, UserNotFoundException {

		final User existentUser = findById(user.getId());
		user.setPassword(existentUser.getPassword());

		validateUser(user);

		userRepository.update(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#findById(long)
	 */
	@Override
	public User findById(final long id) {
		final User user = userRepository.findById(id);
		if (user == null) {
			throw new UserNotFoundException();
		}
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.user.services.UserServices#findByFilter(com.library.app.user.model.filter.UserFilter)
	 */
	@Override
	public PaginatedData<User> findByFilter(final UserFilter userFilter) {
		return userRepository.findByFilter(userFilter);
	}

	private void validateUser(final User user) {
		if (userRepository.alreadyExists(user)) {
			throw new UserExistentException();
		}
		ValidationUtils.validateEntityFields(validator, user);
	}

	@Override
	public void updatePassword(final long id, final String password) {
		final User user = findById(id);
		user.setPassword(PasswordUtils.encryptPassword(password));
		validateUser(user);

		userRepository.update(user);
	}

	@Override
	public User findByEmail(final String email) {
		final User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException();
		}
		return user;

	}

	@Override
	public User findByEmailAndPassword(final String email, final String password) {
		final User user = findByEmail(email);
		if (!user.getPassword().equals(PasswordUtils.encryptPassword(password))) {
			throw new UserNotFoundException();
		}
		return user;
	}
}
