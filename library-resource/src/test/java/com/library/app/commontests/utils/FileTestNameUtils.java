/**
 * 
 */
package com.library.app.commontests.utils;

import org.junit.Ignore;

/**
 * Helper class for file operations
 * 
 * @author iulian
 *
 */
@Ignore
public class FileTestNameUtils {

	private static final String PATH_REQUEST = "/request/";
	private static final String PATH_RESPONSE = "/response/";

	/**
	 * Constructor set to private so that all the static methods
	 * will be correctly invoked (not trying to create an instance and then execute the static methods)
	 */
	private FileTestNameUtils() {

	}

	/**
	 * Builds the request file path
	 * 
	 * @param mainFolder
	 *            - folder name, eg: <code>Category</b>
	 * @param fileName
	 *            - the JSON file name
	 * @return - the relative path that file
	 **/
	public static String getPathFileRequest(final String mainFolder, final String fileName) {
		return mainFolder + PATH_REQUEST + fileName;
	}

	/**
	 * Builds the response file path
	 * 
	 * @param mainFolder
	 *            - folder name, eg: <code>Category</b>
	 * @param fileName
	 *            - the JSON file name
	 * @return - the relative path that file
	 **/
	public static String getPathFileResponse(final String mainFolder, final String fileName) {
		return mainFolder + PATH_RESPONSE + fileName;
	}
}
