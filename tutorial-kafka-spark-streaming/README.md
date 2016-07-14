#Overview
This project contains the sample code for processing a Kafka Queue with Spark Streaming and Splice Machine.  There is a java program that puts JSON messages on the Kafka Queue.  You then start a spark streaming job that will connect to the kafka queue and process the messages, inserting them into Splice machine using a VTI.

#Code
## Java

#Prerequisites
You must have Splice Machine installed and Kafka installed and configured. 

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

- Start Kafka.  The command will be something like the following.  You might want to start is as a background process:

	bin/kafka-server-start.sh config/server.properties

#Put Messages on the Kafka Server<a id="startKafkaProducer"></a>
This assumes that you have copied the splice-tutorial-kafka-2.0.jar to the /opt/splice/default/lib directory.

- Run the following command.  The first parameter is the Kafka server, the second is the kafka topic and the third is the number of messages to put on the queue.

java -cp /opt/splice/default/lib/splice-tutorial-kafka-2.0.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/kafka-clients-0.8.2.1.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/slf4j-log4j12-1.6.1.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/slf4j-api-1.7.6.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/log4j-1.2.16.jar com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 sensor 100

java -cp /opt/splice/default/lib/splice-tutorial-kafka-2.0.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/kafka-clients-0.8.2.1.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/slf4j-log4j12-1.6.1.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/slf4j-api-1.7.6.jar:/opt/kafka/kafka_2.10-0.8.2.1/libs/log4j-1.2.16.jar com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 sensor 100

java -cp /opt/splice/default/lib/splice-tutorial-kafka-2.0.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/kafka-clients-0.9.0.1.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-log4j12-1.7.6.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-api-1.7.6.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/log4j-1.2.17.jar com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 sensor 100


java -cp /opt/splice/default/lib/splice-tutorial-kafka-2.0.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/kafka-clients-0.9.0.1.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-log4j12-1.7.6.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-api-1.7.6.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/log4j-1.2.17.jar com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 sensor 100

java -cp /opt/splice/default/lib/splice-tutorial-kafka-producer-2.0.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/kafka-clients-0.9.0.1.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-log4j12-1.7.6.jar:/opt/kafka/kafka_2.10-0.9.0.1/libs/slf4j-api-1.7.6.jar:/op^Ckafka/kafka_2.10-0.9.0.1/libs/log4j-1.2.17.jar -Dlog4j.configuration=file:///tmp/log4j.properties com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 sensor 100


kafka_2.10-0.10.0.0

- When messages are put on the queue, they are JSON objects that look like the following:

  + {"id": "A_93","location": "Jackson","temperature": 75.14,"humidity": 72.32,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_94","location": "Highlands","temperature": 80.69,"humidity": 70.41,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_95","location": "Manatee","temperature": 44.4,"humidity": 64.36,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_96","location": "Flagler","temperature": 33.16,"humidity": 87.6,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_97","location": "Gilchrist","temperature": 9.02,"humidity": 64.29,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_98","location": "Desoto","temperature": 72.11,"humidity": 70.09,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_99","location": "Nassau","temperature": 76.01,"humidity": 64.36,"recordedTime": "2016-07-12 21:03:41.74"}

#How to look at messages on the queue<a id="viewQueue"></a>
If you want to see messages on the queue run the kafka-console-consumer.sh where the zookeeper is the url for your zookeeper server and the topic is the topic you want to look at

- bin/kafka-console-consumer.sh --zookeeper localhost:5181 --topic rfid
