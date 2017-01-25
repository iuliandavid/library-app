/**
 * 
 */
package com.library.app.category.commontests.db;

/**
 * An interface for providing the commands executed within a transaction
 * Will act as a {@link FunctionalInterface}
 * 
 * @author iulian
 *
 */
public interface DBCommand<T> {

	T execute();
}
