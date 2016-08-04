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
package com.splicemachine.tutorials.sparkstreaming.mqtt;

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
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContextFactory;


import scala.Tuple2;


/**
 * This is an example of spark streaming job that 
 * reads messages from an MQTT queue and inserts them
 * into splice machine.
 * 
 * @author Erin Driggers
 *
 */

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
     * This will start the spark stream that is reading from the MQTT queue
     * 
     * @param broker - MQTT broker url
     * @param topic - MQTT topic name
     * @param numSeconds - Number of seconds between batch size
     */
    public void processMQTT(final String broker, final String topic, final int numSeconds) {


        LOG.info("************ SparkStreamingMQTTOutside.processMQTT start");
        
       // Create the spark application and set the name to MQTT
        SparkConf sparkConf = new SparkConf().setAppName("MQTT");
        
        // Create the spark streaming context with a 'numSeconds' second batch size
        jssc = new JavaStreamingContext(sparkConf, Durations.seconds(numSeconds));
        jssc.checkpoint(checkpointDirectory);

        LOG.info("************ SparkStreamingMQTTOutside.processMQTT about to read the MQTTUtils.createStream");
        //2. MQTTUtils to collect MQTT messages
        JavaReceiverInputDStream<String> messages = MQTTUtils.createStream(jssc, broker, topic);
        
        LOG.info("************ SparkStreamingMQTTOutside.processMQTT about to do foreachRDD");
        //process the messages on the queue and save them to the database
        messages.foreachRDD(new SaveRDD());

        
        LOG.info("************ SparkStreamingMQTTOutside.processMQTT prior to context.strt");
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
