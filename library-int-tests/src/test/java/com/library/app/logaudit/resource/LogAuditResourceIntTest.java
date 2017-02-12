/**
 * 
 */
package com.library.app.logaudit.resource;

import static com.library.app.commontests.logaudit.LogAuditForTestsRepository.*;
import static com.library.app.commontests.user.UserForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;
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

import com.google.gson.JsonArray;
import com.library.app.common.model.HttpCode;
import com.library.app.common.utils.DateUtils;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.IntegrationTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
import com.library.app.logaudit.model.LogAudit;

/**
 * @author iulian
 *
 */
@RunWith(Arquillian.class)
public class LogAuditResourceIntTest {

	/**
	 * We don't know the url resource(aquillian creates one at runtime) that why we let Arquillian decide
	 * The @ArquillianResource will inject the created URL
	 */
	@ArquillianResource
	private URL url;

	private ResourceClient resourceClient;

	private static final String PATH_RESOURCE = ResourceDefinitions.LOGAUDIT.getResourceName();

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

		resourceClient.user(admin());

	}

	@Test
	@RunAsClient
	public void findByFilterFilterPaginationAndOrderDescendingByName() {
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

		final List<LogAudit> allLogs = allLogs();

		// first page
		Response response = resourceClient.resourcePath(
				PATH_RESOURCE + "?page=0&per_page=2&sort=-createdAt").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheLoggs(response, 3, allLogs.get(2), allLogs.get(1));

		response = resourceClient.resourcePath(
				PATH_RESOURCE + "?page=1&per_page=2&sort=-createdAt").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheLoggs(response, 3, allLogs.get(0));
	}

	private void assertResponseContainsTheLoggs(final Response response, final int expectTotalRecords,
			final LogAudit... expectedLogsAudit) {
		final JsonArray logsList = IntegrationTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
				expectTotalRecords, expectedLogsAudit.length);

		for (int i = 0; i < expectedLogsAudit.length; i++) {
			final LogAudit expectedLogAudit = expectedLogsAudit[i];
			assertThat(logsList.get(i).getAsJsonObject().get("createdAt").getAsString(),
					is(equalTo(DateUtils.formatDateTime(expectedLogAudit.getCreatedAt()))));
		}
	}
}
