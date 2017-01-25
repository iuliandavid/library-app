package com.library.app.category.services;

import com.library.app.category.model.Category;
import com.library.app.common.exception.FieldNotValidException;

/**
 * The contract for the Category services
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
public interface CategoryServices {

	Category add(Category category) throws FieldNotValidException;
}
