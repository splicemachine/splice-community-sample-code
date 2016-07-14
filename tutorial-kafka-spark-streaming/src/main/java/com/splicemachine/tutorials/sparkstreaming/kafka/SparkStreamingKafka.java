package com.splicemachine.tutorials.sparkstreaming.kafka;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import kafka.serializer.StringDecoder;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.Durations;


public class SparkStreamingKafka {

    private static final Logger LOG = Logger.getLogger(SparkStreamingKafka.class);
    
    //This is a global static variable so that we can stop the stream
    //from the stored procedure
    private JavaStreamingContext jssc = null;    
    
    // HDFS directory for checkpointing
    String checkpointDirectory = "/checkpoint/";
    
    public static void main(String[] args) {
        SparkStreamingKafka standalone = new SparkStreamingKafka();
        
        //Standard Stream
        if(args.length >= 5) {
            if(args.length == 6) {
                standalone.setLogging(args[5]);
            }
            standalone.processKafka(args[0], args[1], args[2].split(","), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args.length == 3) {
            //Direct Stream
            standalone.processKafka(args[0], args[1], Integer.parseInt(args[2]));            
        }       
   }
    
    public void setLogging(String log) {
        Logger.getLogger("com.splicemachine.tutorials.sparkstreaming.kafka").setLevel(Level.TRACE);
    }
    
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
        for (String topic: topics) {
            LOG.info("topic:" + topic);
          topicMap.put(topic, numThreads);
        }
        
        

        LOG.info("************ SparkStreamingKafka.processKafka about to read the MQTTUtils.createStream");
        //2. KafkaUtils to collect Kafka messages
        JavaPairDStream<String, String> messages = KafkaUtils.createStream(jssc, zookeeper, group, topicMap);
        
        //Convert each tuple into a single string.  We want the second tuple
        JavaDStream<String> lines = messages.map(new TupleFunction());
        
        LOG.info("************ lines:");
        
        lines.print();
        
        LOG.info("************ SparkStreamingKafka.processKafka about to do foreachRDD");
        //process the messages on the queue and save them to the database
        lines.foreachRDD(new SaveRDDWithVTI());

        
        LOG.info("************ SparkStreamingKafka.processKafka prior to context.strt");
        // Start the context
        jssc.start();
        jssc.awaitTermination();
    }
    
    public void processKafka(final String brokers, final String topics, final int numSeconds) {
        LOG.info("************ SparkStreamingKafka.processKafka start");
        /*
       // Create the spark application and set the name to MQTT
        SparkConf sparkConf = new SparkConf().setAppName("KAFKA");
        
        // Create the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(numSeconds));
        jssc.checkpoint(checkpointDirectory);
        
        if(LOG.getLevel().toInt() >= Level.INFO_INT) {
            LOG.info("brokers:" + brokers);
            LOG.info("topics:" + topics);
            LOG.info("numSeconds:" + numSeconds);
        }
        
        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        Map<String, String> kafkaParams = new HashMap<>();
        //kafkaParams.put("metadata.broker.list", brokers);
        kafkaParams.put("bootstrap.servers", brokers);

        LOG.info("************ SparkStreamingKafka.processKafka about to read the MQTTUtils.createStream");
        //2. KafkaUtils to collect Kafka messages
        JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
            );
     

        //Convert each tuple into a single string.  We want the second tuple
        JavaDStream<String> lines = messages.map(new TupleFunction());
        
        LOG.info("************ SparkStreamingKafka.processKafka about to do foreachRDD");
        //process the messages on the queue and save them to the database
        lines.foreachRDD(new SaveRDDWithVTI());

        
        LOG.info("************ SparkStreamingKafka.processKafka prior to context.strt");
        // Start the context
        jssc.start();
        jssc.awaitTermination();
        */
    }
}
