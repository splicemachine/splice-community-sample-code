# Overview
This project contains the sample code for reading lines from a file and placing them on a kafka queue. 

# Code
## Java
- PutMessagesOnKafkaQueueFromFile.java - Reads a file and puts each line on the queue

## Scripts
- /resources/scripts/runKafkaProcedure.sh - Script for starting the PutMessagesOnKafkaQueueFromFile

# Prerequisites
You must have Splice Machine installed, have Kafka and Spark running 

# How to Deploy the Tutorial Code

- Compile and package the code: `mvn clean compile package -PPROFILE_ID`
- Copy the `./target/${project.artifactId}-${version}.jar` to each server and place it in the `/tmp` directory

# How to Run The Tutorial Code

After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure Kafka is started.  See [Start Kafka Server](#startKafkaServer) below.
+ Create a copy of the file /resources/scripts/exampleRunKafkaProcuderForFlatFile.sh and set the parameters for your environment 
+ Start putting messages on the queue by running the script that you created / updated in the previous step.  For example, copy the /resources/scripts/exampleRunKakfaProducerForFlatFileVTI.sh to the /tmp directory where kafka is running.  Run the script as sudo, so sudo ./exampleRunKakfaProducerForFlatFileVTI.sh

# Start the Kafka Server<a id="startKafkaServer"></a>
You must have kafka installed.  

- Start Kafka.  Navigate to the directory where you have Kafka installed.  The command will be something like the following (You might want to start is as a background process):

	bin/kafka-server-start.sh config/server.properties

# Kafka

## Create a Topic
./bin/kafka-topics.sh --create --zookeeper stl-colo-srv40.splicemachine.local:5181 --replication-factor 1 --partitions 1 --topic plenti

## List the topics
./bin/kafka-topics.sh --list --zookeeper stl-colo-srv40.splicemachine.local:5181 

## Start a consumer to list for the messages
./bin/kafka-console-consumer.sh --zookeeper stl-colo-srv40.splicemachine.local:5181  --topic plenti --from-beginning

## Start a producer client

This can be used to test the communication between the broker and the client

./bin/kafka-console-producer.sh --broker-list stl-colo-srv40.splicemachine.local:9092 --topic plenti

## Start the Real Producer
sudo su /tmp/runKafkaProducer.sh
