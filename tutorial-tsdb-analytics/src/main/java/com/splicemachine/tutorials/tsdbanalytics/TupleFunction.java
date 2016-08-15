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

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;

import com.splicemachine.tutorials.tsdbanalytics.dataobjects.ImpressionLog;

import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This Maps the Tuple stream to ImpressionLog stream
 *
 * @author Jyotsna Ramineni
 */

public class TupleFunction implements Function<Tuple2<String, ImpressionLog>, ImpressionLog>, Externalizable {

    private static final Logger LOG = Logger.getLogger(TupleFunction.class);

    @Override
    public ImpressionLog call(Tuple2<String, ImpressionLog> stringLogTuple2) throws Exception {
        return stringLogTuple2._2();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }
}
