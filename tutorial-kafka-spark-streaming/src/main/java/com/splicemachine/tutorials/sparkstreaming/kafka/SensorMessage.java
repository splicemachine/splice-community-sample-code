package com.splicemachine.tutorials.sparkstreaming.kafka;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Connection;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.types.SQLDecimal;
import com.splicemachine.db.iapi.types.SQLTimestamp;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.sql.execute.ValueRow;

public class SensorMessage {
    
    private String id;
    private String location;
    private double temperature;
    private double humidity;
    private Timestamp recordedTime;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public double getHumidity() {
        return humidity;
    }
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public Timestamp getRecordedTime() {
        return recordedTime;
    }
    public void setRecordedTime(Timestamp recordedTime) {
        this.recordedTime = recordedTime;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=["+this.getId() +"]\n");
        sb.append("location=["+this.getLocation() +"]\n");
        sb.append("temperature=["+this.getTemperature() +"]\n");
        sb.append("humidity=["+this.getHumidity() +"]\n");
        sb.append("recordedTime=["+this.getRecordedTime() +"]\n");
        return sb.toString();
    }
    
    public void addParametersToPreparedStatement(PreparedStatement stmt) throws SQLException {
        
        stmt.setString(1,this.getId());
        stmt.setString(2,this.getLocation());
        stmt.setDouble(3,this.getTemperature());
        stmt.setDouble(4,this.getHumidity());
        stmt.setTimestamp(5,this.getRecordedTime());
    }
    
    public static PreparedStatement getPreparedStatement(Connection con) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into iot.SENSOR_MESSAGES values(");
        for(int i=0; i<5; i++) {
            if(i > 0) {
                sb.append(",");
            }
            sb.append("?");
            
        }   
        sb.append(")");
        return con.prepareStatement(sb.toString());
    }
    
    public static String getTableDefinition() {
        return 
        "id varchar(20), " +
        "location varchar(50), " +
        "temperature decimal(12,5), " +
        "humidity decimal(12,5), " +
        "recordedtime timestamp ";
    }
    
    public String[] getDataAsStringArray() {
        String[] rtnVal = new String[5];
        rtnVal[0]="" + this.getId();
        rtnVal[1]="" + this.getLocation();
        rtnVal[2]="" + this.getTemperature();
        rtnVal[3]="" + this.getHumidity();
        rtnVal[4]="" + this.getRecordedTime();
        return rtnVal;
    }

    public ValueRow getRow() throws SQLException, StandardException {       
        ValueRow valueRow = new ValueRow(5);
        valueRow.setColumn(1,new SQLVarchar(this.getId()));
        valueRow.setColumn(2,new SQLVarchar(this.getLocation()));
        valueRow.setColumn(3,new SQLDecimal(BigDecimal.valueOf(this.getTemperature())));
        valueRow.setColumn(4,new SQLDecimal(BigDecimal.valueOf(this.getHumidity())));
        valueRow.setColumn(5,new SQLTimestamp(this.getRecordedTime()));
        return valueRow;
    }
  
    
}
