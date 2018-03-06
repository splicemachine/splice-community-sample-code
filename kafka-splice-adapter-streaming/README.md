## Native Spark DataSource API Example:

Here are the steps to make a simple streaming app that will produce rows from a kafka producer, and consumed using kafka spark streaming and then inserted in batches into the splice engine using Native Spark DataSource APIs:

1. Download kafka_2.10-0.10.0.1 and untar in the home_dir.

1. Start kafka server in one window (or start with nohup command and keep it running in background)
$ bin/kafka-server-start.sh  config/server.properties

2. Register the kafka topic  (Need to do it only once), e.g. to start "test-k" topic, following command is used:
$ bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test-k --partitions 2 --replication-factor 1

4. Keep kafka producer stream ready (kafka-producer/run_prod.sh), it can be used to stream some mock data into this streaming app 

5. Download the Native Spark DataSource API jar file to the current directory from  http://repository.splicemachine.com/nexus/content/groups/public/com/splicemachine, choose the folder version that is matching to the build installed on the cluster. E.g. splicemachine-cdh5.8.3-2.1.0_2.11-2.5.0.<timestamp>.jar 

6. Also copy the spark-streaming-kafka-0-10_2.11-2.2.0.cloudera1.jar to the current dir from SPARK2_HOME

7. If running on kerberized cluster, copy the hbase user keytab file to some dir so the app can reference that. Currently, it will require to use hbase/<masterNode> principal and its keytab. It can be found from master node  /var/run/cloudera-scm-agent/process/, searching for latest hbase-MASTER dir, hbase.keytab should be present in that dir. (This step will require sudo privilege)

8. Main.java in src folder contains the sample streaming app that would consume the data from kafka stream to the spark stream and then insert the data to splice engine using insert() Native Spark DataSource API. Look more APIs at the end of this doc:

To compile the app:
$ mvn clean install

To start the app, launch the spark-submit command with the example mentioned in spark-submit.sh script. You may need to update some config parameters such as kerberos principal, keytab filename, table and schema to insert the data etc. 
To match the schema of the sample app, do create following table the region server  with following schema:
CREATE TABLE TEST_TABLE (COL1 CHAR(30), COL2 INTEGER, COL3 BOOLEAN);

9. Launch the app, by starting spark-submit.sh script, uou can monitor the progress on <namenode>:8088 page to see if the app is launched properly or not, check the logs and look at the stderr for errors

10. Once the app goes to RUNNING state, you may want to start the kafka stream. You could use the kafka-producer/run_prod.sh which will send the kafka producer batch of rows (kafka-producer/stream_rows.sh script can be used to loop through to send the stream of data with few seconds of interval)

11. Logon to region server, and see that the count on 'test_table' is being updated.



## Native Spark DataSource API reference in scala:
```
  /**
    * Get the current connection 
    *
    * @returns currennt connection
    */
  def getConnection(): Connection

  /**
    * Check if table already exists in schema
    *
    * @param schemaTableName - The Schema.tablename string
    * @return true if exists, false otherwise
    */
  def tableExists(schemaTableName: String): Boolean 

  /**
    * Check if table already exists in schema
    *
    * @param schemaName - The Schemaname string
    * @param tableName - The tablename string
    * @return true if exists, false otherwise
    */
  def tableExists(schemaName: String, tableName: String): Boolean 

  /**
    * Drop a table
    * @param schemaName - The Schemaname string
    * @param tableName - The tablename string
    */
  def dropTable(schemaName: String, tableName: String): Unit 

  /**
    * Drop a table
    * @param schemaTableName - The Schema.tablename string
    *
    */
  def dropTable(schemaTableName: String): Unit 

  /**
    * Create a table 
    *
    * @param tableName - Name of table
    * @param structType - schema of table
    * @param keys - Sequence of columnn names
    * @param createTableOptions - 
    */
  def createTable(tableName: String,
                  structType: StructType,
                  keys: Seq[String],
                  createTableOptions: String): Unit 

  /**
    * Get the results of a sql in dataframe format
    *
    * @param sql - Sql to execute
    * @return rows after sql execution in dataframe
    */
  def df(sql: String): Dataset[Row] 

  /** 
    * Get the select columns data in a rdd form
    *
    * @param schemaTableName - The Schema.tablename string
    * @param columnProjection - The columns to be retrieved
    * @return rdd of rows with only projected columns 
    */
  def rdd(schemaTableName: String, columnProjection: Seq[String] = Nil): RDD[Row]

  /** 
    * Insert the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to insert
    * @param schemaTableName - The Schema.tablename string
    */
  def insert(dataFrame: DataFrame, schemaTableName: String): Unit 

  /** 
    * Insert the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to insert
    * @param schema  - The Catalyst schema of the master table
    * @param schemaTableName - The Schema.tablename string
    */
  def insert(rdd: JavaRDD[Row], schema: StructType, schemaTableName: String): Unit 

  /** 
    * Update and insert the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to update/insert
    * @param schemaTableName - The Schema.tablename string
    */
  def upsert(dataFrame: DataFrame, schemaTableName: String): Unit 

  /** 
    * Update and insert the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to update/insert
    * @param schema  - The Catalyst schema of the master table
    * @param schemaTableName - The Schema.tablename string
    */
  def upsert(rdd: JavaRDD[Row], schema: StructType, schemaTableName: String): Unit 

  /** 
    * Delete the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to delete
    * @param schemaTableName - The Schema.tablename string
    */
  def delete(dataFrame: DataFrame, schemaTableName: String): Unit 

  /** 
    * Delete the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to delete
    * @param schema  - The Catalyst schema of the master table
    * @param schemaTableName - The Schema.tablename string
    */
  def delete(rdd: JavaRDD[Row], schema: StructType, schemaTableName: String): Unit 

  /** 
    * Update the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to update
    * @param schemaTableName - The Schema.tablename string
    */
  def update(dataFrame: DataFrame, schemaTableName: String): Unit 

  /** 
    * Update the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to update
    * @param schema  - The Catalyst schema of the master table
    * @param schemaTableName - The Schema.tablename string
    */
  def update(rdd: JavaRDD[Row], schema: StructType, schemaTableName: String): Unit 

  /** 
    * Use bulk import mechanism in the backend to insert the dataframe to the tablename 
    *
    * @param dataFrame - Dataframe to insert
    * @param schemaTableName - The Schema.tablename string
    * @param options - Map of options such as "useSpark", "skipSampling" etc.
    */
  def bulkImportHFile(dataFrame: DataFrame, schemaTableName: String,
                       options: scala.collection.mutable.Map[String, String]): Unit

  /** 
    * Use bulk import mechanism in the backend to insert the dataframe to the tablename of form schema.table
    *
    * @param dataFrame - dataframe to insert
    * @param schemaTableName - The Schema.tablename string
    * @param schema  - The Catalyst schema of the master table
    * @param options - Map of options such as "useSpark", "skipSampling" etc.
    */
  def bulkImportHFile(rdd: JavaRDD[Row], schema: StructType, schemaTableName: String,
                       options: scala.collection.mutable.Map[String, String]): Unit

  /** 
    * Get the schema of the tablename
    *
    * @param schemaTableName - The Schema.tablename string
    * @return A Catalyst schema corresponding to columns in the given order.
    */
  def getSchema(schemaTableName: String): StructType 

  /**
    * Prune all but the specified columns from the specified Catalyst schema.
    *
    * @param schema  - The Catalyst schema of the master table
    * @param columns - The list of desired columns
    * @return A Catalyst schema corresponding to columns in the given order.
    */
  def pruneSchema(schema: StructType, columns: Array[String]): StructType 
```
