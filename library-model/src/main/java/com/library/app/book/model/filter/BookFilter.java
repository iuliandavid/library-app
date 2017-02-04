/**
 * 
 */
package com.library.app.book.model.filter;

import com.library.app.common.model.filter.GenericFilter;

/**
 * @author iulian
 *
 */
public class BookFilter extends GenericFilter {

	private String title;

	private Long categoryId;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the categoryId
	 */
	public Long getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(final Long categoryId) {
		this.categoryId = categoryId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BookFilter [title=" + title + ", categoryId=" + categoryId + "]";
	}

}
