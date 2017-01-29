/**
 * 
 */
package com.library.app.common.exception;

import javax.ejb.ApplicationException;

/**
 * Since EJB does not treat unchecked exptions as application exceptions
 * we need to annotate the exception @ApplicationException
 * 
 * This would not have happened if we extended a checked exception
 * 
 * @author Iulian David david.iulian@gmail.com
 *
 */
@ApplicationException
public class FieldNotValidException extends RuntimeException {

	private static final long serialVersionUID = 4525821332583716666L;

	private final String fieldName;

	public FieldNotValidException(final String fieldName, final String message) {
		super(message);
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return "FieldNotValidException [fieldName=" + fieldName + "]";
	}

}
