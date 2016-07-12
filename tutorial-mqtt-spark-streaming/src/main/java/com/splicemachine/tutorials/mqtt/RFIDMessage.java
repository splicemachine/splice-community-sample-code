package com.splicemachine.tutorials.mqtt;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.types.SQLTimestamp;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.sql.execute.ValueRow;

public class RFIDMessage {

    private String assetNumber;
    private String assetDescription;
    private Timestamp recordedTime;
    private String assetType;
    private String assetLocation;
    
    
    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getAssetDescription() {
        return "Description " + this.assetNumber;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

    public Timestamp getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(Timestamp recordedTime) {
        this.recordedTime = recordedTime;
    }
    
    public void setRecordedTime(String recordedTime) {
        if(recordedTime != null && recordedTime.length() > 0) {
            this.recordedTime = Timestamp.valueOf(recordedTime);
        }
    }

    public String getAssetType() {
        return "Asset Type " + this.assetNumber;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetLocation() {
        return this.assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }
    
    public String toString() {
        return 
            "assetNumber=" + this.getAssetNumber() + "\n" +
            "assetDescription=" + this.getAssetDescription() + "\n" +
            "recordedTime=" + this.getRecordedTime() + "\n" +
            "assetType=" + this.getAssetType() + "\n" +
            "assetLocation=" + this.getAssetLocation() + "\n";
    }
    
    public ValueRow getRow() throws SQLException, StandardException {
        ValueRow valueRow = new ValueRow(5);
        valueRow.setColumn(1,new SQLVarchar(this.getAssetNumber()));
        valueRow.setColumn(2,new SQLVarchar(this.getAssetDescription()));
        valueRow.setColumn(3,new SQLTimestamp(this.getRecordedTime()));
        valueRow.setColumn(4,new SQLVarchar(this.getAssetType()));
        valueRow.setColumn(5,new SQLVarchar(this.getAssetLocation()));
        return valueRow;
    }
    
    public static String getTableDefinition() {
        return
        "ASSET_NUMBER varchar(50), " +
        "ASSET_DESCRIPTION varchar(100), " +
        "RECORDED_TIME TIMESTAMP, " +
        "ASSET_TYPE VARCHAR(50), " +
        "ASSET_LOCATION VARCHAR(50) ";
    }
    
}
