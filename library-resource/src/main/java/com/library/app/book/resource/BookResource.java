/**
 * 
 */
package com.library.app.book.resource;

import static com.library.app.common.model.StandardsOperationResults.*;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.book.services.BookServices;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.JsonWriter;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.ResourceMessage;

/**
 * @author iulian
 *
 */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "EMPLOYEE" })
public class BookResource {

	private Logger logger = LoggerFactory.getLogger(BookResource.class);
	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("book");

	@Inject
	BookServices bookServices;

	@Inject
	BookJsonConverter bookJsonConverter;

	@Context
	UriInfo uriInfo;

	@POST
	public Response add(final String bodyPost) {
		logger.debug("Adding a new book with body {}", bodyPost);
		Book book = bookJsonConverter.convertFrom(bodyPost);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result;
		try {
			book = bookServices.add(book);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(book.getId()));
		} catch (final FieldNotValidException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("One of the fields of the book is not valid", e);
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Category not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");
		} catch (final AuthorNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Author not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "author");
		}

		logger.debug("Returning the operation result after adding book: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	@GET
	@Path("/{id}")
	public Response findById(@PathParam("id") final Long id) {
		logger.debug("Find book: {}", id);
		ResponseBuilder responseBuilder;
		try {
			final Book book = bookServices.findById(id);
			final OperationResult result = OperationResult.success(bookJsonConverter.convertToJsonElement(book));
			responseBuilder = Response.status(HttpCode.OK.getCode()).entity(OperationResultJsonWriter.toJson(result));
			logger.debug("Book found: {}", book);
		} catch (final BookNotFoundException e) {
			logger.error("No book found for id", id);
			responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
		}

		return responseBuilder.build();
	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") final Long id, final String bodyUpdate) {
		logger.debug("Updating the book {} with body {}", id, bodyUpdate);

		final Book book = bookJsonConverter.convertFrom(bodyUpdate);
		book.setId(id);
		HttpCode httpCode = HttpCode.OK;
		OperationResult result = null;
		try {
			bookServices.update(book);
			result = OperationResult.success();
		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the book is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final BookNotFoundException e) {
			logger.error("No book found for id", id);
			httpCode = HttpCode.NOT_FOUND;
			result = getOperationResultNotFound(RESOURCE_MESSAGE);

		} catch (final AuthorNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("No user found for the given id", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "author");

		} catch (final CategoryNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("No category found for the given id", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");

		}

		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}

	@GET
	@PermitAll
	public Response findByFilter() {
		final BookFilter bookFilter = new BookFilterExtractorFromUrl(uriInfo).getFilter();
		logger.debug("Finding books using filter: {}", bookFilter);

		final PaginatedData<Book> books = bookServices.findByFilter(bookFilter);

		logger.debug("Found {} books", books.getNumberOfRows());

		final JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(books,
				bookJsonConverter);
		return Response.status(HttpCode.OK.getCode()).entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
				.build();
	}

}
