/**
 * 
 */
package com.library.app.logaudit.resource;

import javax.ws.rs.core.UriInfo;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.common.utils.DateUtils;
import com.library.app.logaudit.model.filter.LogAuditFilter;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class LogAuditFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

	public LogAuditFilterExtractorFromUrl(final UriInfo uriInfo) {
		super(uriInfo);
	}

	public LogAuditFilter getFilter() {
		final LogAuditFilter logAuditFilter = new LogAuditFilter();
		logAuditFilter.setPaginationData(extractPaginationData());

		final String startDate = getUriInfo().getQueryParameters().getFirst("startDate");
		if (startDate != null) {
			logAuditFilter.setStartDate(DateUtils.getAsDateTime(startDate));
		}

		final String endDate = getUriInfo().getQueryParameters().getFirst("endDate");
		if (endDate != null) {
			logAuditFilter.setEndDate(DateUtils.getAsDateTime(endDate));
		}
		final String userId = getUriInfo().getQueryParameters().getFirst("userId");
		if (userId != null) {
			logAuditFilter.setUserId(Long.parseLong(userId));
		}
		return logAuditFilter;
	}

	@Override
	protected String getDefaultSortField() {
		return "-createdAt";
	}

}
