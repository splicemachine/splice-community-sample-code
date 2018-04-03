/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package main.java.com.splicemachine.tutorials.spark;

import com.splicemachine.spark.splicemachine.SplicemachineContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * This partition receives a list of JSON objects.  A JSON object could refer
 * to more than one object type.  The JSON objects are grouped by its type and
 * a set of key value pairs are extracted from the JSON object.  The entire
 * JSON object is not sent to Splice Machine because it is a large object and would
 * make the I/O much larger than necessary.  The type, columns and key/ value pairs
 * are saved to splice machine by calling a VTI.
 * <p>
 * Created by Erin Driggers
 */
public class MapPartition implements FlatMapFunction<Iterator<String>, Row>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's EXECUTOR log
     */
    private static final Logger LOG = Logger
            .getLogger(MapPartition.class);

    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;

    /**
     * For this initial code, we will pass in the schema where the tables reside.  In the future the
     * requirement will be to use the tenant id for the schema.
     */
    private String spliceSchema;

    /**
     * The splice machine table where we will be inserting data
     */
    private String spliceTable;


    /**
     * The import parameters
     */
    private HashMap<String, String> importParms = null;


    private String characterDelimiter = null;
    private String columnDelimiter = null;
    private String timeFormat = null;
    private String dateFormat = null;
    private String timestampFormat = null;

    private DateFormat df = null;
    private DateFormat tf = null;

    private StructType schema_item = null;


    /**
     * Constructor for the SavePartition which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     *
     * @param spliceJdbcUrl
     * @param spliceSchema
     * @param spliceTable
     * @param importParms
     */
    public MapPartition(String spliceJdbcUrl, String spliceSchema, String spliceTable, HashMap<String, String> importParms) {
        LOG.warn("In MapPartition Constructor:");
        this.spliceJdbcUrl = spliceJdbcUrl;
        this.spliceSchema = spliceSchema;
        this.spliceTable = spliceTable;
        this.importParms = importParms;
        processInputParms();
        setSchema();
    }

    /**
     * Constructor for the SavePartition which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     *
     * @param schema_item
     * @param importParms
     */
    public MapPartition(StructType schema_item, HashMap<String, String> importParms) {
        LOG.warn("In MapPartition2 Constructor:");
        this.importParms = importParms;
        processInputParms();
        this.schema_item = schema_item;
    }

    public void processInputParms() {
        if (importParms != null) {
            characterDelimiter = importParms.get("characterDelimiter");
            columnDelimiter = importParms.get("columnDelimiter");
            timeFormat = importParms.get("timeFormat");
            dateFormat = importParms.get("dateTimeFormat");
            timestampFormat = importParms.get("timestampFormat");
        }

        if (characterDelimiter == null || "null".equals(characterDelimiter))
            characterDelimiter = "\"";
        if (columnDelimiter == null || "null".equals(columnDelimiter))
            columnDelimiter = ",";
        if (timestampFormat == null || "null".equals(timestampFormat))
            timestampFormat = "yyyy-MM-dd HH:mm:ss";
        if (dateFormat == null || "null".equals(dateFormat))
            dateFormat = "yyyy-MM-dd";
        if (timeFormat == null || "null".equals(timeFormat))
            timeFormat = "HH:mm:ss";

        this.df = new SimpleDateFormat(dateFormat);
        this.tf = new SimpleDateFormat(timestampFormat);
    }

    public void setSchema() {
        SplicemachineContext splicemachineContext = new SplicemachineContext(spliceJdbcUrl);
        String SPLICE_TABLE_ITEM = this.spliceSchema + "." + this.spliceTable;
        try {
            schema_item = splicemachineContext.getSchema(SPLICE_TABLE_ITEM);
        } catch (Exception e) {
            schema_item = splicemachineContext.getSchema(SPLICE_TABLE_ITEM);
        }
    }


    /**
     * The messagesIter is an RDD that is comprised of JSON strings that are
     * of various types of objects.  We need to group each
     * unique type of object into its own arraylist and parse out only the
     * fields in the JSON object that are possibly needed.
     * We will and call a VTI to insert the data into Splice Machine.
     *
     * @param messagesIter
     * @return
     * @throws Exception
     */
    public Iterator<Row> call(Iterator<String> messagesIter) throws Exception {
        long startTime = System.currentTimeMillis();
        LOG.warn("Begin Iterator<Row>:");

        List<Row> rowsToInsert = new ArrayList();

        if (messagesIter != null) {
            StructField[] fields = schema_item.fields();

            while (messagesIter.hasNext()) {
                String record = messagesIter.next();
                String[] columns = StringUtils.splitPreserveAllTokens(record, columnDelimiter);

                Object[] vals = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) {

                    StructField fieldDetails = fields[i];
                    DataType fieldType = fieldDetails.dataType();
                    String fieldValue = columns[i];

                    if (fieldType == DataTypes.StringType) {
                        if(fieldValue == null || fieldValue.length() == 0) {
                            vals[i] = null;
                        } else {
                            if (characterDelimiter != null && fieldValue.startsWith(characterDelimiter)) {
                                vals[i] = fieldValue = fieldValue.substring(1, fieldValue.length()-1);
                            } else {
                                vals[i] = fieldValue;
                            }
                            vals[i] = null;
                        }
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : fieldValue;
                    } else if (fieldType == DataTypes.LongType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : Long.valueOf(fieldValue);
                    } else if (fieldType == DataTypes.IntegerType || fieldType == DataTypes.ShortType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : Integer.valueOf(fieldValue);
                    } else if (fieldType == DataTypes.DoubleType || fieldType == DataTypes.FloatType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : Double.valueOf(fieldValue);
                    } else if (fieldType.typeName().startsWith("decimal(")) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : BigDecimal.valueOf(Double.valueOf(fieldValue));
                    } else if (fieldType == DataTypes.DateType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : new java.sql.Date((df.parse(fieldValue)).getTime());
                    } else if (fieldType == DataTypes.TimestampType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : new Timestamp(tf.parse(fieldValue).getTime());
                    } else if (fieldType == DataTypes.BooleanType) {
                        vals[i] = (fieldValue == null || fieldValue.length() == 0) ? null : Boolean.valueOf(fieldValue);
                    } else {
                        LOG.warn("Unknown datatype:" + fieldType.typeName());
                        vals[i] = fieldValue;
                    }
                }
                rowsToInsert.add(RowFactory.create(vals));
            }

        }
        LOG.warn("End Iterator<Row>:" + rowsToInsert.size());
        return rowsToInsert.iterator();

    }
}


