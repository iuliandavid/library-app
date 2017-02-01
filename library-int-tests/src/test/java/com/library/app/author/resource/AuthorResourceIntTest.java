/**
 * 
 */
package com.library.app.author.resource;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

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
import com.library.app.author.model.Author;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntegrationTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;

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
public class AuthorResourceIntTest {

	private final Logger logger = LoggerFactory.getLogger(AuthorResourceIntTest.class);
	/**
	 * We don't know the url resource(aquillian creates one at runtime) that why we let Arquillian decide
	 * The @ArquillianResource will inject the created URL
	 */
	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.AUTHOR.getResourceName();

	@Deployment
	public static WebArchive createDeployment() {
		return ArquillianTestUtils.createDeploymentArchive();
	}

	@Before
	public void initTestCase() {
		this.resourceClient = new ResourceClient(url);
		// Since the tests run as clients, not on server side, the database must be clear after each test
		resourceClient.resourcePath("DB/").delete();
		// adding the Administstrator account
		resourceClient.resourcePath("DB/" + ResourceDefinitions.USER.getResourceName()).postWithContent("");
		resourceClient.user(admin());
	}

	@Test
	@RunAsClient
	public void addValidAuthorAndFindIt() {
		final Long authorId = addAuthorAndGetId("robertMartin.json");
		findAuthorAndAssertResponseWithAuthor(authorId, robertMartin());
	}

	@Test
	@RunAsClient
	public void addAuthorWithNullName() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json"));

		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "authorErrorNullName.json");
	}

	@Test
	@RunAsClient
	public void updateValidAuthor() {
		final Long id = addAuthorAndGetId("robertMartin.json");
		findAuthorAndAssertResponseWithAuthor(id, robertMartin());

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
				getPathFileRequest(PATH_RESOURCE, "uncleBob.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		final Author uncleBob = new Author("Uncle Bob");
		uncleBob.setId(id);
		findAuthorAndAssertResponseWithAuthor(id, uncleBob);
	}

	@Test
	@RunAsClient
	public void updateAuthorNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").putWithFile(
				getPathFileRequest(PATH_RESOURCE, "robertMartin.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findAuthorNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findByFilterAndPaginationAndOrderingDescendingByName() {
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");
		final int expectedRows = 10;
		// first page
		Response response = resourceClient
				.resourcePath(PATH_RESOURCE + "?page=0&per_page=" + expectedRows + "&sort=-name").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheAuthors(response, 12, williamOpdyke(),
				robertMartin(), richardHelm(), ralphJohnson(), martinFowler(),
				kentBeck(), joshuaBlock(), johnVlissides(), johnBrandt(),
				jamesGosling());

		response = resourceClient
				.resourcePath(PATH_RESOURCE + "?page=1&per_page=" + expectedRows + "&sort=-name").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheAuthors(response, 12, erichGamma(), donRoberts());
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		// !!!!!Since the response is set to Unknown(WildFly 8) or Unreadable Entity(WildFly 10) we must USE
		// response.readEntity(String.class)
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private Long addAuthorAndGetId(final String fileName) {
		return IntegrationTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
	}

	private void findAuthorAndAssertResponseWithAuthor(final Long authorIdToBeFound,
			final Author expectedAuthor) {
		final String json = IntegrationTestUtils.findById(resourceClient, PATH_RESOURCE, authorIdToBeFound);

		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
		assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedAuthor.getName())));
	}

	private void assertResponseContainsTheAuthors(final Response response, final int expectedTotalRecords,
			final Author... expectedAuthors) {
		final JsonArray authorsList = IntegrationTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
				expectedTotalRecords, expectedAuthors.length);
		for (int i = 0; i < expectedAuthors.length; i++) {
			final Author expectedAuthor = expectedAuthors[i];
			assertThat(authorsList.get(i).getAsJsonObject().get("name").getAsString(),
					is(equalTo(expectedAuthor.getName())));
		}
	}
}
