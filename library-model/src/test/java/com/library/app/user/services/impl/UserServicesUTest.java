/**
 * 
 */
package com.library.app.user.services.impl;

import static com.library.app.commontests.user.UserArgumentMatcher.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.PasswordUtils;
import com.library.app.user.exception.UserExistentException;
import com.library.app.user.exception.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;
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
		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		when(userRepository.findById(1L)).thenReturn(null);

		userServices.update(user);

	}

	@Test
	public void updateValidUser() {

		when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);
		user.setPassword(null);
		userServices.update(user);

		final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		verify(userRepository).update(userEq(expectedUser));

	}

	@Test
	public void updatePassword() {

		final User user = userWithIdAndCreatedAt(johnDoe(), 1l);

		when(userRepository.findById(1L)).thenReturn(user);

		userServices.updatePassword(1l, "654654");

		final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		expectedUser.setPassword(PasswordUtils.encryptPassword("654654"));
		verify(userRepository).update(userEq(expectedUser));

	}

	@Test(expected = UserNotFoundException.class)
	public void updatePasswordForUserNotFound() {
		when(userRepository.findById(1L)).thenReturn(null);

		userServices.updatePassword(1l, "654654");

		fail("Test should not have gone this far");
	}

	@Test(expected = UserNotFoundException.class)
	public void findUserByEmailNotFound() {
		when(userRepository.findByEmail(johnDoe().getEmail())).thenReturn(null);
		userServices.findUserByEmail(johnDoe().getEmail());
	}

	@Test
	public void findUserByEmail() {
		when(userRepository.findByEmail(johnDoe().getEmail())).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));
		final User user = userServices.findUserByEmail(johnDoe().getEmail());
		assertThat(user, is(notNullValue()));
		assertThat(user.getName(), is(equalTo(johnDoe().getName())));
	}

	@Test(expected = UserNotFoundException.class)
	public void findUserByEmailAndPasswordNotFound() {
		when(userRepository.findById(1L)).thenReturn(null);
		final User user = johnDoe();
		userServices.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
	}

	@Test(expected = UserNotFoundException.class)
	public void findUserByEmailAndPasswordWithInvalidPassword() {
		final User user = johnDoe();
		user.setPassword("1111");

		User returnedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		returnedUser = userWithEncryptedPassword(returnedUser);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(returnedUser);

		userServices.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
	}

	@Test
	public void findUserByEmailAndPassword() {
		User user = johnDoe();

		User returnedUser = userWithIdAndCreatedAt(johnDoe(), 1l);
		returnedUser = userWithEncryptedPassword(returnedUser);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(returnedUser);

		user = userServices.findUserByEmailAndPassword(user.getEmail(), user.getPassword());
		assertThat(user, is(notNullValue()));
		assertThat(user.getName(), is(equalTo(johnDoe().getName())));
	}

	@Test
	public void findUserByFilter() {
		final PaginatedData<User> users = new PaginatedData<>(1, Arrays.asList(userWithIdAndCreatedAt(johnDoe(), 1L)));
		when(userRepository.findByFilter((UserFilter) any())).thenReturn(users);

		final PaginatedData<User> usersReturned = userServices.findByFilter(new UserFilter());
		assertThat(usersReturned.getNumberOfRows(), is(equalTo(1)));
		assertThat(usersReturned.getRow(0).getName(), is(equalTo(johnDoe().getName())));
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
