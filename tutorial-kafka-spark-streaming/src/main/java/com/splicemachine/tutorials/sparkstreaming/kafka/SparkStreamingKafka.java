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
package com.splicemachine.tutorials.sparkstreaming.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.Durations;

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
    private String[] topicList;
    
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
     * args[]
     *     args[0] - Zookeeper Server
     *     args[1] - Kafka Consumer Group
     *     args[2] - Comma delimited list of topics
     *     args[3] - Number of threads per topic
     *     args[4] - Number of seconds between Spark RDD reads
     *     args[5] - The Splice Machine JDBC URL
     * @param args
     */
    public static void main(String[] args) {
        SparkStreamingKafka spark = new SparkStreamingKafka();
        spark.setZookeeperServer(args[0]);
        spark.setKafkaConsumerGroup(args[1]);
        spark.setTopicList(args[2].split(","));
        spark.setNumThreadsPerTopic(Integer.parseInt(args[3]));
        spark.setSparkBatchInterval(Integer.parseInt(args[4]));
        spark.setSpliceJdbcUrl(args[5]);        
        spark.processKafka();
   }
        
    /**
     * Stars the spark streaming job to process the kafka queue
     */
    public void processKafka() {
        LOG.info("************ SparkStreamingKafka.processKafka start");
        
       // Create the spark application
        SparkConf sparkConf = new SparkConf();
        
        //To express any Spark Streaming computation, a StreamingContext object needs to be created. 
        //This object serves as the main entry point for all Spark Streaming functionality.
        //This creates the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(this.getSparkBatchInterval()));
        
        //Set an HDFS for periodic checkpointing of the intermediate data.
        jssc.checkpoint(checkpointDirectory);
        
        //For debugging purposes - print the instance variables
        printInstanceVariables();

        Map<String, Integer> topicMap = new HashMap<>();
        for (String topic: this.getTopicList()) {
          topicMap.put(topic, this.getNumThreadsPerTopic());
        }


        //Create an input stream that pulls messages from Kafka Brokers.
        //Creates a Discretized Stream (DStream), the basic abstraction in Spark Streaming, is a continuous 
        //sequence of RDDs (of the same type) representing a continuous stream of data 
        JavaPairDStream<String, String> messages = KafkaUtils.createStream(jssc, this.getZookeeperServer(), this.getKafkaConsumerGroup(), topicMap);
        
        //Convert each tuple into a single string.  We want the second tuple
        //The map method applies a function (in this case TupleFunction) to all
        //elements in the collection and returns another collection
        JavaDStream<String> lines = messages.map(new TupleFunction());
               
        //Process the messages on the queue and save them to the database
        //The foreachRDD method gives you access to the RDDs in a DStream
        //and applies the passed in function (in this case SaveRDDWithPartition)
        //to each RDD in the source DStream.  The foreachRDD is executed on 
        //the driver node and does not return a value.
        lines.foreachRDD(new SaveRDDWithPartition(this.getSpliceJdbcUrl()));

        
        // Start running the job to receive and transform the data 
        jssc.start();
        
        //Allows the current thread to wait for the termination of the context by stop() or by an exception
        jssc.awaitTermination();
    }
    
    /**
     * Prints the instance variables for debugging purposes
     */
    public void printInstanceVariables() {
        LOG.info("************ zookeeperServer:" + zookeeperServer);
        LOG.info("************ kafkaConsumerGroup:" + kafkaConsumerGroup);
        LOG.info("************ topicList:" + this.getTopicList());
        LOG.info("************ numThreadsPerTopic:" + numThreadsPerTopic);
        LOG.info("************ sparkBatchInterval:" + sparkBatchInterval);
        LOG.info("************ spliceJdbcUrl:" + spliceJdbcUrl);
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

    public String[] getTopicList() {
        return topicList;
    }

    public void setTopicList(String[] topicList) {
        this.topicList = topicList;
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
    
    
}
