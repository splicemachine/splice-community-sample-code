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

import org.apache.commons.io.Charsets;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationLog;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLog;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.PublisherGeoKey;
import com.twitter.algebird.HyperLogLogMonoid;

import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This converts Impression log to Pair of <PublisherKey, AggregateLog> 
 * 
 * @author Jyotsna Ramineni
 *
 */
public class AggregateFunction implements PairFunction< ImpressionLog, PublisherGeoKey, AggregationLog>, Externalizable{

	
    private static final Logger LOG = Logger
            .getLogger(AggregateFunction.class);
    private  HyperLogLogMonoid hyperLogLog = new HyperLogLogMonoid(12);
    
   

	@Override
    public  Tuple2<PublisherGeoKey, AggregationLog>  call(ImpressionLog  log) throws Exception {
    	
		//Create PublisherGeoKey object with Publisher and Geo values from Impression Log
    	 PublisherGeoKey pub = new PublisherGeoKey(log.getPublisher(), log.getGeo());
    	 
    	 //Create AggregationLog object with timestamp, bid, 1 for imp and HyperLogLog value for cookie from 
    	 //imporession log
    	 AggregationLog agg = new AggregationLog(log.getTimestamp(), log.getBid(), 1,
    			 hyperLogLog.create(log.getCookie().getBytes(Charsets.UTF_8))
    			 );
    	return new Tuple2<>(pub, agg);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
