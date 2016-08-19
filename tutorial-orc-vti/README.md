#Overview
This tutorial contains code for a custom Virtual table Interface (VTI) which reads data from a properties file and
returns it as a table.

#How to run the code
1.  Pull the source code from github
2.  Run: mvn clean compile package
3.  Copy the ./target/splice-tutorial-orc-vti-2.0.1.18.jar to each of your servers' /opt/splice/default/lib/ directory
4.  The following hive jars are required. If Hive is installed, then add hive/lib to classpath, else copy them to  /opt/splice/default/lib/ directory on each of the servers
	- hive-ant.jar 
	- hive-common.jar 
	- hive-exec.jar 
	- hive-metastore.jar 
	- hive-serde.jar 
	- hive-shims.jar 
	- hive-shims-0.23.jar 
	- hive-shims-common.jar 
	- lib/hive-shims-scheduler.jar 

4.  Restart the HBase Master and Region Servers
5.  Start the splice command prompt
6.  Create table  in splice machine.  Run the following scripts:

	- /src/main/resources/ddl/create-tables.sql
7. Copying the OrcFile to HDFS folder (Note the instructions are for CDH)
	- Copy sample orcfile to server : copy /src/main/resources/sample_file/test_orc_file to  /tmp on one of the region server
	- Create HDFS folder : Run this on the region server  
		- sudo -su hdfs hadoop fs -mkdir /testorcvti
	- Copy the orc file to HDFS : run this command on the same region server 
		- sudo -su hdfs hadoop fs -copyFromLocal /tmp/test_orc_file /testorcvti

8.  Run this command in SQLShell to use VTI to read the OrcFile:
	- SELECT * FROM new com.splicemachine.tutorials.vti.SpliceORCFileVTI(
  '/testorcvti/test_orc_file') AS b
   (id bigint, name VARCHAR(10), activity char(1), is_fte boolean,
	test_tinyint smallint, test_smallint smallint, test_int int, test_float float,
	test_double double, test_decimal decimal(10,0),
	role varchar(64),	salary decimal(8,2),
	start_dt date, update_dt timestamp
	);
		
		
9. Run this command to insert the Contents of ORcFile to SPlice machine table
	- insert into user_orc SELECT * FROM new com.splicemachine.tutorials.vti.SpliceORCFileVTI(
  '/testorcvti/test_orc_file') AS b
   (id bigint, name VARCHAR(10), activity char(1), is_fte boolean,
	test_tinyint smallint, test_smallint smallint, test_int int, test_float float,
	test_double double, test_decimal decimal(10,0),
	role varchar(64),	salary decimal(8,2),
	start_dt date, update_dt timestamp
	);
		
