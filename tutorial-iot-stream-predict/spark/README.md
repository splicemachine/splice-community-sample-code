# Overview
This project contains the sample code for a spark application that processes messages from a kafka queue.

# Code
## Java
- SavePartition.java - Builds the VTI statement and calls it for each each partition
- SaveRDDWithPartition.java - Calls the SavePartitionWithVTI for each partition in an RDD
- SparkStreamingKafka.java - This is the spark streaming job that reads messages from the Kafka queue.
- TupleFunction - This is a spark function for retrieving the second value of the original Kafka message
- TuplePairFunction - This is a spark function for retrieving the second value of the original Kafka message.  Used in newer versionf of Kafka

## Scripts
- /resources/scripts/run-kafka-spark-streaming.sh - Starts the Spark Streaming Job
- /resources/scripts/exampleRunSparkForFlatFileVTI.sh - An example of calling the run-kafka-spark-streaming.sh with parameters specifying the VTI class name
- /resources/scripts/exampleRunSparkForFlatFileVTI2.sh - An example of calling the run-kafka-spark-streaming.sh with parameters specifying the VTI statement

# Prerequisites
You must have Splice Machine installed, have Kafka and Spark running 

# How to Deploy the Tutorial Code

- Compile and package the code: `mvn clean compile package -PPROFILE_ID`
- Copy the `./target/${project.artifactId}-${version}.jar` to each server and place it in the `<SPLICE_HOME>/lib` directory
- Restart HBase

# How to Run The Tutorial Code

After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Create a copy of one of the example files under /resources/scripts/the file and modify it with your settings
+ Copy it to the /tmp directory on your server
+ Start the Spark Streaming job: ./exampleRunSparkForFlatFileVTI.sh
+ Once your Spark Streaming job has been started, start the kafka process for putting messages on the queue.

