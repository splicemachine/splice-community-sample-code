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

import java.io.Serializable;

import com.twitter.algebird.HLL;

/**
 * AggregationLog Object definition to which impression logs are mapped
 * and aggregations are performed. This part of Pair RDD, where the Key is PublisherGeoKey
 *
 * @author Jyotsna Ramineni
 */
public class AggregationLog implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7604119541801254085L;
    //Fields in AggregationLog
    private Long timestamp;
    private Double sumBids;
    private Integer imps = 1;
    private HLL uniquesHll;

    public AggregationLog(Long timestamp, Double sumBids, Integer imps, HLL uniquesHll) {
        super();
        this.timestamp = timestamp;
        this.sumBids = sumBids;
        this.imps = imps;
        this.uniquesHll = uniquesHll;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getSumBids() {
        return sumBids;
    }

    public void setSumBids(Double sumBids) {
        this.sumBids = sumBids;
    }

    public Integer getImps() {
        return imps;
    }

    public void setImps(Integer imps) {
        this.imps = imps;
    }

    public HLL getUniquesHll() {
        return uniquesHll;
    }

    public void setUniquesHll(HLL uniquesHll) {
        this.uniquesHll = uniquesHll;
    }

}
