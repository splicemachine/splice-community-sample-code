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

import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLog;

/**
 * This generates Impression Log and puts them on Kafka Queue.
 *
 * 
 * @author Jyotsna Ramineni
 *
 */

public class kafkaStreamGenerator {

	// Data to generate impression
	// Number of Advertisers to randomly choose from
	public static int NumAdvertisers = 3;
	// Number of Websites to randomly choose from
	public static int NumWebsites = 10000;
	// Number of User Cookies to randomly choose from
	public static int NumCookies = 10000;

	// value for unknow geo code
	public static String UnknownGeo = "unknown";

	// List of Advertiser names
	public static String[] Advertisers = new String[] { "advertisers_1",
			"advertisers_2", "advertisers_3" };
	// List of Geo (state) codes
	public static String[] Geos = new String[] { UnknownGeo, "NY", "CA", "FL",
			"MI", "HI", "AL", "AK", "AZ", "AR", "CO", "CT", "DE", "GA", "ID",
			"IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MN", "MS",
			"MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NC", "ND", "OH", "OK",
			"OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA",
			"WV", "WI", "WY" };

	private String server = null;
	private long totalEvents = 1;
	private String topic = null;
	private int batchEvents = 10;
	private int numPublishers = 5;
	private int numGeos = 6;
	Random random = new Random();

	/**
	 * Adds impression logs to a Kafka queue
	 * 
	 * @param args
	 *            args[0] - Kafka Broker URL 
	 *            args[1] - Kafka Topic Name 
	 *            args[2] - Total NUmber of Logs to write to Kafka Queue, specify 0 for infinite 
	 *            args[3] - Number of logs per batch i.e. one sec
	 *            args[4] - Number of distinct Publishers to use in generated data 
	 *            args[5] - Number of distinct Geo(States) to use in generated data
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		kafkaStreamGenerator kp = new kafkaStreamGenerator();

		kp.server = args[0];
		kp.topic = args[1];
		kp.totalEvents = Long.parseLong(args[2]);

		if (args.length > 3)
			kp.batchEvents = Integer.parseInt(args[3]);

		if (args.length > 4)
			kp.numPublishers = Integer.parseInt(args[4]);

		if (args.length > 5)
			kp.numGeos = Integer.parseInt(args[5]) +1;  // add 1 for unknown
		

		if (kp.numGeos > Geos.length )
			kp.numGeos = Geos.length;
		kp.generateMessages();

	}

	/**
	 * Sends impression logs to the Kafka queue.
	 * 
	 */
	public void generateMessages() {

		// Define the properties for the Kafka Connection
		Properties props = new Properties();
		props.put("bootstrap.servers", server); // kafka server
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer",
				"com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLogSerializer");

		// Create a KafkaProducer using the Kafka Connection properties
		KafkaProducer<String, ImpressionLog> producer = new KafkaProducer<String, ImpressionLog>(
				props);

		int i = 0;

		while ((totalEvents == 0) || (i < totalEvents)) {

			// Generae Impression Log
			long timestamp = System.currentTimeMillis();
			String publisher = "publishers_"
					+ random.nextInt(this.numPublishers);
			String advertiser = "advertisers_" + random.nextInt(NumAdvertisers);
			String website = "website_" + random.nextInt(NumWebsites) + ".com";
			String cookie = "cookie_" + random.nextInt(NumCookies);
			String geo = Geos[random.nextInt(this.numGeos)];

			double bid = Math.abs(random.nextDouble()) % 1;

			ImpressionLog log = new ImpressionLog(timestamp, publisher,
					advertiser, website, geo, bid, cookie);

			producer.send(new ProducerRecord<String, ImpressionLog>(topic, log));
			i = i + 1;

			if (i % batchEvents == 0) {
				// wait one sec between each batch
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Close the queue
		producer.close();
	}

}
