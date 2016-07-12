package com.splicemachine.tutorials.mqtt;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.commons.lang3.StringUtils;



import com.splicemachine.tutorials.mqtt.ConnectionPool;

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
                Connection con = ConnectionPool.getConnection();
                
                String vtiStatement = "INSERT INTO IOT.RFID " +
                        "select s.* from new com.splicemachine.tutorials.mqtt.RFIDMessageVTI(?) s (" + RFIDMessage.getTableDefinition() + ")";
                
                String temp = StringUtils.join(rfidMessages, "\n");
                LOG.debug("VTI Statement:" + vtiStatement);
                //LOG.debug("Parameters:" + temp);
                
                PreparedStatement ps = con.prepareStatement(vtiStatement);
               //Temporary commented out - Jira - DB-5472 ps.setObject(1,sensorMessages);
                
                int totalCounter = 0;
                int counter = 0;
                String catString = "";
                while(totalCounter < numRcds) {
                    //Only process 20 records at a time
                    //THIS IS TEMPORARY BECAUSE OF Jira - DB-5472 
                    
                    if(counter >= 10) {
                        ps.setString(1, catString);
                        ps.execute();
                        counter = 0;
                        catString = "";
                    } else {
                        if(counter > 0)catString =catString +"\n";
                        catString =catString + rfidMessages.get(totalCounter);
                        counter++;
                    }

                    totalCounter++;
                }
                //Jira - DB-5472 ps.setObject(1, temp);
                //Jira - DB-5472 ps.execute();
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
