/**
 * 
 */
package com.library.app.common.model.filter;

/**
 * @author iulian
 *
 */
public class GenericFilter {

	private PaginationData paginationData;

	public GenericFilter() {
		// TODO Auto-generated constructor stub
	}

	public GenericFilter(final PaginationData paginationData) {
		this.paginationData = paginationData;
	}

	public void setPaginationData(final PaginationData paginationData) {
		this.paginationData = paginationData;
	}

	public PaginationData getPaginationData() {
		return paginationData;
	}

	public boolean hasPaginationData() {
		return getPaginationData() != null;
	}

	public boolean hasOrderField() {
		return hasPaginationData() && getPaginationData().getOrderField() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GenericFilter [paginationData=" + paginationData + "]";
	}

}
