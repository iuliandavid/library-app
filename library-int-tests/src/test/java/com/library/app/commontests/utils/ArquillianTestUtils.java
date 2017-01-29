/**
 * 
 */
package com.library.app.commontests.utils;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * @author iulian
 *
 */

public class ArquillianTestUtils {

	public static WebArchive createDeploymentArchive() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addPackages(true, "com.library.app")
				.addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/test/resources/web.xml"))
				.addAsLibraries(
						Maven.resolver().resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:2.6.8")
								.withTransitivity().asFile());
	}

}