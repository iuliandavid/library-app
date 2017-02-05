/**
 * 
 */
package com.library.app.common.model.filter;

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
public class FilterValidationException extends RuntimeException {

	private static final long serialVersionUID = -2828040078659508401L;

}
