/**
 * 
 */
package com.library.app.author.resource;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;

/**
 * @author iulian
 *
 */
public class AuthorFilterExtractorFromUrlTest {

	@Mock
	private UriInfo uriInfo;

	@Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void onlyDefaultValues() {
		setUpUriInfo(null, null, null, null);

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(0, 10, "name", OrderMode.ASCENDING);

		assertActualPaginatedDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(nullValue()));

	}

	@Test
	public void withPaginationAndNameAndSortAscending() {
		setUpUriInfo("2", "5", "Robert", "id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.ASCENDING);

		assertActualPaginatedDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortAscendingWithPrefix() {
		setUpUriInfo("2", "5", "Robert", "+id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.ASCENDING);

		assertActualPaginatedDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortDescending() {
		setUpUriInfo("2", "5", "Robert", "-id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.DESCENDING);

		assertActualPaginatedDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	/**
	 * @param actual
	 * @param expected
	 */
	private void assertActualPaginatedDataWithExpected(final PaginationData actual, final PaginationData expected) {
		assertThat(actual.getFirstResult(), is(equalTo(expected.getFirstResult())));
		assertThat(actual.getMaxResults(), is(equalTo(expected.getMaxResults())));
		assertThat(actual.getOrderField(), is(equalTo(expected.getOrderField())));
		assertThat(actual.getOrderMode(), is(equalTo(expected.getOrderMode())));
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void setUpUriInfo(final String page, final String perPage, final String name, final String sort) {
		final Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("page", page);
		parameters.put("per_page", perPage);
		parameters.put("name", name);
		parameters.put("sort", sort);

		final MultivaluedMap<String, String> headerParams = mock(MultivaluedMap.class);

		for (final Entry<String, String> keyValue : parameters.entrySet()) {
			when(headerParams.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
		}

		when(uriInfo.getQueryParameters()).thenReturn(headerParams);
	}
}
