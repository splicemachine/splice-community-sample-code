package com.splice.custom.reader;

import org.apache.spark.sql.types.DataType;

import java.io.Serializable;

public class ColumnMetadata implements Serializable {
    private String columnName;
    private DataType dataType;
    private int columnSize;
    private int decimalDigits;
    private boolean isNullable;
    private boolean isAutoIncrement;
    private String defaultValue;

    ColumnMetadata( ColumnMetadata source)
    {
        this.columnName=source.getColumnName();
        this.dataType=source.getDataType();
        this.columnSize=source.getColumnSize();
        this.decimalDigits=source.getDecimalDigits();
        this.isNullable=source.isNullable();
        this.isAutoIncrement=source.isAutoIncrement();
        this.defaultValue=source.getDefaultValue();
    }

    ColumnMetadata( String columnName, DataType dataType, int columnSize, int decimalDigits, boolean isNullable, boolean isAutoIncrement, String defaultValue)
    {
        this.columnName=columnName;
        this.dataType=dataType;
        this.columnSize=columnSize;
        this.decimalDigits=decimalDigits;
        this.isNullable=isNullable;
        this.isAutoIncrement=isAutoIncrement;
        this.defaultValue=defaultValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getDefaultValue() { return defaultValue; }

    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
}
