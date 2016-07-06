package com.splicemachine.tutorials.sparkstreaming;

import com.fasterxml.jackson.databind.ObjectMapper;

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
public class TupleSensor implements Function<Tuple2<String, String>, SensorMessage>, Externalizable{

    private static final Logger LOG = Logger
            .getLogger(TupleSensor.class);
    
    ObjectMapper mapper = new ObjectMapper();

    
    @Override
    public SensorMessage call(Tuple2<String, String> stringStringTuple2) throws Exception {
        LOG.info("=====================In tuple:" + stringStringTuple2.toString());
        String jsonString = stringStringTuple2._2();
        return mapper.readValue(jsonString, SensorMessage.class);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
