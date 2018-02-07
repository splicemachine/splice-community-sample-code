Here are steps to make a simple streaming app that will produce rows from the kafka producer and using spark streaming it will insert into the splice engine with the help of splice-spark adapter:

1. Download kafka_2.10-0.10.0.1 and untar it in the home dir.

1. Start kafka server in one window (or start with nohup command and keep it running in background)
$ bin/kafka-server-start.sh  config/server.properties

2. Register the kafka topic  (Need to do it only once even if kafka server is restarted). e.g. Sample app coonects kafka topic "test-k", soto start "test-k" topic, following command was used:
$ bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test-k --partitions 2 --replication-factor 1

4. Keep kafka producer stream ready (kafka-producer/run_prod.sh), it can be used to stream some mock data to splice-adapater app that will use kafka consumer on the same kafka-topic. 

5. If running on kerberized cluster, copy the hbase users keytab to some common dir such as /tmp on all nodes so that app can reference that. Currently, it will require to use hbase/<masterNode> principal and its keytab. It can cab be found from master node  /var/run/cloudera-scm-agent/process/, searching for latest hbase-MASTER dir, hbase.keytab should be present in that dir. (This step will require sudo privelege)

6. Main.java uner src folder contains a sample streaming app that takes the data from kafka stream to the spark stream and then insert the data to splice engine using splice-spark adapter. You will need to get the build artifacts jars and keep it at the common location say /tmp/dependency on all nodes. Also copy spark jars to /tmp/dependency from the installed base (for cloudera from /opt/cloudera/parcels/SPARK2/lib/spark2/jars, make sure spark-streaming jar is also copied)

To compile the app, use 
$ mvn clean install

To start the app, launch the spark-submit command with the example mentioned in spark-submit.sh script. You may need to update some config parameters such as kerberos principal, keytab filename, table and schema to insert the data etc. 

To match the schema of the sample app, do create following table the region server  with following schema:
CREATE TABLE TEST_TABLE (COL1 CHAR(30), COL2 INTEGER, COL3 BOOLEAN);

7. Launch the app, by starting spark-submit.sh script, uou can monitor the progress on <namenode>:8088 page to see if the app is launched properly or not, check the logs and look at the stderr for errors

8. Once the app goes to RUNNING state, you may want to start the kafka stream. You could use the kafka-producer/run_prod.sh which will send the kafka producer batch of rows (kafka-producer/stream_rows.sh script can be used to loop through to send the stream of data with few seconds of interval)

9. Logon to region server, and see that the count on 'test_table' is being updated.


