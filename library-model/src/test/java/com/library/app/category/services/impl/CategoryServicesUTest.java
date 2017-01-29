/**
 * 
 */
package com.library.app.category.services.impl;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

/**
 * Unit Tests for category Services
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class CategoryServicesUTest {

	private CategoryServices categoryServices;

	/** Using a default validator **/
	private Validator validator;

	/** The mockable concrete class **/
	private CategoryRepository categoryRepository;

	@Before
	public void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		categoryRepository = mock(CategoryRepository.class);
		categoryServices = new CategoryServicesImpl();
		((CategoryServicesImpl) categoryServices).validator = validator;
		((CategoryServicesImpl) categoryServices).categoryRepository = categoryRepository;
	}

	@Test
	public void addCategoryWithNullName() {
		try {
			addCategoryWithInvalidName(null);
		} catch (final FieldNotValidException e) {
			// Test the custom message set to @NutNull validation on Category model
			assertThat(e.getMessage(), is(equalTo("Category Name cannot be null")));
		}

	}

	@Test
	public void addCategoryWithShortName() {
		try {
			addCategoryWithInvalidName("A");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 25")));
		}

	}

	@Test
	public void addCategoryWithLongName() {
		try {
			addCategoryWithInvalidName("This is a long name that will cause an exception");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 25")));
		}

	}

	@Test(expected = CategoryExistentException.class)
	public void addCategoryWithExistentName() {
		when(categoryRepository.alreadyExists(java())).thenReturn(true);
		categoryServices.add(java());
	}

	/**
	 * Test the {@link CategoryServices#add} method
	 * by mocking the result of {@link CategoryRepository#add(Category)}
	 */
	@Test
	public void addValidCategory() {
		when(categoryRepository.alreadyExists(java())).thenReturn(false);

		when(categoryRepository.add(java())).thenReturn(categoryWithID(java(), 1L));
		final Category categoryAdded = categoryServices.add(java());
		assertThat(categoryAdded.getId(), is(equalTo(1l)));
	}

	private void addCategoryWithInvalidName(final String name) {
		try {
			categoryServices.add(new Category(name));
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
			throw e;

		}

	}

	////// Updates

	@Test
	public void updateCategoryWithNullName() {
		try {
			updateCategoryWithInvalidName(null);
		} catch (final FieldNotValidException e) {
			// Test the custom message set to @NutNull validation on Category model
			assertThat(e.getMessage(), is(equalTo("Category Name cannot be null")));
		}

	}

	@Test
	public void updateCategoryWithShortName() {
		try {
			updateCategoryWithInvalidName("A");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 25")));
		}

	}

	@Test
	public void updateCategoryWithLongName() {
		try {
			updateCategoryWithInvalidName("This is a long name that will cause an exception");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 25")));
		}

	}

	@Test(expected = CategoryExistentException.class)
	public void updateCategoryWithExistentName() {
		when(categoryRepository.alreadyExists(categoryWithID(java(), 1l))).thenReturn(true);
		categoryServices.update(categoryWithID(java(), 1l));
	}

	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryNotFound() {
		when(categoryRepository.alreadyExists(categoryWithID(java(), 1l))).thenReturn(false);
		when(categoryRepository.existsById(1l)).thenReturn(false);
		categoryServices.update(categoryWithID(java(), 1l));
	}

	@Test
	public void updateValidCategory() {
		when(categoryRepository.alreadyExists(categoryWithID(java(), 1l))).thenReturn(false);
		when(categoryRepository.existsById(1l)).thenReturn(true);
		categoryServices.update(categoryWithID(java(), 1l));

		// verify that CategoryRepository.update was invoked
		verify(categoryRepository).update(categoryWithID(java(), 1l));
	}

	private void updateCategoryWithInvalidName(final String name) {
		try {
			categoryServices.update(new Category(name));
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
			throw e;

		}

	}

	/// Test findByID
	@Test
	public void findCategoryById() {
		final Category mockedCategory = categoryWithID(java(), 1L);
		when(categoryRepository.findById(1l)).thenReturn(mockedCategory);

		final Category category = categoryServices.findById(1l);
		assertThat(category, is(notNullValue()));
		assertThat(category.getId(), is(equalTo(mockedCategory.getId())));
		assertThat(category.getName(), is(equalTo(mockedCategory.getName())));
	}

	@Test(expected = CategoryNotFoundException.class)
	public void findCategoryByIdNotFound() {
		when(categoryRepository.findById(1l)).thenReturn(null);

		categoryServices.findById(1l);

	}

	/// Test findAll

	@Test
	public void findAllNoCategories() {
		when(categoryRepository.findAll("name")).thenReturn(new ArrayList<>());
		final List<Category> categories = categoryServices.findAll();
		assertThat(categories.isEmpty(), is(equalTo(true)));
	}

	@Test
	public void findAllCategories() {
		when(categoryRepository.findAll("name"))
				.thenReturn(Arrays.asList(categoryWithID(java(), 1l), categoryWithID(networks(), 2l)));
		final List<Category> categories = categoryServices.findAll();
		assertThat(categories.size(), is(equalTo(2)));
		assertThat(categories.get(0).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(networks().getName())));
	}
}
