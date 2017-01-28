/**
 * 
 */
package com.library.app.commontests.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Ignore;

/**
 * Base repository test class
 * 
 * @author iulian
 *
 */
@Ignore
public class TestBaseRepository {

	private EntityManagerFactory emf;

	protected EntityManager em;

	protected DbCommandTransactionalExecutor dbCommandExecutor;

	protected void initializeTestDB() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();

		dbCommandExecutor = new DbCommandTransactionalExecutor(em);

	}

	public void closeEntityManager() {
		em.close();
		emf.close();
	}

}
