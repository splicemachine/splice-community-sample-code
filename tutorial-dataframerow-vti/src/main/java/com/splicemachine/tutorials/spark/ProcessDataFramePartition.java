package com.splicemachine.tutorials.spark;
        
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Row;

import scala.collection.Iterator;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

public class ProcessDataFramePartition extends AbstractFunction1<Iterator<Row>, BoxedUnit> implements Serializable{
    
    private static final long serialVersionUID = -1919222653470217466L;
    
    private static final Logger LOG = Logger
            .getLogger(ProcessDataFramePartition.class);
    
    Connection conn = null;
    String jdbcUrl;
    String schema;
    String table;
    List<String> dataframeColumns;
    List<String> spliceTargetColumnNames;
    
    public ProcessDataFramePartition(Connection conn, String schema, String table, List<String> dataframeColumns, List<String> spliceTargetColumnNames) {
        this.conn = conn;
        this.schema = schema;
        this.table = table;
        this.dataframeColumns = dataframeColumns;
        this.spliceTargetColumnNames = spliceTargetColumnNames;
    }

    public ProcessDataFramePartition(String schema, String table, List<String> dataframeColumns, List<String> spliceTargetColumnNames) {
        this.schema = schema;
        this.table = table;
        this.dataframeColumns = dataframeColumns;
        this.spliceTargetColumnNames = spliceTargetColumnNames;        
        LOG.error("In Constructor of ProcessDataFramePartition");
    }

    public ProcessDataFramePartition(String schema, String table, List<String> dataframeColumns, List<String> spliceTargetColumnNames, String jdbcUrl) throws Exception{
        this.schema = schema;
        this.table = table;
        this.dataframeColumns = dataframeColumns;
        this.spliceTargetColumnNames = spliceTargetColumnNames;
        this.jdbcUrl = jdbcUrl;
        LOG.error("In Constructor of ProcessDataFramePartition");
    }
    
    @Override
    public BoxedUnit apply(Iterator<Row> iterator) {
        try {
            LOG.error("In apply: jdbcUrl:" + this.jdbcUrl);
            
            SaveDataFrameToSensorDataTarget saveToTarget = new SaveDataFrameToSensorDataTarget(conn, schema, table);
            saveToTarget.importDataFrame(iterator, dataframeColumns, spliceTargetColumnNames);
        }catch (Exception e) {
            e.printStackTrace();
            //throw e;
        }
        
        return BoxedUnit.UNIT;
    }
}
