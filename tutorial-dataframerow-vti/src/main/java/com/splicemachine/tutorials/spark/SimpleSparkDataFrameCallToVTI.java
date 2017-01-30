package com.splicemachine.tutorials.spark;
        
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;

import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;

import com.splicemachine.db.iapi.types.SQLLongint;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedConnection;
import com.splicemachine.db.impl.jdbc.EmbedResultSet40;
import com.splicemachine.db.impl.sql.GenericColumnDescriptor;
import com.splicemachine.db.impl.sql.execute.IteratorNoPutResultSet;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.db.iapi.types.DataValueDescriptor;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class SimpleSparkDataFrameCallToVTI {
    
    private static final Logger LOG = Logger
            .getLogger(SimpleSparkDataFrameCallToVTI.class);
    

    public static void populateSensorPartitionTargetTable(ResultSet[] resultSets) throws SQLException {
        Connection conn = null;
        try {
            JavaSparkContext spliceSparkContext = SpliceSpark.getContext();
            SQLContext sqlContext = new SQLContext(spliceSparkContext);
            
            String spliceJdbcUrl = "jdbc:splice://localhost:1527/splicedb;user=splice;password=admin";
            
            // Load the Sensor Messages Source table into a dataframe
            Map<String, String> options = new HashMap<String, String>();
            options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
            options.put( "url", spliceJdbcUrl);
            options.put("dbtable", "SPLICE.SENSOR_MESSAGES_SOURCE");
            DataFrame sourceDF = sqlContext.read().format("jdbc").options(options).load();
            
            LOG.error("sourceDF=" + sourceDF.count());

            //Get a list of column names from the source dataframe that you want to return
            //The order needs to map the target
            List<String> dataframeColumns = new ArrayList<String>();
            dataframeColumns.add("SENSOR_ID");
            dataframeColumns.add("TEMPERATURE");
            dataframeColumns.add("HUMIDITY");
            dataframeColumns.add("RECORDED_TIME");
            
            //Populate a list of the columns in the target database that you want 
            //to insert data into
            List<String> spliceTargetColumnNames = new ArrayList<String>();
            spliceTargetColumnNames.add("SENSOR_ID");
            spliceTargetColumnNames.add("TEMPERATURE");
            spliceTargetColumnNames.add("HUMIDITY");
            spliceTargetColumnNames.add("RECORDEDTIME");

            //Get a connection
            //conn = DriverManager.getConnection("jdbc:default:connection");
            
            LOG.error("About to call foreachpartition");
            
            sourceDF.foreachPartition(new ProcessDataFramePartition("SPLICE", "SENSOR_MESSAGES_TARGET", dataframeColumns,spliceTargetColumnNames));
            
            LOG.error("Done with call to foreachpartition");
            
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
                               
            resultSets[0] = new EmbedResultSet40((EmbedConnection) conn, rs, false, null, true);
            
        } catch (Exception sqle) {
            sqle.printStackTrace();
            LOG.error("Exception processing the dataframe", sqle);
            throw new SQLException(sqle);
        } finally {
            if (conn != null) {
                try {
                ((EmbedConnection) conn).getLanguageConnection().resetBadFile();
                ((EmbedConnection) conn).getLanguageConnection().resetFailedRecords();
                ((EmbedConnection) conn).getLanguageConnection().resetRecordsImported();

                    //conn.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    
    /**
     * Thse are the columns that will be returned in the result set
     */
    private static final ResultColumnDescriptor[] IMPORT_RESULT_COLUMNS = new GenericColumnDescriptor[]{
        new GenericColumnDescriptor("rowsImported", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.BIGINT)),
        new GenericColumnDescriptor("failedRows", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.BIGINT)),
        new GenericColumnDescriptor("failedLog", DataTypeDescriptor.getBuiltInDataTypeDescriptor(Types.VARCHAR))
    };

}