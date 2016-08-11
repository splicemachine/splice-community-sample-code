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
package com.splicemachine.tutorials.tsdbanalytics;


import java.util.HashMap;
import java.util.Map;

import kafka.serializer.StringDecoder;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.Durations;

import com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLog;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLogDecoder;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.PublisherGeoKey;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationLog;

/**
 * Spark Streaming Application that reads Impression Logs from a Kakfa queue using
 * a Spark Stream, then aggregates the data and inserts the aggregated data into a Splice Machine table 
 * 
 * @author Jyotsna Ramineni
 *
 */
public class LogAggregator {

    private static final Logger LOG = Logger.getLogger(LogAggregator.class);
    private JavaStreamingContext jssc = null;    
    
    // HDFS directory for checkpointing
    String checkpointDirectory = "/checkpoint/";
    
    /**
     * args[]
     *     args[0] - Kafka Consumer Group
     *     args[1] - topic
     *     args[2] - Zookeeper Server
     *     args[3] - Number of threads per topic
     *     args[4] - Number of seconds between Spark reads
     * @param args
     */
    public static void main(String[] args) {
        LogAggregator logAgg = new LogAggregator();
        logAgg.processKafka(args[0], args[1].split(","), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
   }
        
    /**
     *
     * 
     * @param zookeeper
     * @param group
     * @param topic
     * @param numThreads
     * @param numSeconds
     */
    public void processKafka( final String group, final String[] topics, final String zookeeper, final int numThreads, final int numSeconds) {
        LOG.info("************ SparkStreamingKafka.processKafka start");
        
       // Create the spark application 
        SparkConf sparkConf = new SparkConf().setAppName("LOGAGG");
        
        // Create the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(numSeconds));
        jssc.checkpoint(checkpointDirectory);

        LOG.info("zookeeper:" + zookeeper);
        LOG.info("group:" + group);
        LOG.info("numThreads:" + numThreads);
        LOG.info("numSeconds:" + numSeconds);

        
        //Set properties for Creating Spark  Stream
        Map<String,String> properties = new HashMap<String, String>();
        properties.put("zookeeper.connect",  zookeeper);
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
       

        
        Map<String, Integer> topicMap = new HashMap<>();
        for (String topic: topics) {
          topicMap.put(topic, numThreads);
        }

        LOG.info("************  read the Kafka.createStream");
        // Create tream for IMpression Logs
        JavaPairDStream<String, ImpressionLog> messages =  KafkaUtils.createStream(jssc, String.class, ImpressionLog.class, StringDecoder.class, ImpressionLogDecoder.class, 
        		properties, topicMap, StorageLevel.MEMORY_AND_DISK());
        
        
        LOG.info("Get Logs from Tuple");
        
        //Map from tuples to ImpressionLog DStream.  We want the second tuple
        JavaDStream<ImpressionLog> logs = messages.map(new TupleFunction());
        
        LOG.info("Filter Logs with Unknown Geo");
        //Filter Unknown Geo
        JavaDStream<ImpressionLog> knownGeoLogs =   logs.filter(
        		new FilterGeoFunction());
        
        LOG.info("Map Impression Log to Pair Dtream in preparation for Aggregattion");
        //Map the Impression logs as <PublisherGeoKey, AggregateLog> pairs to prepare data for aggregation. 
        //PublisherGeoKey has fields Publisher and Geo, on which impression logs will be aggregated
        //AggregateLog has fields timestamp, sumBids, imps, UniqueHll, which map to the timestamp, bid, 1, 
        //and HyperLogLog of cookie of Impression Log. These will correspond to Aggregated values.
  
        JavaPairDStream<PublisherGeoKey,AggregationLog> logsByPubGeo = knownGeoLogs.mapToPair(new AggregateFunction());
        
        
        LOG.info("Perform Aggregattion of data");
        // Perform Aggregation by Key and Window size that is 2 times the batch time. SO if batch time is 1 sec, 
        // aggregation is performed every 2 secs.
        // The aggregations calculates are minimum of timestamp, Average of Bid, sum of Imps and Count of Unique Hlls (i.e. users/cookies).
        JavaPairDStream<PublisherGeoKey,AggregationLog> aggLogs = logsByPubGeo.reduceByKeyAndWindow(new ReduceAggregationLogs(),
        		Durations.seconds(numSeconds*2), Durations.seconds(numSeconds *2));
        
        
        LOG.info("Perform Save foreachRDD");
        //Save the aggregated data to splice machine.
        aggLogs.foreachRDD( new SaveLogAggRDD());
        
        
        LOG.info("Prior to context.strt");
        // Start the context
        jssc.start();
        jssc.awaitTermination();
    }

	
}
