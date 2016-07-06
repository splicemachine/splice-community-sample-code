package com.splicemachine.tutorials.sparkstreaming.vti;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import com.splicemachine.derby.impl.SpliceSpark;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContextFactory;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

import com.splicemachine.tutorials.sparkstreaming.*;


public class SparkStreaming implements Externalizable {
    
    final static String checkpointDirectory = "/data";
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SparkStreaming.class);
    
    //This is a global static variable so that we can stop the stream
    //from the stored procedure
    private static JavaStreamingContext context = null;
    
    /**
     * This will start the spark stream that is reading from the kafka queue
     * 
     * @param zkQuorum
     * @param group
     * @param topicList
     * @param numThreads
     */
    public static void processSensorStream(final String zkQuorum, final String group, final String topicList, final int numThreads) {


        LOG.info("************ processKafkaStream start");

        // Create a factory object that can create a and setup a new JavaStreamingContext
        JavaStreamingContextFactory contextFactory = new JavaStreamingContextFactory() {
            @Override
            public JavaStreamingContext create() {

                LOG.info("************ processAKafkaStream about to create JavaStreamingContext");
                
                JavaSparkContext spliceSparkContext = SpliceSpark.getContext();
                JavaStreamingContext jssc = new JavaStreamingContext(spliceSparkContext, new Duration(2000));
                jssc.ssc().conf().set("spark.io.compression.codec","LZF");
                
                
                LOG.info("************ processAKafkaStream about to create topic list map");

                //Get the Kafka topic names.  If you have multiple topics then
                //we want to separate them with commas.

                Map<String, Integer> topicMap = new HashMap<>();
                String[] topics = topicList.split(",");
                for (String topic : topics) {
                    LOG.info("Topic: " + topic);
                    LOG.info("Threads: " + numThreads);
                    topicMap.put(topic, numThreads);
                }
                
                LOG.info("************ processAKafkaStream about to read the kafkautil.createStream");

                //Read the data that is on Kafka
                JavaPairDStream<String, String> messages = KafkaUtils.createStream(jssc, zkQuorum, group, topicMap);
                
                LOG.info("************ processAKafkaStream about to map");
                //input.print();

                //Create the DStream
                //JavaDStream<String> lines = messages.map(new TupleFunction());
                JavaDStream<String> lines = messages.map(new TupleFunction());
                LOG.info("************ processAKafkaStream about to print");      
                
                //JR Save
                lines.foreachRDD(new SaveRDDWithVTI());

                LOG.info("************ processAKafkaStream about to checkpoint");

                //jssc.checkpoint(checkpointDirectory);                       // set checkpoint directory
                return jssc;
            }
        };
        
        //TODO create connectionpool
       
        // Get JavaStreamingContext from checkpoint data or create a new one
        context = JavaStreamingContext.getOrCreate(checkpointDirectory, contextFactory);

        // Do additional setup on context that needs to be done,
        // irrespective of whether it is being started or restarted
        //context. ...

        LOG.info("************ processAKafkaStream prior to context.strt");

        // Start the context
        context.start();

    }


    /**
     * This is a placeholder for a future stored procedure where you can
     * stop a named spark stream.
     * 
     * @param streamName
     */
    public static void stopSensorStream() {
        
        LOG.info("************ stopStream start:");
                
        if(context != null) {
            LOG.info("************ stopStream about to do context.stop");       
            context.stop(false, true);
        }

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub
        
    }
    
}
