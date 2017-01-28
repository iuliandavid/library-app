[![Build Status](https://travis-ci.org/iuliandavid/library-app.svg?branch=master)](https://travis-ci.org/iuliandavid/library-app)

###library-app
Implementation of Udemy's Build An Application From Scratch: jee-7-java-8-and-wildfly



###CONFIG
## I modified the libraries versions because:
1. The hibernate version did not compile, as a consequence I used the 4.3.11.Final version

	```
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-entitymanager</artifactId>
			<version>4.3.11.Final</version>
			<scope>test</scope>
		</dependency>
	```
2. All the other libraries are put up to date

Since I am using WildFly 10.1.0.Final there are some slightly changes to be made so that the tutorial will run:
## The standalone-full.xml used in Arquillian Integration Tests(module library-int-tests):
	
The file will be copied from the WildFly kit from [http://wildfly.org/downloads/](http://wildfly.org/downloads/), It will be unzipped and the file standalone-full.xml from [wildfly-dir]/standalone/configuration will be copied to [library-int-tests]/src/test/resources/
	
	```
	tar xzvf wildfly-10.1.0.Final.tar.gz
	cd wildfly-10.1.0.Final
	cp standalone/configuration/standalone-full.xml /path_of_library_app_project/library-int-tests/src/test/resources/
	```
	
## The WildFly server config was made through the jboss_cli(since all of my configs, xml copies, didn't work)
Notation used: POSTGRESQL_JDBC_JAR_PATH : the path were the Postgresql JDBC jar is located(intentionally i left one in the utils directory)
So got to your unzipped WildFly instance

	cd [wildfly-dir]
	
1. Start the WildFly server witl the **standalone-full.xml** configuration file 

	```sh
	cd bin
	./standalone.sh -c=standalone-full.xml
	```
2. In another terminal(command-promt, shell..) login into the CLI(command-line-interface) of WildFly from [wildfly-dir]/bin:

	```sh
	./jboss-cli.sh 
	```
	
-- Now in CLI, you must see something like :  
	
	You are disconnected at the moment. Type 'connect' to connect to the server or 'help' for the list of supported commands.
	
	
	```
	connect
	```
	
	```
	module add --name=org.postgresql --resources=/POSTGRESQL_JDBC_JAR_PATH/postgresql-9.4-1206-jdbc42.jar --dependencies=javax.api,javax.transaction.api
	```
	
-- Add the driver in WildFly datasource drivers

	```
	/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
	```			
		
-- You must see something like :
		
		
		{"outcome" => "success"}
		
-- Then you add your datasource 
		
	```		
	data-source add --name=library-pool --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/library --jndi-name=java:jboss/datasources/library --user-name=postgres --password=postgres --jta=true --use-java-context=true --transaction-isolation=TRANSACTION_READ_COMMITTED --min-pool-size=5 --max-pool-size=10 --pool-prefill=true --pool-use-strict-min=false --flush-strategy=FailingConnectionOnly  --validate-on-match=true --background-validation=false --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter --enabled=true
	```	
	
-- If all is OK you should see:

	<subsystem xmlns="urn:jboss:domain:datasources:4.0">
            <datasources>
                <datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE</connection-url>
                    <driver>h2</driver>
                    <security>
                        <user-name>sa</user-name>
                        <password>sa</password>
                    </security>
                </datasource>
                <datasource jta="true" jndi-name="java:jboss/datasources/library" pool-name="library-pool" enabled="true" use-java-context="true">
                    <connection-url>jdbc:postgresql://localhost:5432/library</connection-url>
                    <driver>postgresql</driver>
                    <security>
                        <user-name>postgres</user-name>
                        <password>postgres</password>
                    </security>
                    <validation>
                        <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker"/>
                        <validate-on-match>true</validate-on-match>
                        <background-validation>false</background-validation>
                        <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"/>
                    </validation>
                </datasource>
                <drivers>
                    <driver name="h2" module="com.h2database.h2">
                        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
                    </driver>
                    <driver name="postgresql" module="org.postgresql">
                        <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
                    </driver>
                </drivers>
            </datasources>
        </subsystem>
        
	
	
	
