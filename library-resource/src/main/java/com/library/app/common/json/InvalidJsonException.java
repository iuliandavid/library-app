/**
 * 
 */
package com.library.app.common.json;

/**
 * Parsing JSON exception class
 * 
 * @author iulian
 *
 */
public class InvalidJsonException extends RuntimeException {

	private static final long serialVersionUID = -5475873453184659708L;

	public InvalidJsonException(final String message) {
		super(message);
	}

	public InvalidJsonException(final Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
