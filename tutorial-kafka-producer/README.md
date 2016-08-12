# Overview
This tutorial contains code for putting messages on a Kafka queue.  This code is used by the Kafka Spark Streaming Tutorial.  It is purposely based in its own project because it is execute outside of the hadoop framework and needs 
to use the Kafka Client libraries that are outside of Hadoop.  This was tested with the Kafka 0.9.0.1 version.

# How to run the code
1.  Pull the source code from GitHub
2.  Run: `mvn clean compile package -P<profile.id>`
3.  Copy the `./target/splice-tutorial-kafka-producer-2.0.jar` to each of your servers' `${SPLICE_LIB_DIR}`` directory
4.  Copy the `runKafkaProducer.sh` to your server.
5.  Modify the `runKafkaProducer.sh`  Replace the `${HOST}` name with your server name.  Update the `*_LIB_DIR` variables to match your environment.  If you want logging create the file `/tmp/log4j.properties`, otherwise remove the `-Dlog4j.configuration=file:///tmp/log4j.properties` from the command.
6.  Run the script file passing in the topic name and the number of messages to put on the queue

```
./runKafkaProducer.sh mytopic 100
```

## It puts messages like the following on the Kafka queue

  + {"id": "A_93","location": "Jackson","temperature": 75.14,"humidity": 72.32,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_94","location": "Highlands","temperature": 80.69,"humidity": 70.41,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_95","location": "Manatee","temperature": 44.4,"humidity": 64.36,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_96","location": "Flagler","temperature": 33.16,"humidity": 87.6,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_97","location": "Gilchrist","temperature": 9.02,"humidity": 64.29,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_98","location": "Desoto","temperature": 72.11,"humidity": 70.09,"recordedTime": "2016-07-12 21:03:41.739"}
  + {"id": "A_99","location": "Nassau","temperature": 76.01,"humidity": 64.36,"recordedTime": "2016-07-12 21:03:41.74"}

# How to look at messages on the queue<a id="viewQueue"></a>
If you want to see messages on the queue run the kafka-console-consumer.sh where the zookeeper is the url for your zookeeper server and the topic is the topic you want to look at

```
bin/kafka-console-consumer.sh --zookeeper localhost:5181 --topic rfid
```

