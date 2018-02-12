Here are steps to make a simple streaming app that will produce rows from the kafka producer and using spark streaming it will insert into the splice engine with the help of splice-spark adapter:

1. Download kafka_2.10-0.10.0.1 and untar in the home_dir.

1. Start kafka server in one window (or start with nohup command and keep it running in background)
$ bin/kafka-server-start.sh  config/server.properties

2. Register the kafka topic  (Need to do it only once), e.g. to start "test-k" topic, following command is used:
$ bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test-k --partitions 2 --replication-factor 1

4. Keep kafka producer stream ready (kafka-producer/run_prod.sh), it can be used to stream some mock data to splice-adapater app that will use kafka consumer on the same kafka-topic. 

5. Download the splice-spark adapter jar file to the current directory from  http://repository.splicemachine.com/nexus/content/groups/public/com/splicemachine site, choose the folder version that is matching to the build installed on the cluster. E.g. splicemachine-cdh5.8.3-2.1.0_2.11-2.5.0.<timestamp>.jar 

6. Also copy the spark-streaming-kafka-0-10_2.11-2.2.0.cloudera1.jar to the current dir from SPARK2_HOME

7. If running on kerberized cluster, copy the hbase user keytab file to some dir so the app can reference that. Currently, it will require to use hbase/<masterNode> principal and its keytab. It can cab be found from master node  /var/run/cloudera-scm-agent/process/, searching for latest hbase-MASTER dir, hbase.keytab should be present in that dir. (This step will require sudo privilege)

8. Main.java in src foler contains the sample streaming app that would take the data from kafka stream to the spark stream and then insert the data to splice engine using splice-spark adapter. You will need to get the build artifacts jars and keep it at the common location say /tmp/dependency on all nodes.

To compile the app:
$ mvn clean install

To start the app, launch the spark-submit command with the example mentioned in spark-submit.sh script. You may need to update some config parameters such as kerberos principal, keytab filename, table and schema to insert the data etc. 
To match the schema of the sample app, do create following table the region server  with following schema:
CREATE TABLE TEST_TABLE (COL1 CHAR(30), COL2 INTEGER, COL3 BOOLEAN);

9. Launch the app, by starting spark-submit.sh script, uou can monitor the progress on <namenode>:8088 page to see if the app is launched properly or not, check the logs and look at the stderr for errors

10. Once the app goes to RUNNING state, you may want to start the kafka stream. You could use the kafka-producer/run_prod.sh which will send the kafka producer batch of rows (kafka-producer/stream_rows.sh script can be used to loop through to send the stream of data with few seconds of interval)

11. Logon to region server, and see that the count on 'test_table' is being updated.


