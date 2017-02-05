/**
 * 
 */
package com.library.app.commontests.category;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.library.app.category.model.Category;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.model.HttpCode;

/**
 * Helper endpoint for appending all test categories
 * 
 * <pre>
 * POST ALL
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
@Path("/DB/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResourceDB {

	@Inject
	private CategoryServices categoryServices;

	@Inject
	private CategoryJsonConverter categoryJsonConverter;

	@POST
	public void addAll() {
		allCategories().forEach(categoryServices::add);
	}

	/**
	 * Helper endpoint for retrieving a {@link Category}
	 * that's already been inserted into the DB
	 * through {@link #addAll()} method
	 */
	@GET
	@Path("/{name}")
	public Response findByName(@PathParam("name") final String name) {
		final List<Category> categories = categoryServices.findAll();
		final Optional<Category> category = categories.stream().filter(c -> c.getName().equals(name)).findFirst();
		if (category.isPresent()) {
			final String categoryAsJson = JsonWriter
					.writeToString(categoryJsonConverter.convertToJsonElement(category.get()));
			return Response.status(HttpCode.OK.getCode()).entity(categoryAsJson).build();
		}
		return Response.status(HttpCode.NOT_FOUND.getCode()).build();
	}
}
