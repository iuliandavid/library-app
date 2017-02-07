/**
 * 
 */
package com.library.app.common.exception;

import javax.ejb.ApplicationException;

/**
 * @author iulian
 *
 */
@ApplicationException
public class UserNotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
