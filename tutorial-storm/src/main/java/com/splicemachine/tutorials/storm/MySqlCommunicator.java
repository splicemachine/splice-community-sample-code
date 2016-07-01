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