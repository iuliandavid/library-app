/**
 * 
 */
package com.library.app.commontests.db;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Ignore;

import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.order.model.Order;
import com.library.app.user.model.User;

@Stateless
@Ignore
public class TestBulkDBOperationsEJB {

	@PersistenceContext
	EntityManager em;

	private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(Order.class, Book.class, Category.class,
			Author.class,
			User.class);

	public void deleteAll() {
		for (final Class<?> entityClass : ENTITIES_TO_REMOVE) {
			deleteAllEntityRecords(entityClass);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void deleteAllEntityRecords(final Class<?> entityClass) {

		final List<Object> rows = em.createQuery("SELECT e from " + entityClass.getSimpleName() + " e").getResultList();
		for (final Object row : rows) {
			em.remove(row);
		}

	}

}
