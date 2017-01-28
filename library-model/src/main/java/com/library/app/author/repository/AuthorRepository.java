/**
 * 
 */
package com.library.app.author.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.repository.GenericRepository;

/**
 * @author iulian
 *
 */
@Stateless
public class AuthorRepository extends GenericRepository<Author> {

	@PersistenceContext
	EntityManager em;

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

	@Override
	protected Class<Author> getPersistentClass() {
		return Author.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
