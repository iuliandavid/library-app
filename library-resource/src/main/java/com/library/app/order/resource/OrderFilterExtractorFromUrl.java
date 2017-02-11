/**
 * 
 */
package com.library.app.order.resource;

import javax.ws.rs.core.UriInfo;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.common.utils.DateUtils;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.filter.OrderFilter;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class OrderFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

	public OrderFilterExtractorFromUrl(final UriInfo uriInfo) {
		super(uriInfo);
	}

	public OrderFilter getFilter() {
		final OrderFilter orderFilter = new OrderFilter();
		orderFilter.setPaginationData(extractPaginationData());

		final String customerId = getUriInfo().getQueryParameters().getFirst("customerId");
		if (customerId != null) {
			orderFilter.setCustomerId(Long.valueOf(customerId));
		}

		final String startDate = getUriInfo().getQueryParameters().getFirst("startDate");
		if (startDate != null) {
			orderFilter.setStartDate(DateUtils.getAsDateTime(startDate));
		}

		final String endDate = getUriInfo().getQueryParameters().getFirst("endDate");
		if (endDate != null) {
			orderFilter.setEndDate(DateUtils.getAsDateTime(endDate));
		}
		final String status = getUriInfo().getQueryParameters().getFirst("status");
		if (status != null) {
			orderFilter.setStatus(OrderStatus.valueOf(status));
		}
		return orderFilter;
	}

	@Override
	protected String getDefaultSortField() {
		return "createdAt";
	}

}
