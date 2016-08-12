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
 */
public class SparkStreamingKafka {

    private static final Logger LOG = Logger.getLogger(SparkStreamingKafka.class);
    // HDFS directory for checkpointing
    String checkpointDirectory = "/checkpoint/";
    private JavaStreamingContext jssc = null;

    /**
     * args[]
     * args[0] - Zookeeper Server
     * args[1] - Kafka Consumer Group
     * args[2] - Comma delimited list of topics
     * args[3] - Number of threads per topic
     * args[4] - Number of seconds between Spark RDD reads
     *
     * @param args
     */
    public static void main(String[] args) {
        SparkStreamingKafka standalone = new SparkStreamingKafka();
        standalone.processKafka(args[0], args[1], args[2].split(","), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
    }

    /**
     * @param zookeeper
     * @param group
     * @param topics
     * @param numThreads
     * @param numSeconds
     */
    public void processKafka(final String zookeeper, final String group, final String[] topics, final int numThreads, final int numSeconds) {
        LOG.info("************ SparkStreamingKafka.processKafka start");

        // Create the spark application and set the name to MQTT
        SparkConf sparkConf = new SparkConf().setAppName("KAFKA");

        // Create the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(numSeconds));
        jssc.checkpoint(checkpointDirectory);

        LOG.info("zookeeper:" + zookeeper);
        LOG.info("group:" + group);
        LOG.info("numThreads:" + numThreads);
        LOG.info("numSeconds:" + numSeconds);


        Map<String, Integer> topicMap = new HashMap<>();
        for (String topic : topics) {
            LOG.info("topic:" + topic);
            topicMap.put(topic, numThreads);
        }

        LOG.info("************ SparkStreamingKafka.processKafka about to read the MQTTUtils.createStream");
        //2. KafkaUtils to collect Kafka messages
        JavaPairReceiverInputDStream<String, String> messages = KafkaUtils.createStream(jssc, zookeeper, group, topicMap);

        //Convert each tuple into a single string.  We want the second tuple
        JavaDStream<String> lines = messages.map(new TupleFunction());

        LOG.info("************ SparkStreamingKafka.processKafka about to do foreachRDD");
        //process the messages on the queue and save them to the database
        lines.foreachRDD(new SaveRDDWithVTI());


        LOG.info("************ SparkStreamingKafka.processKafka prior to context.strt");
        // Start the context
        jssc.start();
        jssc.awaitTermination();
    }
}
