/**
 * 
 */
package com.library.app.category.repository;

import javax.persistence.EntityManager;

import com.library.app.category.model.Category;

/**
 * @author iulian
 *
 */
public class CategoryRepository {

	EntityManager em;

	/**
	 * Inserts a new @Category into DB and returns the persisted one from
	 * the DB
	 * 
	 * @param category
	 *            - The category to be inserted into the DB
	 * @return @Category with the id from DB
	 */
	public Category add(final Category category) {
		em.persist(category);
		return category;
	}
}
