/**
 * 
 */
package com.library.app.author.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.PaginatedData;

/**
 * @author iulian
 *
 */
@Stateless
public class AuthorRepository {

	@PersistenceContext
	EntityManager em;

	/**
	 * Inserts a new @Author into DB and returns the persisted one from
	 * the DB
	 * 
	 * @param author
	 *            - The author to be inserted into the DB
	 * @return @Author with the id from DB
	 */
	public Author add(final Author author) {
		em.persist(author);
		return author;
	}

	/**
	 * Finds a @Category using an Id
	 * 
	 * @param id
	 *            - The id criteria
	 *            <p>
	 *            Can be <b>null</b>
	 *            </p>
	 * @return found @Author or <b>null</b>
	 */
	public Author findById(final Long id) {
		if (id == null) {
			return null;
		}
		return em.find(Author.class, id);
	}

	/**
	 * Updates an Author
	 * 
	 * @param author
	 *            - the @Author to update
	 * @return The updated @Author
	 */
	public Author update(final Author author) {
		return em.merge(author);
	}

	/**
	 * Returns all the {@link Author} entities and orders the results by the filed passed as param
	 * 
	 * @param orderField
	 *            - The column to order by , <b>not null</b>
	 * @return a list with all the {@link Author} entities
	 */
	@SuppressWarnings("unchecked")
	public List<Author> findAll(final String orderField) {
		Objects.requireNonNull(orderField);
		final List<Author> authors = (em.createQuery("SELECT e FROM Author e ORDER by e." + orderField)
				.getResultList());
		return authors;
	}

	/**
	 * Verifies if an id is already stored
	 * 
	 * @param id
	 * @return true if found, else false
	 */
	public boolean existsById(final Long id) {
		return em.createQuery("SELECT 1 FROM Author e where e.id = :id")
				.setParameter("id", id).setMaxResults(1).getResultList().size() > 0;
	}

	@SuppressWarnings("unchecked")
	public PaginatedData<Author> findByFilter(final AuthorFilter filter) {

		final StringBuilder clause = new StringBuilder("WHERE e.id is not null");
		final Map<String, Object> queryParameters = new HashMap<>();
		if (filter.getName() != null) {
			clause.append(" AND UPPER(e.name) LIKE UPPER(:name)");
			queryParameters.put("name", "%" + filter.getName() + "%");
		}
		final StringBuilder clauseSort = new StringBuilder();
		if (filter.getName() != null) {
			clauseSort.append(" ORDER BY e." + filter.getPaginationData().getOrderField());
			// then we add ORDERING
			clauseSort.append(filter.getPaginationData().isAscending() ? " ASC " : " DESC");
		} else {
			clauseSort.append(" ORDER BY e.name ASC");
		}
		final Query queryAuthors = em
				.createQuery("SELECT e FROM Author e " + clause.toString() + " " + clauseSort.toString());
		if (filter.hasPaginationData()) {
			queryAuthors.setFirstResult(filter.getPaginationData().getFirstResult());
			queryAuthors.setMaxResults(filter.getPaginationData().getMaxResults());
		}
		applyQueryParametersOnQuery(queryParameters, queryAuthors);

		final List<Author> authors = queryAuthors.getResultList();

		final Query queryCount = em.createQuery("SELECT Count(e) FROM Author e " + clause.toString());

		applyQueryParametersOnQuery(queryParameters, queryCount);

		final Integer count = ((Long) queryCount.getSingleResult()).intValue();

		return new PaginatedData<>(count, authors);
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
