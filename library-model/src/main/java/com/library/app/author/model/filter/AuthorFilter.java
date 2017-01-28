/**
 * 
 */
package com.library.app.author.model.filter;

import com.library.app.common.model.filter.GenericFilter;

/**
 * @author iulian
 *
 */
public class AuthorFilter extends GenericFilter {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthorFilter [name=" + name + ", toString()=" + super.toString() + "]";
	}

}
