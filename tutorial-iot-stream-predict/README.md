# Overview

This project contains the sample code for a kafka producer, a spark streaming job that reads messages from a kafka queue,
some sample vti's that are used by the spark streaming application and stored procedures for creating of Predictive Model, and using it to 
make realtime predictions.  The code for each of these is separated into its own
module because there is a high probability that the all three will run on different servers.  For example, your kafka server
may be separate from your spark servers, and your spark job may run outside of Splice Machine's environment.

## Software Version
* SpliceMachine : 2.5.0 
* Spark : 2.0.2
* Kafka : 2.11-0.10.2.0

```Note: It is very important that Kafka and Spark versions are same as mentioned, since the kafka queue and spark streaming are version sensitive, any mismatch will mean the spark stream will not read the messages from kafka queue.```

## Kafka Install Instructions
* Connect to server
* cd /opt
* sudo su
* mkdir kafka
* cd kafka
* wget http://www-eu.apache.org/dist/kafka/0.10.2.0/kafka_2.11-0.10.2.0.tgz
* tar -xf kafka_2.11-0.10.2.0.tgz
* ln -s kafka_2.11-0.10.2.0 default
* Update /opt/kafka/default/config/server.properties
    * Uncomment:delete.topic.enable=true
    * Update zookeeper.connect if you are running on a different server


## Install Spark Standalone Cluster (Do the following on each server in Cluster)
* Connect to server
* cd /opt
* sudo su
* mkdir spark
* cd spark
* wget http://d3kbcqa49mib13.cloudfront.net/spark-2.0.2-bin-hadoop2.6.tgz
* tar -xf spark-2.0.2-bin-hadoop2.6.tgz
* ln -s spark-2.0.2-bin-hadoop2.6 default


## Compile the code
* Download the source
* Navigate to the <EXTRACT_DIR>/kafka
* Compile kafka: mvn clean compile package
* Navigate to the <EXTRACT_DIR>/spark
* Compile spark: mvn clean compile package
* Navigate to the <EXTRACT_DIR>/splicemachine
* Compile splice machine: mvn clean compile package -Pcdh5.8.3

## Deploy the Code
* Navigate to the <EXTRACT_DIR>
* Copy the kafka jar to the server where kafka is to the /tmp directory
    * scp ./kafka/target/splice-tutorial-iot-kafka-2.5.0.1707.jar abcd@srvNN1:/tmp
* Copy the files under <EXTRACT_DIR>/kafka/src/main/resources/scripts to the server running kafka and place it in the /tmp directory
* Copy the spark jar to each server in your cluster.  Put it in the /tmp directory
    * scp ./spark/target/splice-tutorial-iot-spark-2.5.0.1707.jar abcd@srvNN2:/tmp
* Copy the /opt/kafka/default/libs/kafka-clients-0.10.2.0.jar to the /tmp directory on each server where you are running spark.
* Copy the files under <EXTRACT_DIR>/spark/src/main/resources/scripts to the /tmp directory on the server where you will be running the spark master
* Copy the ./splicemachine/target/splice-tutorial-iot-vti-model-2.5.0.1707.jar to each server in your cluster to the <splice defualt>/lib directory
* Copy the files <EXTRACT_DIR>/splicemachine/src/main/resources/ddl to /tmp directory on one of the server running regionserver
* Copy the files <EXTRACT_DIR>/splicemachine/src/main/resources/data to /tmp directory on one of the server running regionserver
* Restart Splice Machine


## Splice Database changes
* Create tables in Splice Machine, run this command on server with regionserver
    * sqlhell.sh  -f /tmp/create-tables.sql
* Create Views in SPlice Machine, run this command on server with regionserver
    * sqlhell.sh  -f /tmp/create-views.sql
* Create Stored Pprocedures - For Predioction Model creation and make predictions
    sqlshell.sh -f /tmp/create-procedures.sql
