package com.splicemachine.tutorials.sparkstreaming.vti;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;

import com.splicemachine.tutorials.sparkstreaming.SensorMessage;
import com.splicemachine.tutorials.sparkstreaming.ConnectionPool;

import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;


/**
 * Created by jleach on 5/19/16.
 */
public class SaveRDDWithVTI implements Function<JavaRDD<String>, Void>, Externalizable{

    private static final Logger LOG = Logger
            .getLogger(SaveRDDWithVTI.class);

    @Override
    public Void call(JavaRDD<String> sensorMessagesRdd) throws Exception {
        LOG.debug("About to read results:");
        if(sensorMessagesRdd!=null && sensorMessagesRdd.count() > 0) {
            LOG.debug("Data to process:");   
            
            //Convert to list 
            List<String> sensorMessages = sensorMessagesRdd.collect();
            int numRcds = sensorMessages.size();
            
            if(numRcds > 0) {
                Connection con = ConnectionPool.getConnection();
                
                String vtiStatement = "INSERT INTO IOT.SENSOR_MESSAGES " +
                        "select s.* from new com.splicemachine.tutorials.sparkstreaming.vti.SensorMessageVTI(?) s (" + SensorMessage.getTableDefinition() + ")";
                
                LOG.debug("VTI Statement:" + vtiStatement);
                
                PreparedStatement ps = con.prepareStatement(vtiStatement);
                        ps.setObject(1,sensorMessages);
                ps.execute();
            }
                       
        } else {
            LOG.debug("NO data to process:");
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
