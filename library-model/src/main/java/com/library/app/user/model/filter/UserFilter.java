/**
 * 
 */
package com.library.app.user.model.filter;

import com.library.app.common.model.filter.GenericFilter;
import com.library.app.user.model.User.UserType;

/**
 * @author iulian
 *
 */
public class UserFilter extends GenericFilter {

	private String name;
	private UserType userType;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the userType
	 */
	public UserType getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(final UserType userType) {
		this.userType = userType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserFilter [name=" + name + ", userType=" + userType + ", toString()=" + super.toString() + "]";
	}

}
