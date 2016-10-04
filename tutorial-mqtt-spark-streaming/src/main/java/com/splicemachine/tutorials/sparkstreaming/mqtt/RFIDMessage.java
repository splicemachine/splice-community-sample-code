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
package com.splicemachine.tutorials.sparkstreaming.mqtt;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.types.SQLTimestamp;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.sql.execute.ValueRow;

/**
 * This is a POJO that represents
 * an RFID message.  It is used in converting
 * incoming messages from a CSV entry to an object
 *
 * @author Erin Driggers
 */
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
        if (recordedTime != null && recordedTime.length() > 0) {
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

    /**
     * Builds a string with the values of the instance variables
     */
    public String toString() {
        return "assetNumber=" + this.getAssetNumber() + "\n"
        + "assetDescription=" + this.getAssetDescription() + "\n"
        + "recordedTime=" + this.getRecordedTime() + "\n"
        + "assetType=" + this.getAssetType() + "\n"
        + "assetLocation=" + this.getAssetLocation() + "\n";
    }

    /**
     * Used by the VTI to build a Splice Machine compatible resultset
     *
     * @return
     * @throws SQLException
     * @throws StandardException
     */
    public ValueRow getRow() throws SQLException, StandardException {
        ValueRow valueRow = new ValueRow(5);
        valueRow.setColumn(1, new SQLVarchar(this.getAssetNumber()));
        valueRow.setColumn(2, new SQLVarchar(this.getAssetDescription()));
        valueRow.setColumn(3, new SQLTimestamp(this.getRecordedTime()));
        valueRow.setColumn(4, new SQLVarchar(this.getAssetType()));
        valueRow.setColumn(5, new SQLVarchar(this.getAssetLocation()));
        return valueRow;
    }

    /**
     * Table definition to use when using a VTI that is an instance of a class
     *
     * @return
     */
    public static String getTableDefinition() {
        return "ASSET_NUMBER varchar(50), "
        + "ASSET_DESCRIPTION varchar(100), "
        + "RECORDED_TIME TIMESTAMP, "
        + "ASSET_TYPE VARCHAR(50), "
        + "ASSET_LOCATION VARCHAR(50) ";
    }
}
