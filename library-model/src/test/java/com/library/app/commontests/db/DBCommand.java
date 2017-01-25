/**
 * 
 */
package com.library.app.commontests.db;

/**
 * An interface for providing the commands executed within a transaction
 * Will act as a {@link FunctionalInterface}
 * 
 * @author iulian
 *
 */
@FunctionalInterface
public interface DBCommand<T> {

	T execute();
}
