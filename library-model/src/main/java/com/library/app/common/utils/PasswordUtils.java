/**
 * 
 */
package com.library.app.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Iulian David david.iulian@gmail.com
 *
 */
public final class PasswordUtils {

	private PasswordUtils() {
	}

	/**
	 * Encrypts a String to <code>SHA-256</code> and then
	 * encodes it with {@link Base64}
	 * 
	 * @param password
	 * @return a Base64 encoded SHA-256 encrypted password
	 */
	public static String encryptPassword(final String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		md.update(password.getBytes());
		return Base64.getMimeEncoder().encodeToString(md.digest());
	}

}
