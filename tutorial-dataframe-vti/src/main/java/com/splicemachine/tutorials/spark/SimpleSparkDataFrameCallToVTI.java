package com.splicemachine.tutorials.spark;
        
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaSparkContext;

import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;
import com.splicemachine.db.iapi.types.SQLDouble;
import com.splicemachine.db.iapi.types.SQLLongint;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedConnection;
import com.splicemachine.db.impl.jdbc.EmbedResultSet40;
import com.splicemachine.db.impl.sql.GenericColumnDescriptor;
import com.splicemachine.db.impl.sql.execute.IteratorNoPutResultSet;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.utils.SpliceAdmin;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

public class SimpleSparkDataFrameCallToVTI {
    
    private static final Logger LOG = Logger
            .getLogger(SimpleSparkDataFrameCallToVTI.class);
    
    public static void populateSensorTargetTable(ResultSet[] resultSets) throws SQLException {
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
            
            LOG.info("sourceDF=" + sourceDF.count());

            
            
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
            conn = DriverManager.getConnection("jdbc:default:connection");
                        
            SaveDataFrameToSensorDataTarget saveToTarget = new SaveDataFrameToSensorDataTarget(conn, "SPLICE", "SENSOR_MESSAGES_TARGET");
            resultSets[0] = saveToTarget.importDataFrame(sourceDF, dataframeColumns, spliceTargetColumnNames);
            
            
        } catch (Exception sqle) {
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
}