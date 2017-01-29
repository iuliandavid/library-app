/**
 * 
 */
package com.library.app.commontests.authors;

import static com.library.app.commontests.author.AuthorForTestsRepository.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.library.app.author.services.AuthorServices;

/**
 * Helper endpoint for appending all test authors
 * 
 * <pre>
 * POST ALL
 * </pre>
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Stateless
@Path("/DB/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResourceDB {

	@Inject
	private AuthorServices authorServices;

	@POST
	public void addAll() {
		allAuthors().forEach(authorServices::add);
	}
}
