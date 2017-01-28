/**
 * 
 */
package com.library.app.category.services.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;

/**
 * Since there is a pool of {@link Stateless} ejbs is more useful and scalable to use the {@link Stateless} instead of
 * {@link Stateful} EJB
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
public class CategoryServicesImpl implements CategoryServices {

	/**
	 * Using container's own implementation
	 */
	@Inject
	Validator validator;

	@Inject
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
