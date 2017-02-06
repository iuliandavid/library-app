/**
 * 
 */
package com.library.app.order.exception;

import javax.ejb.ApplicationException;

/**
 * Since EJB does not treat unchecked exceptions as application exceptions
 * we need to annotate the exception {@link ApplicationException}
 * 
 * This would not have happened if we extended a checked exception
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@ApplicationException
public class OrderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2133792760482435181L;

}
