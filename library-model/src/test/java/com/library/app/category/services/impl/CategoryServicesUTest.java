/**
 * 
 */
package com.library.app.category.services.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
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

	private Validator validator;

	@Before
	public void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		categoryServices = new CategoryServicesImpl();
		((CategoryServicesImpl) categoryServices).validator = validator;
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

	private void addCategoryWithInvalidName(final String name) {
		try {
			categoryServices.add(new Category(name));
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
			throw e;

		}

	}
}
