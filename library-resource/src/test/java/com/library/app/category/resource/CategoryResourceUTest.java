/**
 * 
 */
package com.library.app.category.resource;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ResourceDefinitions;

/**
 * Unit tests for {@link CategoryResource}
 * 
 * @author Iulian David
 *
 */
public class CategoryResourceUTest {

	private CategoryResource categoryResource;

	private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();
	@Mock
	private CategoryServices categoryServices;

	@Before
	public void setUp() {
		// needed so that Mockito will take into account all @Mock annotations
		MockitoAnnotations.initMocks(this);

		categoryResource = new CategoryResource();
		categoryResource.categoryServices = categoryServices;
		categoryResource.categoryJsonConverter = new CategoryJsonConverter();
	}

	// Adds
	@Test
	public void addValidCategory() {
		when(categoryServices.add(java())).thenReturn(categoryWithID(java(), 1L));

		final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"newCategory.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addExistentCategory() {
		when(categoryServices.add(java())).thenThrow(new CategoryExistentException());

		final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"newCategory.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	public void addCategoryWithNullName() {
		when(categoryServices.add(new Category()))
				.thenThrow(new FieldNotValidException("name", "Category Name cannot be null"));

		final Response response = categoryResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"categoryWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	// Updates
	@Test
	public void updateValidCategory() {
		when(categoryServices.add(java())).thenReturn(categoryWithID(java(), 1L));

		final Response response = categoryResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertThat(response.getEntity().toString(), is(equalTo("")));
		verify(categoryServices).update(categoryWithID(java(), 1l));
	}

	@Test
	public void updteCategoryWithNameBelongingToTotherCategory() {
		doThrow(new CategoryExistentException()).when(categoryServices).update(categoryWithID(java(), 1L));

		final Response response = categoryResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
	}

	@Test
	public void updateCategoryWithNullName() {
		doThrow(new FieldNotValidException("name", "Category Name cannot be null")).when(categoryServices)
				.update(categoryWithID(new Category(), 1L));

		final Response response = categoryResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"categoryWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "categoryErrorNullName.json");
	}

	@Test
	public void updateCategoryNotFound() {
		doThrow(new CategoryNotFoundException()).when(categoryServices)
				.update(categoryWithID(java(), 1L));

		final Response response = categoryResource.update(1l, readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"category.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
		assertJsonResponseWithFile(response, "categoryNotFound.json");
	}

	// Test findByID
	@Test
	public void findCategory() {
		when(categoryServices.findById(1l)).thenReturn(categoryWithID(java(), 1L));
		final Response response = categoryResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "categoryFound.json");
	}

	@Test
	public void findCategoryNotFound() {
		when(categoryServices.findById(1L)).thenThrow(new CategoryNotFoundException());

		final Response response = categoryResource.findById(1l);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	// Test findAll
	@Test
	public void findAllNoCategory() {
		when(categoryServices.findAll()).thenReturn(new ArrayList<>());
		final Response response = categoryResource.findAll();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "emptyListOfCategories.json");
	}

	@Test
	public void findAllTwoCategories() {
		when(categoryServices.findAll())
				.thenReturn(Arrays.asList(categoryWithID(java(), 1L), categoryWithID(networks(), 2L)));
		final Response response = categoryResource.findAll();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "twoCategories.json");
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}
}
