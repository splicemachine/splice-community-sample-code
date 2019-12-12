package com.splice.custom.reader;

import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class TargetTable implements Serializable {

    private static String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static String dateTimeFormat2 = "yyyy-MM-dd HH:mm:ss.SSSSSSS";
    private static String dateTimeFormat3 = "yyyy-MM-dd HH:mm:ss";

    private static final Logger LOG = Logger
            .getLogger(MultipleSchemasStreamer.class);


    // instance properties
    String schema;
    String table;
    ArrayList<ColumnMetadata> tableColumns;


    TargetTable(String schema, String table, ArrayList<ColumnMetadata> columns)
    {
        this.schema=schema;
        this.table=table;
        this.tableColumns= copyColumns(columns);
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( schema + "." + table + " - Columns:\n" );
        for( ColumnMetadata col : tableColumns)
        {
            sb.append( "   "+ col.getColumnName() +" "+col.getDataType() + "\n" );

        }
        return sb.toString();
    }

    private ArrayList<ColumnMetadata> copyColumns( ArrayList<ColumnMetadata> columns)
    {
        ArrayList<ColumnMetadata> result = new ArrayList<ColumnMetadata>();
        for ( ColumnMetadata cm:columns) {
            result.add(new ColumnMetadata(cm));
        }
        return result;
    }

    TargetTable(String jdbcURL,  String schema, String table) throws SQLException
    {

        this.table = table;
        this.schema = schema;

        Connection conn = DriverManager.getConnection(jdbcURL);
        try
        {
            DatabaseMetaData dbm = conn.getMetaData();
            tableColumns = new ArrayList<>();
            ResultSet rs = dbm.getColumns( null, schema, table, null);
            while(rs.next())
            {

                ColumnMetadata colMetadata = new ColumnMetadata(
                        rs.getString("COLUMN_NAME"),
                        getDataTypeForJDBCType(rs.getInt("DATA_TYPE")),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getInt("DECIMAL_DIGITS"),
                        rs.getBoolean("IS_NULLABLE"),
                        rs.getBoolean("IS_AUTOINCREMENT"),
                        rs.getString("COLUMN_DEF")
                );

                tableColumns.add( colMetadata);
            }
        }
        catch (Exception ex)
        {
            LOG.error("Error getting table metadata"+ex.getMessage(), ex);
        }
        finally
        {
            conn.close();
        }

    }


    public String getSchemaName() {
        return this.schema;
    }
    public String getTable() {
        return this.table;
    }

    private DataType getDataTypeForJDBCType(int dataType)
    {

        switch (dataType)
        {
            case java.sql.Types.BIGINT: return DataTypes.IntegerType; //not sure if this should be LongType
            case java.sql.Types.INTEGER: return DataTypes.IntegerType;
            case java.sql.Types.SMALLINT: return DataTypes.ShortType;
            case java.sql.Types.TINYINT: return DataTypes.ByteType;
            case java.sql.Types.BOOLEAN: return DataTypes.BooleanType;

            case java.sql.Types.CHAR:
            case java.sql.Types.VARCHAR:
                return DataTypes.StringType;

            case java.sql.Types.DATE: return DataTypes.DateType;

            case java.sql.Types.TIME: return DataTypes.IntegerType;

            case java.sql.Types.TIMESTAMP: return DataTypes.TimestampType;

            case java.sql.Types.BLOB: return DataTypes.BinaryType;

            case java.sql.Types.CLOB:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
                return DataTypes.StringType;

            case java.sql.Types.DECIMAL:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.REAL:
            case java.sql.Types.FLOAT:
                return DataTypes.DoubleType;

            default:
                return DataTypes.NullType;
        }

    }




    public static StructType getSchema( TargetTable instance ) {

        StructField[] structFields = new StructField[instance.getTableColumns().size()];

        int i=0;
        for (ColumnMetadata col: instance.getTableColumns()) {
            structFields[i]= new StructField( col.getColumnName(), col.getDataType(), col.isNullable(), Metadata.empty());
            i++;
        }
        return new StructType(structFields);
    }

    public  ArrayList<ColumnMetadata> getTableColumns()
    {
        return tableColumns;
    }

    public static Row getRow( TargetTable instance, Map<String,Object> r) throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {

        Object[] values = new Object[instance.getTableColumns().size()];

        // search for tableColumns that are present in the inbound Map and build a row with all the available values
        // unavailable values are set to NULL (or just left out?).
        int i=0;
        for(ColumnMetadata col: instance.getTableColumns()){

            if (col.getDataType().equals(DataTypes.TimestampType)) {
                values[i]=getTimestamp((String) r.get(col.getColumnName()));
            }
            else
            {
                Object value = r.get(col.getColumnName());
                // JSON fields containing nest JSON array or object and mapped to ArrayList and LinkedHashMap
                if (value instanceof ArrayList || value instanceof LinkedHashMap)
                {
                    String out;
                    final ObjectMapper mapper = new ObjectMapper();

                    values[i] = mapper.writeValueAsString( value);
                }
                else {
                    values[i] = value;
                }
            }

            if (values[i]==null )
            {
                values[i]=convertDefaultToTargetType(col.getDefaultValue(), col.getDataType());
            }
            i++;
        }

        return RowFactory.create(values);
    }

    /**
     * Convert a database default value to appropriate object for target Row
     * @param value string representation of value
     * @param type target data type for conversion
     * @return converted object
     */
    private static Object convertDefaultToTargetType( String value, DataType type) throws ParseException
{
    Object result = null;


    if (value != null) {

        value = value.replace("'","");

        if (type.equals(DataTypes.StringType)) {
            result = value;
        }
        else if (type.equals(DataTypes.BooleanType)) {
            result = Boolean.parseBoolean(value);
        }
        else if (type.equals(DataTypes.DateType))
        {
            //TODO: Parameterize date format
            result = (new SimpleDateFormat("yyyy-MM-dd")).parse(value);
        }
        else if (type.equals(DataTypes.TimestampType))
        {
            result = getTimestamp(value);
        }
        else if (type.equals(DataTypes.DoubleType))
        {
            result = Double.parseDouble(value);
        }
        else if (type.equals(DataTypes.FloatType))
        {
            result = Float.parseFloat(value);
        }
        else if (type.equals(DataTypes.IntegerType))
        {
            result = Integer.parseInt(value);
        }
        else if (type.equals(DataTypes.LongType))
        {
            result = Long.parseLong(value);
        }
        else if (type.equals(DataTypes.ShortType))
        {
            result = Short.parseShort(value);
        }
        else
        {
            throw new NotImplementedException();
        }
    }

    return result;
}

/**
 * @param date string representation of a timestamp
 * @return Timestamp object result of parsing date parameter
 */

public static Timestamp getTimestamp(String date) {
    Timestamp updatedDate = null;
    SimpleDateFormat tf = null;

    if (date != null && date.length() > 0) {
        if (date.length()==19)
        {
            tf = new SimpleDateFormat(dateTimeFormat3);
        }
        else if (date.endsWith("Z")) {
            tf = new SimpleDateFormat(dateTimeFormat);
        } else {
            tf = new SimpleDateFormat(dateTimeFormat2);
        }

        try {

            updatedDate = new Timestamp(tf.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return updatedDate;
}


}
