/**
 * 
 */
package com.library.app.commontests.authors;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.PaginatedData;

/**
 * Helper endpoint for appending all test authors
 * 
 * <pre>
 * POST ALL
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
@Path("/DB/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResourceDB {

	@Inject
	private AuthorServices authorServices;

	@Inject
	private AuthorJsonConverter authorJsonConverter;

	@POST
	public void addAll() {
		allAuthors().forEach(authorServices::add);
	}

	/**
	 * Helper endpoint for retrieving a {@link Author}
	 * that's already been inserted into the DB
	 * through {@link #addAll()} method
	 */
	@GET
	@Path("/{name}")
	public Response findByName(@PathParam("name") final String name) {
		final AuthorFilter authorFilter = new AuthorFilter();
		authorFilter.setName(name);
		final PaginatedData<Author> paginatedData = authorServices.findByFilter(authorFilter);
		if (paginatedData != null && paginatedData.getNumberOfRows() > 0) {
			final String authorAsJson = JsonWriter
					.writeToString(authorJsonConverter.convertToJsonElement(paginatedData.getRow(0)));
			return Response.status(HttpCode.OK.getCode()).entity(authorAsJson).build();
		}
		return Response.status(HttpCode.NOT_FOUND.getCode()).build();
	}
}
