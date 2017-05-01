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
package com.splicemachine.tutorials.spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;



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
     * The type of vti being passed in, it is either a full
     * VTI statement or just the vti class name.  Valid values are:
     * vtiname or vtifullstmt
     */
    private String vtitype;
    
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
     * The vti class name, only used when the  vtitype = vtiname
     */
    private String vtiClassName;
    
    /**
     * The vit statement, only used when the vtitype = vtifullstmt
     */
    private String vtiStatement;
    
    /**
     * args[]
     *     args[0] - Zookeeper Server
     *     args[1] - Kafka Consumer Group
     *     args[2] - Comma delimited list of topics
     *     args[3] - Number of threads per topic
     *     args[4] - Number of seconds between Spark RDD reads
     *     args[5] - The Splice Machine JDBC URL
     *     args[6] - type: vtiname or vtifullstmt
     *     args[7] - VTI - If the type = vtiname then the next two args Schema and Table are required.
     *                     Otherwise it is a full VTI statement and nothing is needed.
     *     args[8] - Splice Schema
     *     args[9] - Splice Table
     *     
     * @param args
     */
    public static void main(String[] args) {
        try {
            SparkStreamingKafka standalone = new SparkStreamingKafka();
            standalone.setBrokerList(args[0]);
            standalone.setKafkaConsumerGroup(args[1]);
            standalone.setTopicList(args[2]);
            standalone.setSparkBatchInterval(Integer.parseInt(args[3]));           
            standalone.setSpliceJdbcUrl(args[4]);
            standalone.setVtitype(args[5]);
            if(standalone.isVTIClassName()) {
                standalone.setVtiClassName(args[6]);
                standalone.setSpliceSchema(args[7]);
                standalone.setSpliceTable(args[8]);         
            } else {
                standalone.setVtiStatement(args[6]);
            }
            standalone.processKafka();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
        
    /**
     * Stars the spark streaming job to process the kafka queue
     */
    public void processKafka() throws InterruptedException{
        LOG.info("************ SparkStreamingKafka.processKafka start");
        
       // Create the spark application
        SparkConf sparkConf = new SparkConf();
        
        //To express any Spark Streaming computation, a StreamingContext object needs to be created. 
        //This object serves as the main entry point for all Spark Streaming functionality.
        //This creates the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(this.getSparkBatchInterval()));
        
        //Set an HDFS for periodic checkpointing of the intermediate data.
        //jssc.checkpoint(checkpointDirectory);
        
        //For debugging purposes - print the instance variables
        printInstanceVariables();
        
        
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

        
        JavaPairDStream<String, String> toPair = messages.mapToPair(new TuplePairFunction());
        
        //Convert each tuple into a single string.  We want the second tuple
        //The map method applies a function (in this case TupleFunction) to all
        //elements in the collection and returns another collection
        JavaDStream<String> lines = toPair.map(new TupleFunction());
        
        //Process the messages on the queue and save them to the database
        //The foreachRDD method gives you access to the RDDs in a DStream
        //and applies the passed in function (in this case SaveRDDWithPartition)
        //to each RDD in the source DStream.  The foreachRDD is executed on 
        //the driver node and does not return a value.


        if(this.isVTIClassName()) {
            lines.foreachRDD(new SaveRDDWithPartition(this.getSpliceJdbcUrl(), this.getSpliceSchema(), this.getSpliceTable(), this.getVtiClassName()));
        } else {
            lines.foreachRDD(new SaveRDDWithPartition(this.getSpliceJdbcUrl(), this.getVtiStatement()));
        }
        
                
        // Start running the job to receive and transform the data 
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
        LOG.info("************ numThreadsPerTopic:" + getNumThreadsPerTopic());
        LOG.info("************ sparkBatchInterval:" + getSparkBatchInterval());
        LOG.info("************ spliceJdbcUrl:" + getSpliceJdbcUrl());
        LOG.info("************ vtiType:" + getVtitype());
        if(this.isVTIClassName()) {
            LOG.info("************ vtiClassName:" + getVtiClassName());
            LOG.info("************ spliceSchema:" + getSpliceSchema());
            LOG.info("************ spliceTable:" + getSpliceTable());         
        } else {
            LOG.info("************ vtiStatement:" + getVtiStatement());
        }
    }

    public String getZookeeperServer() {
        return zookeeperServer;
    }

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    public String getKafkaConsumerGroup() {
        return kafkaConsumerGroup;
    }

    public void setKafkaConsumerGroup(String kafkaConsumerGroup) {
        this.kafkaConsumerGroup = kafkaConsumerGroup;
    }


    public int getNumThreadsPerTopic() {
        return numThreadsPerTopic;
    }

    public void setNumThreadsPerTopic(int numThreadsPerTopic) {
        this.numThreadsPerTopic = numThreadsPerTopic;
    }

    public int getSparkBatchInterval() {
        return sparkBatchInterval;
    }

    public void setSparkBatchInterval(int sparkBatchInterval) {
        this.sparkBatchInterval = sparkBatchInterval;
    }

    public String getSpliceJdbcUrl() {
        return spliceJdbcUrl;
    }

    public void setSpliceJdbcUrl(String spliceJdbcUrl) {
        this.spliceJdbcUrl = spliceJdbcUrl;
    }

    public String getVtitype() {
        return vtitype;
    }

    public void setVtitype(String vtitype) {
        this.vtitype = vtitype;
    }

    public String getSpliceSchema() {
        return spliceSchema;
    }

    public void setSpliceSchema(String spliceSchema) {
        this.spliceSchema = spliceSchema;
    }

    public String getSpliceTable() {
        return spliceTable;
    }

    public void setSpliceTable(String spliceTable) {
        this.spliceTable = spliceTable;
    }

    public String getVtiClassName() {
        return vtiClassName;
    }

    public void setVtiClassName(String vtiClassName) {
        this.vtiClassName = vtiClassName;
    }

    public String getVtiStatement() {
        return vtiStatement;
    }

    public void setVtiStatement(String vtiStatement) {
        this.vtiStatement = vtiStatement;
    }
    
    public boolean isVTIClassName() {
        if("vtiname".equalsIgnoreCase(getVtitype())) {
            return true;
        }
        return false;
    }
    
}
