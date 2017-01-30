/**
 * 
 */
package com.library.app.user.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author iulian
 *
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User {

	private static final long serialVersionUID = -6100894877953675646L;

	public Employee() {
		setUserType(UserType.EMPLOYEE);
	}

	@Override
	protected List<Roles> getDefaultRoles() {
		return Arrays.asList(Roles.EMPLOYEE);
	}

}
