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

/**
 * This is an example of spark streaming function that 
 * inserts data into Splice Machine using a VTI.
 * 
 * @author Erin Driggers
 *
 */

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
                try {
                    Connection con = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
                    
                    //Syntax for using a class instance in a VTI, this could also be a table function
                    String vtiStatement = "INSERT INTO IOT.RFID " +
                            "select s.* from new com.splicemachine.tutorials.sparkstreaming.mqtt.RFIDMessageVTI(?) s (" + RFIDMessage.getTableDefinition() + ")";
                    
                    PreparedStatement ps = con.prepareStatement(vtiStatement);
                    ps.setObject(1,rfidMessages);
                    ps.execute();
                } catch (Exception e) {
                    //It is important to catch the exceptions as log messages because it is difficult
                    //to trace what is happening otherwise
                    LOG.error("Exception saving MQTT records to the database" + e.getMessage(), e);
                } finally {
                    LOG.info("Complete insert into IOT.RFID");
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
