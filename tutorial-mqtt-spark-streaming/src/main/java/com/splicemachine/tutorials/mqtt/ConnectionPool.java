package com.splicemachine.tutorials.mqtt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ConnectionPool {
    
    private static final Logger LOG = Logger
            .getLogger(ConnectionPool.class);

    private static Connection con =  null;
    
    public static Connection getConnection() {
        try {
            if(con == null || con.isClosed()) {
                LOG.info("================ creating connection");
                con = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
            } else {
                LOG.info("================ using existing");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return con;
    }
}
