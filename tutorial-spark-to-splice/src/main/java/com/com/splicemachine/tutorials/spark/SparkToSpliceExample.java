/*
 * Copyright 2012 - 2018 Splice Machine, Inc.
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

package com.splicemachine.tutorials.spark;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a simple example of inserting data into Splice Machine
 * using Spark JDBC.
 *
 * To compile this code:
 *   - Navigate to the tutorial-spark-to-splice folder
 *   - Run: mvn clean compile package
 *
 * To run this code:
 *   - Navigate to your instance of spark
 *   - Run: ./bin/spark-submit --class com.splicemachine.tutorials.spark.SparkToSpliceExample /path/to/tutorial-spark-to-splice-2.5.0.1803.jar
 *
 * Created by erindriggers on 1/24/18.
 */
public class SparkToSpliceExample {

    private static String DB_URL = "jdbc:splice://localhost:1527/splicedb";
    private static final String DRIVER = "com.splicemachine.db.jdbc.ClientDriver";
    private static String DB_USER = "splice";
    private static String DB_PWD = "admin";

    public static void main(String[] args) {

        if(args.length  > 0) {
            DB_URL = args[0];
        }

        SparkSession spark = SparkSession
                .builder()
                .appName("Splice Machine Spark Examples")
                .getOrCreate();

        runSimpleJDBCExample(spark);

        spark.stop();
    }

    /**
     * Use spark's JDBC to insert data into splice machine
     * @param spark
     */
    private static void runSimpleJDBCExample(SparkSession spark) {

        //Create the table in Splice machine
        createPresidentTable();

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", DB_USER);
        connectionProperties.put("password", DB_PWD);
        connectionProperties.put("driver",DRIVER);

        //Get the table definition
        Dataset<Row> presidentTableDS = spark.read()
                .jdbc(DB_URL, "SPLICE.PRESIDENT", connectionProperties);

        //Print out the schema
        presidentTableDS.printSchema();

        //Let's create some data
        List<Row> presidents = new ArrayList<Row>();
        presidents.add(RowFactory.create(1,"George","","Washington", 1789,1797));
        presidents.add(RowFactory.create(2,"John","","Adams",1797,1801));
        presidents.add(RowFactory.create(3,"Thomas","","Jefferson",1801,1809));
        presidents.add(RowFactory.create(4,"James","","Madison",1809,1817));
        presidents.add(RowFactory.create(5,"James","","Monroe",1817,1825));
        presidents.add(RowFactory.create(6,"John","Quincy","Adams",1825,1829));
        presidents.add(RowFactory.create(7,"Andrew","","Jackson",1829,1837));
        presidents.add(RowFactory.create(8,"Martin","","Van Buren",1837,1841));
        presidents.add(RowFactory.create(9,"William","Henry","Harrison",1841,1841));

        //Get SQL Context
        SQLContext sqlContext = spark.sqlContext();

        //Convert the presidents list to a dataset
        Dataset<Row> rowsToAdd = sqlContext.createDataFrame(presidents, presidentTableDS.schema());
        rowsToAdd.show();

        //Save the record in Splice Machine
        rowsToAdd.write().mode(SaveMode.Append)
                .jdbc(DB_URL, "SPLICE.PRESIDENT", connectionProperties);
    }



    /**
     * Use standard JDBC to create a table in Splice Machine
     * called PRESIDENT
     */
    private static void createPresidentTable() {
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        DB_URL, DB_USER, DB_PWD);

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement()
        ) {
            //Create the database table to store the president records
            String sqlCreateTable = "CREATE TABLE SPLICE.PRESIDENT " +
                    "(president_number INTEGER not NULL, " +
                    " first_name VARCHAR(50), " +
                    " middle_name VARCHAR(50), " +
                    " last_name VARCHAR(100), " +
                    " begin_year INTEGER," +
                    " end_year INTEGER)";

            stmt.executeUpdate(sqlCreateTable);
            stmt.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Use standard JDBC to drop a table in Splice Machine
     * called SPLICE.PRESIDENT
     */
    private static void dropPresidentTable() {
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        DB_URL, DB_USER, DB_PWD); // MySQL

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement()
        ) {
            //Create the database table to store the product history records
            String sqlCreateTable = "DROP TABLE SPLICE.PRESIDENT";

            stmt.executeUpdate(sqlCreateTable);
            stmt.close();

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
