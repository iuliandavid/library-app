/**
 * 
 */
package com.library.app.author.repository;

import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.library.app.author.model.Author;

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
}
