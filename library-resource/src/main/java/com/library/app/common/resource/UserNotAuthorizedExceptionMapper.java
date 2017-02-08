/**
 * 
 */
package com.library.app.common.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.library.app.common.exception.UserNotAuthorizedException;
import com.library.app.common.model.HttpCode;

/**
 * Create a {@link ExceptionMapper} for a common exception like {@link UserNotAuthorizedException}
 * instead of trying to catch the exception every time it appears
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Provider
public class UserNotAuthorizedExceptionMapper implements ExceptionMapper<UserNotAuthorizedException> {

	@Override
	public Response toResponse(final UserNotAuthorizedException arg0) {
		return Response.status(HttpCode.FORBIDDEN.getCode()).build();
	}

}
