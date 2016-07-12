#Overview
In this tutorial we are  demonostrating ow 

#How to Deploy the tutorial code
- Compile and package the code: mvn clean compile package
- Copy the ./target/splice-cs-mqtt-0.0.1-SNAPSHOT.jar to each server and place it in the /opt/splice/default/lib directory
- Copy spark-streaming-mqtt_2.10-1.6.1.jar to each server in the /opt/splice/default/lib directory
- Copy org.eclipse.paho.client.mqttv3-1.1.0.jar to each server in the /opt/splice/default/lib directory
- Restart Hbase
- Create the target table in splice machine.  Run the SQL script create-table.sql


#How to run the tutorial code
After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure mosquitto is started.  See [Start mosquitto](#mosquittoStart) below.
+ Start the spark streaming script: run-mqtt-spark-streaming.sh.  The first parameter is the address for the mqtt broker, The second is the topic name and the third is the number of seconds each stream should run
+ sudo -su mapr ./run-mqtt-spark-streaming.sh tcp://srv61:1883 /testing 10
+ Start putting messages on the queue.  Follow the instructions under [How to put messages on the queue](#mosquittoPublisher)


#Mosquitto 
Mosquitto (mosquitto.org) is an open source message broker that implements the MQTT protocol.

##Add Repostory to centos
cd /etc/yum.repos.d
wget xxx
sudo yum update

##Install mosquitto & mosquitto-clients
sudo yum install mosquitto
sudo yum install mosquitto-clients

##Start mosquitto<a id="mosquittoStart"></a>
sudo su /usr/sbin/mosquitto -d -c /etc/mosquitto/mosquitto.conf > /var/log/mosquitto.log 2>&1


##Start the client to listen for messages on the queue 
You only want to do this for testing purposes.  When you are running the spark demo it should not be running.

- mosquitto_sub -h localhost -t /testing

##How to put messages on the queue<a id="mosquittoPublisher"></a>
There is a java program that is setup to put messages on the queue.  This is the syntax for running the program.  The first parameter is the topic name, the second parameter is the Broker URL and the third parameter is the number of iterations to execute

java -cp /opt/splice/default/lib/splice-cs-mqtt-0.0.1-SNAPSHOT.jar:/opt/splice/default/lib/org.eclipse.paho.client.mqttv3-1.1.0.jar com.splicemachine.tutorials.mqtt.MQTTPublisher tcp://localhost:1883 /testing 1000

###It puts messages like the following on the MQTT queue

Asset0,Location0,2016-07-08 20:38:42.181
Asset1,Location1,2016-07-08 20:38:42.243
Asset2,Location2,2016-07-08 20:38:42.268
Asset3,Location3,2016-07-08 20:38:42.285
Asset4,Location4,2016-07-08 20:38:42.302
Asset5,Location5,2016-07-08 20:38:42.319
Asset6,Location6,2016-07-08 20:38:42.336
Asset7,Location7,2016-07-08 20:38:42.352
Asset8,Location8,2016-07-08 20:38:42.369
Asset9,Location9,2016-07-08 20:38:42.395

