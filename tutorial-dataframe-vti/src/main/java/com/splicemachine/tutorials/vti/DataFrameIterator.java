package com.splicemachine.tutorials.vti;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;

import com.splicemachine.db.iapi.services.io.StoredFormatIds;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataValueDescriptor;
import com.splicemachine.db.iapi.types.DateTimeDataValue;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.utils.SpliceDateFunctions;

class DataFrameIterator implements Iterable<LocatedRow>{
    
    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the HBASE Region Server logs
     */    
    private static final Logger LOG = Logger.getLogger(DataFrameIterator.class);
    
    /**
     * DataFrame containing objects to be saved
     */
    private DataFrame dataFrame = null;
    
    /**
     * List of columns from the source DataFrame to 
     * be saved
     */
    private ArrayList<String> sourceColumns = null;

    /**
     * The rows in the DataFrame
     */
    private java.util.List<Row> rows = null;
    
    /**
     * Contains the format of the row as it is expected
     */
    private ExecRow execRow;
    
    private String timeFormat;
    
    private String dateTimeFormat;

    //2017-01-12 17:29:11.048
    private String timestampFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private transient Calendar calendar;
    
    private transient DateTimeFormatter parser;
    

    /**
     * Constructor for the DataFrameIterator
     * 
     * @param pDataFrame - DataFrame to be imported
     * @param pSourceColumns - The columns from the DataFrame to be imported
     * @param pExecRow - The execRow with the Splice Target details
     */
    public DataFrameIterator(DataFrame pDataFrame, ArrayList<String> pSourceColumns, ExecRow pExecRow) {

        LOG.info("Creating DataFrameIterator - DataFrame");
        this.dataFrame = pDataFrame;
        if(this.dataFrame != null) {
            this.rows = this.dataFrame.collectAsList();
        }
        this.sourceColumns = pSourceColumns;
        this.execRow = pExecRow;
        parser = DateTimeFormat.forPattern(timestampFormat);
    }
    
    public DataFrameIterator(java.util.List<Row> pRows, ArrayList<String> pSourceColumns, ExecRow pExecRow) {
        LOG.info("Begin - Creating DataFrameIterator with list of rows");
        
        if(pRows != null) {
            this.rows = pRows;
        }
        this.sourceColumns = pSourceColumns;
        this.execRow = pExecRow;
        parser = DateTimeFormat.forPattern(timestampFormat);
    }
    
    public Iterator<LocatedRow> iterator(){
        
        return new Iterator<LocatedRow>() {
            private int count=0;
           
            public boolean hasNext(){
                if(rows == null) return false;
                return count < rows.size();
            }
            public LocatedRow next(){
                return getRow( rows.get(count++)); 
            }

            public void remove(){
                throw new UnsupportedOperationException();
            }
        };
    }
    
    /**
     * This will dynamically determine what the response should be
     * 
     * @param jsonString
     * @return
     * @throws Exception 
     */
    public LocatedRow getRow(Row dataFrameRow) {     
        try {
            
            //We need to retrieve the keys and values from the Hashmpa object
            //part of the reason we can't do it on the fly is that there is
            //a case sensitivity issue between was comes in the JSON vs
            //what the database column name.
            
            ExecRow returnRow = execRow.getClone();
            
            boolean dataTypeMappingComplete = false;
            
            for (int i = 1; i <= returnRow.nColumns(); i++) {
                DataValueDescriptor dvd = returnRow.getColumn(i);
                int type = dvd.getTypeFormatId();
                
                //Get the column Name
                String columnName = sourceColumns.get((i-1));
                
                //Get the field Index
                int sourceFieldIndex =  dataFrameRow.fieldIndex(columnName);
                
                //Determine if the field is null
                boolean isNull = dataFrameRow.isNullAt(sourceFieldIndex);

                Object objVal = dataFrameRow.apply(sourceFieldIndex);
                
                String value = objVal.toString();
                
                
                LOG.error("Column Name:[" + columnName + "] isNull:[" + isNull + "] value:[" + value + "]");
                
                if (value != null && (value.equals("null") || value.equals("NULL") || value.isEmpty()))
                    value = null;
                if (type == StoredFormatIds.SQL_TIME_ID) {
                    if(calendar==null)
                        calendar = new GregorianCalendar();
                    if (timeFormat == null || isNull){
                        ((DateTimeDataValue)dvd).setValue(value,calendar);
                    }else
                        dvd.setValue(SpliceDateFunctions.TO_TIME(value, timeFormat),calendar);
                } else if (type == StoredFormatIds.SQL_TIMESTAMP_ID) {
                    if(calendar==null)
                        calendar = new GregorianCalendar();
                    if (timestampFormat == null || isNull)
                        ((DateTimeDataValue)dvd).setValue(value,calendar);
                    else
                        dvd.setValue(new Timestamp(parser.withOffsetParsed().parseDateTime(value).getMillis()),calendar);
                } else if (type == StoredFormatIds.SQL_DATE_ID) {
                    if(calendar==null)
                        calendar = new GregorianCalendar();
                    if (dateTimeFormat == null || isNull)
                        ((DateTimeDataValue)dvd).setValue(value,calendar);
                    else
                        dvd.setValue(SpliceDateFunctions.TO_DATE(value, dateTimeFormat),calendar);
                } else {
                    dvd.setValue(value);
                }
            }
            return new LocatedRow(returnRow);
        } catch (Exception e) {
            LOG.error("Exception building row", e);
            throw new RuntimeException(e);
        }
    }
}