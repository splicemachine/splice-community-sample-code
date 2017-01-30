package com.splicemachine.tutorials.vti;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.vti.VTICosting;
import com.splicemachine.db.vti.VTIEnvironment;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.vti.iapi.DatasetProvider;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Erin Driggers on 01/12/2017
 */
public class DataFrameRowIteratorVTI implements DatasetProvider, VTICosting {
       
    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the HBASE Region Server logs
     */    
    private static final Logger LOG = Logger.getLogger(DataFrameRowIteratorVTI.class);
    
    private List<Row> rows = null;
    
    private ArrayList<String> dataFrameColumns;
    
    private OperationContext operationContext;

    public DataFrameRowIteratorVTI() {}
    
    /**
     * Constructor for the DataFrameVTI class.  
     * 
     * @param pDataFrameRowIterator - The dataFrameRow Iterator containing the data to save to Splice Machine
     * 
     * @param dataFrameColumns - Column names in the dataframe that you want to insert
     *                  in the sequence that you want it returned in it.
     * 
     */
    public DataFrameRowIteratorVTI(Blob  pDataFrameRowIterator, ArrayList<String> dataFrameColumns) throws SQLException, ClassNotFoundException, IOException{
        this.rows = (List<Row>)getBlobResults(pDataFrameRowIterator);
        this.dataFrameColumns = dataFrameColumns;
    }


    /**
     * Static Constructor for the DataFrameVTI class.  
     * 
     * @param pDataFrame - The dataFrame containing the data to save to Splice Machine
     * 
     * @param dataFrameColumns - Column names in the dataframe that you want to insert
     *                  in the sequence that you want it returned in it.
     * 
     */
    public static DataFrameRowIteratorVTI getDataFrameVTI(Blob  pDataFrameRowIterator, ArrayList<String> dataFrameColumns) throws SQLException, ClassNotFoundException, IOException {
        return new DataFrameRowIteratorVTI(pDataFrameRowIterator,dataFrameColumns);
    }

    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        
        //Create an arraylist to store the key / value pairs
        DataSet<LocatedRow> resp = null;
        
        try {
            
            LOG.error("In DataFrameVTI - about to do iterator:");

            // Create the iterator to read the records in ORC files.
            DataFrameRowIterator it = new DataFrameRowIterator(this.rows, this.dataFrameColumns, execRow);

            // Create DataSet of the records to return

            resp = dsp.createDataSet(it); 
 
        } catch (Exception e) {
            LOG.error("Exception processing DataFrameVTI", e);
            throw e;
        } finally {
            operationContext.popScope();
        }
        return resp;

    }
    
    /**
     * Converts the blog back into an Iterator<Row>
     * @param data
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Object getBlobResults(Blob data) throws SQLException, ClassNotFoundException, IOException {
        Object obj = null;
        if(data == null) {
            return obj;
        }
        long length = data.length();
        if(length > 0) {
            byte[] bytes = data.getBytes(Long.valueOf(1), Integer.valueOf((length)+""));
            obj = deserialize(bytes);
        }

        return obj;
    }
    
    /**
     * Deserializes the object
     * 
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = null;
        ObjectInputStream o = null;
        Object obj = null;
        
        try {
            b = new ByteArrayInputStream(bytes);
            o = new ObjectInputStream(b);
            o.close();
            obj = o.readObject();
            
        } finally {
            if(o != null) try { o.close(); } catch (Exception e){};
            if(b != null) try { b.close(); } catch (Exception e){};
        }
        return obj;
    }


    /**
     * The estimated number of rows returned in a single instantiation.
     */
    @Override
    public double getEstimatedRowCount(VTIEnvironment vtiEnvironment) throws SQLException {
        return VTICosting.defaultEstimatedRowCount;
    }

    /**
     * The estimated cost to instantiate and iterate through the results.  This method
     * returns MICROSECONDS, the internal unit for costs in splice machine (displayed
     * as milliseconds in explain plan output).  
     */
    @Override
    public double getEstimatedCostPerInstantiation(VTIEnvironment vtiEnvironment) throws SQLException {
        return VTICosting.defaultEstimatedCost;
    }

    /**
     * Whether or not the it can be instantiated multiple times 
     * within a single query execution.
     */
    @Override
    public boolean supportsMultipleInstantiations(VTIEnvironment vtiEnvironment) throws SQLException {
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
        return operationContext;
    }
    
}
