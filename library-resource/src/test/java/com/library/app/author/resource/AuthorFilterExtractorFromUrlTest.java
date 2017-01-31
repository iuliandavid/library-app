/**
 * 
 */
package com.library.app.author.resource;

import static com.library.app.commontests.utils.FilterExtractorTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

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

		assertActualPaginationDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(nullValue()));

	}

	@Test
	public void withPaginationAndNameAndSortAscending() {
		setUpUriInfo("2", "5", "Robert", "id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.ASCENDING);

		assertActualPaginationDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortAscendingWithPrefix() {
		setUpUriInfo("2", "5", "Robert", "+id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.ASCENDING);

		assertActualPaginationDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortDescending() {
		setUpUriInfo("2", "5", "Robert", "-id");

		final AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfo);
		final AuthorFilter authorFilter = extractor.getFilter();

		final PaginationData actual = authorFilter.getPaginationData();
		final PaginationData expected = new PaginationData(10, 5, "id", OrderMode.DESCENDING);

		assertActualPaginationDataWithExpected(actual, expected);

		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void setUpUriInfo(final String page, final String perPage, final String name, final String sort) {
		final Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("page", page);
		parameters.put("per_page", perPage);
		parameters.put("name", name);
		parameters.put("sort", sort);

		setUpUriInfoWithMap(uriInfo, parameters);
	}
}
