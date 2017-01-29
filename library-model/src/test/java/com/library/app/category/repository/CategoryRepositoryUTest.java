/**
 * 
 */
package com.library.app.category.repository;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
import com.library.app.commontests.db.TestBaseRepository;

/**
 * Unit testing for CategoryRepository
 * 
 * @author iulian
 *
 */
public class CategoryRepositoryUTest extends TestBaseRepository {

	private CategoryRepository categoryRepository;

	@Before
	public void initTestCase() {

		initializeTestDB();
		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;

	}

	@After
	public void setDownTestCase() {
		closeEntityManager();
	}

	@Test
	public void addCategory() {

		final Long categoryAddedId = dbCommandExecutor
				.executeCommand(() -> categoryRepository.add(java()).getId());
		assertThat(categoryAddedId, is(notNullValue()));
	}

	@Test
	public void findAddedCategory() {

		final Long categoryAddedId = dbCommandExecutor
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

		final Long categoryAddedId = dbCommandExecutor
				.executeCommand(() -> categoryRepository.add(java()).getId());

		final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		final Category categoryAfterUpdate = dbCommandExecutor
				.executeCommand(() -> categoryRepository.update(categoryAfterAdd));

		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));

	}

	@Test
	public void findAllCategories() {
		// Given
		// all categories inserted
		dbCommandExecutor.executeCommand(() -> {
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

	@Test(expected = NullPointerException.class)
	public void findAllCategoriesNullOrderFieldThrowsError() {
		categoryRepository.findAll(null);

	}

	@Test
	public void alreadyExistsForAdd() {
		dbCommandExecutor
				.executeCommand(() -> categoryRepository.add(java()).getId());
		assertThat(categoryRepository.alreadyExists("name", java().getName(), java().getId()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists("name", cleanCode().getName(), cleanCode().getId()),
				is(equalTo(false)));
	}

	@Test
	public void alreadyExistsCategoryWithId() {
		final Category java = dbCommandExecutor
				.executeCommand(() -> {
					categoryRepository.add(cleanCode());
					return categoryRepository.add(java());
				});

		assertThat(categoryRepository.alreadyExists("name", java.getName(), java.getId()), is(equalTo(false)));

		java.setName(cleanCode().getName());
		assertThat(categoryRepository.alreadyExists("name", java.getName(), java.getId()), is(equalTo(true)));

		java.setName(networks().getName());
		assertThat(categoryRepository.alreadyExists("name", java.getName(), java.getId()), is(equalTo(false)));
	}

	@Test
	public void existsById() {
		final Long categoryAddedId = dbCommandExecutor
				.executeCommand(() -> categoryRepository.add(java()).getId());
		assertThat(categoryRepository.existsById(categoryAddedId), is(equalTo(true)));
		assertThat(categoryRepository.existsById(999L), is(equalTo(false)));
	}
}
