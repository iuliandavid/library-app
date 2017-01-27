/**
 * 
 */
package com.library.app.category;

import javax.ejb.ApplicationException;

/**
 * Since EJB does not treat unchecked exptions as application exceptions
 * we need to annotate the exception @ApplicationException
 * 
 * This would not have happend if we extended a checked exception
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@ApplicationException
public class CategoryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 546392963115311749L;

}
