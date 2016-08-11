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
package com.splicemachine.tutorials.tsdbanalytics;

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
import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationResult;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This is VTI that reads in a list of AggregationResult objects
 * and returns the list in splice format for insert into table values
 * 
 * @author  Jyotsna Ramineni
 *
 */
public class AggregationResultVTI  implements DatasetProvider, VTICosting{
    
    private static final Logger LOG = Logger.getLogger(AggregationResultVTI.class);

    private List<AggregationResult> records = null;
    
    protected OperationContext operationContext;


    public AggregationResultVTI (List<AggregationResult> pRecords) {
        this.records = pRecords;
    }
    
    public static DatasetProvider getSensorMessageVTI(List<AggregationResult> pRecords) {
        return new AggregationResultVTI(pRecords);
    }

    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        
        //Create an arraylist to store aggregation results
        ArrayList<LocatedRow> items = new ArrayList<LocatedRow>();
        
        try {
            
            
            int numRcds = this.records == null ? 0 : this.records.size();
            
            if(numRcds > 0 ) {        
                LOG.error("Records to process:" + numRcds);
                //Loop through each record and then set the values
                for(AggregationResult aggResult : records) {  
                	 items.add(new LocatedRow(aggResult.getRow()));
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
