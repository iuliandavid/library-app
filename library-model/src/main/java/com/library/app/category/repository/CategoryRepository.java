/**
 * 
 */
package com.library.app.category.repository;

import java.util.List;
import java.util.Objects;

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
	 *            - The column to order by , <b>not null</b>
	 * @return a list with all the {@link Category} entities
	 */
	@SuppressWarnings("unchecked")
	public List<Category> findAll(final String orderField) {
		Objects.requireNonNull(orderField);
		final List<Category> categories = (em.createQuery("SELECT e FROM Category e ORDER by e." + orderField)
				.getResultList());
		return categories;
	}

	/**
	 * Verifies if the @Category exists in the Database based on the fact that <code>name</code> is <b>unique</b>
	 * 
	 * @see {@link Category}
	 * @param category
	 * @return true if found , false otherwise
	 */
	public boolean alreadyExists(final Category category) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT 1 FROM Category e where e.name = :name");
		if (category.getId() != null) {
			jpql.append(" AND e.id != :id");
		}

		final Query query = em.createQuery(jpql.toString());
		query.setParameter("name", category.getName());
		if (category.getId() != null) {
			query.setParameter("id", category.getId());
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	/**
	 * Same as {@link #alreadyExists(Category)} but with Id as criteria
	 * 
	 * @param id
	 * @return
	 */
	public boolean existsById(final Long id) {
		return em.createQuery("SELECT 1 FROM Category e where e.id = :id")
				.setParameter("id", id).setMaxResults(1).getResultList().size() > 0;
	}
}
