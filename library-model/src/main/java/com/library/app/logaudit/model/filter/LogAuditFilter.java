/**
 * 
 */
package com.library.app.logaudit.model.filter;

import java.util.Date;

import com.library.app.common.model.filter.GenericFilter;

/**
 * @author iulian
 *
 */
public class LogAuditFilter extends GenericFilter {

	private Date startDate;
	private Date endDate;
	private Long userId;

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogAuditFilter [startDate=" + startDate + ", endDate=" + endDate + ", userId=" + userId + "]";
	}
}
