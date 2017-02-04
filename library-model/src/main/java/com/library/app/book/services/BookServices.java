/**
 * 
 */
package com.library.app.book.services;

import javax.ejb.Local;

import com.library.app.author.exception.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.book.exception.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.filter.BookFilter;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.model.PaginatedData;

/**
 * The contract for the {@link Author} services
 * 
 * It will not be a remote call, that's why it'a annotated as {@link Local}
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Local
public interface BookServices {
	Book add(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException;

	void update(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException,
			BookNotFoundException;

	Book findById(Long id) throws BookNotFoundException;

	PaginatedData<Book> findByFilter(BookFilter bookFilter);
}
