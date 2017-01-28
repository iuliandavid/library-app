/**
 * 
 */
package com.library.app.author.repository;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.author.model.Author;
import com.library.app.commontests.db.DbCommandTransactionalExecutor;

/**
 * Unit testing for AuthorRepository
 * 
 * @author iulian
 *
 */
public class AuthorRepositoryUTest {

	private EntityManagerFactory emf;

	private EntityManager em;
	private AuthorRepository authorRepository;

	private DbCommandTransactionalExecutor dbCommandTransactionalExecutor;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();
		authorRepository = new AuthorRepository();
		authorRepository.em = em;

		dbCommandTransactionalExecutor = new DbCommandTransactionalExecutor(em);

	}

	@After
	public void closeEntityManager() {
		em.close();
		emf.close();
	}

	@Test
	public void addAuthor() {

		final Long authorAddedId = dbCommandTransactionalExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());
		assertThat(authorAddedId, is(notNullValue()));
	}

	@Test
	public void addAuthorAndFindIt() {

		final Long authorAddedId = dbCommandTransactionalExecutor
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

		final Long authorAddedId = dbCommandTransactionalExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());

		final Author authorAfterAdd = authorRepository.findById(authorAddedId);
		assertThat(authorAfterAdd.getName(), is(equalTo(robertMartin().getName())));

		authorAfterAdd.setName(jamesGrowling().getName());
		final Author authorAfterUpdate = dbCommandTransactionalExecutor
				.executeCommand(() -> authorRepository.update(authorAfterAdd));

		assertThat(authorAfterUpdate.getName(), is(equalTo(jamesGrowling().getName())));

	}

	@Test
	public void findAllAuthors() {
		// Given
		// all categories inserted
		dbCommandTransactionalExecutor.executeCommand(() -> {
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
		final Long authorAddedId = dbCommandTransactionalExecutor
				.executeCommand(() -> authorRepository.add(robertMartin()).getId());
		assertThat(authorRepository.existsById(authorAddedId), is(equalTo(true)));
		assertThat(authorRepository.existsById(999L), is(equalTo(false)));
	}
}