* Import data
    * Copy files train_FD001.txt, test_FD001.txt and RUL_FD001.txt to hdfs 
        * hadoop fs -copyFromLocal /tmp/train_FD001  /iotdemo/
        * hadoop fs -copyFromLocal /tmp/test_FD001  /iotdemo/
        * hadoop fs -copyFromLocal /tmp/RUL_FD001  /iotdemo/
    * Insert data from files into tables, using with VTI
        sqlshell.sh -f /tmp/import_data.sql
    * Update/set labels for Training and Test data
        sqlshell.sh -f create_train_and_test_labels.sql
* Build Prediction Model
    * Start sqlshell command tool
        *./sqlshell.sh
    *at the prompt run stored procedure to build the model
        call iot.BUILDRULMODEL('IOT.TRAIN_AGG_1_VIEW', 'IOT.TEST_AGG_1_VIEW', '/tmp/');



* Get the file train_FD001.txt to read for the kafka producer and place it on the server where kafka is installed in folder /tmp

## Zeppelin Setup
* Create Splice interpreter in Zeppelin
    * Name = splice
    * Interpreter group = jdbc
    * default.url = jdbc:splice://srv054:1527/splicedb
    * default.user = splice
    * default.password = admin
    * default.driver = com.splicemachine.db.jdbc.ClientDriver
    Leave the following as is:
    * common.max_count
    * zeppelin.jdbc.concurrent.use
    * zeppelin.jdbc.concurrent.max_connection
    Remove all other properties
    
    * In add dependencies, add the path to db-client-2.5.0.1707.jar



* Import Notebook IoTDemo.json in Extract Folder


## Kafka Startup
* Start kafka server:
    * cd /opt/kafka/default
    * sudo ./bin/kafka-server-start.sh ./config/server.properties
* Create a topic with multiple partitions - the partitions increase parallelism
    * cd /opt/kafka/default
    * ./bin/kafka-topics.sh --create --zookeeper stl-colo-srvXX.splicemachine.local:5181 --replication-factor 1 --partitions 10 --topic spliceload
* Update the file /tmp/exampleRunIoTKakfaProducerForFile.sh to have your kafka server name, the topic name and the file you want to read from, and adjust the volume and the duration of the data to be placed on Queue
* Update the paths in the /tmp/runIoTKafkaProducer.sh file
* Make sure the /tmp/*.sh are executable
    * chmod +x /tmp/*.sh


## Spark Startup
* Choose one server as the master server
* Start the spark master server:
    * sudo /opt/spark/default/sbin/start-master.sh
* On the other servers in your cluster start the spark slave servers
    * sudo /opt/spark/default/sbin/start-slave.sh spark://stl-colo-srvYY.splicemachine.colo:7077


## Start the Spark Streaming Job
* Update the file /tmp/exampleRunSparkForIoTFileVTI.sh to have your server details, topic and target table names
* Update the paths in the file /tmp/run-kafka-spark-streaming.sh file
* cd /tmp
* sudo ./exampleRunSparkForIoTFileVTI.sh


##Start the Spark UI
Make sure the spark ui / streaming starts
* Open a browser and navigate to the spark UI
* http://stl-colo-srv055.splicemachine.colo:8081/


## Initalize for prediction
Before starting the quueue production remove data from the tables before start the prediction queue

* clear external table with predictions
    sudo -su <hadoopuser> hadoop fs -rm hadoop fs -ls /tmp/data_pred/predictions/*.csv
* Start sqlshell command prompt on one of the regionservers
    * ./sqlshell.sh
*   Clear tables
    * truncate table iot.sensor_data;
    * truncate table iot.to_process_sensor;
    * insert into iot.PREDICTIONS_EXT values (1,1,0,0); 
* Start the Prediction Stored Procedure - at Sqlshell prompt
    * call iot.PredictRUL('IOT.SENSOR_AGG_1_VIEW', 'IOT.PREDICTION_RESULTS', '/tmp/', 1);


## Start the Kafka Producer Job
Once you have confirmed that the spark streaming starts, start the producer
* sudo /tmp/exampleRunIoTKakfaProducerForFile.sh


## Open Zeppelin Notebook 
* Run queries for verify data