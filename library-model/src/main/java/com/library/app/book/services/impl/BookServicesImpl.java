/**
 * 
 */
package com.library.app.book.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.services.AuthorServices;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.book.repository.BookRepository;
import com.library.app.book.services.BookServices;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;
import com.library.app.common.utils.ValidationUtils;

/**
 * Since there is a pool of {@link Stateless} ejbs is more useful and scalable to use the {@link Stateless} instead of
 * {@link Stateful} EJB
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
public class BookServicesImpl implements BookServices {

	/** Will be used by container's own Validator implementation **/
	@Inject
	public Validator validator;

	@Inject
	BookRepository bookRepository;

	@Inject
	CategoryServices categoryServices;

	@Inject
	AuthorServices authorServices;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.book.services.BookServices#add(com.library.app.book.model.Book)
	 */
	@Override
	public Book add(final Book book) {
		ValidationUtils.validateEntityFields(validator, book);

		checkCategoryAndSetItOnBook(book);
		checkAuthorsAndSetThemOnBook(book);

		return bookRepository.add(book);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.book.services.BookServices#update(com.library.app.book.model.Book)
	 */
	@Override
	public void update(final Book book)
			throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException, BookNotFoundException {

		ValidationUtils.validateEntityFields(validator, book);

		if (!bookRepository.existsById(book.getId())) {
			throw new BookNotFoundException();
		}

		checkCategoryAndSetItOnBook(book);
		checkAuthorsAndSetThemOnBook(book);

		bookRepository.update(book);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.book.services.BookServices#findById(java.lang.Long)
	 */
	@Override
	public Book findById(final Long id) throws BookNotFoundException {
		final Book book = bookRepository.findById(id);
		if (book == null) {
			throw new BookNotFoundException();
		}
		return book;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.library.app.book.services.BookServices#findByFilter(com.library.app.book.model.filter.BookFilter)
	 */
	@Override
	public PaginatedData<Book> findByFilter(final BookFilter bookFilter) {
		return bookRepository.findByFilter(bookFilter);
	}

	private void checkAuthorsAndSetThemOnBook(final Book book) {
		final List<Author> newAuthorList = new ArrayList<>();
		book.getAuthors().forEach(author -> {
			final Author authorExistent = authorServices.findById(author.getId());
			newAuthorList.add(authorExistent);
		});

		book.setAuthors(newAuthorList);
	}

	private void checkCategoryAndSetItOnBook(final Book book) {
		final Category category = categoryServices.findById(book.getCategory().getId());
		book.setCategory(category);
	}

}
