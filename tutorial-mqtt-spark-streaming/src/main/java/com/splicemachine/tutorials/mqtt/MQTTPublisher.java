package com.splicemachine.tutorials.mqtt;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MQTTPublisher {

    MqttClient client;
    
    String topicName = "testing";
    String broker = "localhost";
    String clientId = "javaTest";
    int numMessages = 10;
    
    public MQTTPublisher() {}

    public static void main(String[] args) {
        
        if(args.length != 3) {
            System.out.println("The program expects 3 parameters: broker, topic name and number of messages to produce");
            System.out.println("java com.splicemachine.tutorials.mqtt.MQTTPublisher tcp://localhost:1883 /testing 100");
            return;
        }
        MQTTPublisher pub = new MQTTPublisher();
        pub.broker = args[0];
        pub.topicName = args[1];
        pub.numMessages = Integer.parseInt(args[2]);
        
        pub.printParms();
        pub.doDemo();
    }
    
    public void printParms() {
        System.out.println("topicName:" + topicName);
        System.out.println("broker:" + broker);
        System.out.println("numMessages:" + numMessages);
        System.out.println("clientId:" + clientId);
    }

    public void doDemo() {
      try {
        client = new MqttClient(broker, clientId);
        client.connect();
        MqttMessage message = new MqttMessage();
        for(int i=0; i<numMessages; i++) {
            message.setPayload(( "Asset" + i + ",Location" + i + "," + new Timestamp((new Date()).getTime())).getBytes());
            client.publish(topicName, message);
        }
        client.disconnect();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
    
}
