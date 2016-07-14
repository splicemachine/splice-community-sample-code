package com.splicemachine.tutorials.sparkstreaming.kafka;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;


import scala.Tuple2;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Clob;
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
        LOG.info("In call for SaveRDDWithVTI");   
        if(sensorMessagesRdd!=null && sensorMessagesRdd.count() > 0) {
            LOG.info("Data to process:");   
            
            //Convert to list 
            List<String> sensorMessages = sensorMessagesRdd.collect();
            int numRcds = sensorMessages.size();
            
            if(numRcds > 0) {
                LOG.info("numRcds:" + numRcds); 
                Connection con = DriverManager.getConnection("jdbc:splice://stl-colo-srv54:1527/splicedb;user=splice;password=admin");
                
                String vtiStatement = "INSERT INTO IOT.SENSOR_MESSAGES " +
                        "select s.* from new com.splicemachine.tutorials.sparkstreaming.kafka.SensorMessageVTI(?) s (" + SensorMessage.getTableDefinition() + ")";
                
                PreparedStatement ps = con.prepareStatement(vtiStatement);
                
                ps.setObject(1,sensorMessages);
                try {
                    ps.execute();
                } catch (Exception e) {
                    LOG.error("Exception inserting data:" + e.getMessage(), e); 
                } finally {
                    LOG.info("Inserted Complete");
                }
            }
                       
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
