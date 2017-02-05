package com.library.app.commontests.books;

import static com.library.app.commontests.book.BookForTestsRepository.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.library.app.book.services.BookServices;

/**
 * Helper endpoint for appending all test books
 * 
 * <pre>
 * POST ALL
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
@Path("/DB/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResourceDB {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BookServices bookServices;

	@POST
	public void addAll() {
		allBooks().forEach(book -> bookServices.add(normalizeDependencies(book, em)));
	}
}
