package com.library.app.commontests.utils;

import org.junit.Ignore;

/**
 * Contains all the REST paths
 * used in tests
 * @author Iulian David david.iulian@gmail.com
 *
 */
@Ignore
public enum ResourceDefinitions {
	CATEGORY("categories");
	
	private String resourceName;
	
	private ResourceDefinitions(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public String getResourceName() {
		return resourceName;
	}
	
}