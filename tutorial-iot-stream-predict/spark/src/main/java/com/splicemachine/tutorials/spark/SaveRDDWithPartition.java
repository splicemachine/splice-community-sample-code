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
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;



/**
 * This processes each RDD that is read in from the spark stream
 * 
 * @author Erin Driggers
 *
 */
public class SaveRDDWithPartition implements VoidFunction<JavaRDD<String>>, Externalizable{

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's DRIVER log
     */
    private static final Logger LOG = Logger
            .getLogger(SaveRDDWithPartition.class);
    
    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;
    
    /**
     * For this initial code, we will pass in the schema where the tables reside.  In the future the
     * requirement will be to use the tenant id for the schema.
     */
    private String spliceSchema;
    
    /**
     * This is the table where we want to insert data
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
     * Constructor for the SaveRDDWithPartions which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     * 
     * @param spliceJdbcUrl
     * @param spliceSchema
     * @param spliceTable
     * @param vtiClassName
     */
    public SaveRDDWithPartition(String spliceJdbcUrl, String spliceSchema, String spliceTable, String vtiClassName) {
        this.spliceJdbcUrl = spliceJdbcUrl;
        this.spliceSchema = spliceSchema;
        this.spliceTable = spliceTable;
        this.vtiClassName = vtiClassName;
    }

    /**
     * Constructor for the SaveRDDWithPartions which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     * 
     * @param spliceJdbcUrl
     * @param spliceSchema
     * @param spliceTable
     * @param vtiClassName
     */
    public SaveRDDWithPartition(String spliceJdbcUrl, String vtiStatement) {
        this.spliceJdbcUrl = spliceJdbcUrl;
        this.vtiStatement = vtiStatement;
    }
    
    /**
     * A RDD is created on the driver for the blocks created during the batchInterval. 
     * The blocks generated during the batchInterval are partitions of the RDD. Each 
     * partition is a task in spark.
     * 
     * This takes the incoming RDD's partitions and for each partition
     * it calls SavePartition.  The SavePartition processes is distributed to the 
     * non-driver executors to perform their operations and result in 
     * Parallelization.
     * 
     * To understand this in more detail, read the section 'Level of 
     * Parallelism in Data Receiving' in the Spark Streaming programming 
     * guide
     * 
     * @param messagesRdd - an RDD that is comprised of JSON strings describing various thing types
     * @return
     * @throws Exception
     */
    @Override
    public void call(JavaRDD<String> messagesRdd) throws Exception {  
        if(messagesRdd!=null) { 
            if(vtiStatement == null) {
                messagesRdd.foreachPartition(new  SavePartition(this.spliceJdbcUrl,this.spliceSchema, this.spliceTable, this.vtiClassName));
            } else {
                messagesRdd.foreachPartition(new  SavePartition(this.spliceJdbcUrl,this.vtiStatement));
            }
        }   
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
