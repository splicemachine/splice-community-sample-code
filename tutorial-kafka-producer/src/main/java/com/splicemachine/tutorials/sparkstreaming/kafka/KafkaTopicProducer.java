package com.splicemachine.tutorials.sparkstreaming.kafka;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaTopicProducer {
    
    
    private String server = null;
    private long totalEvents = 1;
    private String topic = null;
    Random r = new Random();
    DecimalFormat df = new DecimalFormat("#.##");
    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


    /**
     * Adds records to a Kafka queue
     * 
     * @param args
     *      args[0] - Kafka Broker URL
     *      args[1] - Kafka Topic Name
     *      args[2] - Number of messages to add to the queue
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        
        KafkaTopicProducer kp = new KafkaTopicProducer();
        kp.server = args[0];
        kp.topic = args[1];
        kp.totalEvents = Long.parseLong(args[2]);
        kp.generateMessages();
        
    }
    
    public void generateMessages() {
        df.setRoundingMode(RoundingMode.CEILING);
        
        Properties props = new Properties();
        props.put("bootstrap.servers", server); //kafka server
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        
        //Put messages on the queue
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        long nEvents = 0;
        for (nEvents = 0; nEvents < totalEvents; nEvents++) { 
            String id = "A_" + nEvents;
            String json = "{\"id\": \"" + id + "\"," +
                    "\"location\": \"" + getLocation() + "\"," +
                    "\"temperature\": " + formatDouble(getTemperature()) + "," +
                    "\"humidity\": " + formatDouble(getHumidity()) + "," +
                    "\"recordedTime\": \"" + sd.format(new Timestamp((new Date()).getTime())) + "\"}";
            producer.send(new ProducerRecord<String, String>(topic, id, json));
        }
        producer.flush();
        producer.close();
        System.out.println("messages pushed:" + nEvents);
        

    }
    
    public double getTemperature() {
        return 9.0 + (95.5 - 9.0) * r.nextDouble();
    }
    
    public double getHumidity() {
        return 54.8 + (90.7 - 54.8) * r.nextDouble();
    }
    
    public String formatDouble(double dbl) {
        return df.format(dbl);
    }
    
    public String getLocation() {
        int max = locations.length;
        int randomNum = r.nextInt((max - 0)) + 0;
        return locations[randomNum];
    }
    
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

}