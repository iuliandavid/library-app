/**
 * 
 */
package com.library.app.category.services.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class CategoryServicesImpl implements CategoryServices {

	// using javax validator
	Validator validator;
	CategoryRepository categoryRepository;

	@Override
	public Category add(final Category category) throws FieldNotValidException {
		validateCategory(category);
		return categoryRepository.add(category);
	}

	/**
	 * @param category
	 */
	private void validateCategory(final Category category) {
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();
		if (itErrors.hasNext()) {
			final ConstraintViolation<Category> violation = itErrors.next();
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
		if (categoryRepository.alreadyExists(category)) {
			throw new CategoryExistentException();
		}
	}

	@Override
	public void update(final Category category) {
		validateCategory(category);

		if (!categoryRepository.existsById(category.getId())) {
			throw new CategoryNotFoundException();
		}

		categoryRepository.update(category);
	}

	@Override
	public Category findById(final long id) {
		final Category foundCategory = categoryRepository.findById(id);
		if (foundCategory == null) {
			throw new CategoryNotFoundException();
		}
		return foundCategory;
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll("name");
	}

}
