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
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Establishes a connection with mysql and returns an Connection object
 */
public class MySqlConnector {

    String dbUrl = null;
    Connection con = null;

    public Connection getConnection(final String server, final String database, final String user, final String pwd)
            throws ClassNotFoundException, SQLException {
        dbUrl = "jdbc:mysql://" + server + "/" + database;
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(dbUrl, user, pwd);
        return con;
    }
}
