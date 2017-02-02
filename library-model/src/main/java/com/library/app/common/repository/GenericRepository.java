/**
 * 
 */
package com.library.app.common.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.library.app.category.model.Category;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;

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
	 * Verifies if the {@link Entity} exists in the Database based on the fact that <code>name</code> is <b>unique</b>
	 * A special case for verifying is when the id is given the search is made also against Id
	 * 
	 * <pre>
	 * Entity entity = genericEntity.add(new Entity())
	 * alreadyExists(entity) == false
	 * </pre>
	 * 
	 * @see {@link Category}
	 * 
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

	@SuppressWarnings("unchecked")
	protected PaginatedData<T> findByParameters(final String clause, final PaginationData paginationData,
			final Map<String, Object> queryParameters, final String defaultSortFieldWithDirection) {

		final String clauseSort = "ORDER BY e." + getSortField(paginationData, defaultSortFieldWithDirection);
		final Query queryEntities = getEntityManager()
				.createQuery("SELECT e FROM " + getPersistentClass().getSimpleName()
						+ " e " + clause + " " + clauseSort);
		applyQueryParametersOnQuery(queryParameters, queryEntities);

		applyPaginationOnQuery(paginationData, queryEntities);

		final List<T> entities = queryEntities.getResultList();

		final Integer count = countWithFilter(clause, queryParameters);

		return new PaginatedData<T>(count, entities);
	}

	private Integer countWithFilter(final String clause, final Map<String, Object> queryParameters) {
		final Query queryCount = getEntityManager()
				.createQuery("SELECT Count(e) FROM " + getPersistentClass().getSimpleName()
						+ " e " + clause);
		applyQueryParametersOnQuery(queryParameters, queryCount);

		return ((Long) queryCount.getSingleResult()).intValue();
	}

	private void applyPaginationOnQuery(final PaginationData paginationData, final Query query) {
		if (paginationData != null) {
			query.setFirstResult(paginationData.getFirstResult());
			query.setMaxResults(paginationData.getMaxResults());
		}

	}

	private String getSortField(final PaginationData paginationData, final String defaultSortField) {
		if (paginationData == null || paginationData.getOrderField() == null) {
			return defaultSortField;
		}
		return paginationData.getOrderField() + getSortDirection(paginationData);
	}

	private String getSortDirection(final PaginationData paginationData) {
		return paginationData.isAscending() ? " ASC" : " DESC";
	}

	/**
	 * @param queryParameters
	 * @param query
	 */
	private void applyQueryParametersOnQuery(final Map<String, Object> queryParameters, final Query query) {
		queryParameters.forEach((key, value) -> {
			query.setParameter(key, value);
		});
	}
}
