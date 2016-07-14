package com.splicemachine.tutorials.vti;

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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The is an example of a custom VTI that can be executed using a table
 * function as well as a full method name.
 * 
 * The DatasetProvider is the mechanism for constructing the execution tree
 * 
 * The VTICosting is the interface that the query optimizer uses to determine
 * the cost of executing the table function.
 * 
 * @author erindriggers
 *
 */
public class PropertiesFileVTI  implements DatasetProvider, VTICosting{
    
    //Used for logging
    private static final Logger LOG = Logger.getLogger(PropertiesFileVTI.class);

    //Instance variable that will store the name of the properties file that is being read
    private String fileName;
    
    //Provide external context which can be carried with the operation
    protected OperationContext operationContext;

    /**
     * This empty constructor is required if you want to access this VTI
     * using a Table Function
     */
    public PropertiesFileVTI () {

    }
    
    /**
     * This is the signature used by invoking the VTI using the class name.
     * 
     * @param pfileName
     */
    public PropertiesFileVTI (String pfileName) {
        this.fileName = pfileName;
    }
    
    /** 
     * This static constructor is called by the VTI - Table Function
     * 
     * @param fileName
     * @return
     */
    public static DatasetProvider getPropertiesFileVTI(String fileName) {
        return new PropertiesFileVTI(fileName);
    }
    
    /**
     * Method is called by the VTIOperation process to get the resultset to be returned.
     * 
     * op - Reference to the operation at the top of the stack
     * dsp - The mechanism for constructing the execution tree
     * execRow - 
     * 
     */
    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        
        //Create an arraylist to store the key / value pairs
        ArrayList<LocatedRow> items = new ArrayList<LocatedRow>();
        
        try {
            Properties properties = new Properties();
            
            //Load the properties file
            properties.load( getClass().getClassLoader().getResourceAsStream(fileName) );
            
            //Loop through the properties and create an array
            for(String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                ValueRow valueRow = new ValueRow(2);
                valueRow.setColumn(1,new SQLVarchar(key));
                valueRow.setColumn(2,new SQLVarchar(value));
                items.add(new LocatedRow(valueRow));
              }
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + this.fileName, e);
        } catch (IOException e) {
            LOG.error("Unexpected IO Exception: " + this.fileName, e);
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
        EmbedResultSetMetaData.getResultColumnDescriptor("KEY1", Types.VARCHAR, false, 200),
        EmbedResultSetMetaData.getResultColumnDescriptor("VALUE", Types.VARCHAR, false, 200),
    };

    private static final ResultSetMetaData metadata = new EmbedResultSetMetaData(columnInfo);
    
}
