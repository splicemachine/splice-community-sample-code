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
package com.splicemachine.tutorials.vti;


import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.vti.VTICosting;
import com.splicemachine.db.vti.VTIEnvironment;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.function.FileFunction;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.stream.spark.SparkDataSet;
import com.splicemachine.derby.vti.iapi.DatasetProvider;

import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * This is an example of a VTI that reads in a list of Strings
 * and parses it and inserts a record into the database for each 
 * string
 * 
 * @author Erin Driggers
 *
 */
public class ArrayListOfStringVTI  implements DatasetProvider, VTICosting{
    
    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the HBASE Region Server logs
     */    
    private static final Logger LOG = Logger.getLogger(ArrayListOfStringVTI.class);

    /**
     * List of Name / Value pairs to be saved
     * to the database
     */
    private ArrayList<String> records = null;
    
    private OperationContext operationContext;
    
    
    private String characterDelimiter;
    private String columnDelimiter = "|";
    private String timeFormat;
    private String dateTimeFormat;
    private String timestampFormat;
    private int[] columnIndex;
    
    /**
     * Constructor for the HashMapVTI class.  It expects
     * an arraylist of HashMap objects
     * 
     * @parm pFullTableName - full table name like SPLICE.MYTABLE
     * @param pRecords - Arraylist of HashMap Objects
     * @param columns - Columns to insert
     */
    public ArrayListOfStringVTI (Blob  pRecords) {
        
        try {
            if(pRecords != null) {
                this.records = (ArrayList<String>)getBlobResults(pRecords);
                if(this.records == null) this.records =  new ArrayList<String>();
            } else {
                this.records =  new ArrayList<String>();
            }           
        } catch (Exception e) {
            LOG.error("Exception calling the HashMapVTI", e);
        }              
    }


    public Object getBlobResults(Blob data) {
        Object obj = null;

        if(data == null) {
            return obj;
        }
        
        try {
            long length = data.length();
            if(length > 0) {
                byte[] bytes = data.getBytes(Long.valueOf(1), Integer.valueOf((length)+""));
                obj = deserialize(bytes);
            }
        } catch (IOException e) {
            LOG.error("IOException getting the blob results", e);
        } catch (ClassNotFoundException e) {
            LOG.error("ClassNotFoundException getting the blob results", e);
        } catch (SQLException e) {
            LOG.error("SQLException getting the blob results", e);
        } 
        return obj;
    }
    

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        Object obj = null;
        
        try {
            b = new ByteArrayInputStream(bytes);
            o = new ObjectInputStream(b);
            o.close();
            obj = o.readObject();
            
        } catch (Exception e) {
            LOG.error("Exception deserializing results", e);
        } finally {
            if(o != null) try { o.close(); } catch (Exception e){};
            if(b != null) try { b.close(); } catch (Exception e){};
        }
        return obj;
    }


    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        //Convert list of strings to a DataSet<String>
        //DataSet<String> textSet =  new SparkDataSet<>(SpliceSpark.getContext().parallelize(Lists.newArrayList(this.records)));
        DataSet<String> textSet =  dsp.createDataSet(this.records.iterator());
        //DataSet<String> textSet =  new SparkDataSet<>(SpliceSpark.getContext().parallelize(Lists.newArrayList(this.records)));
        return textSet.flatMap(new FileFunction(characterDelimiter, columnDelimiter, execRow, columnIndex, timeFormat, dateTimeFormat, timestampFormat, operationContext), true);
    }

    /**
     * The estimated cost to instantiate and iterate through the Table 
     * Function.  Hard coded this to 2,000,000,000 to force it to use
     * spark
     */
    @Override
    public double getEstimatedCostPerInstantiation(VTIEnvironment arg0)
            throws SQLException {
        return VTICosting.defaultEstimatedCost;
    }

    /**
     * The estimated number of rows returned by the Table Function in a 
     * single instantiation.
     */
    @Override
    public double getEstimatedRowCount(VTIEnvironment arg0) throws SQLException {
        return VTICosting.defaultEstimatedRowCount;
    }

    /**
     * Whether or not the Table Function can be instantiated multiple times 
     * within a single query execution.
     */
    @Override
    public boolean supportsMultipleInstantiations(VTIEnvironment arg0)
            throws SQLException {
        return false;
    }

    /**
     * Dynamic MetaData used to dynamically bind a function.
     * 
     * Metadata
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("not supported");
    }


    @Override
    public OperationContext getOperationContext() {
        return this.operationContext;
    }

    
}
