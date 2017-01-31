/**
 * 
 */
package com.library.app.user.resource;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntegrationTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.user.model.User;

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
public class UserResourceIntTest {

	private static final String DATABASE_BULK_OPERATIONS = "/DB";

	/**
	 * We don't know the url resource(aquillian creates one at runtime) that why we let Arquillian decide
	 * The @ArquillianResource will inject the created URL
	 */
	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.USER.getResourceName();

	@Deployment
	public static WebArchive createDeployment() {
		return ArquillianTestUtils.createDeploymentArchive();
	}

	@Before
	public void initTestCase() {
		this.resourceClient = new ResourceClient(url);
		// Since the tests run as clients, not on server side, the database must be clear after each test
		resourceClient.resourcePath(DATABASE_BULK_OPERATIONS).delete();
		// adding the Administstrator account
		resourceClient.resourcePath(DATABASE_BULK_OPERATIONS + PATH_RESOURCE + "/admin").postWithContent("");
	}

	@Test
	@RunAsClient
	public void addValidCustomerAndFindIt() {

		// since the response will be a String we will read the Body (response.readEntity) as String
		final Long id = addUserAndGetId("customerJohnDoe.json");

		findUserAndAssertResponseWithUser(id, johnDoe());
	}

	@Test
	@RunAsClient
	public void addExistentUser() {
		resourceClient.resourcePath(PATH_RESOURCE)
				.postWithFile(getPathFileRequest(PATH_RESOURCE, "customerJohnDoe.json"));

		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "customerJohnDoe.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "userAlreadyExists.json");
	}

	// @Test
	// @RunAsClient
	// public void updateValidCategory() {
	// final Long id = addCategoryAndGetId("category.json");
	// findCategoryAndAssertResponseWithCategory(id, java());
	//
	// final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
	// getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
	// assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
	//
	// findCategoryAndAssertResponseWithCategory(id, cleanCode());
	// }
	//
	// @Test
	// @RunAsClient
	// public void updateCategoryWithNameBelongingToOtherCategory() {
	// final Long javaCategoryId = addCategoryAndGetId("category.json");
	// addCategoryAndGetId("categoryCleanCode.json");
	//
	// final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + javaCategoryId).putWithFile(
	// getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
	// assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
	// assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	// }
	//
	// @Test
	// @RunAsClient
	// public void updateCategoryNotFound() {
	// final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").putWithFile(
	// getPathFileRequest(PATH_RESOURCE, "category.json"));
	// assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	// }
	//
	// @Test
	// @RunAsClient
	// public void findCategoryNotFound() {
	// final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
	// assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	// }
	//
	// /**
	// *
	// */
	// @Test
	// @RunAsClient
	// public void addCategoryWithNullName() {
	// final Response response = resourceClient.resourcePath(PATH_RESOURCE)
	// .postWithFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json"));
	//
	// assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
	// assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	// }

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		// !!!!!Since the response is set to Unknown(wildfly 8) or Unreadable Entity(wildfly 10) we must USE
		// response.readEntity(String.class)
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private Long addUserAndGetId(final String fileName) {
		return IntegrationTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
	}

	private void findUserAndAssertResponseWithUser(final Long userIdToBeFound,
			final User expectedUser) {
		final String json = IntegrationTestUtils.findById(resourceClient, PATH_RESOURCE, userIdToBeFound);

		assertResponseWithUser(json, expectedUser);
	}

	/**
	 * @param expectedUser
	 * @param bodyResponse
	 */
	private void assertResponseWithUser(final String bodyResponse, final User expectedUser) {
		final JsonObject userJson = JsonReader.readAsJsonObject(bodyResponse);
		assertThat(JsonReader.getStringOrNull(userJson, "id"), is(equalTo(expectedUser.getId())));
		assertThat(JsonReader.getStringOrNull(userJson, "name"), is(equalTo(expectedUser.getName())));
		assertThat(JsonReader.getStringOrNull(userJson, "email"), is(equalTo(expectedUser.getEmail())));
		assertThat(JsonReader.getStringOrNull(userJson, "type"), is(equalTo(expectedUser.getUserType().toString())));
		assertThat(JsonReader.getStringOrNull(userJson, "createdAt"), is(notNullValue()));
		final JsonArray roles = userJson.getAsJsonArray("roles");
		assertThat(roles.size(), is(equalTo(expectedUser.getRoles().size())));
		for (int i = 0; i < roles.size(); i++) {
			final String actualRole = roles.get(i).getAsJsonPrimitive().getAsString();
			final String expectedRole = expectedUser.getRoles().get(i).toString();
			assertThat(actualRole, is(equalTo(expectedRole)));
		}
	}

	private void assertResponseContainsTheUsers(final Response response, final int expectedTotalRecords,
			final User... expectedUsers) {
		final JsonArray categoryList = IntegrationTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(
				response,
				expectedTotalRecords, expectedUsers.length);
		for (int i = 0; i < expectedUsers.length; i++) {
			final User expectedUser = expectedUsers[i];
			assertThat(categoryList.get(i).getAsJsonObject().get("name").getAsString(),
					is(equalTo(expectedUser.getName())));
		}
	}
}
