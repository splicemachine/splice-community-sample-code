package com.splicemachine.tutorials.spark;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.log4j.Logger;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.api.java.JavaSparkContext;


/**
 * Spark Streaming Application that kicks off a splice machine vti
 * 
 * @author Erin Driggers
 *
 */
public class SparkLoadApplication {

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark application's log file
     */
    private static final Logger LOG = Logger.getLogger(SparkLoadApplication.class);   
    
    
    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;
    
    /**
     * This main program starts the Spark Application for connecting to the Kafka stream and processing the data.
     * There is a script file (/resources/startSparkStreamingJob.sh) which will kick off this process. If you are 
     * running this on Cloudera Manager, then you should copy that script file to the /tmp director and run it with
     * sudo -su hdfs.
     * 
     * The expected command line arguments are as follows:
     * 
     *     args[0] - JDBC URL
     * @param args
     */
    public static void main(String[] args) {
        int exitCode = 0;
        SparkLoadApplication standalone = new SparkLoadApplication();
        standalone.setSpliceJdbcUrl("jdbc:splice://stl-colo-srv54:1527/splicedb;user=splice;password=admin");
    
        try {
            standalone.processStart();
        } catch (InterruptedException e) {
            LOG.error("Exception processing dataframe",e);
            exitCode = 1;
        } catch (Exception e) {
            LOG.error("Exception processing dataframe",e);
            exitCode = 1;
        }
        System.exit(exitCode);
   }
        
    /**
     * Creates and starts the Spark Streaming job
     * @throws InterruptedException 
     * 
     */
    public void processStart() throws InterruptedException, Exception {
        LOG.info("************ SparkLoadApplication.processStart start");
        
       // Create the spark application
        SparkConf sparkConf = new SparkConf();
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        
        SQLContext sqlContext = new SQLContext(sparkContext);
        
        
        
        // Load the Sensor Messages Source table into a dataframe
        Map<String, String> options = new HashMap<String, String>();
        options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
        options.put( "url", getSpliceJdbcUrl());
        options.put("dbtable", "SPLICE.SENSOR_MESSAGES_SOURCE");
        DataFrame sourceDF = sqlContext.read().format("jdbc").options(options).load();

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

        LOG.error("About to call foreachpartition");
        sourceDF.foreachPartition(new ProcessDataFramePartition("SPLICE", "SENSOR_MESSAGES_TARGET", dataframeColumns,spliceTargetColumnNames, this.getSpliceJdbcUrl()));
        LOG.error("Done with call to foreachpartition");
        
    }
        
    public String getSpliceJdbcUrl() {
        return spliceJdbcUrl;
    }

    public void setSpliceJdbcUrl(String spliceJdbcUrl) {
        this.spliceJdbcUrl = spliceJdbcUrl;
    }

}
