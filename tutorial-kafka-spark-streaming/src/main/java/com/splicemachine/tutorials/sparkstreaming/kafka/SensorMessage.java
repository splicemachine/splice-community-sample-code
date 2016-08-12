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

/**
 * This is a POJO that represents
 * a SENSOR_MESSAGE message.
 * <p>
 * Created by Erin Driggers
 */

public class SensorMessage {

    private String id;
    private String location;
    private double temperature;
    private double humidity;
    private Timestamp recordedTime;

    /**
     * Utility function to build an prepared insert statement
     *
     * @param con
     * @return
     * @throws SQLException
     */
    public static PreparedStatement getPreparedStatement(Connection con) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into iot.SENSOR_MESSAGES values(");
        for (int i = 0; i < 5; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("?");

        }
        sb.append(")");
        return con.prepareStatement(sb.toString());
    }

    /**
     * Table definition to use when using a VTI that is an instance of a class
     *
     * @return
     */
    public static String getTableDefinition() {
        return
                "id varchar(20), " +
                        "location varchar(50), " +
                        "temperature decimal(12,5), " +
                        "humidity decimal(12,5), " +
                        "recordedtime timestamp ";
    }

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

    /**
     * Builds a string with the values of the instance variables
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=[" + this.getId() + "]\n");
        sb.append("location=[" + this.getLocation() + "]\n");
        sb.append("temperature=[" + this.getTemperature() + "]\n");
        sb.append("humidity=[" + this.getHumidity() + "]\n");
        sb.append("recordedTime=[" + this.getRecordedTime() + "]\n");
        return sb.toString();
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
        valueRow.setColumn(1, new SQLVarchar(this.getId()));
        valueRow.setColumn(2, new SQLVarchar(this.getLocation()));
        valueRow.setColumn(3, new SQLDecimal(BigDecimal.valueOf(this.getTemperature())));
        valueRow.setColumn(4, new SQLDecimal(BigDecimal.valueOf(this.getHumidity())));
        valueRow.setColumn(5, new SQLTimestamp(this.getRecordedTime()));
        return valueRow;
    }


}
