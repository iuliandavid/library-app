/**
 * 
 */
package com.library.app.category.repository;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing for CategoryRepository
 * 
 * @author iulian
 *
 */
public class CategoryRepositoryUTest {

	private EntityManagerFactory emf;

	private EntityManager em;
	private CategoryRepository categoryRepository;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();
		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;
	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addCategoryAndFindIt() {
		try {
			em.getTransaction().begin();
			final Long categoryAddedId = categoryRepository.add(java()).getId();
			assertThat(categoryAddedId, is(notNullValue()));
			em.getTransaction().commit();
			em.clear();
		} catch (final Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
	}

}
