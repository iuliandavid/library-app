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

import com.library.app.category.commontests.db.DbCommandTransactionalExector;
import com.library.app.category.model.Category;

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

	private DbCommandTransactionalExector dbCommandTransactionalExector;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();
		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;

		dbCommandTransactionalExector = new DbCommandTransactionalExector(em);

	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addCategory() {

		final Long categoryAddedId = dbCommandTransactionalExector
				.executeCommand(() -> categoryRepository.add(java()).getId());
		assertThat(categoryAddedId, is(notNullValue()));
	}

	@Test
	public void findAddedCategory() {

		final Long categoryAddedId = dbCommandTransactionalExector
				.executeCommand(() -> categoryRepository.add(java()).getId());

		final Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test
	public void findCategoryByIdNotFound() {

		final Category category = categoryRepository.findById(999L);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void findCategoryByIdWithNullId() {

		final Category category = categoryRepository.findById(null);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void updateCategory() {

		final Long categoryAddedId = dbCommandTransactionalExector
				.executeCommand(() -> categoryRepository.add(java()).getId());

		final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		final Category categoryAfterUpdate = dbCommandTransactionalExector
				.executeCommand(() -> categoryRepository.update(categoryAfterAdd));

		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));

	}
}
