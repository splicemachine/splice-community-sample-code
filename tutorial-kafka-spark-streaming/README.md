#Overview
This project contains the sample code for processing a Kafka Queue with Spark Streaming and Splice Machine.  At a high level there is a python script that puts messages on a Kafka Queue.  In Splice Machine we started a Spark Streaming job though a stored procedure that reads messages from the kafka Queue, then calls a Splice Machine VTI to store the data in Splice Machine.


#Prerequisites
You must have Splice Machine installed and you must install and configure the code from https://github.com/claudiofahey/global_anomaly_detection_demo.  This code is used to put messages on the Kafka queue.

#How to run the code
1.  Pull the source code from github
2.  Run: mvn clean compile package
3.  Copy the ./target/splice-cs-kafka-0.0.1-SNAPSHOT.jar to each of your servers /opt/splice/default/lib/ directory
4.  Copy the /src/main/resources/global_anomaly_detection_demo folder to your server where Kafka is running
5.  Restart the HBase Master and Region Servers
6.  Start the splice command prompt
7.  Create the table (IOT.SENSOR_MESSAGES) where the data is going to be stored and the stored procedure definitions.  Run the following two scripts:
		- /src/main/resources/ddl/create-tables.sql
		- /src/main/resources/ddl/create-procedures.sql
8.  Call the stored procedure to start the Spark Streaming process.  Run the following command updating the values for zookeeper to match your environment.
		- call iot.processSensorStreamVTI('localhost:5181','test','sensor_messages',1);
9.  Start the process for putting messages on the Kafka Queue: global_anomaly_detection_demo/streaming_data_generator.sh
10.  You can see the Spark Streaming Process by going to the Spark UI for Splice Machine and selecting the 'Streaming' tab.
11.  If you want to see the number of records that have been added to the table run the followng SQL statement:  select count(1) from IOT.SENSOR_MESSAGES;

## Notes
The current build process is setup to be compiled and executed on a MapR environment.  You will need to change the properties section in your pom.xml in order to compile and run it against other distributions.

# Data
The messages put on the Kafka queue look like the following:

{"num_access_files": 0, "src_bytes": 0, "srv_count": 15, "num_compromised": 0, "rerror_rate": 1.0, "urgent": 0, "dst_host_same_srv_rate": 0.059999999999999998, "duration": 0, "label": "neptune.", "srv_rerror_rate": 1.0, "srv_serror_rate": 0.0, "is_host_login": 0, "wrong_fragment": 0, "uuid": "a6d069d5-857b-4de5-b4e1-30994c81b33a", "service": "private", "serror_rate": 0.0, "num_outbound_cmds": 0, "is_guest_login": 0, "dst_host_rerror_rate": 1.0, "dst_host_srv_serror_rate": 0.0, "diff_srv_rate": 0.059999999999999998, "hot": 0, "dst_host_srv_count": 15, "logged_in": 0, "num_shells": 0, "dst_host_srv_diff_host_rate": 0.0, "index": 4655378, "srv_diff_host_rate": 0.0, "dst_host_same_src_port_rate": 0.0, "root_shell": 0, "flag": "REJ", "su_attempted": 0, "dst_host_count": 255, "num_file_creations": 0, "protocol_type": "tcp", "count": 116, "utc": "2016-05-18T20:38:22.521750", "land": 0, "same_srv_rate": 0.13, "dst_bytes": 0, "sequence_id": 4803, "dst_host_diff_srv_rate": 0.070000000000000007, "dst_host_srv_rerror_rate": 1.0, "num_root": 0, "num_failed_logins": 0, "dst_host_serror_rate": 0.0}