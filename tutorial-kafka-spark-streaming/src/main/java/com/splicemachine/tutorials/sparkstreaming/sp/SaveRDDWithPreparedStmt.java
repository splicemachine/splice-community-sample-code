package com.splicemachine.tutorials.sparkstreaming.sp;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;


import com.splicemachine.tutorials.sparkstreaming.ConnectionPool;
import com.splicemachine.tutorials.sparkstreaming.SensorMessage;

import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Created by jleach on 5/19/16.
 */
public class SaveRDDWithPreparedStmt implements Function<JavaRDD<SensorMessage>, Void>, Externalizable{

    private static final Logger LOG = Logger
            .getLogger(SaveRDDWithPreparedStmt.class);

    @Override
    public Void call(JavaRDD<SensorMessage> sensorMessagesRdd) throws Exception {
        LOG.info("About to read results:");
        if(sensorMessagesRdd!=null && sensorMessagesRdd.count() > 0) {
            LOG.info("Data to process:");   
            
            List<SensorMessage> sensorMessages = sensorMessagesRdd.collect();
            int numRcds = sensorMessages.size();
            
            if(numRcds > 0) {
                Connection con = ConnectionPool.getConnection();
                PreparedStatement ps = SensorMessage.getPreparedStatement(con);
                
                for(int i=0; i<numRcds; i++) {
                    sensorMessages.get(i).addParametersToPreparedStatement(ps);
                    ps.addBatch();
                }
                LOG.info("About to commit batch statement:");   
                ps.executeBatch();
                if(ps != null) {
                    ps.close();
                }
            }
                       
        } else {
            LOG.info("NO data to process:");
        }
        return null;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
