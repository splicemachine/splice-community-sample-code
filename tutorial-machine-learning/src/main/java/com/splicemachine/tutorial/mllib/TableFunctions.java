package com.splicemachine.tutorial.mllib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import com.splicemachine.example.SparkMLibUtils;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary;
import org.apache.spark.mllib.stat.Statistics;

import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary;
import org.apache.spark.mllib.stat.Statistics;


public class TableFunctions {
    
    
    public static void continuousFeatureReport(String tableName) {      
        
        try {
        
        Connection conn = DriverManager.getConnection( "jdbc:default:connection" );

        //Execute a query to get a list of the columns / features that will be included 
        //for the table in the continuous feature report.
        PreparedStatement ps = conn.prepareStatement("select COLUMN_NAME, PARSE_METHOD FROM MOVIELENS.DATA_FEATURE where TABLE_NAME = ? and CONTINUOUS = ?");
        ps.setString(1, tableName);
        ps.setString(2, "Y");
        ResultSet rs = ps.executeQuery();
        
        //Now we are going to process the columns that should be included in the report
        StringBuilder columns = new StringBuilder();
        int numColumns = 0;
        while(rs.next()) {
            String columnName = rs.getString(1);            
            if(numColumns > 0) columns.append(", ");
            columns.append(columnName);            
            numColumns++;
        }
        
        
        if(numColumns > 0) {
            //Now execute another query to get the data for those columns            
            PreparedStatement ps2 = conn.prepareStatement("select " + columns.toString() + " FROM " + tableName);
            ResultSet rs2 = ps.executeQuery();
            
            //Convert the resultset into a JavaRDD
            EmbedResultSet40 ers = (EmbedResultSet40)rs2;
            com.splicemachine.db.iapi.sql.ResultSet rsUnderlying = ers.getUnderlyingResultSet();
            JavaRDD<LocatedRow> resultSetRDD = SparkMLibUtils.resultSetToRDD(rsUnderlying);

            
        }



        //Run the sql statement to get the columns / features that are going to be in the table
        
        
        
        /*
         * (
        FEATURE_NAME varchar(100),
        *COUNT integer,
        MISSING_PERCENT double,
        CARDINALITY integer,
        *MINIMUM double,
        QUARTILE_1 double,
        *MEAN double,
        MEDIAN double,
        QUARTILE_3 double,
        *MAXIMUM double,
        STD_DEVIATION double
     )
         */
        
        } catch (Exception e) {
            //TODO: Do the correct thing
        }
    }
    
    

}
