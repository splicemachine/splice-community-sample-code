/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.splicemachine.tutorial.machinelearning;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;



public class MachineLearningSetup {
    
    private static Logger LOG = Logger.getLogger(MachineLearningSetup.class);

    
    //TODO: These messages should be externalized for localization
    private static final String ERROR_PROVIDE_MISSING_PROJECT_NAME = "Please provide the project name";
    private static final String ERROR_PROVIDE_MISSING_PROJECT_DESC = "Please provide the project description";
    private static final String ERROR_PROVIDE_MISSING_TRAINING_TABLE_NAME = "Please provide the training data set table name";
    private static final String ERROR_PROVIDE_MISSING_TEST_TABLE_NAME = "Please provide the test data set table name";
    private static final String ERROR_PROVIDE_MISSING_LABEL_COLUMN = "Please provide the label column";
    private static final String ERROR_UNEXPECTED_EXCEPTION = "An unexpected error occurred please check the log files";
    private static final String SUCCESS_PROJECT_SETUP = "The project and dataset were successfully setup"; 
    private static final String ERROR_TRAINING_TABLE_NOT_FOUND = "The training table was not found";
    private static final String ERROR_TEST_TABLE_NOT_FOUND = "The test table was not found";
    private static final String ERROR_LABEL_COLUMN_NOT_FOUND = "The label column was not found";
    
    /**
     * This stored procedure will insert the initial setup for a machine learning project and the
     * corresponding data set.
     * 
     * @param projectName - name of the project
     * @param projectDescription - description of the project
     * @param trainingTable - training table
     * @param testTable - test table
     * @param labelColumn - the label column
     * @param returnResultset
     */
    public static void createInitialProjectStructure(String projectName, String projectDescription,
            String trainingTable, String testTable, String labelColumn, ResultSet[] returnResultset) {
        
        boolean success = true;
        String responseMessage = "";
        
        //Validate all of the parameters have values
        if(projectName == null || projectName.trim().length() == 0) {
            success = false;
            responseMessage = ERROR_PROVIDE_MISSING_PROJECT_NAME;
        } else if (projectDescription == null || projectDescription.trim().length() == 0) {
            success = false;
            responseMessage = ERROR_PROVIDE_MISSING_PROJECT_DESC;            
        } else if (trainingTable == null || trainingTable.trim().length() == 0) {
            success = false;
            responseMessage = ERROR_PROVIDE_MISSING_TRAINING_TABLE_NAME;
        } else if (testTable == null || testTable.trim().length() == 0) {
            success = false;
            responseMessage = ERROR_PROVIDE_MISSING_TEST_TABLE_NAME;
        } else if (labelColumn == null || labelColumn.trim().length() == 0) {
            success = false;
            responseMessage = ERROR_PROVIDE_MISSING_LABEL_COLUMN;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:default:connection"); 
            stmt = conn.createStatement();  
            
            if(success) {
                LOG.error("Passed validations.  About to begin getting the columns.");
                boolean labelFound = false;
                int indexOfPeriod = trainingTable.indexOf(".");
                String tableName;
                String schemaName = "SPLICE";
                if(indexOfPeriod > -1) {
                    schemaName = trainingTable.substring(0,indexOfPeriod);
                    tableName = trainingTable.substring(indexOfPeriod+1);
                } else {
                    tableName = trainingTable;
                }

                //Get the columns for the training table
                ArrayList<Column> columns = new ArrayList<Column>();
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet res = meta.getColumns(null, schemaName, tableName, null);
                while(res.next()) {
                    Column col = new Column(res.getString("COLUMN_NAME"),
                            res.getInt("DATA_TYPE"));
                    
                    if(col.getColumnName().equals(labelColumn)) {
                        labelFound = true;
                        continue;
                    }
                    
                    if(!col.getMachineLearningDataType().equals("INVALID")) {
                        columns.add(col);
                    }
                    
                } 
                
                if(columns.size() == 0) {
                    success = false;
                    responseMessage = ERROR_TRAINING_TABLE_NOT_FOUND;
                }
                
                //Confirm the label was found
                if(!labelFound) {
                    success = false;
                    responseMessage = ERROR_LABEL_COLUMN_NOT_FOUND;
                    
                }
                
                
                //Confirm the test table exists
                LOG.error("Validating the test table.");
                indexOfPeriod = testTable.indexOf(".");
                schemaName = "SPLICE";
                if(indexOfPeriod > -1) {
                    schemaName = testTable.substring(0,indexOfPeriod);
                    tableName = testTable.substring(indexOfPeriod+1);
                } else {
                    tableName = testTable;
                }
                if(success) {
                    res = meta.getColumns(null, schemaName, tableName, null);
                    if(!res.next()) {
                        success = false;
                        responseMessage = ERROR_TEST_TABLE_NOT_FOUND;                       
                    } 
                }   
                
                //Lets start inserting the data
                if(success) {
                    //Insert data to the MACHINE_LEARNING_PROJECT table
                    LOG.error("Begin inserting data");
                    //Get the PROJECT_ID
                    int projectId = -1;
                    pstmt = conn.prepareStatement("insert into SPLICE.MACHINE_LEARNING_PROJECT (NAME,DESCRIPTION) values (?,?)");
                    pstmt.setString(1, projectName);
                    pstmt.setString(2, projectDescription);
                    pstmt.executeUpdate();
                    
                    //TODO: This would be an issue with concurrency if an insert occurred at the same time
                    
                    res = stmt.executeQuery("select max(PROJECT_ID) from SPLICE.MACHINE_LEARNING_PROJECT");
                    if(res.next()) {
                        projectId = res.getInt(1);
                    }
                    
                    int numLabels = 2;
                    res = stmt.executeQuery("select max(" + labelColumn + ") from " + trainingTable);
                    if(res.next()) {
                        numLabels = res.getInt(1);
                    }
                    
                    
                    //Insert data to the DATASET table
                    pstmt = conn.prepareStatement("insert into SPLICE.DATASET (PROJECT_ID,TRAINING_TABLE,TEST_TABLE,NUM_LABELS) values (?,?,?,?)");
                    pstmt.setInt(1, projectId);
                    pstmt.setString(2, trainingTable);
                    pstmt.setString(3, testTable);
                    pstmt.setInt(4, numLabels);
                    pstmt.executeUpdate();
                    
                    //Get the DATASET_ID
                    //TODO: This would be an issue with concurrency if an insert occurred at the same time
                    int datasetId = -1;
                    res = stmt.executeQuery("select max(DATASET_ID) from SPLICE.DATASET");
                    if(res.next()) {
                        datasetId = res.getInt(1);
                    }
                    
                    //Insert data to the DATASET_FEATURE_DEFINITION table
                    pstmt = conn.prepareStatement("insert into SPLICE.DATASET_FEATURE_DEFINITION (DATASET_ID,DATABASE_COLUMN_NAME,FEATURE_NAME,FEATURE_TYPE) values (?,?,?,?)");
                    for(Column col : columns) {
                        pstmt.setInt(1, datasetId);
                        pstmt.setString(2, col.getColumnName());
                        pstmt.setString(3, col.getFeatureName());
                        pstmt.setString(4, col.getMachineLearningDataType());
                        pstmt.addBatch();                        
                    }
                    pstmt.executeBatch();
                    
                    //Now insert the label
                    pstmt = conn.prepareStatement("insert into SPLICE.DATASET_FEATURE_DEFINITION (DATASET_ID,DATABASE_COLUMN_NAME,FEATURE_NAME,IS_LABEL) values (?,?,?,?)");
                    pstmt.setInt(1, datasetId);
                    pstmt.setString(2, labelColumn);
                    pstmt.setString(3, labelColumn);
                    pstmt.setBoolean(4, true);
                    pstmt.executeUpdate();
                    
                    //Now insert a relationship for each ML Model
                    //We may want to change this in the future
                    //But for now since we only have 1 model it makes
                    //It easier for setup
                    ArrayList<Integer> machineLearningMethods = new ArrayList<Integer>();
                    res = stmt.executeQuery("select MACHINE_LEARNING_ID from SPLICE.MACHINE_LEARNING_METHOD");
                    while(res.next()) {
                        machineLearningMethods.add(new Integer(res.getInt(1)));
                        datasetId = res.getInt(1);
                    }
                    
                    pstmt = conn.prepareStatement("insert into SPLICE.DATASET_MACHINE_LEARNING_METHODS (DATASET_ID,MACHINE_LEARNING_ID) values (?,?)");
                    for(Integer col : machineLearningMethods) {
                        pstmt.setInt(1, datasetId);
                        pstmt.setInt(2, col.intValue());
                        pstmt.addBatch();                        
                    }
                    pstmt.executeBatch();
                }

            }
            if(success) {
                responseMessage = SUCCESS_PROJECT_SETUP;
            }
            
            returnResultset[0] = stmt.executeQuery("values('" + success + "','" + responseMessage + "')");

            
        } catch (Exception e) {
            LOG.error("Exception:", e);
            success = false;
            responseMessage = ERROR_UNEXPECTED_EXCEPTION;
        }

    }
}

