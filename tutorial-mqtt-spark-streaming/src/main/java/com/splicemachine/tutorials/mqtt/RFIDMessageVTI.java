package com.splicemachine.tutorials.mqtt;

import java.io.StringReader;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.impl.jdbc.EmbedResultSetMetaData;
import com.splicemachine.db.vti.VTICosting;
import com.splicemachine.db.vti.VTIEnvironment;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.control.ControlDataSet;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.vti.iapi.DatasetProvider;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * The purpose of this de
 * 
 * @author erindriggers
 *
 */
public class RFIDMessageVTI  implements DatasetProvider, VTICosting{
    
    //Used for logging
    private static final Logger LOG = Logger.getLogger(RFIDMessageVTI.class);

    //List of records to be saved
    private List<String> records = null;
    
    // only map the first 3 columns - setting header elements to null means those columns are ignored
    final String[] header = new String[] { "assetNumber", "assetLocation", "recordedTime"};
    
    final CellProcessor[] processors = new CellProcessor[] { new Optional(), new Optional(), new Optional()};
    
    //Provide external context which can be carried with the operation
    protected OperationContext operationContext;
    
    /**
     * Null constructor required to use as a TABLE FUNCTION
     */
    public RFIDMessageVTI() {
        
    }

    /*temporary - Jira - DB-5472.
    public RFIDMessageVTI (List<String> pRecords) {
        this.records = pRecords;
    }
    */

    /**
     * This function is a temporary workaround until the CAST exception
     * is fixed - Jira - DB-5472.
     * 
     * @param pConcatenatedRecords
     */
    public RFIDMessageVTI (String pConcatenatedRecords) {
        if(pConcatenatedRecords != null) {
            String[] aRecords = pConcatenatedRecords.split("\n");        
            this.records = Arrays.asList(aRecords);  
        }
    }
    
    /* Temporary Jira - DB-5472.
    public static DatasetProvider getRFIDMessageVTI(List<String> pRecords) {
        return new SensorMessageVTI(pRecords);
    }
    */

    /**
     * This function is a temporary workaround until the CAST exception in 
     * Jira - DB-5472 is fixed.
     * 
     * @param pRecords
     * @return
     */
    public static DatasetProvider getRFIDMessageVTI(String pConcatenatedRecords) {        
        return new RFIDMessageVTI(pConcatenatedRecords);
    }
    
    
    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        
        //Create an arraylist to store the key / value pairs
        ArrayList<LocatedRow> items = new ArrayList<LocatedRow>();
        
        try {
            
            int numRcds = this.records == null ? 0 : this.records.size();
            
            if(numRcds > 0 ) {        
                
                LOG.info("Records to process:" + numRcds);
                //Loop through each record convert to a SensorObject
                //and then set the values
                for(String csvString : records) {  
                    CsvBeanReader beanReader = new CsvBeanReader(new StringReader(csvString), CsvPreference.STANDARD_PREFERENCE);
                    RFIDMessage msg = beanReader.read(RFIDMessage.class, header, processors);
                    items.add(new LocatedRow(msg.getRow()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception processing RFIDMessageVTI", e);
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

    @Override
    public OperationContext getOperationContext() {
        return this.operationContext;
    }
    
    /**
     * Dynamic MetaData used to dynamically bind a function.
     * 
     * Metadata
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return metadata;
    }
    
    private static final ResultColumnDescriptor[] columnInfo = {
        EmbedResultSetMetaData.getResultColumnDescriptor("ASSET_NUMBER", Types.VARCHAR, false, 50),
        EmbedResultSetMetaData.getResultColumnDescriptor("ASSET_DESCRIPTION", Types.VARCHAR, false, 100),
        EmbedResultSetMetaData.getResultColumnDescriptor("RECORDED_TIME", Types.TIMESTAMP, false, 100),
        EmbedResultSetMetaData.getResultColumnDescriptor("ASSET_TYPE", Types.VARCHAR, false, 50),
        EmbedResultSetMetaData.getResultColumnDescriptor("ASSET_LOCATION", Types.VARCHAR, false, 50),
    };

    private static final ResultSetMetaData metadata = new EmbedResultSetMetaData(columnInfo);
    
}
