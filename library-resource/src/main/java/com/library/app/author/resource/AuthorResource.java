/**
 * 
 */
package com.library.app.author.resource;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.ResourceMessage;
import com.library.app.common.model.StandardsOperationResults;

/**
 * @author iulian
 *
 */
@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "EMPLOYEE" })
public class AuthorResource {

	private Logger logger = LoggerFactory.getLogger(AuthorResource.class);
	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("author");

	@Inject
	AuthorServices authorServices;

	@Inject
	AuthorJsonConverter authorJsonConverter;

	@Context
	UriInfo uriInfo;

	/**
	 * The endpoint that will be used for creating a new Author
	 * <ul>
	 * <li>A Author instance will be created from the post message</li>
	 * <li>The Author instance will be passed to {@link AuthorServices#add(Author)}</li>
	 * <li>Finally the Author instance will be saved into database by calling
	 * {@link AuthorRepository#add(Author)}</li>
	 * </ul>
	 * 
	 * @param bodyPost
	 * @return
	 */
	@POST
	public Response add(final String bodyPost) {
		logger.debug("Adding a new author with body: {}", bodyPost);

		Author author = authorJsonConverter.convertFrom(bodyPost);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result = null;
		try {
			author = authorServices.add(author);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(author.getId()));

		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the author is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		}
		logger.debug("Returning the operation result after adding author: {} ", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") final Long id, final String bodyUpdate) {
		logger.debug("Updating the author {} with body: {}", id, bodyUpdate);

		final Author author = authorJsonConverter.convertFrom(bodyUpdate);
		author.setId(id);

		HttpCode httpCode = HttpCode.OK;
		OperationResult result = null;
		try {
			authorServices.update(author);
			result = OperationResult.success();

		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the category is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final AuthorNotFoundException e) {
			logger.error("No author was found for the id {}", id);
			httpCode = HttpCode.NOT_FOUND;
			result = StandardsOperationResults.getOperationResultNotFound(RESOURCE_MESSAGE);
		}
		logger.debug("Returning the operation result after adding author: {} ", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

	}

	@GET
	@Path("/{id}")
	public Response findById(@PathParam("id") final Long id) {
		ResponseBuilder responseBuilder;
		try {
			final Author author = authorServices.findById(id);
			final OperationResult result = OperationResult
					.success(authorJsonConverter.convertToJsonElement(author));
			responseBuilder = Response.status(HttpCode.OK.getCode())
					.entity(OperationResultJsonWriter.toJson(result));
		} catch (final AuthorNotFoundException e) {
			logger.error("No author was found for the id {}", id);
			responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
		}
		return responseBuilder.build();
	}

	/**
	 * @see <a href="https://docs.oracle.com/cd/E19798-01/821-1841/gipyw/index.html">Extracting Request Parameters</a>
	 * @return
	 */
	@GET
	@PermitAll
	public Response findByFilter() {
		ResponseBuilder responseBuilder;

		final AuthorFilter authorFilter;

		final MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		if (queryParams == null) {
			authorFilter = new AuthorFilter();
		} else {
			authorFilter = new AuthorFilterExtractorFromUrl(uriInfo).getFilter();
		}
		final PaginatedData<Author> paginatedData = authorServices.findByFilter(authorFilter);
		final OperationResult result = OperationResult
				.success(authorJsonConverter.convertToJsonElement(paginatedData));
		responseBuilder = Response.status(HttpCode.OK.getCode())
				.entity(OperationResultJsonWriter.toJson(result));

		return responseBuilder.build();
	}
}