class Column {
    private String columnName;
    private int databaseColumnType = -1;
    
    public Column(String columnName, int databaseColumnType) {
        this.columnName = columnName;
        this.databaseColumnType = databaseColumnType;
    }
    
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getFeatureName() {
        return columnName;
    }
    
    public int getDatabaseColumnType() {
        return databaseColumnType;
    }
    public void setDatabaseColumnType(int databaseColumnType) {
        this.databaseColumnType = databaseColumnType;
    }
    
    public String getMachineLearningDataType() {
        String type = "INVALID";
        switch (this.databaseColumnType) {

        case java.sql.Types.BIGINT:
        case java.sql.Types.DECIMAL:
        case java.sql.Types.NUMERIC:
        case java.sql.Types.DOUBLE:
        case java.sql.Types.FLOAT:
        case java.sql.Types.INTEGER:
        case java.sql.Types.REAL:
        case java.sql.Types.SMALLINT:
        case java.sql.Types.TINYINT:
        case java.sql.Types.BOOLEAN:
            type = "CONTINUOUS";
            break;
        
        case java.sql.Types.CHAR:
        case java.sql.Types.NVARCHAR:
        case java.sql.Types.VARCHAR:
            type = "CATEGORICAL";
            break;
        case java.sql.Types.BINARY:
        case java.sql.Types.BLOB:
        case java.sql.Types.VARBINARY:
        case java.sql.Types.BIT:
        case java.sql.Types.CLOB:
        case java.sql.Types.DATE:
        case java.sql.Types.TIME:
        case java.sql.Types.TIMESTAMP:
        default:
            break;
        }
        
        return type;
    }
   
    
    
}