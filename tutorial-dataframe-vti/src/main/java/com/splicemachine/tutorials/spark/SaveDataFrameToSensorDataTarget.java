package com.splicemachine.tutorials.spark;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.DataFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;


import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;
import com.splicemachine.db.iapi.types.DataValueDescriptor;
import com.splicemachine.db.iapi.types.SQLLongint;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedConnection;
import com.splicemachine.db.impl.jdbc.EmbedResultSet40;
import com.splicemachine.db.impl.load.ColumnInfo;
import com.splicemachine.db.impl.sql.GenericColumnDescriptor;
import com.splicemachine.db.impl.sql.execute.IteratorNoPutResultSet;
import com.splicemachine.db.impl.sql.execute.ValueRow;


/**
 * This class is used for inserting a dataframe into Splice Machine.  This class
 * calls the DataFrameVTI which will process the rows in the VTI and insert it
 * into the Splice Machine database.  It will return the same type of messages
 * that are returned by the import process.
 * 
 * Created by Erin Driggers
 */
public class SaveDataFrameToSensorDataTarget implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's EXECUTOR log
     */       
    private static final Logger LOG = Logger
            .getLogger(SaveDataFrameToSensorDataTarget.class);
    
    /**
     * Connection to the database
     */
    private Connection conn;
    

    /**
     * The schema name
     */
    private String schemaName;
    
    /**
     * The table name in splice machine
     */
    private String tableName;
            
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
     * Creates an instance of SaveDataFrameToSensorDataTarget using the default values
     * for the badRecordDirecotory, badRecordsAllowed and isUpsert 
     * 
     * @param pConn - Connection Object
     * @param pSchema - Target Schema in Splice Machine
     * @param pTable - Target Table in Splice Machine
     */
    public SaveDataFrameToSensorDataTarget(Connection pConn, String pSchema, String pTable) {
        this(pConn, pSchema, pTable, "/BAD", -1,  false);
    }

    /**
     * Creates an instance of SaveDataFrameToSensorDataTarget
     * 
     * @param pConn - Connection Object
     * @param pSchema - Target Schema in Splice Machine
     * @param pTable - Target Table in Splice Machine
     * @param bBadDirectory - The directory in which bad record information is logged. Splice Machine logs information 
     *          to the <import_file_name>.bad file in this directory; for example, bad records in an input file named 
     *          foo.csv would be logged to a file named badRecordDirectory/foo.csv.bad.
     * @param badRecordsAllowed - The number of rejected (bad) records that are tolerated before the import fails. 
     *          If this count of rejected records is reached, the import fails, and any successful record imports are rolled back.
     * @param upsert - Indicates if the action is an insert or an upsert
     */
    public SaveDataFrameToSensorDataTarget(Connection pConn, String pSchema, String pTable, String bBadDirectory, int badRecordsAllowed, boolean upsert) {
        this.conn = pConn;
        this.schemaName = pSchema;
        this.tableName = pTable;
        this.badRecordDirectory = bBadDirectory;
        this.badRecordsAllowed = badRecordsAllowed;
        this.isUpsert = upsert;
    }
   

    /**
     * Kicks off the VTI process for importing data via a dataframe.
     * 
     * @param dataframe - the DataFrame containing the source data
     * @param sourceColumns - the columns from the source dataframe that should be mapped to the target database table
     * @param targetColumns - the columns in the target database
     * 
     * @return
     * @throws Exception
     */
    public ResultSet importDataFrame(DataFrame dataframe, List<String> sourceDataFrameColumns, List<String> targetSpliceColumns) throws Exception {
         long startTime = System.currentTimeMillis();
         
         ResultSet[] results = new ResultSet[1]; 
         if(dataframe!=null) {


            try {
                
                LOG.info("In importDataFrame dataframe(count)=" + dataframe.count());

                //Get the VTI Insert Statement
                String insertSql = this.getVTISyntaxForType(conn, targetSpliceColumns);
                
                //Connection conn2 = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");;
                
                //prepare the import statement to hit any errors before locking the table
                //execute the import operation.
                try (PreparedStatement ips = conn.prepareStatement(insertSql)) {
                    
                    //Set the parameters on the prepared statement
                    //byte[] bytes = serialize(dataframe);
                    byte[] bytes = serialize(dataframe.collectAsList());
                    ips.setBytes(1, bytes);
                    ips.setObject(2, sourceDataFrameColumns);
                    
                    LOG.info("About to execute insert");
                    
                    //Execute the Insert Statement
                    ips.executeUpdate();
                    
                    LOG.info("After execute insert");
                    
                    //Get the Bad File
                    String badFileName = ((EmbedConnection) conn).getLanguageConnection().getBadFile();
                    
                    //Create a result record which contains the details of the records
                    //That were imported, the failed records

                    ExecRow result = new ValueRow(3);
                    result.setRowArray(new DataValueDescriptor[]{
                        new SQLLongint(((EmbedConnection) conn).getLanguageConnection().getRecordsImported()),
                        new SQLLongint(((EmbedConnection) conn).getLanguageConnection().getFailedRecords()),
                        new SQLVarchar((badFileName == null || badFileName.isEmpty() ? "NONE" : badFileName))
                    });
                    Activation act = ((EmbedConnection) conn).getLanguageConnection().getLastActivation();
                    IteratorNoPutResultSet rs =
                        new IteratorNoPutResultSet(Collections.singletonList(result),
                                                   (IMPORT_RESULT_COLUMNS), act);
                    rs.open();
                                       
                    results[0] = new EmbedResultSet40((EmbedConnection) conn, rs, false, null, true);

                } catch (Exception e) {
                    throw new SQLException(e);
                }
            } finally {
                if (conn != null) {
                    ((EmbedConnection) conn).getLanguageConnection().resetBadFile();
                    ((EmbedConnection) conn).getLanguageConnection().resetFailedRecords();
                    ((EmbedConnection) conn).getLanguageConnection().resetRecordsImported();
                    conn.close();
                }
            }
            
        }
        return results[0];
    }

    
    /**
     * Convert the DataFrame object to a byte array to send over as a BLOB to the VTI
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
            throw e;
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
     * @param con - JDBC Connection
     * @param targetColumns - The columns to be written to in the target database
     * 
     * @return
     * @throws SQLException 
     */
    public String getVTISyntaxForType(Connection con, List<String> targetColumns) throws SQLException {
        
        //Get the column details from Splice Machine
        ColumnInfo columnInfo = new ColumnInfo(con, this.getSchemaName(), this.getTableName(), targetColumns);       
        
        StringBuffer sb = new StringBuffer("new com.splicemachine.tutorials.vti.DataFrameVTI (?,?) ");

        String columns = columnInfo.getInsertColumnNames();

        //Use this insert statement if you are on a release prior to 2.0.1.41
        String insertSql = "INSERT INTO " + this.getSchemaName() + "." + this.getTableName() + "(" + columns + ") " +
                " SELECT " +  columns + " from " +
                sb.toString() + " AS importVTI (" + columnInfo.getImportAsColumns() + ")";
        
        
        //Use this insert statement if you are on a release equal to or greater than 2.0.1.41
        //It sets the following hints for the insert statement:
        //     useSpark - tells the process to use spark or not use spark
        //     insertMode - indicates the insert type either INSERT or UPSERT
        //     statusDirectory - The Bad directory where the details will be written
        //     badRecordsAllowed - The number of bad records allowed before the process stop
        /*
        String insertSql2 = "INSERT INTO " + this.getSchemaName() + "." + this.getTableName() + "(" + columns + ") " +
               "--splice-properties useSpark=true, insertMode=" + (isUpsert ? "UPSERT" : "INSERT") + ", statusDirectory=" +
               badRecordDirectory + ", badRecordsAllowed=" + badRecordsAllowed + "\n" +
               " SELECT " +  columns + " from " +
               sb.toString() + " AS importVTI (" + columnInfo.getImportAsColumns() + ")";
        */

        return insertSql;
    }    
    
    /**
     * Thse are the columns that will be returned in the result set
     */
    private static final ResultColumnDescriptor[] IMPORT_RESULT_COLUMNS = new GenericColumnDescriptor[]{
        new GenericColumnDescriptor("rowsImported", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.BIGINT)),
        new GenericColumnDescriptor("failedRows", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.BIGINT)),
        new GenericColumnDescriptor("failedLog", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.VARCHAR))
    };

    /**
     * Get the Connection Object
     * @return
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * Sets the Connection object
     * @param conn
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * Gets the schema name
     * @return
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the target schema name
     * 
     * @param schemaName
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Gets the target table name
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the target table name
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets the number of bad records allowed
     * @return
     */
    public int getBadRecordsAllowed() {
        return badRecordsAllowed;
    }

    /**
     * Sets the number of bad records allowed
     * @param badRecordsAllowed
     */
    public void setBadRecordsAllowed(int badRecordsAllowed) {
        this.badRecordsAllowed = badRecordsAllowed;
    }

    /**
     * Returns true if the action should be an upsert, false
     * if it should be an insert
     * 
     * @return
     */
    public boolean isUpsert() {
        return isUpsert;
    }

    /**
     * Sets the value of the isUpsert.  True means it does
     * an upsert, false means it does an insert
     * @param isUpsert
     */
    public void setUpsert(boolean isUpsert) {
        this.isUpsert = isUpsert;
    }

    /**
     * Gets the bad directory
     * @return
     */
    public String getBadRecordDirectory() {
        return badRecordDirectory;
    }

    /**
     * Sets the bad directory
     * @param badRecordDirectory
     */
    public void setBadRecordDirectory(String badRecordDirectory) {
        this.badRecordDirectory = badRecordDirectory;
    }
    
}
