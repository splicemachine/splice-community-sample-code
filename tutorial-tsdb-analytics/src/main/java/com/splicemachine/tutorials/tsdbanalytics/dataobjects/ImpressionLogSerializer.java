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
package com.splicemachine.tutorials.tsdbanalytics.dataobjects;

import java.io.Closeable;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;

/**
 * Serializer Class for ImpressionLog, used with Kafka Queue.
 * The ImpressionLog is encoded as Json String by producer. And Consumer convert the JasonString to 
 * IMpressionLOg object
 * 
 * @author Jyotsna Ramineni
 *
 */
public class ImpressionLogSerializer implements Closeable, AutoCloseable, Serializer<ImpressionLog>, Deserializer<ImpressionLog> {

private Gson gson ;


public ImpressionLogSerializer() {
	this(null);
}

public ImpressionLogSerializer(Gson gson) {
	super();
	this.gson = gson;
}

@Override
public void close() {
	 gson  = null;
	
}

@Override
public void configure(Map<String, ?> map, boolean b) {
	if(gson == null)
		gson = new Gson();
	
}

@Override
public byte[] serialize(String s, ImpressionLog log) {
	final String jsonStr = gson.toJson(log, ImpressionLog.class);
	return jsonStr.getBytes();
}

@Override
public ImpressionLog deserialize(String s, byte[] bytes) {
	String jsonString = new String(bytes, Charsets.UTF_8);
	final ImpressionLog impressionLog = gson.fromJson(jsonString, ImpressionLog.class);
	return impressionLog;
}

}