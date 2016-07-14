package com.splicemachine.tutorials.sparkstreaming.kafka;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by jleach on 5/19/16.
 */
public class TupleFunction implements Function<Tuple2<String, String>, String>, Externalizable{

    private static final Logger LOG = Logger
            .getLogger(TupleFunction.class);

    
    @Override
    public String call(Tuple2<String, String> stringStringTuple2) throws Exception {
        LOG.info("=====================In tuple:" + stringStringTuple2.toString());
        return stringStringTuple2._2();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
