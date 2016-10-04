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

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

import org.apache.commons.io.Charsets;

import com.google.gson.Gson;

/**
 * Decoder Class for ImpressionLog, used with Kafka Queue.
 * The ImpressionLog is encoded as Json String by the Producer.
 * The Consumer converts the Json String to an ImpressionLog object
 *
 * @author Jyotsna Ramineni
 */

public class ImpressionLogDecoder implements Decoder<ImpressionLog> {

    public ImpressionLogDecoder() {
        this(null);
    }

    public ImpressionLogDecoder(VerifiableProperties props) {
        /* This constructor must be present for successful compile. */
    }

    @Override
    public ImpressionLog fromBytes(byte[] bytes) {
        String jsonString = new String(bytes, Charsets.UTF_8);
        Gson gson = new Gson();
        final ImpressionLog impressionLog = gson.fromJson(jsonString, ImpressionLog.class);
        return impressionLog;
    }

}
