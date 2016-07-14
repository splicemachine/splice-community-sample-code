package com.splicemachine.tutorials.sparkstreaming.mqtt;

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


public class SaveRDD implements Function<JavaRDD<String>, Void>, Externalizable{

    private static final Logger LOG = Logger
            .getLogger(SaveRDD.class);

    @Override
    public Void call(JavaRDD<String> rddRFIDMessages) throws Exception {
        LOG.debug("About to read results:");
        if(rddRFIDMessages!=null && rddRFIDMessages.count() > 0) {
            LOG.debug("Data to process:");               
            //Convert to list 
            List<String> rfidMessages = rddRFIDMessages.collect();
            int numRcds = rfidMessages.size();
            
            if(numRcds > 0) {
                Connection con = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
                
                String vtiStatement = "INSERT INTO IOT.RFID " +
                        "select s.* from new com.splicemachine.tutorials.mqtt.RFIDMessageVTI(?) s (" + RFIDMessage.getTableDefinition() + ")";
                
                PreparedStatement ps = con.prepareStatement(vtiStatement);
                ps.setObject(1,rfidMessages);
                ps.execute();
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
