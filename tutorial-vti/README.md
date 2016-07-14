#Overview
This tutorial contains code for a custom Virtual table Interface (VTI) which reads data from a properties file and
returns it as a table.

#How to run the code
1.  Pull the source code from github
2.  Run: mvn clean compile package
3.  Copy the ./target/splice-tutorial-vti-2.0.jar to each of your servers' /opt/splice/default/lib/ directory
4.  Restart the HBase Master and Region Servers
5.  Start the splice command prompt
6.  Create the table virtual table definition.  Run the following scripts:
		- /src/main/resources/ddl/create-table-functions.sql
7.  There are two ways that the VTI can be called.  The first is using a TABLE FUNCTION syntax: select * from table (propertiesFile('sample.properties')) b;
8.  The other is using a VTI syntax.  Using the table function syntax:
		
		select * from new com.splicemachine.tutorials.vti.PropertiesFileVTI('sample.properties') as b (KEY_NAME VARCHAR(20), VALUE VARCHAR(100));
		
## Notes
The current build process is setup to be compiled using a pre-released SNAPSHOT version of splice machine.  Please update the pom.xml file accordingly when you compile this code.
		
