package com.library.app.order.model.filter;

import java.util.Date;

import com.library.app.common.model.filter.GenericFilter;
import com.library.app.order.model.Order.OrderStatus;

/**
 * @author iulian
 *
 */
public class OrderFilter extends GenericFilter {

	private Date startDate;
	private Date endDate;
	private Long customerId;
	private OrderStatus status;

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
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId
	 *            the customerId to set
	 */
	public void setCustomerId(final Long customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the status
	 */
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final OrderStatus status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrderFilter [startDate=" + startDate + ", endDate=" + endDate + ", customerId=" + customerId
				+ ", status=" + status + "]";
	}

}
