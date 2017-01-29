/**
 * 
 */
package com.library.app.category.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import com.library.app.common.model.StandardsOperationResults;

/**
 * @author iulian
 *
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

	@Inject
	CategoryServices categoryServices;

	@Inject
	CategoryJsonConverter categoryJsonConverter;

	private Logger logger = LoggerFactory.getLogger(CategoryResource.class);

	/**
	 * The endpoint that will be used for creating a new Category
	 * <ul>
	 * <li>A Category instance will be created from the post message</li>
	 * <li>The Category instance will be passed to {@link CategoryServices#add(Category)}</li>
	 * <li>Finally the Category instance will be saved into database by calling
	 * {@link CategoryRepository#add(Category)}</li>
	 * </ul>
	 * 
	 * @param bodyPost
	 * @return
	 */
	@POST
	public Response add(final String bodyPost) {
		logger.debug("Adding a new category with body: {}", bodyPost);

		Category category = categoryJsonConverter.convertFrom(bodyPost);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result = null;
		try {
			category = categoryServices.add(category);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));

		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the category is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryExistentException categoryExistentException) {
			logger.error("There's already a category for the given name", categoryExistentException);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultExistent(RESOURCE_MESSAGE, "name");

		}
		logger.debug("Returning the operation result after adding category: {} ", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

	}

	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") final Long id, final String bodyUpdate) {
		logger.debug("Updating the category {} with body: {}", id, bodyUpdate);

		final Category category = categoryJsonConverter.convertFrom(bodyUpdate);
		category.setId(id);

		HttpCode httpCode = HttpCode.OK;
		OperationResult result = null;
		try {
			categoryServices.update(category);
			result = OperationResult.success();

		} catch (final FieldNotValidException e) {
			logger.error("One of the field of the category is not valid", e);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryExistentException categoryExistentException) {
			logger.error("There's already a category for the given name", categoryExistentException);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultExistent(RESOURCE_MESSAGE, "name");

		} catch (final CategoryNotFoundException e) {
			logger.error("No category was found for the id {}", id);
			httpCode = HttpCode.NOT_FOUND;
			result = StandardsOperationResults.getOperationResultNotFound(RESOURCE_MESSAGE);
		}
		logger.debug("Returning the operation result after adding category: {} ", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

	}

	@GET
	@Path("/{id}")
	public Response findById(@PathParam("id") final Long id) {
		ResponseBuilder responseBuilder;
		try {
			final Category category = categoryServices.findById(id);
			final OperationResult result = OperationResult
					.success(categoryJsonConverter.convertToJsonElement(category));
			responseBuilder = Response.status(HttpCode.OK.getCode())
					.entity(OperationResultJsonWriter.toJson(result));
		} catch (final CategoryNotFoundException e) {
			logger.error("No category was found for the id {}", id);
			responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
		}
		return responseBuilder.build();
	}

	@GET
	public Response findAll() {
		ResponseBuilder responseBuilder;
		final List<Category> categories = categoryServices.findAll();
		final OperationResult result = OperationResult
				.success(getJsonElementWithPagingAndEntries(categories));
		responseBuilder = Response.status(HttpCode.OK.getCode())
				.entity(OperationResultJsonWriter.toJson(result));
		return responseBuilder.build();
	}

	/**
	 * @param categories
	 * @return
	 */
	private JsonElement getJsonElementWithPagingAndEntries(final List<Category> categories) {
		final JsonObject jsonObject = new JsonObject();
		final JsonObject totalRecordsElement = new JsonObject();
		totalRecordsElement.addProperty("totalRecords", categories.size());
		jsonObject.add("paging", totalRecordsElement);
		jsonObject.add("entries", categoryJsonConverter.convertToJsonElement(categories));
		return jsonObject;
	}
}
