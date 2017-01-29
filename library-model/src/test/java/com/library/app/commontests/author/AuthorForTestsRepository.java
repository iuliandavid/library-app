/**
 * 
 */
package com.library.app.commontests.author;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;

import com.library.app.author.model.Author;

/**
 * Helper class for authors testing
 * Creates {@link Author} objects that will be used in tests
 * 
 * @author iulian
 *
 */
@Ignore
public class AuthorForTestsRepository {

	private AuthorForTestsRepository() {

	}

	public static Author robertMartin() {
		return new Author("Robert Martin");
	}

	public static Author jamesGosling() {
		return new Author("James Gosling");
	}

	public static Author martinFowler() {
		return new Author("Martin Fowler");
	}

	public static Author erichGamma() {
		return new Author("Erich Gamma");
	}

	public static Author richardHelm() {
		return new Author("Richard Helm");
	}

	public static Author ralphJohnson() {
		return new Author("Ralph Johnson");
	}

	public static Author johnVlissides() {
		return new Author("John Vlissides");
	}

	public static Author kentBeck() {
		return new Author("Kent Beck");
	}

	public static Author johnBrandt() {
		return new Author("John Brandt");
	}

	public static Author williamOpdyke() {
		return new Author("William Opdyke");
	}

	public static Author joshuaBlock() {
		return new Author("Joshua Block");
	}

	public static Author donRoberts() {
		return new Author("Don Roberts");
	}

	public static List<Author> allAuthors() {
		return Arrays.asList(robertMartin(), jamesGosling(), martinFowler(),
				erichGamma(), richardHelm(), ralphJohnson(),
				johnVlissides(), kentBeck(), johnBrandt(),
				williamOpdyke(), joshuaBlock(), donRoberts());
	}

	/**
	 * Generic builder of an {@link Author} instance with id set
	 * 
	 * @return a full {@link Author} with all properties set
	 */
	public static Author authorWithId(final Author author, final Long id) {
		author.setId(id);
		return author;
	}
}
