/**
 * 
 */
package com.library.app.common.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds custom headers to responses so that we won't receive
 * a CORS exception
 * 
 * @author iulian
 *
 */
@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		logger.debug("Running CORS filter.....");
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().add("Access-Control-Allow-Headers",
				"Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
		responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
		responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
	}

}
