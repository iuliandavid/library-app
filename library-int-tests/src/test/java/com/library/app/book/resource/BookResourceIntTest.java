/**
 * 
 */
package com.library.app.book.resource;

import static com.library.app.commontests.book.BookForTestsRepository.*;
import static com.library.app.commontests.logaudit.LogAuditTestUtils.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntegrationTestUtils;
import com.library.app.commontests.utils.JsonTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAudit.Action;

/**
 * Integration Tests made with Arquillian
 * 
 * <pre>
 * Since the response body is set to
 * </pre>
 * <ul>
 * <li>Unknown(Wildfly 8)</li>
 * <li>Unreadable Entity(Wildfly 10)</li>
 * </ul>
 * 
 * <pre>
 * We MUST USE response.readEntity(String.class) for reading the Content of a response
 * </pre>
 * 
 * <pre>
 * For testing purposes all inserts must be deleted so it will be created a <b>/DB</b> DELETE endpoint for this operation
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@RunWith(Arquillian.class)
public class BookResourceIntTest {

	private final Logger logger = LoggerFactory.getLogger(BookResourceIntTest.class);
	/**
	 * We don't know the url resource(aquillian creates one at runtime) that why we let Arquillian decide
	 * The @ArquillianResource will inject the created URL
	 */
	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.BOOK.getResourceName();

	private static final String ELEMENT_NAME = Book.class.getSimpleName();

	@Deployment
	public static WebArchive createDeployment() {
		return ArquillianTestUtils.createDeploymentArchive();
	}

	@Before
	public void initTestCase() {
		this.resourceClient = new ResourceClient(url);
		// Since the tests run as clients, not on server side, the database must be clear after each test
		resourceClient.resourcePath("DB/").delete();
		// adding all the users accounts to the database
		resourceClient.resourcePath("DB/" + ResourceDefinitions.USER.getResourceName()).postWithContent("");
		resourceClient.resourcePath("DB/" + ResourceDefinitions.CATEGORY.getResourceName()).postWithContent("");
		resourceClient.resourcePath("DB/" + ResourceDefinitions.AUTHOR.getResourceName()).postWithContent("");

		resourceClient.user(admin());
	}

	@Test
	@RunAsClient
	public void addValidBookAndFindIt() {
		final Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
		findBookAndAssertResponseWithBook(bookId, designPatterns());

		assertAuditLogs(resourceClient, 1, new LogAudit(admin(), Action.ADD, ELEMENT_NAME));
	}

	@Test
	@RunAsClient
	public void addBookWithNullTitle() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		book.setTitle(null);
		addBookWithValidationError(book, "bookErrorNullTitle.json");

		assertAuditLogs(resourceClient, 0);
	}

	@Test
	@RunAsClient
	public void addBookWithInexistentCategory() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		book.getCategory().setId(999L);
		addBookWithValidationError(book, "bookErrorInexistentCategory.json");

		assertAuditLogs(resourceClient, 0);

	}

	@Test
	@RunAsClient
	public void addBookWithInexistentAuthor() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		book.getAuthors().get(0).setId(999L);
		addBookWithValidationError(book, "bookErrorInexistentAuthor.json");

		assertAuditLogs(resourceClient, 0);

	}

	@Test
	@RunAsClient
	public void updateValidBook() {
		final Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
		findBookAndAssertResponseWithBook(bookId, designPatterns());

		final Book book = normalizeDependenciesWithRest(designPatterns());
		book.setPrice(10D);
		book.getAuthors().remove(0);

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + bookId)
				.putWithContent(getJsonForBook(book));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		findBookAndAssertResponseWithBook(bookId, book);

		assertAuditLogs(resourceClient, 2, new LogAudit(admin(), Action.UPDATE, ELEMENT_NAME),
				new LogAudit(admin(), Action.ADD, ELEMENT_NAME));

	}

	@Test
	@RunAsClient
	public void updateBookNotFound() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999L)
				.putWithContent(getJsonForBook(book));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));

		assertAuditLogs(resourceClient, 0);
	}

	@Test
	@RunAsClient
	public void findBookNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));

		assertAuditLogs(resourceClient, 0);
	}

	@Test
	@RunAsClient
	public void findByFilterPaginationAndOrderingDesscendingByTitle() {
		// load all books
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

		// first page
		Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=3&sort=-title").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheBooks(response, 5, refactoring(), peaa(), effectiveJava());

		final LogAudit[] logs = new LogAudit[5];
		Arrays.fill(logs, new LogAudit(admin(), Action.ADD, ELEMENT_NAME));
		assertAuditLogs(resourceClient, 5, logs);

		// second page
		response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=3&sort=-title").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheBooks(response, 5, designPatterns(), cleanCode());

		assertAuditLogs(resourceClient, 5, logs);
	}

	@Test
	@RunAsClient
	public void findByFilterWithNoUser() {
		final Response response = resourceClient.user(null).resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.UNAUTHORIZED.getCode())));

		assertAuditLogs(resourceClient, 0);
	}

	@Test
	@RunAsClient
	public void findByFilterWithUserCustomer() {
		final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		assertAuditLogs(resourceClient, 0);
	}

	@Test
	@RunAsClient
	public void findByIdIdWithUserCustomer() {
		final Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
	}

	private void assertResponseContainsTheBooks(final Response response, final int expectedTotalRecords,
			final Book... expectedBooks) {

		final JsonArray booksList = IntegrationTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
				expectedTotalRecords, expectedBooks.length);

		for (int i = 0; i < expectedBooks.length; i++) {
			final Book expectedBook = expectedBooks[i];
			assertThat(booksList.get(i).getAsJsonObject().get("title").getAsString(),
					is(equalTo(expectedBook.getTitle())));
		}

	}

	private void addBookWithValidationError(final Book book, final String fileName) {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithContent(getJsonForBook(book));

		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, fileName);
	}

	/**
	 * @param book
	 */
	private Book normalizeDependenciesWithRest(final Book book) {
		final Category category = loadCategoryFromRest(book.getCategory());
		book.getCategory().setId(category.getId());

		final List<Author> bookAuthors = book.getAuthors();
		bookAuthors.forEach(author -> {
			final Author authorReceived = loadAuthorFromRest(author);
			author.setId(authorReceived.getId());
		});

		return book;
	}

	/**
	 * @param author
	 * @return
	 */
	private Author loadAuthorFromRest(final Author author) {
		final Response authorResponse = resourceClient
				.resourcePath(
						"DB/" + ResourceDefinitions.AUTHOR.getResourceName() + "/" + author.getName())
				.get();
		assertThat(authorResponse.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		final String authorResult = authorResponse.readEntity(String.class);
		assertThat(authorResult, is(notNullValue()));
		final Long authorID = JsonTestUtils.getIdFromJson(authorResult);
		return new Author(authorID);
	}

	/**
	 * @param book
	 * @return
	 */
	private Category loadCategoryFromRest(final Category category) {
		final Response response = resourceClient
				.resourcePath(
						"DB/" + ResourceDefinitions.CATEGORY.getResourceName() + "/" + category.getName())
				.get();

		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		final String categoryResult = response.readEntity(String.class);
		assertThat(categoryResult, is(notNullValue()));
		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(categoryResult);
		assertThat(JsonReader.getLongOrNull(categoryAsJson, "id"), is(notNullValue()));
		final Long categoryID = JsonReader.getLongOrNull(categoryAsJson, "id");
		return new Category(categoryID);
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		// !!!!!Since the response is set to Unknown(WildFly 8) or Unreadable Entity(WildFly 10) we must USE
		// response.readEntity(String.class)
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private Long addBookAndGetId(final Book book) {
		return IntegrationTestUtils.addElementWithContentAndGetId(resourceClient, PATH_RESOURCE,
				getJsonForBook(book));
	}

	private String getJsonForBook(final Book book) {
		final JsonObject bookJson = new JsonObject();
		/**
		 * "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
		 * "description": "Even bad code can function. But if code isn't clean, ...",
		 * "categoryId": 1,
		 * "authorsIds": [2],
		 * "price": 35.06
		 */
		bookJson.addProperty("title", book.getTitle());
		bookJson.addProperty("description", book.getDescription());
		bookJson.addProperty("categoryId", book.getCategory().getId());
		final JsonArray authorIds = new JsonArray();
		book.getAuthors().forEach(author -> {
			authorIds.add(new JsonPrimitive(author.getId()));
		});
		bookJson.add("authorsIds", authorIds);
		bookJson.addProperty("price", book.getPrice());

		return JsonWriter.writeToString(bookJson);
	}

	private void findBookAndAssertResponseWithBook(final Long bookIdToBeFound, final Book expectedBook) {
		final String bodyResponse = IntegrationTestUtils.findById(resourceClient, PATH_RESOURCE, bookIdToBeFound);
		final JsonObject bookJson = JsonReader.readAsJsonObject(bodyResponse);
		assertThat(bookJson.get("id").getAsLong(), is(notNullValue()));
		assertThat(bookJson.get("title").getAsString(), is(equalTo(expectedBook.getTitle())));
		assertThat(bookJson.get("description").getAsString(), is(equalTo(expectedBook.getDescription())));
		assertThat(bookJson.getAsJsonObject("category").get("name").getAsString(), is(equalTo(expectedBook
				.getCategory().getName())));

		final JsonArray authors = bookJson.getAsJsonArray("authors");
		assertThat(authors.size(), is(equalTo(expectedBook.getAuthors().size())));
		for (int i = 0; i < authors.size(); i++) {
			final String actualAuthorName = authors.get(i).getAsJsonObject().get("name").getAsString();
			final String expectedAuthorName = expectedBook.getAuthors().get(i).getName();
			assertThat(actualAuthorName, is(equalTo(expectedAuthorName)));
		}

		assertThat(bookJson.get("price").getAsDouble(), is(equalTo(expectedBook.getPrice())));
	}
}
