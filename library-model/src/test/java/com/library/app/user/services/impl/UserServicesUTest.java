/**
 * 
 */
package com.library.app.user.services.impl;

import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.common.exception.FieldNotValidException;
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

	private void addUserWithInvalidField(final User user, final String expectedInvalidFiledName) {
		try {
			userServices.add(user);
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo(expectedInvalidFiledName)));

		}

	}

}
