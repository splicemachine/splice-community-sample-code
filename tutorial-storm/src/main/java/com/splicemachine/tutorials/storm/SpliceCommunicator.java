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
package com.splicemachine.tutorials.storm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/*
 * This class implements methods for communicating with splice.
 */
public class SpliceCommunicator {

    private Connection con = null;
    private PreparedStatement prepstmt = null;
    private StringBuffer queryStmt;
    private StringBuffer queryValues;
    private int noOfColumns = 0;
    private int result = 0;
    Map<String, String> tableDetails;

    public SpliceCommunicator(Connection con) {
        super();
        this.con = con;
    }

    public int insertRow(String tableName, ArrayList<String> fieldNames, ArrayList<Object> fieldValues) throws SQLException {
        result = 0;
        try {
            prepstmt = null;
            queryStmt = new StringBuffer();
            queryValues = new StringBuffer();
            noOfColumns = fieldNames.size();
            queryStmt.append("insert into ");
            queryStmt.append(tableName);
            queryStmt.append(" (");
            for (int i = 0; i <= noOfColumns - 1; i++) {
                if (i != noOfColumns - 1) {
                    queryStmt.append(fieldNames.get(i));
                    queryStmt.append(", ");
                    queryValues.append("?,");
                } else {
                    queryStmt.append(fieldNames.get(i));
                    queryStmt.append(") ");
                    queryValues.append("?");
                }
            }
            queryStmt.append(" values (");
            queryStmt.append(queryValues);
            queryStmt.append(")");
            prepstmt = con.prepareStatement(queryStmt.toString());
            for (int j = 0; j <= noOfColumns - 1; j++) {
                prepstmt.setObject(j + 1, fieldValues.get(j));
            }
            result = prepstmt.executeUpdate();
            if (result != 0) {
                System.out.println("Inserted data successfully");
            } else {
                System.out.println("Insertion failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}