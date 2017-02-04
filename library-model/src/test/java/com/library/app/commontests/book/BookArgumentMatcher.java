/**
 * 
 */
package com.library.app.commontests.book;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.ArgumentMatcher;

import com.library.app.book.model.Book;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public class BookArgumentMatcher implements ArgumentMatcher<Book> {
	private Book expectedBook;

	/**
	 * @param expectedBook
	 */
	public BookArgumentMatcher(final Book expectedBook) {
		this.expectedBook = expectedBook;
	}

	public static Book bookEq(final Book expectedBook) {
		return argThat(new BookArgumentMatcher(expectedBook));
	}

	@Override
	public boolean matches(final Book actualBook) {
		assertThat(actualBook.getId(), is(equalTo(expectedBook.getId())));
		assertThat(actualBook.getTitle(), is(equalTo(expectedBook.getTitle())));
		assertThat(actualBook.getDescription(), is(equalTo(expectedBook.getDescription())));
		assertThat(actualBook.getPrice(), is(equalTo(expectedBook.getPrice())));
		assertThat(actualBook.getCategory(), is(equalTo(expectedBook.getCategory())));
		assertThat(actualBook.getAuthors(), is(equalTo(expectedBook.getAuthors())));
		return true;
	}
}
