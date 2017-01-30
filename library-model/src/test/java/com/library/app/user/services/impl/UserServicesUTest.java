/**
 * 
 */
package com.library.app.user.services.impl;

import static com.library.app.commontests.user.UserArgumentMatcher.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.utils.PasswordUtils;
import com.library.app.user.exception.UserExistentException;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.repository.UserRepository;
import com.library.app.user.services.UserServices;

/**
 * Unit Tests for User Services
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class UserServicesUTest {

	private UserServices userServices;

	/** Using a default validator **/
	private Validator validator;

	/** The mockable concrete class **/
	private UserRepository userRepository;

	@Before
	public void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		userRepository = mock(UserRepository.class);
		userServices = new UserServicesImpl();
		((UserServicesImpl) userServices).validator = validator;
		((UserServicesImpl) userServices).userRepository = userRepository;
	}

	@Test
	public void addUserWithNullName() {
		final User user = johnDoe();
		user.setName(null);
		addUserWithInvalidField(user, "name");

	}

	@Test
	public void addUserWithShortName() {
		final User user = johnDoe();
		user.setName("Jo");
		addUserWithInvalidField(user, "name");

	}

	@Test
	public void addUserWithNullEmail() {
		final User user = johnDoe();
		user.setEmail(null);
		addUserWithInvalidField(user, "email");

	}

	@Test
	public void addUserWithInvalidEmail() {
		final User user = johnDoe();
		user.setEmail("invalidEmail");
		addUserWithInvalidField(user, "email");

	}

	@Test
	public void addUserWithNullPassword() {
		final User user = johnDoe();
		user.setPassword(null);
		addUserWithInvalidField(user, "password");

	}

	@Test(expected = UserExistentException.class)
	public void addExistentUser() {
		when(userRepository.alreadyExists(johnDoe())).thenThrow(new UserExistentException());

		userServices.add(johnDoe());

	}

	@Test
	public void addValidUser() {
		when(userRepository.alreadyExists(johnDoe())).thenReturn(false);

		when(userRepository.add(userEq(userWithEncryptedPassword(johnDoe()))))
				.thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

		final User user = userServices.add(johnDoe());
		assertThat(user, is(notNullValue()));
		assertThat(user.getId(), is(equalTo(1l)));
	}

	@Test(expected = UserNotFoundException.class)
	public void findUserByIdNotFound() {
		when(userRepository.findById(1L)).thenReturn(null);
		userServices.findById(1l);
	}

	@Test
	public void findUserById() {
		when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1l));
		final User user = userServices.findById(1L);
		assertThat(user, is(notNullValue()));
		assertThat(user.getName(), is(equalTo(johnDoe().getName())));
	}

	@Test
	public void updateUserWithNullName() {
		when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1l));
		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		user.setName(null);
		try {
			userServices.update(user);
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}

	}

	@Test(expected = UserExistentException.class)
	public void updateUserExistent() {
		when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1l));

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		when(userRepository.alreadyExists(user)).thenReturn(true);
		userServices.update(user);

	}

	@Test(expected = UserNotFoundException.class)
	public void updateUserNotFound() {
		when(userRepository.findById(1L)).thenReturn(null);

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		userServices.update(user);

	}

	public void updateValidUser() {

		when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		user.setPassword(null);
		userServices.update(user);

		final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		verify(userRepository).update(userEq(expectedUser));

	}

	public void updatePassword() {

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);

		when(userRepository.findById(1L)).thenReturn(user);

		userServices.updatePassword(1l, "654654");

		final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		expectedUser.setPassword(PasswordUtils.encryptPassword("6546541"));
		verify(userRepository).update(userEq(expectedUser));

	}

	private void addUserWithInvalidField(final User user, final String expectedInvalidFiledName) {
		try {
			userServices.add(user);
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo(expectedInvalidFiledName)));

		}

	}

}
