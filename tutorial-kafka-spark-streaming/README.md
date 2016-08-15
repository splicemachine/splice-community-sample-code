# Overview
This project contains the sample code for processing a Kafka Queue with Spark Streaming and Splice Machine.  There is a java program that puts JSON messages on the Kafka Queue.  You then start a spark streaming job that will connect to the kafka queue and process the messages, inserting them into Splice machine using a VTI.

# Code
## Java
- SavePartitionWithVTI.java - inserts the data into Splice Machine using the SensorMessageVTI class
- SaveRDDWithPartition.java - Calls the SavePartitionWithVTI for each partition in an RDD
- SensorMessage.java - POJO for converting from a JSON string to an object to a database entry
- SensorMessageVTI.java - VTI for parsing an SensorMessage
- SparkStreamingKafka.java - This is the spark streaming job that reads messages from the Kafka queue.
- TupleFunction - This is a spark function for retrieving the second value of the original Kafka message

## Scripts
- /resources/ddl/create-tables.sql - Create the table where SENSOR_MESSAGES messages will be stored
- /resources/scripts/run-kafka-spark-streaming.sh - Script for starting the Spark Streaming Job with MapR
- /resources/scripts/run-kafka-spark-streaming-cdh.sh - Script for starting the Spark Streaming Job with Cloudera
- /resources/scripts/run.sh - Example of calling run-kafka-spark-streaming.sh

# Prerequisites
You must have Splice Machine installed and Kafka installed and configured. You should also have the tutorial-kafka-producer code setup.

# How to Deploy the Tutorial Code

- Compile and package the code: `mvn clean compile package -P<profile.id>`
- Copy the `./target/${project.artifactId}-${version}.jar` to each server and place it in the `${SPLICE_LIB_DIR}` directory
- Restart Hbase
- Create the target table in splice machine.  Run the SQL script create-tables.sql

# How to Run The Tutorial Code

After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure Kafka is started.  See [Start Kafka Server](#startKafkaServer) below.
+ Start the spark streaming script: run-kafka-spark-streaming.sh.  The first parameter is the address for the zookeper server, The second is the group, the third is the topic list, the fourth is the number of threads, the fifth is the number of seconds each stream should run, the sixth is the splice machine jdbc url
+ `sudo -su mapr ./run-kafka-spark-streaming.sh localhost:5181 group1 rfid 1 10 "jdbc:splice://stl-colo-srv54:1527/splicedb;user=splice;password=admin"`
+ Start putting messages on the queue.  Follow the instructions under [Put Messages on the Kafka Server](#startKafkaProducer)


# Start the Kafka Server<a id="startKafkaServer"></a>
You must have kafka installed.  

- Start Kafka.  Navigate to the directory where you have Kafka installed.  The command will be something like the following (You might want to start is as a background process):

	bin/kafka-server-start.sh config/server.properties

# Put Messages on the Kafka Server<a id="startKafkaProducer"></a>
This assumes that you have followed the instructions in the tutorial-kafka-producer GitHub repository and deployed the code.  Follow the instructions in the README.md called 'How to run the code'

##CDH Notes
To run this using CDH:
- Copy the script /resources/scripts/run-kafka-spark-streaming-cdh.sh to a temp directory
- Run this script: sudo -su hdfs ./run-kafka-spark-streaming-cdh.sh
