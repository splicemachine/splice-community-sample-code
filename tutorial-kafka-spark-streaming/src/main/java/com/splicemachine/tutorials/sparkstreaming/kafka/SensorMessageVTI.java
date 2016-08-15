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
package com.splicemachine.tutorials.sparkstreaming.kafka;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedResultSetMetaData;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.db.vti.VTICosting;
import com.splicemachine.db.vti.VTIEnvironment;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.control.ControlDataSet;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.vti.iapi.DatasetProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This is an example of a VTI that reads in a list of strings
 * where each string is in a JSON format, it converts it into an
 * RFIDMessage object and returns the list in a Splice Machine
 * compatible format.
 *
 * @author Erin Driggers
 */
public class SensorMessageVTI  implements DatasetProvider, VTICosting{
    
    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the HBase Region server log
     */
    private static final Logger LOG = Logger.getLogger(SensorMessageVTI.class);
    
    
    protected OperationContext operationContext;

    /**
     * The records to be inserted
     */
    private List<String> records = null;
    
    /**
     * Object that helps map the incoming json string to SensorMessage object
     */
    private ObjectMapper mapper = new ObjectMapper();


    /**
     * 
     * @param pRecords - List of String with the SensorMessage in JSON format
     */
    public SensorMessageVTI (List<String> pRecords) {
        this.records = pRecords;
    }
    
    /**
     * Static method to allow you to call with VTI using a TABLE FUNCTION
     * 
     * @param pRecords
     * @return
     */
    public static DatasetProvider getSensorMessageVTI(List<String> pRecords) {
        return new SensorMessageVTI(pRecords);
    }

    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);

        //Create an arraylist to store the key / value pairs
        ArrayList<LocatedRow> items = new ArrayList<LocatedRow>();
        
        try {            
            int numRcds = this.records == null ? 0 : this.records.size();            
            if(numRcds > 0 ) {        
                //Loop through each record convert to a SensorObject
                //and then set the values
                for(String jsonString : records) {  
                    SensorMessage sensor = mapper.readValue(jsonString, SensorMessage.class);
                    items.add(new LocatedRow(sensor.getRow()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception processing SensorMessageVTI", e);
        } finally {
            operationContext.popScope();
        }
        return new ControlDataSet<>(items);
    }

    /**
     * The estimated cost to instantiate and iterate through the Table
     * Function.
     */
    @Override
    public double getEstimatedCostPerInstantiation(VTIEnvironment arg0)
            throws SQLException {
        return 0;
    }

    /**
     * The estimated number of rows returned by the Table Function in a
     * single instantiation.
     */
    @Override
    public double getEstimatedRowCount(VTIEnvironment arg0) throws SQLException {
        return 0;
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
     * <p>
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
