package com.splicemachine.tutorials.storm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Establishes a connection with splice and returns an Connection object
 */
public class SpliceConnector {
	
    String dbUrl = null;
    Connection con = null;
    
    public Connection getConnection(final String server) throws ClassNotFoundException, SQLException {
        dbUrl = "jdbc:splice://" + server + ":1527/splicedb;user=splice;password=admin";
        Class.forName("com.splicemachine.db.jdbc.ClientDriver");
        con = DriverManager.getConnection(dbUrl);
        return con;
    }
}
