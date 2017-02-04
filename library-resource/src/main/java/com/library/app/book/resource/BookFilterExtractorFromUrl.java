/**
 * 
 */
package com.library.app.book.resource;

import javax.ws.rs.core.UriInfo;

import com.library.app.book.model.filter.BookFilter;
import com.library.app.common.resource.AbstractFilterExtractorFromUrl;

/**
 * @author iulian
 *
 */
public class BookFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

	public BookFilterExtractorFromUrl(final UriInfo uriInfo) {
		super(uriInfo);
	}

	public BookFilter getFilter() {
		final BookFilter bookFilter = new BookFilter();
		bookFilter.setPaginationData(extractPaginationData());
		bookFilter.setTitle(getUriInfo().getQueryParameters().getFirst("title"));

		final String categoryId = getUriInfo().getQueryParameters().getFirst("categoryId");
		if (categoryId != null) {
			bookFilter.setCategoryId(Long.valueOf(categoryId));
		}
		return bookFilter;
	}

	@Override
	protected String getDefaultSortField() {
		return "title";
	}

}
