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
package com.splicemachine.tutorials.spark;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.VoidFunction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;

import com.splicemachine.db.impl.load.ColumnInfo;


/**
 * This partition receives a list of JSON objects.  A JSON object could refer
 * to more than one object type.  The JSON objects are grouped by its type and
 * a set of key value pairs are extracted from the JSON object.  The entire 
 * JSON object is not sent to Splice Machine because it is a large object and would
 * make the I/O much larger than necessary.  The type, columns and key/ value pairs
 * are saved to splice machine by calling a VTI.
 * 
 * Created by Erin Driggers
 */
public class SavePartition implements VoidFunction<Iterator<String>>, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's EXECUTOR log
     */       
    private static final Logger LOG = Logger
            .getLogger(SavePartition.class);

    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;
    
    /**
     * TODO: Determine where the schema will come from
     * 
     * For this initial code, we will pass in the schema where the tables reside.  In the future the
     * requirement will be to use the tenant id for the schema.
     */
    private String spliceSchema;
    
    /**
     * The splice machine table where we will be inserting data
     */
    private String spliceTable;
        
    /**
     * This is the name of the VTI class to execute when importing data
     */
    private String vtiClassName = null;
    
    /**
     * This is the VTI statement
     */
    private String vtiStatement = null;
    
    /**
     * Number of bad records allowed.  We are always setting it to -1
     */
    private int badRecordsAllowed = -1;
    
    /**
     * Setting upsert to true.  If your table does not have a primary key
     * then this needs to be false
     */
    private boolean isUpsert = false;
    
    /**
     * Directory in HDFS where bad records are written to
     */    
    private String badRecordDirectory = "/BAD";
            
   
    /**
     * Constructor for the SavePartition which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     * 
     * @param spliceJdbcUrl
     * @param spliceSchema
     * @param spliceTable
     * @param vti
     */
    public SavePartition(String spliceJdbcUrl, String spliceSchema, String spliceTable, String vtiClassName) {
        this.spliceJdbcUrl = spliceJdbcUrl;
        this.spliceSchema = spliceSchema;
        this.spliceTable = spliceTable;
        this.vtiClassName = vtiClassName;
    }

    public SavePartition(String spliceJdbcUrl, String vtiStatement) {
        this.spliceJdbcUrl = spliceJdbcUrl;
        this.vtiStatement = vtiStatement;
    }
   

    /**
     * The messagesIter is an RDD that is comprised of JSON strings that are
     * of various types of objects.  We need to group each
     * unique type of object into its own arraylist and parse out only the
     * fields in the JSON object that are possibly needed.  
     * We will and call a VTI to insert the data into Splice Machine.
     * 
     * @param messagesIter
     * @return
     * @throws Exception
     */
    public void call(Iterator<String> messagesIter) throws Exception {
         long startTime = System.currentTimeMillis();
  
        if(messagesIter!=null) {
            List<String> myList = Lists.newArrayList(messagesIter);
            
            int numRcds = myList.size();            
            if(numRcds > 0) {
                LOG.info("numRcds:" + numRcds); 
                LOG.error("spliceJdbcUrl:" + spliceJdbcUrl); 

                //Get a splice machine connection to the database
                Connection con = DriverManager.getConnection(this.spliceJdbcUrl); 
                
                //Get the VTI statement
                String vti = getVTISyntaxForType(con, this.spliceSchema, this.spliceTable);
                String vtiIOTPred = getVTISyntaxForType(con, "IOT", "TO_PROCESS_SENSOR");

                //Get the start time
                long dbStartTime = System.currentTimeMillis();
                processMessageList(con,myList, vti);
                long dbEndTime = System.currentTimeMillis();
                LOG.info("Database save duration:" + (dbEndTime-dbStartTime)); 
                
              //Insert IOT Prediction data
                 dbStartTime = System.currentTimeMillis();
                processMessageList(con,myList, vtiIOTPred);
                 dbEndTime = System.currentTimeMillis();
                LOG.info("Database save duration:" + (dbEndTime-dbStartTime)); 
            }
            long endTime = System.currentTimeMillis();
            LOG.info("Processing Duration:" + (endTime-startTime));
        }
        
    }
    
    /**
     * This loops through the list of messages and builds a shorten list of messages to
     * get around a current defect in Splice Machine where an object's size is being compared
     * to a string size and therefore only allowing objects of sizes 32,000.
     * 
     * 
     * @param messagesList
     * @param type
     * @param vti
     */
    public void processMessageList(Connection con, List<String> messagesList, String vti) {
        saveToDatabase(vti, messagesList, con);
    }
    
    /**
     * Calls the VTI in splice machine passing in an array of strings
     * 
     * @param vti
     * @param shortMessageList
     * @param con
     * @param type
     * @return
     */
    public boolean saveToDatabase(String vti, List<String> shortMessageList, Connection con) {
        boolean success = false;
        PreparedStatement ps = null;
        try {
            LOG.info("Calling VTI for the number of records :" + shortMessageList.size());            
            ps = con.prepareStatement(vti);
            byte[] bytes = serialize(shortMessageList);
            ps.setBytes(1, bytes);
            ps.execute();   
            success = true;
        } catch (Exception e) {
            success = false;
            LOG.error("Exception inserting  failed: " + e.getMessage(), e); 
        } finally {
            if(ps != null) {try {ps.close();} catch(Exception e){}}
        } 
        return success;
    }
    
    /**
     * Convert the list of messages to a byte array to send over as a BLOB to the VTI
     * 
     * @param obj
     * @return
     * @throws IOException
     */
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = null;
        ObjectOutputStream o = null;
        byte[] bytes = null;
        try {
            b = new ByteArrayOutputStream();
            o = new ObjectOutputStream(b);
            o.writeObject(obj);
            bytes = b.toByteArray();
        } catch (Exception e) {
            LOG.error("Exception serializing the list of messages.", e);
        } finally {
            if(o != null) try { o.close(); } catch (Exception e){};
            if(b != null) try { b.close(); } catch (Exception e){};
        }
        return bytes;

    }
 
    /**
     * Method to generically build the VTI statement for an object.  Ultimately
     * we are building a statement like the following:
     * 
     * insert into MYSCHEMA.TABLE (COL1, COL2, COL3) select (COL1, COL2, COL3)
     *    from new com.splicemachine.tutorials.vti.HashMapVTI(?) s (
     *    COL1 VARCHAR(32), COL2 VARCHAR(32), COL3 VARCHAR(32))
     *    
     * 
     * @param tenantType
     * @param node
     * @return
     * @throws SQLException 
     */
    public String getVTISyntaxForType(Connection con, String schemaName, String tableName) throws SQLException {
        
        if(vtiStatement != null) {
            return vtiStatement;
        }
        
        StringBuffer sb = new StringBuffer("new ");
        sb.append(this.vtiClassName);
        sb.append("(?)") ;

        //ColumnInfo columnInfo = new ColumnInfo(con, this.spliceSchema, this.spliceTable, null);
        ColumnInfo columnInfo = new ColumnInfo(con, schemaName, tableName, null);
        
        String insertSql = "INSERT INTO " + schemaName + "." + tableName + 
                " --splice-properties insertMode=" + (isUpsert ? "UPSERT" : "INSERT") + ", statusDirectory=" +
                badRecordDirectory + ", badRecordsAllowed=" + badRecordsAllowed + "\n" +
                " SELECT * from " +
                sb.toString() + " AS importVTI (" + columnInfo.getImportAsColumns() + ")";
        
        return insertSql;
    }        
}
