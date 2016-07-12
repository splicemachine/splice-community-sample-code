package com.splicemachine.tutorials.mqtt;

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

import org.apache.spark.streaming.mqtt.MQTTUtils;

import org.apache.spark.SparkConf;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContextFactory;


import scala.Tuple2;



public class SparkStreamingMQTT implements Externalizable {
        
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SparkStreamingMQTT.class);
    
    //This is a global static variable so that we can stop the stream
    //from the stored procedure
    private JavaStreamingContext jssc = null;    
    
    // HDFS directory for checkpointing
    String checkpointDirectory = "/checkpoint/";
    
    public static void main(String[] args) {
        
        SparkStreamingMQTT standalone = new SparkStreamingMQTT();
        standalone.processMQTT(args[0], args[1], Integer.parseInt(args[2]));
        
        
    }
    
    /**
     * This will start the spark stream that is reading from the kafka queue
     * 
     * @param zkQuorum
     * @param group
     * @param topicList
     * @param numThreads
     */
    public void processMQTT(final String broker, final String topic, final int numSeconds) {


        System.out.println("************ SparkStreamingMQTTOutside.processMQTT start");
        
        System.out.println("************ SparkStreamingMQTTOutside.processMQTT about to create JavaStreamingContext");

       //1. Create the spark streaming context with a 1 second batch size
        SparkConf sparkConf = new SparkConf().setAppName("MQTT");
        // Create the context with a 1 second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(numSeconds));
        jssc.checkpoint(checkpointDirectory);

        System.out.println("************ SparkStreamingMQTTOutside.processMQTT about to read the MQTTUtils.createStream");

        //2. MQTTUtils to collect MQTT messages
        JavaReceiverInputDStream<String> messages = MQTTUtils.createStream(jssc, broker, topic);
        
        System.out.println("************ SparkStreamingMQTTOutside.processMQTT about to do foreachRDD");
        //Save
        messages.foreachRDD(new SaveRDD());

        
        System.out.println("************ SparkStreamingMQTTOutside.processMQTT prior to context.strt");

        // Start the context
        jssc.start();

        jssc.awaitTermination();
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
