#Overview
This project contains the sample code for processing a Kafka Queue with Spark Streaming and Splice Machine.  There is a java program that puts JSON messages on the Kafka Queue.  You then start a spark streaming job that will connect to the kafka queue and process the messages, inserting them into Splice machine using a VTI.

#Code
## Java
- SaveRDDWithVTI.java - inserts the data into Splice Machine using the SensorMessageVTI class
- SensorMessage.java - POJO for converting from a JSON string to an object to a database entry
- SensorMessageMessageVTI.java - VTI for parsing an SensorMessage
- SparkStreamingKafka.java - This is the spark streaming job that reads messages from the Kafka queue.
- TupleFunction - This is a spark function for retrieving the second value of the original Kafka message

##Scripts
- /ddl/create-tables.sql - Create the table where SENSOR_MESSAGES messages will be stored
- /scripts/run-kafka-spark-streaming.sh - Script for starting the Spark Streaming Job


#Prerequisites
You must have Splice Machine installed and Kafka installed and configured. You should also have the tutorial-kafka-producer code setup.

#How to Deploy the Tutorial Code
- Compile and package the code: mvn clean compile package
- Copy the ./target/splice-tutorial-kafka-2.0.jar to each server and place it in the /opt/splice/default/lib directory
- Restart Hbase
- Create the target table in splice machine.  Run the SQL script create-tables.sql

#How to Run The Tutorial Code
After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure Kafka is started.  See [Start Kafka Server](#startKafkaServer) below.
+ Start the spark streaming script: run-kafka-spark-streaming.sh.  The first parameter is the address for the zookeper server, The second is the group, the third is the topic list, the fourth is the number of threads and the fourth is the number of seconds each stream should run
+ sudo -su mapr ./run-kafka-spark-streaming.sh localhost:5181 group1 rfid 0 10
+ Start putting messages on the queue.  Follow the instructions under [Put Messages on the Kafka Server](#startKafkaProducer)


#Start the Kafka Server<a id="startKafkaServer"></a>
You must have kafka installed.  

- Start Kafka.  Navigate to the directory where you have Kafka installed.  The command will be something like the following.  You might want to start is as a background process:

	bin/kafka-server-start.sh config/server.properties

#Put Messages on the Kafka Server<a id="startKafkaProducer"></a>
This assumes that you have copied the splice-tutorial-kafka-2.0.jar to the /opt/splice/default/lib directory.
Follow the instructions in the tutorial tutorial-kafka-producer for putting messages on the kafka queue.

## Notes
The current build process is setup to be compiled and executed on a MapR environment using a SNAPSHOT version of Splice Machine 2.0.  You will need to change the properties section in your pom.xml in order to compile and run it against other distributions.  This was tested with Kafka 0.9.0.1
