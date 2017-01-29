/**
 * 
 */
package com.library.app.common.repository;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.library.app.category.model.Category;

/**
 * Generic class for operations spefici to repositories
 * 
 * @author iulian
 *
 */
public abstract class GenericRepository<T> {

	protected abstract Class<T> getPersistentClass();

	protected abstract EntityManager getEntityManager();

	/**
	 * Inserts a new {@link Entity} of type T into DB and returns the persisted one from
	 * the DB
	 * 
	 * @param entity
	 *            - The entity to be inserted into the DB
	 * @return {@link Entity} with the id from DB
	 */
	public T add(final T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	/**
	 * Finds a {@link Entity} of type T using an Id
	 * 
	 * @param id
	 *            - The id criteria
	 *            <p>
	 *            Can be <b>null</b>
	 *            </p>
	 * @return found {@link Entity} or <b>null</b>
	 */
	public T findById(final Long id) {
		if (id == null) {
			return null;
		}
		return getEntityManager().find(getPersistentClass(), id);
	}

	/**
	 * Updates an {@link Entity} of type T
	 * 
	 * @param entity
	 *            - the {@link Entity} of type T to update
	 * @return The updated {@link Entity} of type T
	 */
	public T update(final T entity) {
		return getEntityManager().merge(entity);
	}

	/**
	 * Verifies if an id is already stored
	 * 
	 * @param id
	 * @return true if found, else false
	 */
	public boolean existsById(final Long id) {
		return getEntityManager()
				.createQuery("SELECT 1 FROM " + getPersistentClass().getSimpleName() + " e where e.id = :id")
				.setParameter("id", id).setMaxResults(1).getResultList().size() > 0;
	}

	/**
	 * Returns all the {@link Entity} of type T entities and orders the results by the filed passed as param
	 * 
	 * @param orderField
	 *            - The column to order by , <b>not null</b>
	 * @return a list with all the {@link Entity} of type T
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(final String orderField) {
		Objects.requireNonNull(orderField);
		return (getEntityManager()
				.createQuery("SELECT e FROM " + getPersistentClass().getSimpleName() + " e ORDER by e." + orderField)
				.getResultList());
	}

	/**
	 * Verifies if the @Category exists in the Database based on the fact that <code>name</code> is <b>unique</b>
	 * 
	 * @see {@link Category}
	 * @param category
	 * @return true if found , false otherwise
	 */
	public boolean alreadyExists(final String propertyName, final String propertyValue, final Long id) {
		final StringBuilder jpql = new StringBuilder();
		jpql.append(
				"SELECT 1 FROM " + getPersistentClass().getName() + " e where e." + propertyName + " = :propertyValue");
		if (id != null) {
			jpql.append(" AND e.id != :id");
		}

		final Query query = getEntityManager().createQuery(jpql.toString());
		query.setParameter("propertyValue", propertyValue);
		if (id != null) {
			query.setParameter("id", id);
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}
}
