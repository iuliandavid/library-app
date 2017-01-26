/**
 * 
 */
package com.library.app.category.resource;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
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
public class CategoryResource {

	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");

	CategoryServices categoryServices;

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
	public Response add(final String bodyPost) {
		logger.debug("Adding a new category with body: {}", bodyPost);

		Category category = categoryJsonConverter.convertFrom(bodyPost);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result = null;
		try {
			category = categoryServices.add(category);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
			logger.debug("Returning the operation result after adding category: {} ", result);

		} catch (final CategoryExistentException categoryExistentException) {
			logger.error("There's already a category for the given name", categoryExistentException);
			httpCode = HttpCode.VALIDATION_ERROR;
			result = StandardsOperationResults.getOperationResultExistent(RESOURCE_MESSAGE, "name");

		}

		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();

	}
}
