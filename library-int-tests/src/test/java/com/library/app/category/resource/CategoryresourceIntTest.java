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

import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;

/**
 * Integration Tests made with Arquillian
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@RunWith(Arquillian.class)
public class CategoryresourceIntTest {

	/**
	 * We don't know the url resource that why we let Arquillian decide
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
				.addPackages(true, "comm.library.app")
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
	}

	@Test
	@RunAsClient
	public void addValidCategoryAndFindIt() {
		final Response response = resourceClient.resourcePath(PATH_RESOURCE)
				.postWithContent(getPathFileRequest(PATH_RESOURCE, "category.json"));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));

		// since the response will be a String we will read the Body (response.readEntity) as String
		final Long id = getIdFromJson(response.readEntity(String.class));

		// issue the GET Category
		final Response getResponse = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).get();
		assertThat(getResponse.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		final JsonObject categoryAsJson = JsonReader.readAsJsonObject(getResponse.readEntity(String.class));
		assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(java().getName())));
	}
}
