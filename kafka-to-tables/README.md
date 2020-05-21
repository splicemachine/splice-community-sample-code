## kafka-to-tables

This example reads from multiple Kafka queues, each queue is mapped to a target table.
JSON messages from each queue are mapped into Row where JSON fields are mapped to table columns by name.
JSON values are converted to the matching target column data type.
Nested JSON values are converted to strings and can be loaded into VARCHAR or CLOB target columns.
Source JSON fields which are not on the table are ignored.
Target tables columns which are not in the inbound JSON message are set to their default value or null if not available.

## Build
To build the kafka-to-tables jar:
$ mvn clean package

## Spark Submit
To start the streaming application on Spark:
1. Build kafka-to-tables jar and copy it into a work folder.
2. Copy jar and scripts from src/main/resources/scripts/* into work folder.
3. Edit copy of setenv.sh in work

3.1. Change environment variables according to cluster configuration:
  ```
  export SPARK_HOME="/opt/cloudera/parcels/SPARK2/lib/spark2"
  export CLOUDERA_KAFKA="/opt/cloudera/parcels/SPARK2/lib/spark2/kafka-0.10/*"
  export HBASE_JAR="/opt/cloudera/parcels/CDH/lib/hbase/lib/*"
  export SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"
  ```
3.2. Change application parameter variables
  Set JDBC URL and Kafka Broker URL:
  ```
  export SPLICE_JDBC_URL="jdbc:splice://hostname:1527/splicedb;user=splice;password=admin"
  export KAFKA_BROKER="kafkahostname:9092"
  ```
  Optionally change Kafka Consumer Group Name and Maximum Rate:
  ```
  export KAFKA_CONSUMER_GROUP="CONSUMER_GROUP1"
  export MAX_RATE="30000"
  ```
  Configure kafka topic to table mapping comma-delimited list where each mapping had format:
    ```topicname:offset>schemaname.tablename```
  Example:
    ```
    TOPIC_MAPPING=sales-topic:earliest>RETAIL.SALESTRANSACTION,customer-topic:earliest>RETAIL.CUSTOMER
    ```

4. Make sure target tables referenced in TOPIC_MAPPING exist in the database.

5. Start streaming application from work folder by running
    ```
    ./start-spark-streaming.sh
    ```
