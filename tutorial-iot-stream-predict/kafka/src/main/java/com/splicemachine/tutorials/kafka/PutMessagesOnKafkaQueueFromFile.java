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
package com.splicemachine.tutorials.kafka;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * This is a utility class to put messages on a Kafka queue. You have the option
 * of passing in the directory where the file resides or a filename itself.  It
 * will continue running until it is killed. If you passed in a directory
 * it will continue processing the entire directory over and over, if you passed
 * in a file, it will continue processing that file over and over.  
 * 
 * @author Erin Driggers
 * 
 */
public class PutMessagesOnKafkaQueueFromFile {

    /**
     * The kafka broker server and port (ie 52.209.245.34:9092)
     */
    private String kafkaBrokerServer = null;
    
    /**
     * The kafka topic for the queue
     */
    private String kafkaTopic = null;

    /**
	 * The number of messages that should
	 * be sent before pausing for a second 
	 */
    private int batchSize = 50;

    /**
     * Name of the file or path that contains the data to be read in to 
     * be placed on the queue
     */
    private String dataInputFileName = null;
    
    /**
     * The duration to wait between batches
     */
    long pauseDurationInMilliseconds = 1000;
    
    long recordsWritten = 0;


    /**
     * Adds records to a Kafka queue
     * 
     * @param args
     *            args[0] - (required) Kafka Broker URL 
     *            args[1] - (required) Kafka Topic Name 
     *            args[2] - (required) Data file path or name and name (ie /tmp/myfolder or /tmp/myfolder/myfile.csv)
     *            args[3] - (optional) Number of messages per sec.  Defaults to 50
     *            args[4] - (optional) Pause Duration in Milliseconds - defaults to 1000
     * 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        PutMessagesOnKafkaQueueFromFile kp = new PutMessagesOnKafkaQueueFromFile();
        kp.kafkaBrokerServer = args[0];
        kp.kafkaTopic = args[1];
        kp.dataInputFileName = args[2];
        if (args.length > 3)
            kp.batchSize = Integer.parseInt(args[3]);
        if (args.length > 4)
            kp.pauseDurationInMilliseconds = Integer.parseInt(args[4]);

        kp.generateMessages();

    }

    /**
     * Puts messages on the kafka queue
     */
    public void generateMessages() {
        
        System.out.println("kafkaBrokerServer:" + kafkaBrokerServer);
        System.out.println("topic:" + kafkaTopic);
        System.out.println("file:" + dataInputFileName);
        System.out.println("batchSize:" + batchSize);
        System.out.println("pauseDurationInMilliseconds:" + pauseDurationInMilliseconds);

        // Define the properties for the Kafka Connection
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaBrokerServer); // kafka server
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        // Create a KafkaProducer using the Kafka Connection properties
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
                props);
        
        File f = new File(dataInputFileName);
        try {
            if(f.exists()) {
                if(f.isDirectory()) {
                    processDirectory(producer, f);
                } else {
                    putDataOnQueue(producer, f);
                }
            } else {
                System.out.println("Filename or path does not exist:" + dataInputFileName);
            }
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:" + e);
        }
        
        System.out.println("Records Written:" + recordsWritten);
    }
    
    
    public void processDirectory(KafkaProducer<String, String> producer, File fileDirectory) throws java.io.FileNotFoundException, java.io.IOException {
        File[] listOfFiles = fileDirectory.listFiles();
        for(File currentFile : listOfFiles) {
            putDataOnQueue(producer, currentFile);
        }
    }
    
    
    public void putDataOnQueue(KafkaProducer<String, String> producer, File fileToProcess) throws java.io.FileNotFoundException, java.io.IOException {
        int i = 0;
        System.out.println("Processing file:" + fileToProcess.getAbsolutePath());
        FileInputStream fis = new FileInputStream(fileToProcess);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            producer.send(new ProducerRecord<String, String>(kafkaTopic, line));
            recordsWritten++;
            i++;
            
            if (i >= batchSize) {
                i = 0;
                try {
                    Thread.sleep(pauseDurationInMilliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        br.close();
    }
    
}