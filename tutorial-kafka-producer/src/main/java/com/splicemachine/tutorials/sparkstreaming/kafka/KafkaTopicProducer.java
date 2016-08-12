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

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaTopicProducer {


    /**
     * Static list of locations
     */
    public static final String[] locations = {
            "Alachua",
            "Baker",
            "Bay",
            "Bradford",
            "Brevard",
            "Broward",
            "Calhoun",
            "Charlotte",
            "Citrus",
            "Clay",
            "Collier",
            "Columbia",
            "Desoto",
            "Dixie",
            "Duval",
            "Escambia",
            "Flagler",
            "Franklin",
            "Gadsden",
            "Gilchrist",
            "Glades",
            "Gulf",
            "Hamilton",
            "Hardee",
            "Hendry",
            "Hernando",
            "Highlands",
            "Hillsborough",
            "Holmes",
            "Indian River",
            "Jackson",
            "Jefferson",
            "Lafayette",
            "Lake",
            "Pinellas",
            "Polk",
            "Putnam",
            "St. Johns",
            "St. Lucie",
            "Santa Rosa",
            "Sarasota",
            "Seminole",
            "Sumter",
            "Suwannee",
            "Taylor",
            "Union",
            "Volusia",
            "Wakulla",
            "Walton",
            "Washington",
            "Lee",
            "Leon",
            "Levy",
            "Liberty",
            "Madison",
            "Manatee",
            "Marion",
            "Martin",
            "Miami-Dade",
            "Monroe",
            "Nassau",
            "Okaloosa",
            "Okeechobee",
            "Orange",
            "Osceola",
            "Palm Beach",
            "Pasco"};
    Random r = new Random();
    DecimalFormat df = new DecimalFormat("#.##");
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String server = null;
    private long totalEvents = 1;
    private String topic = null;

    /**
     * Adds records to a Kafka queue
     *
     * @param args args[0] - Kafka Broker URL
     *             args[1] - Kafka Topic Name
     *             args[2] - Number of messages to add to the queue
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        KafkaTopicProducer kp = new KafkaTopicProducer();
        kp.server = args[0];
        kp.topic = args[1];
        kp.totalEvents = Long.parseLong(args[2]);
        kp.generateMessages();

    }

    /**
     * Sends messages to the Kafka queue.
     */
    public void generateMessages() {
        df.setRoundingMode(RoundingMode.CEILING);

        //Define the properties for the Kafka Connection
        Properties props = new Properties();
        props.put("bootstrap.servers", server); //kafka server
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        //Create a KafkaProducer using the Kafka Connection properties
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        long nEvents = 0;

        //Loop through for the number of messages you want to put on the Queue
        for (nEvents = 0; nEvents < totalEvents; nEvents++) {
            String id = "A_" + nEvents;

            //Build a String that contains JSON data
            String json = "{\"id\": \"" + id + "\"," +
                    "\"location\": \"" + getLocation() + "\"," +
                    "\"temperature\": " + formatDouble(getTemperature()) + "," +
                    "\"humidity\": " + formatDouble(getHumidity()) + "," +
                    "\"recordedTime\": \"" + sd.format(new Timestamp((new Date()).getTime())) + "\"}";
            //Put the id and json string on the Kafka queue
            producer.send(new ProducerRecord<String, String>(topic, id, json));
        }
        //Flush and close the queue
        producer.flush();
        producer.close();
        //display the number of messages that aw
        System.out.println("messages pushed:" + nEvents);
    }

    /**
     * Get a randomly generated temperature value
     *
     * @return
     */
    public double getTemperature() {
        return 9.0 + (95.5 - 9.0) * r.nextDouble();
    }

    /**
     * Get a randomly generated humidy value
     *
     * @return
     */
    public double getHumidity() {
        return 54.8 + (90.7 - 54.8) * r.nextDouble();
    }

    /**
     * Format the double to 2 decimal places
     *
     * @param dbl
     * @return
     */
    public String formatDouble(double dbl) {
        return df.format(dbl);
    }

    /**
     * Get a randomly generated value for location
     *
     * @return
     */
    public String getLocation() {
        int max = locations.length;
        int randomNum = r.nextInt((max - 0)) + 0;
        return locations[randomNum];
    }

}