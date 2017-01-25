/**
 * 
 */
package com.library.app.category.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

	/**
	 * Finds a @Category using an Id
	 * 
	 * @param id
	 *            - The id criteria
	 *            <p>
	 *            Can be <b>null</b>
	 *            </p>
	 * @return found @Category or <b>null</b>
	 */
	public Category findById(final Long id) {
		if (id == null) {
			return null;
		}
		return em.find(Category.class, id);
	}

	/**
	 * Updates a Category
	 * 
	 * @param category
	 *            - the @Category to update
	 * @return The updated Category
	 */
	public Category update(final Category category) {
		return em.merge(category);
	}

	/**
	 * Returns all the {@link Category} entities and orders the results by the filed passed as param
	 * 
	 * @param orderField
	 *            - The column to order by
	 * @return a list with all the {@link Category} entities
	 */
	@SuppressWarnings("unchecked")
	public List<Category> findAll(final String orderField) {
		final List<Category> categories = (em.createQuery("SELECT e FROM Category e ORDER by e." + orderField)
				.getResultList());
		return categories;
	}

	public boolean alreadyExists(final Category category) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT 1 FROM Category e where e.name = :name");

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", category.getName());

		return query.setMaxResults(1).getResultList().size() > 0;
	}
}
