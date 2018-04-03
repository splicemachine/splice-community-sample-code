/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package main.java.com.splicemachine.tutorials.spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



/**
 * Spark Streaming Application that reads messages from a Kakfa queue using
 * a Spark Stream, then inserts that data into a Splice Machine table using
 * a VTI
 * 
 * @author Erin Driggers
 *
 */
public class SparkStreamingKafka {

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark application's log file
     */
    private static final Logger LOG = Logger.getLogger(SparkStreamingKafka.class);
    
    /**
     * This object serves as the main entry point for all Spark Streaming functionality.
     */
    private JavaStreamingContext jssc = null;    
    
    /**
     * Spark Streaming needs to checkpoint enough information to a fault- tolerant storage 
     * system (HDFS) such that it can recover from failures. It saves Metadata and data.
     * This is the directory on HDFS where the data is stored
     */
    String checkpointDirectory = "/checkpoint/";
    
    
    /**
     */
    private String brokerList;
    
    /**
     */
    private String zookeeperServer;
    
    /**
     * Kafka scales topic consumption by distributing partitions among a consumer group, 
     * which is a set of consumers sharing a common group identifier.  You could
     * start multiple spark jobs reading from the same topic and specify the same
     * consumer group to increase performance and ensure that each message is read
     * only once. 
     */
    private String kafkaConsumerGroup;
    
    /**
     * Array list of topics.  More than likely it will only be one topic that
     * is being consumed by this process, but if you decided to have multiple topics, 
     * you can use this same code
     */
    private String topicList;
    
    /**
     * This property is a per-topic number of Kafka partitions to consume.
     * It increases the number of threads per topic that are consumed within a single consumer
     */
    private int numThreadsPerTopic;
    
    /**
     * The time interval at which streaming data will be divided into batches
     */
    private int sparkBatchInterval;
    
    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;

    
    /**
     * The name of the schema where you want to insert data.  This
     * is only required if the vtitype = vtiname
     */
    private String spliceSchema;
    
    /**
     * The name of the table where you want to insert data.  This
     * is only required if the vtitype = vtiname
     */
    private String spliceTable;

    /**
     * The character delimiter
     */
    private String characterDelimiter="\"";
    /**
     * The column delimiter
     */
    private String columnDelimiter=",";
    /**
     * The format for the time in the incoming data
     */
    private String timeFormat="HH:mm:ss";
    /**
     * The format for the date in the incoming data
     */
    private String dateTimeFormat="yyyy-MM-dd";
    
    /**
     * The format for the timestamps in the incoming data
     */
    private String timestampFormat="yyyy-MM-dd HH:mm:ss";


    private String maxRate = "5000";
    
    /**
     * args[]
     *     args[0] - Kafka Broker list
     *     args[1] - Kafka Consumer Group
     *     args[2] - Comma delimited list of topics
     *     args[3] - Number of seconds between Spark RDD reads
     *     args[4] - The Splice Machine JDBC URL
     *     args[5] - Splice Schema
     *     args[6]- Splice Table
     *     args[7] - Character Delimiter
     *     args[8] - Column Delimiter
     *     args[9] - Time Format
     *     args[10] - DateTime Format
     *     args[11] - Timestamp Format
     *     
     * @param args
     */
    public static void main(String[] args) {
        try {

            LOG.info("Begin example 1");

            if(args != null && args.length > 0) {
                for (int i=0; i<args.length; i++) {
                    LOG.info("************ args:" + i + "=" + args[i]);
                }
            }


            SparkStreamingKafka standalone = new SparkStreamingKafka();
            standalone.setBrokerList(args[0]);
            standalone.setKafkaConsumerGroup(args[1]);
            standalone.setTopicList(args[2]);
            standalone.setSparkBatchInterval(Integer.parseInt(args[3]));           
            standalone.setSpliceJdbcUrl(args[4]);
            standalone.setSpliceSchema(args[5]);
            standalone.setSpliceTable(args[6]);
            standalone.setMaxRate(args[7]);
            if(args.length > 8) {
                standalone.setCharacterDelimiter(args[8]);
                standalone.setColumnDelimiter(args[9]);
                standalone.setTimeFormat(args[10]);
                standalone.setDateTimeFormat(args[11]);
                standalone.setTimestampFormat(args[12]);
            } else {
                standalone.setCharacterDelimiter(null);
                standalone.setColumnDelimiter(null);
                standalone.setTimeFormat(null);
                standalone.setDateTimeFormat(null);
                standalone.setTimestampFormat(null);
            }
            standalone.processKafka();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
        
    /**
     * Start the spark streaming job to process the kafka queue
     */
    public void processKafka() throws InterruptedException{
        LOG.info("************ SparkStreamingKafka.processKafka start");
        
       // Create the spark application
        SparkConf sparkConf = new SparkConf();
        sparkConf.set("spark.streaming.backpressure.enabled","true");
        sparkConf.set("spark.streaming.kafka.maxRate",maxRate);  //For some reason the initial records are 10,000
        sparkConf.set("spark.streaming.kafka.maxRatePerPartition",maxRate);

        LOG.info("************ Spark configuration:" + sparkConf.toDebugString());
        LOG.info("************ Parameters:");
        printInstanceVariables();


        //To express any Spark Streaming computation, a StreamingContext object needs to be created. 
        //This object serves as the main entry point for all Spark Streaming functionality.
        //This creates the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(this.getSparkBatchInterval()));

        //Set an HDFS for periodic checkpointing of the intermediate data.
        //jssc.checkpoint(checkpointDirectory);
        
        //For debugging purposes - print the instance variables
        printInstanceVariables();

        //Create a hashma of the import paramters
        HashMap<String,String> importParms = new HashMap<String,String>();
        importParms.put("characterDelimiter", characterDelimiter);
        importParms.put("columnDelimiter", columnDelimiter);
        importParms.put("timeFormat", timeFormat);
        importParms.put("dateTimeFormat", dateTimeFormat);
        importParms.put("timestampFormat", timestampFormat);


        //List of parameters
        //For a complete list of available settings see: http://kafka.apache.org/documentation.html#newconsumerconfigs
        Map<String, Object> kafkaParams = getKafkaParameters();
      
        
        //List of kafka topics to process
        Collection<String> topics = Arrays.asList(this.getTopicList().split(","));

        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
            jssc,
            LocationStrategies.PreferConsistent(),
            ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
          );


        JavaDStream<String> lines = messages.map(ConsumerRecord::value);

        //Process the messages on the queue and save them to the database
        //The foreachRDD method gives you access to the RDDs in a DStream
        //and applies the passed in function (in this case SaveRDDWithPartition)
        //to each RDD in the source DStream.  The foreachRDD is executed on 
        //the driver node and does not return a value.

        lines.foreachRDD(new SaveRDDWithPartition(this.getSpliceJdbcUrl(), this.getSpliceSchema(), this.getSpliceTable(), importParms));



        // Start running the job to receive and transform the data
        LOG.info(">>>>>>>>>>> Start streaming");
        jssc.start();
        
        //Allows the current thread to wait for the termination of the context by stop() or by an exception
        jssc.awaitTermination();
    }
    
    public Map<String,Object> getKafkaParameters() {
        
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", this.getBrokerList());
        kafkaParams.put("group.id", this.getKafkaConsumerGroup());
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", false);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        return kafkaParams;
    }

    public String getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }
    
