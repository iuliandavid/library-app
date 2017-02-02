/**
 * 
 */
package com.library.app.category.exception;

import javax.ejb.ApplicationException;

/**
 * Since EJB does not treat unchecked exceptions as application exceptions
 * we need to annotate the exception @ApplicationException
 * 
 * This would not have happened if we extended a checked exception
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@ApplicationException
public class CategoryExistentException extends RuntimeException {

	private static final long serialVersionUID = -2471998894249943457L;

}
