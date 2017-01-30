/**
 * 
 */
package com.library.app.commontests.user;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.ArgumentMatcher;

import com.library.app.user.model.User;

/**
 * @author Iulian David iulian.david@ropardo.ro
 *
 */
public class UserArgumentMatcher implements ArgumentMatcher<User> {
	private User expectedUser;

	/**
	 * @param expectedUser
	 */
	public UserArgumentMatcher(final User expectedUser) {
		this.expectedUser = expectedUser;
	}

	public static User userEq(final User expectedUser) {
		return argThat(new UserArgumentMatcher(expectedUser));
	}

	@Override
	public boolean matches(final User actualUser) {
		assertThat(actualUser.getId(), is(equalTo(expectedUser.getId())));
		assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
		assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
		assertThat(actualUser.getPassword(), is(equalTo(expectedUser.getPassword())));
		return true;
	}
}
