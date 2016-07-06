package com.splicemachine.tutorials.sparkstreaming.vti;

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
import com.splicemachine.tutorials.sparkstreaming.SensorMessage;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The purpose of this de
 * 
 * @author erindriggers
 *
 */
public class SensorMessageVTI  implements DatasetProvider, VTICosting{
    
    private static final Logger LOG = Logger.getLogger(SensorMessageVTI.class);

    private List<String> records;
    private ObjectMapper mapper = new ObjectMapper();
    
    protected OperationContext operationContext;
    
    public SensorMessageVTI (List<String> pRecords) {
        this.records = pRecords;
    }
       
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

    @Override
    public double getEstimatedCostPerInstantiation(VTIEnvironment arg0)
            throws SQLException {
        return 0;
    }

    @Override
    public double getEstimatedRowCount(VTIEnvironment arg0) throws SQLException {
        return 0;
    }

    @Override
    public boolean supportsMultipleInstantiations(VTIEnvironment arg0)
            throws SQLException {
        return false;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public OperationContext getOperationContext() {
        return this.operationContext;
    }
    
    
}
