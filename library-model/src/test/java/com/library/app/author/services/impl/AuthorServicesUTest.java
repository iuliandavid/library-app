/**
 * 
 */
package com.library.app.author.services.impl;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.filter.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;

/**
 * Unit Tests for Author Services
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class AuthorServicesUTest {

	private AuthorServices authorServices;

	/** Using a default validator **/
	private Validator validator;

	/** The mockable concrete class **/
	private AuthorRepository authorRepository;

	@Before
	public void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		authorRepository = mock(AuthorRepository.class);
		authorServices = new AuthorServicesImpl();
		((AuthorServicesImpl) authorServices).validator = validator;
		((AuthorServicesImpl) authorServices).authorRepository = authorRepository;
	}

	@Test
	public void addAuthorWithNullName() {
		try {
			addAuthorWithInvalidName(null);
		} catch (final FieldNotValidException e) {
			// Test the custom message set to @NutNull validation on Author model
			assertThat(e.getMessage(), is(equalTo("Author Name cannot be null")));
		}

	}

	@Test
	public void addAuthorWithShortName() {
		try {
			addAuthorWithInvalidName("A");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 40")));
		}

	}

	@Test
	public void addAuthorWithInvalidName() {
		try {
			addAuthorWithInvalidName("This is a long name that will cause an exception");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 40")));
		}

	}

	/**
	 * Test the {@link AuthorServices#add} method
	 * by mocking the result of {@link authorRepository#add(Category)}
	 */
	@Test
	public void addValidAuthor() {

		when(authorRepository.add(robertMartin())).thenReturn(authorWithID(robertMartin(), 1L));
		final Author authorAdded = authorServices.add(robertMartin());
		assertThat(authorAdded.getId(), is(equalTo(1l)));
	}

	private void addAuthorWithInvalidName(final String name) {
		try {
			authorServices.add(new Author(name));
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
			throw e;

		}

	}

	////// Updates

	@Test
	public void updateAuthorWithNullName() {
		try {
			updateAuthorWithInvalidName(null);
		} catch (final FieldNotValidException e) {
			// Test the custom message set to @NutNull validation on Category model
			assertThat(e.getMessage(), is(equalTo("Author Name cannot be null")));
		}

	}

	@Test
	public void updateAuthorWithShortName() {
		try {
			updateAuthorWithInvalidName("A");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 40")));
		}

	}

	@Test
	public void updateAuthorWithLongName() {
		try {
			updateAuthorWithInvalidName("This is a long name that will cause an exception");
		} catch (final FieldNotValidException e) {
			assertThat(e.getMessage(), is(equalTo("size must be between 2 and 40")));
		}

	}

	@Test(expected = AuthorNotFoundException.class)
	public void updateCategoryNotFound() {
		when(authorRepository.existsById(1l)).thenReturn(false);
		authorServices.update(authorWithID(robertMartin(), 1l));
	}

	@Test
	public void updateValidAuthor() {
		when(authorRepository.existsById(1l)).thenReturn(true);
		authorServices.update(authorWithID(robertMartin(), 1l));

		// verify that authorRepository.update was invoked
		verify(authorRepository).update(authorWithID(robertMartin(), 1l));
	}

	private void updateAuthorWithInvalidName(final String name) {
		try {
			authorServices.update(new Author(name));
			fail("An error should be thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

	/// Test findByID
	@Test
	public void findAuthorById() {
		final Author mockedAuthor = authorWithID(robertMartin(), 1L);
		when(authorRepository.findById(1l)).thenReturn(mockedAuthor);

		final Author author = authorServices.findById(1l);
		assertThat(author, is(notNullValue()));
		assertThat(author.getId(), is(equalTo(mockedAuthor.getId())));
		assertThat(author.getName(), is(equalTo(mockedAuthor.getName())));
	}

	@Test(expected = AuthorNotFoundException.class)
	public void findAuthorByIdNotFound() {
		when(authorRepository.findById(1l)).thenReturn(null);

		authorServices.findById(1l);

	}

	public void findAuthorByFilter() {
		final PaginatedData<Author> authors = new PaginatedData<>(1, Arrays.asList(authorWithID(robertMartin(), 1L)));
		when(authorRepository.findByFilter((AuthorFilter) any())).thenReturn(authors);
		final PaginatedData<Author> authorsReturned = authorServices.findByFilter(new AuthorFilter());

		assertThat(authorsReturned.getNumberOfRows(), is(equalTo(1)));
		assertThat(authorsReturned.getRow(0).getName(), is(equalTo(robertMartin().getName())));
	}

	/// Test findAll

	@Test
	public void findAllNoAuthors() {
		when(authorRepository.findAll("name")).thenReturn(new ArrayList<>());
		final List<Author> authors = authorServices.findAll();
		assertThat(authors.isEmpty(), is(equalTo(true)));
	}

	@Test
	public void findAllAuthors() {
		when(authorRepository.findAll("name"))
				.thenReturn(Arrays.asList(authorWithID(robertMartin(), 1l), authorWithID(johnBrandt(), 2l)));
		final List<Author> authors = authorServices.findAll();
		assertThat(authors.size(), is(equalTo(2)));
		assertThat(authors.get(0).getName(), is(equalTo(robertMartin().getName())));
		assertThat(authors.get(1).getName(), is(equalTo(johnBrandt().getName())));
	}
}
