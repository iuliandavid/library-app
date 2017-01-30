/**
 * 
 */
package com.library.app.commontests.user;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;

import com.library.app.user.model.Customer;
import com.library.app.user.model.Employee;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;

/**
 * Helper class for users testing
 * Creates {@link User} objects that will be used in tests
 * 
 * @author iulian
 *
 */
@Ignore
public class UserForTestsRepository {

	private UserForTestsRepository() {
	}

	public static User johnDoe() {
		final User user = new Customer();
		user.setName("John Doe");
		user.setEmail("john@domain.com");
		user.setPassword("123456");
		return user;
	}

	public static User mary() {
		final User user = new Customer();
		user.setName("Mary");
		user.setEmail("mary@domain.com");
		user.setPassword("987789");
		return user;
	}

	public static User admin() {
		final User user = new Employee();
		user.setName("Admin");
		user.setEmail("admin@domain.com");
		user.setPassword("654321");
		user.setRoles(Arrays.asList(Roles.EMPLOYEE, Roles.ADMINISTRATOR));
		return user;
	}

	public static List<User> allUsers() {
		return Arrays.asList(admin(), johnDoe(), mary());
	}
}
