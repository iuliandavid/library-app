package com.library.app.user.repository;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.library.app.common.model.PaginatedData;
import com.library.app.common.repository.GenericRepository;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;

/**
 * @author iulian
 *
 */
@Stateless
public class UserRepository extends GenericRepository<User> {

	@PersistenceContext
	EntityManager em;

	@Override
	protected Class<User> getPersistentClass() {
		return User.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public boolean alreadyExists(final User user) {
		return alreadyExists("email", user.getEmail(), user.getId());
	}

	public User findByEmail(final String email) {
		try {
			return (User) em.createQuery("SELECT e FROM User  e WHERE e.email = :email")
					.setParameter("email", email)
					.getSingleResult();
		} catch (final NoResultException e) {
			return null;
		}
	}

	public PaginatedData<User> findByFilter(final UserFilter filter) {
		final StringBuilder clause = new StringBuilder("WHERE e.id is not null");
		final Map<String, Object> queryParameters = new HashMap<>();
		if (filter.getName() != null) {
			clause.append(" AND UPPER(e.name) LIKE UPPER(:name)");
			queryParameters.put("name", "%" + filter.getName() + "%");
		}
		if (filter.getUserType() != null) {
			clause.append(" AND e.userType = :userType");
			queryParameters.put("userType", filter.getUserType());
		}

		return findByParameters(clause.toString(), filter.getPaginationData(), queryParameters, "name ASC");
	}
}
