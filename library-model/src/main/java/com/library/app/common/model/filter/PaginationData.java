/**
 * 
 */
package com.library.app.common.model.filter;

import com.library.app.category.services.CategoryServices;

/**
 * Class for paginating the results on server side
 * 
 * Contains page number, ordering and the orderField
 * 
 * It will be used in findAll methods
 * 
 * @see {@link CategoryServices#findAll()}
 * 
 * @author iulian
 *
 */
public class PaginationData {

	private final int firstResult;
	private final int maxResults;
	private final String orderField;
	private final OrderMode orderMode;

	public enum OrderMode {
		ASCENDING, DESCENDING
	}

	public PaginationData(final int firstResult, final int maxResults, final String orderField,
			final OrderMode orderMode) {
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.orderField = orderField;
		this.orderMode = orderMode;
	}

	/**
	 * @return the firstResult
	 */
	public int getFirstResult() {
		return firstResult;
	}

	/**
	 * @return the maxResults
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * @return the orderField
	 */
	public String getOrderField() {
		return orderField;
	}

	/**
	 * @return the orderMode
	 */
	public OrderMode getOrderMode() {
		return orderMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaginationData [firstResult=" + firstResult + ", maxResults=" + maxResults + ", orderField="
				+ orderField + ", orderMode=" + orderMode + "]";
	}

	public boolean isAscending() {
		return (orderMode == OrderMode.ASCENDING);
	}

}
