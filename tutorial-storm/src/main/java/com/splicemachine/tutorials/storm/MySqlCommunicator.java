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
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class implements methods for communicating with mysql.
 */
public class MySqlCommunicator {

    private Connection con = null;
    
    public MySqlCommunicator(Connection con) {
        super();
        this.con = con;
    }
    
    public ResultSet selectRows(String tableName) throws SQLException {

    	ResultSet rs = null;
        try {       
            PreparedStatement prepstmt = con.prepareStatement("SELECT * FROM " + tableName);
            rs = prepstmt.executeQuery();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
}