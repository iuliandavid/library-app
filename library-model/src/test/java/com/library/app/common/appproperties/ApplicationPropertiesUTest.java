/**
 * 
 */
package com.library.app.common.appproperties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author iulian
 *
 */
public class ApplicationPropertiesUTest {

	private ApplicationProperties applicationProperties;

	@Before
	public void setUpTest() {
		applicationProperties = new ApplicationProperties();
		applicationProperties.init();
	}

	@Test
	public void getDaysBeforeExpiration() {
		assertThat(applicationProperties.getDaysBeforeOrderExpiration(), is(equalTo(7)));
	}
}
