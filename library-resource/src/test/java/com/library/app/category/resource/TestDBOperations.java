/**
 * 
 */
package com.library.app.category.resource;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

import com.library.app.commontests.db.TestBulkDBOperationsRepository;

/**
 * A helper class for accessin bulk operations on database
 * Example: DELETE ALL records
 * 
 * @author iulian
 *
 */
@Path("/DB")
public class TestDBOperations {

	@Inject
	TestBulkDBOperationsRepository testBulkDBServices;

	@DELETE
	public void deleteAll() {
		testBulkDBServices.deleteAll();
	}
}
