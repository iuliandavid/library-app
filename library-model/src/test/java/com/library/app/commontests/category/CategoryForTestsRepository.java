/**
 * 
 */
package com.library.app.commontests.category;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;

import com.library.app.category.model.Category;

/**
 * Helper class for category testing
 * Creates categories that will be used in tests
 * 
 * @author iulian
 *
 */
@Ignore
public class CategoryForTestsRepository {
	public static Category java() {
		return new Category("Java");
	}

	public static Category cleanCode() {
		return new Category("Clean Code");
	}

	public static Category architecture() {
		return new Category("Architecture");
	}

	public static Category networks() {
		return new Category("Networks");
	}

	public static List<Category> allCategories() {
		return Arrays.asList(java(), cleanCode(), architecture(), networks());
	}

	/**
	 * @return
	 */
	public static Category categoryWithID(final Category category, final Long id) {
		category.setId(1l);
		return category;
	}
}
