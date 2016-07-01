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
