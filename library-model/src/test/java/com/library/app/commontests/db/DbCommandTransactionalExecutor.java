/**
 * 
 */
package com.library.app.commontests.db;

import javax.persistence.EntityManager;

import org.junit.Ignore;

/**
 * Initializes a transaction and executes the {@link DBCommand} passed to it
 * 
 * @author iulian
 *
 */
@Ignore
public class DbCommandTransactionalExecutor {

	/**
	 * The entity manager in which transaction will be performed
	 */
	private EntityManager em;

	public DbCommandTransactionalExecutor(final EntityManager em) {
		this.em = em;
	}

	public <T> T executeCommand(final DBCommand<T> dbCommand) {
		try {
			em.getTransaction().begin();
			final T toReturn = dbCommand.execute();
			em.getTransaction().commit();
			em.clear();
			return toReturn;
		} catch (final Exception e) {
			em.getTransaction().rollback();
			throw new IllegalStateException(e);
		}
	}
}
