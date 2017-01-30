/**
 * 
 */
package com.library.app.author.repository;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	@Override
	protected Class<Author> getPersistentClass() {
		return Author.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PaginatedData<Author> findByFilter(final AuthorFilter filter) {
		final StringBuilder clause = new StringBuilder("WHERE e.id is not null");
		final Map<String, Object> queryParameters = new HashMap<>();
		if (filter.getName() != null) {
			clause.append(" AND UPPER(e.name) LIKE UPPER(:name)");
			queryParameters.put("name", "%" + filter.getName() + "%");
		}

		return findByParameters(clause.toString(), filter.getPaginationData(), queryParameters, "name ASC");
	}

}
