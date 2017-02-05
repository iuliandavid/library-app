package com.library.app.category.services;

import java.util.List;

import javax.ejb.Local;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.common.exception.FieldNotValidException;

/**
 * The contract for the Category services
 * 
 * It will not be a remote call, that's why it'a annotated as {@link Local}
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Local
public interface CategoryServices {

	Category add(Category category) throws FieldNotValidException, CategoryExistentException;

	void update(Category category) throws FieldNotValidException, CategoryExistentException;

	Category findById(Long id);

	List<Category> findAll();
}
