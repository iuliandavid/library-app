/**
 * 
 */
package com.library.app.category.repository;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

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

	@Test
	public void findAllCategories() {
		// Given
		// all categories inserted
		dbCommandTransactionalExector.executeCommand(() -> {
			allCategories().forEach(categoryRepository::add);
			return null;
		});
		// when
		final List<Category> categories = categoryRepository.findAll("name");
		// then
		assertThat(categories, is(notNullValue()));
		assertThat(categories.size(), is(equalTo(4)));
		// Testing the ordering based on the parameter
		assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
		assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));

	}

	@Test
	public void alreadyExistsForAdd() {
		dbCommandTransactionalExector
				.executeCommand(() -> categoryRepository.add(java()).getId());
		assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
	}
}
