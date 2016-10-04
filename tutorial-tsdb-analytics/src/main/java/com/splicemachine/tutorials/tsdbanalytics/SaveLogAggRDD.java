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

import java.io.Externalizable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationLog;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationResult;
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.PublisherGeoKey;

/**
 * This converts  AggregationLog to AggregationResult and invokes
 * Save for each Partition to save the Aggregation Results to database.
 *
 * @author Jyotsna Ramineni
 */

public class SaveLogAggRDD implements VoidFunction<JavaPairRDD<PublisherGeoKey, AggregationLog>>, Externalizable {
    private static final Logger LOG = Logger
            .getLogger(SaveLogAggRDD.class);

    @Override
    public void call(JavaPairRDD<PublisherGeoKey, AggregationLog> logsRDD) throws Exception {

        if (logsRDD != null) {
            LOG.info(" Data to process in RDD:" + logsRDD.count());

            JavaRDD<AggregationResult> aggResRDD = logsRDD.map(new Function<Tuple2<PublisherGeoKey, AggregationLog>, AggregationResult>() {
                @Override
                public AggregationResult call(
                        Tuple2<PublisherGeoKey, AggregationLog> arg0)
                        throws Exception {
                    PublisherGeoKey p = arg0._1;
                    AggregationLog a = arg0._2;
                    return new AggregationResult(new Timestamp(a.getTimestamp()),
                            p.getPublisher(), p.getGeo(), a.getImps(),
                            (int) a.getUniquesHll().estimatedSize(),
                            a.getSumBids() / a.getImps());
                }
            });
            LOG.info(" Call Data Process Partition");
            aggResRDD.foreachPartition(new SaveLogAggPartition());
        } else
            LOG.error("Data to process:" + 0);
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
