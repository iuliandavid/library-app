/**
 * 
 */
package com.library.app.category.services.impl;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class CategoryServicesImpl implements CategoryServices {

	// using javax validator
	Validator validator;

	@Override
	public Category add(final Category category) throws FieldNotValidException {
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();
		if (itErrors.hasNext()) {
			final ConstraintViolation<Category> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return null;
	}

}
