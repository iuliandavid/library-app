/**
 * 
 */
package com.library.app.category.resource;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;

/**
 * @author iulian
 *
 */
public class CategoryResource {

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

		category = categoryServices.add(category);

		final OperationResult result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));

		logger.debug("Returning the operation result after adding category: {} ", result);
		return Response.status(HttpCode.CREATED.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}
}
