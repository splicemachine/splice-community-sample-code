#Overview
In this tutorial we are demonstrating how to read data from an MQTT queue, put them in a spark stream and store the data in Splice Machine. MQTTPublisher places messages in csv format on the MQTT queue, a spark application reads the data from the queue converts it into an RFIDMessage and then saves it to Splice Machine using a VTI.

#Code
## Java
- MQTTPublisher.java - This is a class which will put some csv messages on an MQTT queue.
- RFIDMessage.java - POJO for converting from a csv string to an object to a database entry
- RFIDMessageVTI.java - VTI for parsing an RFIDMessage
- SaveRDD.java - inserts the data into Splice Machine using the RFIDMessageVTI class
- SparkStreamingMQTT.java - This is the spark streaming job that reads messages from the MQTT queue.

##Scripts
- /ddl/create-tables.sql - Create the table where RFID messages will be stored
- /scripts/run-mqtt-spark-streaming.sh - Script for starting the Spark Streaming Job

#How to Deploy the tutorial code
- Compile and package the code: mvn clean compile package
- Copy the ./target/splice-tutorial-mqtt-2.0.jar to each server and place it in the /opt/splice/default/lib directory
- Copy spark-streaming-mqtt_2.10-1.6.1.jar to each server in the /opt/splice/default/lib directory
- Copy org.eclipse.paho.client.mqttv3-1.1.0.jar to each server in the /opt/splice/default/lib directory
- Restart Hbase
- Create the target table in splice machine.  Run the SQL script create-tables.sql


#How to run the tutorial code
After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure mosquitto is started.  See [Start mosquitto](#mosquittoStart) below.
+ Start the spark streaming script: run-mqtt-spark-streaming.sh.  The first parameter is the address for the mqtt broker, The second is the topic name and the third is the number of seconds each stream should run
+ sudo -su mapr ./run-mqtt-spark-streaming.sh tcp://srv61:1883 /testing 10
+ Start putting messages on the queue.  Follow the instructions under [How to put messages on the queue](#mosquittoPublisher)


#Mosquitto 
Mosquitto (mosquitto.org) is an open source message broker that implements the MQTT protocol.

##Add Repostory to centos
- cd /etc/yum.repos.d
- wget xxx
- sudo yum update

##Install mosquitto & mosquitto-clients
- sudo yum install mosquitto
- sudo yum install mosquitto-clients

##Start mosquitto<a id="mosquittoStart"></a>
- sudo su /usr/sbin/mosquitto -d -c /etc/mosquitto/mosquitto.conf > /var/log/mosquitto.log 2>&1


##Start the client to listen for messages on the queue 
You only want to do this for testing purposes.  When you are running the spark demo it should not be running.

- mosquitto_sub -h localhost -t /testing

##How to put messages on the queue<a id="mosquittoPublisher"></a>
There is a java program that is setup to put messages on the queue.  This is the syntax for running the program.  The first parameter is the topic name, the second parameter is the Broker URL,  the third parameter is the number of iterations to execute and the fourth parameter is a prefix for the run

java -cp /opt/splice/default/lib/splice-tutorial-mqtt-2.0-SNAPSHOT.jar:/opt/splice/default/lib/org.eclipse.paho.client.mqttv3-1.1.0.jar com.splicemachine.tutorials.sparkstreaming.mqtt.MQTTPublisher tcp://localhost:1883 /testing 1000 R1

__NOTE__: The source code for this utility is under a different github project: tutorial-kafka-producer


## Notes
The current build process is setup to be compiled and executed on a MapR environment using a SNAPSHOT version of Splice Machine 2.0.  You will need to change the properties section in your pom.xml in order to compile and run it against other distributions.
