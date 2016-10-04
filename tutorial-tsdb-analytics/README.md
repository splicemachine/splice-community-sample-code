
#Overview
This project contains the sample code for Real time Analytics of Time Series data with Spark Streaming, Splice Machine and Zeppelin. There is a java program to put Impression logs on the Kafka Queue.  Next start a spark streaming job that will connect to the kafka queue and process the Impression Logs to aggregate them on Publisher and Geo, and inserting the aggregate results into Splice machine using a VTI. The Aggregated data can then be used 
to plot graphs in Zeppelin.

#Code
## Java
+ AggregateFunction.java : This is the function for mapping ImpressionLog to AggregateLog Object
+ AggregationResultVTI.java : VTI for parsing AggregationResult object for inserting into database
+ FilterGeoFunction.java : This is the function for filtering IMpression Logs with unknown Geo.
+ kafkaStreamGenerator.java : This is the producer of Kafka Queue Impression Logs.
+ LogAggregator.java : THis is the spark streaming job reading the Impression Logs from Kafka Queue
+ ReduceAggregationLogs.java : This is the function for performing aggegation by publisher and geo.	
+ SaveLogAggPartition.java : This is the save function for a Partition in AggregationResult RDD
+ SaveLogAggRDD.java : This is the save function for AggregationResult RDD
+ TupleFunction.java : This is function for retrieving the Impression Log from the original Kafka message
+ dataobjects/AggregationLog.java : POJO for Aggregation Format of Impression Log
+ dataobjects/AggregationResult.java : POJO for final Aggregation record to be saved to splice
+ dataobjects/ImpressionLog.java : POJO for original Impression Log that is put on Queue 
+ dataobjects/ImpressionLogDecoder.java : Decoder for the Impression Log - converted to JSON string
+ dataobjects/ImpressionLogEncoder.java	 : Encoder for the Impression Log - converted to JSON string
+ dataobjects/ImpressionLogSerializer.java : Serializer for the Impression Log - converted to JSON string
+ dataobjects/PublisherGeoKey.java  : POJO for Key to perform aggregation on .
			
	
##Scripts
+ /ddl/create-tables.sql - Create the table where AggregateResults that will store the aggregated Results
+ /scripts/runKafkaProducer.sh - Script for starting the ImpressionLog producer, which puts 
	the Impression Logs on Kafka Queue
+ /scripts/runKafkaConsumer.sh - Script for starting the spark streaming job, to read the Impression Logs, process them
	and save results to splice database



#Prerequisites
You must have Splice Machine installed and Kafka installed and configured. 


#How to Deploy the Tutorial Code
+ Compile and package the code: mvn clean compile package
+ Copy the ./target/splice-tutorial-tsdb-analytics-2.0.jar to each server and place it in the /opt/splice/default/lib directory
+ Copy thirdparty jar algebird-core_2.10-0.12.1.jar  to each server and place it in the /opt/splice/default/lib directory
+ Copy thirdparty jar gson-2.7.jar to each server and place it in the /opt/splice/default/lib directory
+ Restart Hbase
+ Create the target table in splice machine.  Run the SQL script create-tables.sql

#How to Run The Tutorial Code
After completing the steps under How to Deploy the Tutorial Code, do the following:

+ Make sure Kafka is started.  See [Start Kafka Server](#startKafkaServer) below.
+ Start the spark streaming script: runKafkaConsumer.sh.  The first parameter is the address for the zookeper server, The second  the number of threads and the third is the number of seconds each stream should run . This stream is using the group : test and topic:tsdb

+ sudo -su mapr ./runKafkaConsumer.sh localhost:5181   2 2

+ Start the Kafka Impression Log producer by running the script : runKafkaProducer.sh. The first parameter is the total number of Logs to write to the queue, use 0 for infinite. The second parameter is the batch size of the logs(note this is the number of logs put on queue wait 1 sec, then put next batch). The third parameter is the Number of distinct publishers to use in generation of the log. The fourth parameter is the Number of distinct Geo codes to use in generation of logs. The third and fourth are optional.
The producer is using kafka broker at localhost:9092 and topic tsdb 

+ ./runKafkaProducer.sh <total Logs> <batch size> <no of publishers> <no of geo>
ex : To create a continues stream with 50 logs in a batch and with 10 publishers and 10 geo, use this command
	./runKafkaProducer.sh 0 50 10 10

+ Next to View the Aggregated Results in Zeppelin 
	-  Create Interpretor to Splice Database using JDBC
	-  Create  New Note, ensure the interpretor for Splice is at the top of the list 
	- Add this to the Paragraph
		%jdbc
		select * from logagg.AggregateResults where TIMESTAMPDIFF(SQL_TSI_MINUTE,logdate,  CURRENT_TIMESTAMP) <= 2
	- Select Bar Graph with Keys = Publisher, Groups = Geo and Values = Imps SUM
	- Set Run Scheduler for 1 min.
You should see the graph sowing results for last 2 mins, and results refreshed every min.


#Start the Kafka Server<a id="startKafkaServer"></a>
You must have kafka installed.  

+ Start Kafka.  Navigate to the directory where you have Kafka installed.  The command will be something like the following.  You might want to start is as a background process:

	bin/kafka-server-start.sh config/server.properties


## Notes
The current build process is setup to be compiled and executed on a MapR environment using a SNAPSHOT version of Splice Machine 2.0.  You will need to change the properties section in your pom.xml in order to compile and run it against other distributions.  This was tested with Kafka 0.9.0.1


## Troubleshoot
To verify the producer is working fine,

+ Stop the above Producer and Consumer(spark stream)
+ Start the base Kafka Consumer : run the kafka-console-consumer.sh where the zookeeper is the url for your zookeeper server 

<kafkahome>/bin/kafka-console-consumer.sh --zookeeper localhost:5181 --topic tsdb

+ Start the producer to put few records with this command
./runKafkaProducer.sh 20 10 5 5

You should see these Impression Logs similar to logs shown below in the Kafka Consumer

{"timestamp":1470848711704,"publisher":"publishers_2","advertiser":"advertisers_0","website":"website_2370.com","geo":"AK","bid":0.7704085762882753,"cookie":"cookie_9899"}
{"timestamp":1470848711837,"publisher":"publishers_7","advertiser":"advertisers_2","website":"website_1790.com","geo":"HI","bid":0.8228280109734045,"cookie":"cookie_7027"}
{"timestamp":1470848711837,"publisher":"publishers_1","advertiser":"advertisers_0","website":"website_2471.com","geo":"NY","bid":0.5618725329185594,"cookie":"cookie_7328"}
{"timestamp":1470848711838,"publisher":"publishers_1","advertiser":"advertisers_0","website":"website_4732.com","geo":"FL","bid":0.6251258997979503,"cookie":"cookie_6561"}
{"timestamp":1470848711839,"publisher":"publishers_5","advertiser":"advertisers_1","website":"website_2410.com","geo":"AL","bid":0.5052532761705746,"cookie":"cookie_7032"}


For Consumer, look in the Error Logs for the Yarn Servers for the Yarn Application