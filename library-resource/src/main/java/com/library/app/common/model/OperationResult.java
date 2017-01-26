/**
 * 
 */
package com.library.app.common.model;

/**
 * Model class for webservice response
 * 
 * @author iulian
 *
 */
public class OperationResult {

	private boolean success;

	private String errorIdentification;

	private String errorDescription;

	private Object entity;

	/**
	 * Constructor for building an error response
	 * 
	 * @param errorIdentification
	 * @param errorDescription
	 */
	private OperationResult(final String errorIdentification, final String errorDescription) {
		this.success = false;
		this.errorIdentification = errorIdentification;
		this.errorDescription = errorDescription;
	}

	/**
	 * Constructor for building a success response
	 * 
	 * @param entity
	 *            - response body
	 */
	private OperationResult(final Object entity) {
		this.success = false;
		this.entity = entity;
	}

	/**
	 * Method factory for a successful response
	 * Builds an instance with success constructor
	 * 
	 * @see #OperationResult(Object)
	 * 
	 * @param entity
	 *            - the message associated with the response
	 * @return
	 */
	public static OperationResult success(final Object entity) {
		return new OperationResult(entity);
	}

	/**
	 * Method factory for a successful response
	 * Builds an instance with success constructor
	 * 
	 * @see #OperationResult(Object)
	 * 
	 *      It does not contain any associated message
	 * @return
	 */
	public static OperationResult success() {
		return new OperationResult(null);
	}

	/**
	 * Method factory for an error response
	 * Builds an instance with error constructor
	 * 
	 * @see #OperationResult(String, String)
	 * 
	 * @param errorIdentification
	 *            - The id of the error occurred
	 * @param errorDescription
	 *            - A description of the error
	 * @return
	 */
	public static OperationResult error(final String errorIdentification, final String errorDescription) {
		return new OperationResult(errorIdentification, errorDescription);
	}

	public String getErrorIdentification() {
		return errorIdentification;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public Object getEntity() {
		return entity;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public String toString() {
		return "OperationResult [success=" + success + ", errorIdentification=" + errorIdentification
				+ ", errorDescription=" + errorDescription + ", entity=" + entity + "]";
	}

}
