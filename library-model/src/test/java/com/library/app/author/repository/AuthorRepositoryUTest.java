/**
 * 
 */
package com.library.app.author.repository;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.model.filter.PaginationData;
import com.library.app.common.model.filter.PaginationData.OrderMode;
import com.library.app.commontests.db.TestBaseRepository;

/**
 * Unit testing for AuthorRepository
 * 
 * @author iulian
 *
 */
public class AuthorRepositoryUTest extends TestBaseRepository {

	private AuthorRepository authorRepository;

	@Before
	public void initTestCase() {

		initializeTestDB();
		authorRepository = new AuthorRepository();
		authorRepository.em = em;

	}

	@After
	public void setDownTestCase() {
		closeEntityManager();
	}

	@Test
	public void addAuthor() {

		final Long authorAddedId = dbCommandExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());
		assertThat(authorAddedId, is(notNullValue()));
	}

	@Test
	public void addAuthorAndFindIt() {

		final Long authorAddedId = dbCommandExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());

		final Author author = authorRepository.findById(authorAddedId);
		assertThat(author.getName(), is(equalTo(robertMartin().getName())));
	}

	@Test
	public void findAuthorByIdNotFound() {

		final Author author = authorRepository.findById(999L);
		assertThat(author, is(nullValue()));
	}

	@Test
	public void findAuthorByIdWithNullId() {

		final Author author = authorRepository.findById(null);
		assertThat(author, is(nullValue()));
	}

	@Test
	public void updateAuthor() {

		final Long authorAddedId = dbCommandExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());

		final Author authorAfterAdd = authorRepository.findById(authorAddedId);
		assertThat(authorAfterAdd.getName(), is(equalTo(robertMartin().getName())));

		authorAfterAdd.setName(jamesGrowling().getName());
		final Author authorAfterUpdate = dbCommandExecutor
				.executeCommand(() -> authorRepository.update(authorAfterAdd));

		assertThat(authorAfterUpdate.getName(), is(equalTo(jamesGrowling().getName())));

	}

	@Test
	public void findAllAuthors() {
		// Given
		// all categories inserted
		dbCommandExecutor.executeCommand(() -> {
			allAuthors().forEach(authorRepository::add);
			return null;
		});
		// when
		final List<Author> authors = authorRepository.findAll("name");
		// then
		assertThat(authors, is(notNullValue()));
		assertThat(authors.size(), is(equalTo(allAuthors().size())));

		final List<Author> expectedOrderAuthors = allAuthors();
		expectedOrderAuthors.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

		for (int i = 0; i < expectedOrderAuthors.size(); i++) {
			final Author expectedAuthor = expectedOrderAuthors.get(i);
			assertThat(authors.get(i).getName(),
					is(equalTo(expectedAuthor.getName())));
		}

	}

	@Test(expected = NullPointerException.class)
	public void findAllAuthorsNullOrderFieldThrowsError() {
		authorRepository.findAll(null);

	}

	@Test
	public void existsById() {
		final Long authorAddedId = dbCommandExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());
		assertThat(authorRepository.existsById(authorAddedId), is(equalTo(true)));
		assertThat(authorRepository.existsById(999L), is(equalTo(false)));
	}

	@Test
	public void findFilterNoFilter() {
		loadDataForFindFilter();
		final PaginatedData<Author> authors = authorRepository.findByFilter(new AuthorFilter());
		assertThat(authors.getNumberOfRows(), is(equalTo(4)));
		assertThat(authors.getRows().size(), is(equalTo(4)));
		assertThat(authors.getRow(0).getName(), is(equalTo(erichGamma().getName())));
		assertThat(authors.getRow(1).getName(), is(equalTo(jamesGrowling().getName())));
		assertThat(authors.getRow(2).getName(), is(equalTo(martinFowler().getName())));
		assertThat(authors.getRow(3).getName(), is(equalTo(robertMartin().getName())));
	}

	@Test
	public void findFilterFilteringByNameAndPaginatingAndOrderingDescending() {
		loadDataForFindFilter();

		final AuthorFilter authorFilter = new AuthorFilter();
		authorFilter.setName("o");
		authorFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.DESCENDING));
		PaginatedData<Author> authors = authorRepository.findByFilter(authorFilter);
		assertThat(authors.getNumberOfRows(), is(equalTo(3)));
		assertThat(authors.getRows().size(), is(equalTo(2)));
		assertThat(authors.getRow(0).getName(), is(equalTo(robertMartin().getName())));
		assertThat(authors.getRow(1).getName(), is(equalTo(martinFowler().getName())));

		authorFilter.setPaginationData(new PaginationData(2, 2, "name", OrderMode.DESCENDING));
		authors = authorRepository.findByFilter(authorFilter);
		assertThat(authors.getNumberOfRows(), is(equalTo(3)));
		assertThat(authors.getRows().size(), is(equalTo(1)));
		assertThat(authors.getRow(0).getName(), is(equalTo(jamesGrowling().getName())));

	}

	private void loadDataForFindFilter() {
		dbCommandExecutor.executeCommand(() -> {
			authorRepository.add(robertMartin());
			authorRepository.add(jamesGrowling());
			authorRepository.add(martinFowler());
			authorRepository.add(erichGamma());
			return null;
		});

	}
}
