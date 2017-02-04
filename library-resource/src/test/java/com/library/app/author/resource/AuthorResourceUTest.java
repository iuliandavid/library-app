/**
 * 
 */
package com.library.app.author.resource;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.commontests.utils.ResourceDefinitions;

/**
 * @author iulian
 *
 */
public class AuthorResourceUTest {

	private AuthorResource authorResource;

	private static final String PATH_RESOURCE = ResourceDefinitions.AUTHOR.getResourceName();
	@Mock
	private AuthorServices authorServices;

	@Mock
	UriInfo uriInfo;

	@Before
	public void setUp() {
		// needed so that Mockito will take into account all @Mock annotations
		MockitoAnnotations.initMocks(this);

		authorResource = new AuthorResource();
		authorResource.authorServices = authorServices;
		authorResource.authorJsonConverter = new AuthorJsonConverter();
		authorResource.uriInfo = uriInfo;
	}

	@Test
	public void addValidAuthor() {
		when(authorServices.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1l));

		final Response response = authorResource
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addAuthorWithNullName() {
		when(authorServices.add(new Author()))
				.thenThrow(new FieldNotValidException("name", "Author Name cannot be null"));

		final Response response = authorResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"authorWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "authorErrorNullName.json");
	}

	// Updates
	@Test
	public void updateValidAuthor() {
		when(authorServices.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1L));

		final Response response = authorResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"robertMartin.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertThat(response.getEntity().toString(), is(equalTo("")));
		verify(authorServices).update(authorWithId(robertMartin(), 1l));
	}

	@Test
	public void updateAuthorWithNullName() {
		doThrow(new FieldNotValidException("name", "Author Name cannot be null")).when(authorServices)
				.update(authorWithId(new Author(), 1L));

		final Response response = authorResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"authorWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "authorErrorNullName.json");
	}

	@Test
	public void updateAuthorNotFound() {
		doThrow(new AuthorNotFoundException()).when(authorServices)
				.update(authorWithId(robertMartin(), 1L));

		final Response response = authorResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"robertMartin.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
		assertJsonResponseWithFile(response, "authorNotFound.json");
	}

	// Test findByID
	@Test
	public void findAuthor() {
		when(authorServices.findById(1l)).thenReturn(authorWithId(robertMartin(), 1L));
		final Response response = authorResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "robertMartinFound.json");
	}

	@Test
	public void findAuthorNotFound() {
		doThrow(new AuthorNotFoundException()).when(authorServices)
				.findById(1l);

		final Response response = authorResource.findById(1l);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	// Test findByfilter
	@SuppressWarnings("unchecked")
	@Test
	public void findFilterNoFilter() {
		final List<Author> authors = Arrays.asList(
				authorWithId(erichGamma(), 2L), authorWithId(jamesGosling(), 3L),
				authorWithId(martinFowler(), 4L), authorWithId(robertMartin(), 1L));

		final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
		when(uriInfo.getQueryParameters()).thenReturn(multiMap);
		when(authorServices.findByFilter((AuthorFilter) any()))
				.thenReturn(new PaginatedData<Author>(authors.size(), authors));

		final Response response = authorResource.findByFilter();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "authorsAllInOnePage.json");
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public void findByFilterAndPaginationAndOrderingDescendingByName() {
		final List<Author> authors = Arrays.asList(authorWithId(robertMartin(), 1L),
				authorWithId(martinFowler(), 4L), authorWithId(jamesGosling(), 3L),
				authorWithId(erichGamma(), 2L));

		final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
		final Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("page", "0");
		parameters.put("per_page", "4");
		parameters.put("name", "");
		parameters.put("sort", "-name");

		final MultivaluedMap<String, String> headerParams = mock(MultivaluedMap.class);

		for (final Entry<String, String> keyValue : parameters.entrySet()) {
			when(headerParams.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
		}

		when(uriInfo.getQueryParameters()).thenReturn(headerParams);
		final AuthorFilter authorFilter = new AuthorFilter();
		final PaginationData expected = new PaginationData(0, 4, "name", OrderMode.DESCENDING);
		authorFilter.setPaginationData(expected);
		authorFilter.setName("");
		when(authorServices.findByFilter((AuthorFilter) any()))
				.thenReturn(new PaginatedData<Author>(authors.size(), authors));

		final Response response = authorResource.findByFilter();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "authorsAllInOnePageDescending.json");
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		final String result = response.getEntity().toString();

		assertJsonMatchesFileContent(result, getPathFileResponse(PATH_RESOURCE, fileName));
	}
}