    public String getTopicList() {
        return topicList;
    }

    public void setTopicList(String topicList) {
        this.topicList = topicList;
    }
    
    /**
     * Prints the instance variables for debugging purposes
     */
    public void printInstanceVariables() {
        LOG.info("************ brokerList:" + getBrokerList());
        LOG.info("************ kafkaConsumerGroup:" + getKafkaConsumerGroup());
        LOG.info("************ topicList:" + this.getTopicList());
        LOG.info("************ schema:" + getSpliceSchema());
        LOG.info("************ table:" + getSpliceTable());
        LOG.info("************ numThreadsPerTopic:" + getNumThreadsPerTopic());
        LOG.info("************ maxRate:" + getMaxRate());
        LOG.info("************ sparkBatchInterval:" + getSparkBatchInterval());
        LOG.info("************ spliceJdbcUrl:" + getSpliceJdbcUrl());
        LOG.info("************ characterDelimiter:" + characterDelimiter);
        LOG.info("************ columnDelimiter:" + columnDelimiter);
        LOG.info("************ timeFormat:" + timeFormat);
        LOG.info("************ dateTimeFormat:" + dateTimeFormat);
        LOG.info("************ timestampFormat:" + timestampFormat);

    }

    private String getMaxRate() { return maxRate;}

    private void setMaxRate(String maxRate) { this.maxRate = maxRate;}

    private String getZookeeperServer() {
        return zookeeperServer;
    }

    private void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    private String getKafkaConsumerGroup() {
        return kafkaConsumerGroup;
    }

    private void setKafkaConsumerGroup(String kafkaConsumerGroup) {
        this.kafkaConsumerGroup = kafkaConsumerGroup;
    }


    private int getNumThreadsPerTopic() {
        return numThreadsPerTopic;
    }

    private void setNumThreadsPerTopic(int numThreadsPerTopic) {
        this.numThreadsPerTopic = numThreadsPerTopic;
    }

    private int getSparkBatchInterval() {
        return sparkBatchInterval;
    }

    private void setSparkBatchInterval(int sparkBatchInterval) {
        this.sparkBatchInterval = sparkBatchInterval;
    }

    private String getSpliceJdbcUrl() {
        return spliceJdbcUrl;
    }

    private void setSpliceJdbcUrl(String spliceJdbcUrl) {
        this.spliceJdbcUrl = spliceJdbcUrl;
    }

    private String getSpliceSchema() {
        return spliceSchema;
    }

    private void setSpliceSchema(String spliceSchema) {
        this.spliceSchema = spliceSchema;
    }

    private String getSpliceTable() {
        return spliceTable;
    }

    private void setSpliceTable(String spliceTable) {
        this.spliceTable = spliceTable;
    }

    public String getCharacterDelimiter() {
        return characterDelimiter;
    }

    public void setCharacterDelimiter(String characterDelimiter) {
        if (characterDelimiter == null || "null".equals(characterDelimiter))
            this.characterDelimiter = "\"";
        else
            this.characterDelimiter = characterDelimiter;
    }

    public String getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(String columnDelimiter) {
        if (columnDelimiter == null || "null".equals(columnDelimiter))
            this.columnDelimiter = ",";
        else
            this.columnDelimiter = columnDelimiter;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        if (timeFormat == null || "null".equals(timeFormat))
            this.timeFormat = "HH:mm:ss";
        else
            this.timeFormat = timeFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        if (dateTimeFormat == null || "null".equals(dateTimeFormat))
            this.dateTimeFormat = "yyyy-MM-dd";
        else
            this.dateTimeFormat = dateTimeFormat;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        if (timestampFormat == null || "null".equals(timestampFormat))
            this.timestampFormat = "yyyy-MM-dd HH:mm:ss";
        else
            this.timestampFormat = timestampFormat;
    }

}

