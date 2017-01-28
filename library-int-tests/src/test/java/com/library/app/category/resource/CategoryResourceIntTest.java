/**
 * 
 */
package com.library.app.category.resource;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
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
public class CategoryResourceIntTest {

	private static final String DATABASE_BULK_OPERATIONS = "/DB";

	/**
	 * We don't know the url resource(aquillian creates one at runtime) that why we let Arquillian decide
	 * The @ArquillianResource will inject the created URL
	 */
	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addPackages(true, "com.library.app")
				.addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/test/resources/web.xml"))
				.addAsLibraries(
						Maven.resolver().resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:2.6.8")
								.withTransitivity().asFile());
	}

	@Before
	public void initTestCase() {
		this.resourceClient = new ResourceClient(url);
		// Since the tests run as clients, not on server side, the database must be clear after each test
		resourceClient.resourcePath(DATABASE_BULK_OPERATIONS).delete();
	}

	@Test
	@RunAsClient
	public void addValidCategoryAndFindIt() {

		// since the response will be a String we will read the Body (response.readEntity) as String
		final Long id = addCategoryAndGetId("category.json");

		findCategoryAndAssertResponseWithCategory(id, java());
	}

	@Test
	@RunAsClient
	public void addExistentCategory() {
		resourceClient.resourcePath(PATH_RESOURCE).postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));

		final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	@RunAsClient
	public void updateValidCategory() {
		final Long id = addCategoryAndGetId("category.json");
		findCategoryAndAssertResponseWithCategory(id, java());

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
				getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		findCategoryAndAssertResponseWithCategory(id, cleanCode());
	}

	@Test
	@RunAsClient
	public void updateCategoryWithNameBelongingToOtherCategory() {
		final Long javaCategoryId = addCategoryAndGetId("category.json");
		addCategoryAndGetId("categoryCleanCode.json");

		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + javaCategoryId).putWithFile(
				getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	@RunAsClient
	public void updateCategoryNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").putWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findCategoryNotFound() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	/**
	 * 
	 */
	@Test
	@RunAsClient
	public void addCategoryWithNullName() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE)
				.postWithFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json"));

		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	@Test
	@RunAsClient
	public void findAllCategories() {
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");
		final Response response = resourceClient.resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		// Compare the result
		final JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));
		final int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();

		assertThat(totalRecords, is(equalTo(allCategories().size())));

		final JsonArray categoriesList = result.getAsJsonArray("entries");
		assertThat(categoriesList.size(), is(equalTo(allCategories().size())));

		final List<Category> expectedOrderCategory = allCategories();
		expectedOrderCategory.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

		for (int i = 0; i < expectedOrderCategory.size(); i++) {
			final Category expectedCategory = expectedOrderCategory.get(i);
			assertThat(categoriesList.get(i).getAsJsonObject().get("name").getAsString(),
					is(equalTo(expectedCategory.getName())));
		}

	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		// !!!!!Since the response ise set to Unknown(wildfly 8) or Unreadable Entity(wildfly 10) we must USE
		// response.readEntity(String.class)
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private Long addCategoryAndGetId(final String fileName) {
		return IntegrationTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
	}

	private void findCategoryAndAssertResponseWithCategory(final Long categoryIdToBeFound,
			final Category expectedCategory) {
		final String json = IntegrationTestUtils.findById(resourceClient, PATH_RESOURCE, categoryIdToBeFound);

		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
		assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedCategory.getName())));
	}
}
