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
package com.splicemachine.tutorial.tensorflow;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.splicemachine.db.impl.ast.StringUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class ModelDefinition {
    
    private static Logger LOG = Logger.getLogger(ModelDefinition.class);
    
    public static String CTLF = "\r\n";
    
    
    public static void createModelForDataSet(
            String dataOutputPath,
            String modelOutputPath,
            Integer datasetId,
            ResultSet[] returnResultset) {
        
        Statement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        
        
        try{
            //For now, keep logging on for this stored procedure
            LOG.setLevel(Level.INFO);
            
            //This connection is to be able to return a resultset
            conn = DriverManager.getConnection("jdbc:default:connection"); 
            stmt = conn.createStatement();      
            
            long currentTimestamp = System.currentTimeMillis();
            
            //Get a list of the default parameter set for all models associated
            //with this data set 
            ArrayList<Integer> parameterSetIds = new ArrayList<Integer>();
            PreparedStatement pstmt = conn.prepareStatement("select distinct(PARAMETER_SET_ID) from DATASET_MACHINE_LEARNING_METHODS a, PARAMETER_SET b where a.MACHINE_LEARNING_ID = b.MACHINE_LEARNING_ID and a.DATASET_ID = ? and b.STATUS = ? and b.IS_DEFAULT = ? {LIMIT 1}");
            pstmt.setInt(1, datasetId);
            pstmt.setString(2, "A");
            pstmt.setBoolean(3, true);
            rs = pstmt.executeQuery();        
            while(rs.next()) {            
                parameterSetIds.add(rs.getInt(1));
            }
            
            LOG.info("Found " + parameterSetIds.size() + " Default parameter sets for the dataset id " + datasetId);
            
            for(Integer parameterSetId : parameterSetIds) {
                createModelForParameterSetId (dataOutputPath,modelOutputPath,datasetId,parameterSetId,returnResultset);
            }
            
            pstmt = conn.prepareStatement("select * from MODEL_CREATION_RESULTS where DATASET_ID = ? and START_TIME >= ?");
            pstmt.setInt(1, datasetId);
            pstmt.setTimestamp(2, new Timestamp(currentTimestamp));
            returnResultset[0] = pstmt.executeQuery(); 
                        
        }catch(Exception e){
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
            LOG.error("Exception generating model file.", e);
        } finally {
            if(conn != null) { try {conn.close();} catch (Exception e){}} 
        }
        
    }

    /**
     * 
     * @param dataOutputPath
     * @param modelOutputPath
     * @param datasetId
     * @param groupId
     * @param returnResultset
     */
    public static void createModelForGroupId(
            String dataOutputPath,
            String modelOutputPath,
            Integer datasetId,
            Integer groupId,  
            ResultSet[] returnResultset
            ) {
        
        Statement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        
        
        try{
            //For now, keep logging on for this stored procedure
            LOG.setLevel(Level.INFO);
            
            //This connection is to be able to return a resultset
            conn = DriverManager.getConnection("jdbc:default:connection"); 
            stmt = conn.createStatement();      
            
            long currentTimestamp = System.currentTimeMillis();
            
            //Get a list of the default parameter set for all models associated
            //with this data set 
            ArrayList<Integer> parameterSetIds = new ArrayList<Integer>();
            PreparedStatement pstmt = conn.prepareStatement("select b.PARAMETER_SET_ID from MODEL_CREATION_GROUP a, MODEL_CREATION_GROUP_DETAILS b where a.MODEL_CREATION_GROUP_ID = ? and a.STATUS = ? and a.MODEL_CREATION_GROUP_ID = b.MODEL_CREATION_GROUP_ID ");
            pstmt.setInt(1, groupId);
            pstmt.setString(2, "A");
            rs = pstmt.executeQuery();        
            while(rs.next()) {            
                parameterSetIds.add(rs.getInt(1));
            }
            
            for(Integer parameterSetId : parameterSetIds) {
                createModelForParameterSetId (dataOutputPath,modelOutputPath,datasetId,parameterSetId,returnResultset);
            }
            
            pstmt = conn.prepareStatement("select * from MODEL_CREATION_RESULTS where DATASET_ID = ? and START_TIME >= ?");
            pstmt.setInt(1, datasetId);
            pstmt.setTimestamp(2, new Timestamp(currentTimestamp));
            returnResultset[0] = pstmt.executeQuery(); 
                        
        }catch(Exception e){
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
            LOG.error("Exception generating model file.", e);
        } finally {
            if(conn != null) { try {conn.close();} catch (Exception e){}} 
        }
    }
    
    /**
     * 
     * @param dataOutputPath
     * @param modelOutputPath
     * @param datasetId
     * @param parameterSetId
     * @param returnResultset
     */
    public static void createModelForParameterSetId(
            String dataOutputPath,
            String modelOutputPath,
            Integer datasetId,
            Integer parameterSetId,
            ResultSet[] returnResultset
            ) {
        
        Statement stmt = null;
        Connection conn = null;
        Connection conn2 = null;
        
        long startTime = System.currentTimeMillis();
        String status = "inprogress";

        try{
            //For now, keep logging on for this stored procedure
            LOG.setLevel(Level.INFO);
            
            //There is a defect currently in base code which requires that we
            //use the full jdbc connection see: string: https://splice.atlassian.net/browse/SPLICE-1103
            conn = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
            
            //This second connection is to be able to return a resultset
            conn2 = DriverManager.getConnection("jdbc:default:connection");            
            stmt = conn2.createStatement();    
            
            //Get the machine learning id and populate the default values
            int machineLearningId = -1;
            HashMap<String,String> parameterValues = new HashMap<String,String>();
            PreparedStatement pstmt = conn2.prepareStatement("select b.MACHINE_LEARNING_ID, PARAMETER_NAME, DEFAULT_VALUE from PARAMETER_SET a, MACHINE_LEARNING_PARAMETERS b  where a.PARAMETER_SET_ID = ? and a.STATUS = ? and a.MACHINE_LEARNING_ID = b.MACHINE_LEARNING_ID");
            pstmt.setInt(1, parameterSetId);
            pstmt.setString(2, "A");
            ResultSet rs = pstmt.executeQuery();        
            while(rs.next()) {       
                machineLearningId = rs.getInt(1);
                parameterValues.put(rs.getString(2), rs.getString(3));
            }
            
            //Create the output directory for the model
            //There will be sub directories under this folder
            //For the data as well as the model output
            File fRootModelOutputDir = new File(modelOutputPath);
            if(!fRootModelOutputDir.exists()) {
                fRootModelOutputDir.mkdirs();
            }
            
            //Create the model directory.  If it already exists
            //clean out the files in that directory
            File fModelOutputDir = new File(fRootModelOutputDir, datasetId + "/" + parameterSetId + "/model");
            if(fModelOutputDir.exists()) {
                purgeDirectory(fModelOutputDir); 
            } else {
                fModelOutputDir.mkdirs();
            }
            
            //TODO: Needs to be either HDFS or S3
            //Create the data directory
            File fRootDataOutputDir = new File(dataOutputPath);
            if(!fRootDataOutputDir.exists()) {
                fRootDataOutputDir.mkdirs();
            }
            
            //Create the data directory.  If it already exists
            //clean out the files in that directory
            File fDataOutputDir = new File(fRootDataOutputDir, datasetId + "/" + parameterSetId + "/data");
            if(fDataOutputDir.exists()) {
                purgeDirectory(fDataOutputDir); 
            } else {
                fDataOutputDir.mkdirs();
            }
            
            //Get the parameters for the parameter set
            pstmt = conn2.prepareStatement("select a.PARAMETER_NAME, b.PARAMETER_VALUE  from MACHINE_LEARNING_PARAMETERS a, PARAMETER_SET_VALUES b where a.MACHINE_LEARNING_PARAMETER_ID = b.MACHINE_LEARNING_PARAMETER_ID and b.PARAMETER_SET_ID = ?");
            pstmt.setInt(1, parameterSetId);
            rs = pstmt.executeQuery();        
            while(rs.next()) {            
                parameterValues.put(rs.getString(1), rs.getString(2));
            }

            //Get the training and test tables
            String trainTable = null;
            String testTable = null;
            pstmt = conn2.prepareStatement("select TRAINING_TABLE,TEST_TABLE from DATASET a where a.DATASET_ID = ?");
            pstmt.setInt(1, datasetId);
            rs = pstmt.executeQuery();        
            if(rs.next()) {            
                trainTable = rs.getString(1);
                testTable = rs.getString(2);
            }
            
            //A JSON Object is passed into the python script so that it
            //know the structure of the data and corresponding groupings
            JsonObject inputDict = new JsonObject();
            StringBuilder labelField = new StringBuilder();
            StringBuilder exportColumns = buildDictionaryObject(conn, datasetId, inputDict,labelField);           
            
            //Export the training data
            String exportPathTrain = fDataOutputDir.getAbsolutePath() + "/train";
            String trainingFilePath = exportPathTrain + "/part-r-00000.csv";
            String exportCmd = "EXPORT('" + exportPathTrain + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + trainTable;
            exportData(conn, exportCmd, exportPathTrain + "/* ", exportPathTrain + "/traindata.txt");        
            inputDict.addProperty("train_data", trainingFilePath);
            
            //Export the test data
            String exportPathTest = fDataOutputDir.getAbsolutePath() + "/test";
            String testFilePath = exportPathTest + "/part-r-00000.csv";
            exportCmd = "EXPORT('" + exportPathTest + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + testTable;
            exportData(conn, exportCmd, exportPathTest + "/* ", exportPathTest + "/testdata.txt");
            inputDict.addProperty("test_data", testFilePath);

            //Add the model parameters to the JSON
            Iterator<String> itr = parameterValues.keySet().iterator();
            while(itr.hasNext()) {
                String key = itr.next();
                inputDict.addProperty(key, parameterValues.get(key));
            }
            
            inputDict.addProperty("model_dir", fModelOutputDir.getAbsolutePath());
            
            //Build JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String jsonData = gson.toJson(inputDict);
            
            LOG.error("JSON data being setnt to model:" + jsonData);
            
            //CloseableHttpClient client = HttpClients.createDefault();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://localhost:8000/train_and_eval");
         
            StringEntity entity = new StringEntity(jsonData);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
         
            
            HashMap<String,String> responseData = new HashMap<String,String>();
            HttpResponse response = client.execute(httpPost);
            int exitCode = response.getStatusLine().getStatusCode();
            if(exitCode == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                String jsonResponseData = result.toString();
                jsonResponseData = jsonResponseData.substring(1,jsonResponseData.length()-1);              
                jsonResponseData = StringUtils.replace(jsonResponseData, "\\\"", "\"");
                
                //We want to remove the leading and closing double quote
                LOG.error("Response JSON:" + jsonResponseData);
                
                JsonParser parser = new JsonParser();                
                JsonObject root = (JsonObject)parser.parse(jsonResponseData);  
                Set<Entry<String, JsonElement>> entrySet = root.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    String key = entry.getKey();
                    String fieldValue = root.get(key).getAsString();
                    responseData.put(key, fieldValue);
                }
            } else {
                //There was a problem
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                
                LOG.error("Error calling web service - error code:" + exitCode);
                LOG.error("Error calling web service - response body:" + result.toString());
                
            }
            
            long endTime = System.currentTimeMillis();
            
            if(exitCode != 200) {
                status = "Python Failed";
                String insertStatement = "INSERT INTO MODEL_CREATION_RESULTS " + 
                        "(DATASET_ID,PARAMETER_SET_ID,MACHINE_LEARNING_ID,START_TIME,END_TIME,STATUS)" +
                        "values (?,?,?,?,?,?)";
                pstmt = conn2.prepareStatement(insertStatement);   
                pstmt.setInt(1, datasetId);
                pstmt.setInt(2, parameterSetId);
                pstmt.setInt(3, machineLearningId);
                pstmt.setTimestamp(4, new Timestamp(startTime));
                pstmt.setTimestamp(5, new Timestamp(endTime));
                pstmt.setString(6, status);
                pstmt.executeUpdate();
            } else {
                status = "Success";
                String insertStatement = "INSERT INTO MODEL_CREATION_RESULTS " + 
                        "(DATASET_ID,PARAMETER_SET_ID,MACHINE_LEARNING_ID,START_TIME,END_TIME,STATUS) " +
                        "values (?,?,?,?,?,?)";
                pstmt = conn2.prepareStatement(insertStatement);
                pstmt.setInt(1, datasetId);
                pstmt.setInt(2, parameterSetId);
                pstmt.setInt(3, machineLearningId);
                pstmt.setTimestamp(4, new Timestamp(startTime));
                pstmt.setTimestamp(5, new Timestamp(endTime));
                pstmt.setString(6, status);
                pstmt.executeUpdate();
                
                int modelResultsId = -1;
                
                pstmt = conn2.prepareStatement("Select IDENTITY_VAL_LOCAL() from MODEL_CREATION_RESULTS");
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    modelResultsId = rs.getInt(1);
                }
                if(modelResultsId != -1) {
                    pstmt = conn.prepareStatement("insert into SPLICE.MODEL_CREATION_RESULTS_DETAILS (MODEL_RESULTS_ID,FIELD,FIELD_VALUE) values (?,?,?)");
                    Iterator<String> itr2 = responseData.keySet().iterator();
                    while(itr2.hasNext()) {
                        String key = itr2.next();
                        pstmt.setInt(1, modelResultsId);
                        pstmt.setString(2, key);
                        pstmt.setString(3, responseData.get(key));
                        pstmt.addBatch();    
                        
                    }
                    pstmt.executeBatch();
                }
            }
               
            //Get the entry to be returned
            String selectStmt = "select * from MODEL_CREATION_RESULTS where DATASET_ID = ? and PARAMETER_SET_ID = ? and START_TIME = ? and END_TIME = ?";
            pstmt = conn2.prepareStatement(selectStmt);
            pstmt.setInt(1, datasetId);
            pstmt.setInt(2, parameterSetId);
            pstmt.setTimestamp(3, new Timestamp(startTime));
            pstmt.setTimestamp(4, new Timestamp(endTime));
            returnResultset[0] = pstmt.executeQuery();         

        }catch(Exception e){
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
            LOG.error("Exception generating model file.", e);
        } finally {
            if(conn != null) { try {conn.close();} catch (Exception e){}} 
        }
        
    }

    /**
     * Cleans out all the files and folders in a directory
     * @param dir
     */
    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) purgeDirectory(file);
            file.delete();
        }
    }
    
    /**
     * Predicts the label for a specified record in a database table
     * 
     * @param modelOutputPath - The full path to the model output directory
     * @param rootOutputDirectory - the root output directory for data and models
     * @param modelId - The id of the model to be used to retrieve the list of columns.  It should correspond to
     *       a value in the MODEL_ID column in the MODEL table.
     * @param modelInputId - The id of the model inputs to be used to retrieve the details and location of the model.  It should
     *      correspond to the MODEL_INPUT_ID in the MODEL_INPUTS table
     * @param sourceTable - Table containing the live data
     * @param sourceId - Key to the record you want to predict
     * @param returnResultset
     */
    public static void predictModel(
            String modelOutputPath,
            Integer datasetId,
            Integer parameterSetId,
            String sourceTable,
            int sourceId, 
            ResultSet[] returnResultset) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{

            //For right now keep logging on
            LOG.setLevel(Level.INFO);
            conn = DriverManager.getConnection("jdbc:default:connection");
            
            //Create the model directory.  If it already exists
            //clean out the files in that directory
            File fModelOutputDir = new File(modelOutputPath + "/" + datasetId + "/" + parameterSetId + "/model");
            
            //Get the default parameters for the parameter set
            PreparedStatement pstmt = conn.prepareStatement("select b.MACHINE_LEARNING_ID, PARAMETER_NAME, DEFAULT_VALUE from PARAMETER_SET a, MACHINE_LEARNING_PARAMETERS b  where a.PARAMETER_SET_ID = ? and a.STATUS = ? and a.MACHINE_LEARNING_ID = b.MACHINE_LEARNING_ID");
            pstmt.setInt(1, parameterSetId);
            pstmt.setString(2, "A");
            rs = pstmt.executeQuery();     
            
            HashMap<String,String> parameterValues = new HashMap<String,String>();       
            while(rs.next()) {       
                parameterValues.put(rs.getString(2), rs.getString(3));
            }
            rs.close();
            
            //Get the parameters for the parameter set
            pstmt = conn.prepareStatement("select a.PARAMETER_NAME, b.PARAMETER_VALUE  from MACHINE_LEARNING_PARAMETERS a, PARAMETER_SET_VALUES b where a.MACHINE_LEARNING_PARAMETER_ID = b.MACHINE_LEARNING_PARAMETER_ID and b.PARAMETER_SET_ID = ?");
            pstmt.setInt(1, parameterSetId);
            rs = pstmt.executeQuery();        
            while(rs.next()) {            
                parameterValues.put(rs.getString(1), rs.getString(2));
            }
            rs.close();

            //A JSON Object is passed into the python script so that it
            //know the structure of the data and corresponding groupings
            JsonObject inputDict = new JsonObject();

            StringBuilder labelField = new StringBuilder();
            StringBuilder exportColumns = buildDictionaryObject(conn, datasetId, inputDict,labelField);           
                
            //Retrieve the record from the database
            StringBuilder recordAsCSV = null;
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT " + exportColumns.toString() + " FROM " + sourceTable + " where ID = " + sourceId);
            if(rs.next()) {
                recordAsCSV = new StringBuilder();
                int numCols = rs.getMetaData().getColumnCount();
                for(int i=0; i<numCols; i++) {
                    if(i != 0) recordAsCSV.append(",");
                    recordAsCSV.append(rs.getObject(i+1));
                }
                inputDict.addProperty("input_record", recordAsCSV.toString()); 
            } else {
                returnResultset[0] = stmt.executeQuery("values(-1, 'Record not found in the database')");
                return;
            }
            rs.close();
                
            //Add the model parameters to the JSON
            Iterator<String> itr = parameterValues.keySet().iterator();
            while(itr.hasNext()) {
                String key = itr.next();
                inputDict.addProperty(key, parameterValues.get(key));
            }
            inputDict.addProperty("model_dir", fModelOutputDir.getAbsolutePath());               
                                
            //Build JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String jsonData = gson.toJson(inputDict);
            
            LOG.error("JSON data being setnt to model:" + jsonData);
            
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://localhost:8000/predict_outcome");
         
            StringEntity entity = new StringEntity(jsonData);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            
            HashMap<String,String> responseData = new HashMap<String,String>();
            HttpResponse response = client.execute(httpPost);
            int exitCode = response.getStatusLine().getStatusCode();
            if(exitCode == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                String jsonResponseData = result.toString();
                jsonResponseData = jsonResponseData.substring(1,jsonResponseData.length()-1);              
                jsonResponseData = StringUtils.replace(jsonResponseData, "\\\"", "\"");
                
                //We want to remove the leading and closing double quote
                LOG.error("Response JSON:" + jsonResponseData);
                
                JsonParser parser = new JsonParser();                
                JsonObject root = (JsonObject)parser.parse(jsonResponseData);  
                Set<Entry<String, JsonElement>> entrySet = root.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    String key = entry.getKey();
                    String fieldValue = root.get(key).getAsString();
                    responseData.put(key, fieldValue);
                }
                
                String prediction = responseData.get("prediction");
                prediction = StringUtils.replace(prediction, "[","");
                prediction = StringUtils.replace(prediction, "]","");
                
                String updateSql = "UPDATE " + sourceTable + " set " + labelField.toString() + " = ? where ID = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setObject(1, prediction);
                pstmt.setInt(2, sourceId);
                pstmt.execute();
                returnResultset[0] = stmt.executeQuery("select * from "+ sourceTable + " where ID = " + sourceId);
                
            } else {
                //There was a problem
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                
                LOG.error("Error calling web service - error code:" + exitCode);
                LOG.error("Error calling web service - response body:" + result.toString());
                
                returnResultset[0] = stmt.executeQuery("values(-1, 'Exception running the Python model.')");
            }
        } catch (Exception e) {
            LOG.error("Exception calling pythong script.", e);
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
        } finally {
            if(rs != null) { try {rs.close();} catch (Exception e){}}  
        }
    }
    
    /**
     * This method is used to build the JSON object that is feed into the Tensorflow model.  This needs to be called
     * when both the model is created and when you use it to predict a value.
     * 
     * @param conn - Connection Object
     * @param modelName - Name of the model - value must exist in the MODEL column in the MODEL_DICTIONARY table
     * @param inputDict - JSONObject that is being appended to
     * 
     * @return List of the columns for the model
     * @throws SQLException
     */
    private static StringBuilder buildDictionaryObject(Connection conn, int datasetId, JsonObject inputDict, StringBuilder sbLabel) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        //Get Columns
        pstmt = conn.prepareStatement("select FEATURE_NAME from DATASET_FEATURE_DEFINITION where DATASET_ID = ? and DATABASE_COLUMN_NAME IS NOT NULL order by FEATURE_NAME");
        pstmt.setInt(1, datasetId);
        rs = pstmt.executeQuery();
               
        //Contains a list of columns to export 
        StringBuilder exportColumns = new StringBuilder();
        
        JsonArray jsonArr = new JsonArray();
        int numColumns = 0;
        while(rs.next()) {            
            String name = rs.getString(1);
            
            if(numColumns > 0) { exportColumns.append(",");}
            exportColumns.append(name);
            
            jsonArr.add(name.toLowerCase());
            numColumns++;
        }
        if(numColumns > 0) {
            inputDict.add("columns", jsonArr);
        }
        
        //Get the categorical columns
        pstmt.clearParameters();
        pstmt = conn.prepareStatement("select FEATURE_NAME from DATASET_FEATURE_DEFINITION where DATASET_ID = ? and FEATURE_TYPE = ?");
        pstmt.setInt(1, datasetId);
        pstmt.setString(2, "CATEGORICAL");
        rs = pstmt.executeQuery();        
        jsonArr = new JsonArray();
        numColumns = 0;
        while(rs.next()) {            
            jsonArr.add(rs.getString(1).toLowerCase());
            numColumns++;
        }
        if(numColumns > 0) {
            inputDict.add("categorical_columns", jsonArr);
        }
        
        //Get the continuous columns
        pstmt.clearParameters();
        pstmt.setInt(1, datasetId);
        pstmt.setString(2, "CONTINUOUS");
        rs = pstmt.executeQuery();        
        jsonArr = new JsonArray();
        numColumns = 0;
        while(rs.next()) {            
            jsonArr.add(rs.getString(1).toLowerCase());
            numColumns++;
        }
        if(numColumns > 0) {
            inputDict.add("continuous_columns", jsonArr);
        }

        //Get label_column
        pstmt.clearParameters();
        pstmt = conn.prepareStatement("select FEATURE_NAME from DATASET_FEATURE_DEFINITION where DATASET_ID = ? and IS_LABEL = ?");
        pstmt.setInt(1, datasetId);
        pstmt.setBoolean(2, true);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            String labelField = rs.getString(1);
            inputDict.addProperty("label_column", labelField.toLowerCase());
            sbLabel.append(labelField);
        }
        
        //Get bucketized_columns
        JsonObject buckets = new JsonObject();
        pstmt = conn.prepareStatement("select FEATURE_NAME, FEATURE_BUCKETS, FEATURE_BUCKET_DATA_TYPE from DATASET_FEATURE_DEFINITION where DATASET_ID = ? and FEATURE_BUCKETS IS NOT NULL ");
        pstmt.setInt(1, datasetId);
        rs = pstmt.executeQuery(); 
        numColumns = 0;
        if(rs.next()) {
            String column = rs.getString(1).toLowerCase();
            String groupingDetails = rs.getString(2);
            String groupingDetailsType = rs.getString(3);
            
            JsonObject bucketObject = new JsonObject();
            
            //Add the column label
            JsonArray bucketValues = new JsonArray();
            
            JsonArray bucketDetailsValues = new JsonArray();
            String[] groupVals = groupingDetails.split(",");
            for(String val: groupVals) {               
                if(groupingDetailsType.equals("INTEGER")) {
                    bucketDetailsValues.add(Integer.parseInt(val.trim()));
                } else {
                    bucketDetailsValues.add(val);
                }
            }    
            //Add the bucket groups
            bucketValues.addAll(bucketDetailsValues);
            bucketObject.add(column, bucketValues);
            
            buckets.add(column + "_buckets", bucketObject);
            
            numColumns++;
        }
        if(numColumns > 0) {
            inputDict.add("bucketized_columns", buckets);
        }
        
        //Get the crossed
        pstmt = conn.prepareStatement("select FEATURE_CROSS_NAME, FEATURE_NAME from DATASET_FEATURE_CROSS where DATASET_ID = ? order by FEATURE_CROSS_NAME");
        pstmt.setInt(1, datasetId);
        rs = pstmt.executeQuery();
        
        JsonArray crossed_columns = new JsonArray();
        numColumns = 0;
        String currentGrouping = "";
        JsonArray group = null;
        while(rs.next()) {
            String grouping = rs.getString(1);
            if(numColumns == 0 || !currentGrouping.equals(grouping) ){
                if (numColumns != 0 && !currentGrouping.equals(grouping)) {
                    crossed_columns.add(group);
                }
                group = new JsonArray();
                group.add(rs.getString(2).toLowerCase());
                currentGrouping = grouping;
            } else {
                group.add(rs.getString(2).toLowerCase());
            }               
            numColumns++;
        }
        if(numColumns > 0) {
            crossed_columns.add(group);
            inputDict.add("crossed_columns", crossed_columns);
        }   
        return exportColumns;
    }
    
    /**
     * The python script needs to read data from the local file system.  We will export all the records for
     * the specified table 
     * 
     * @param conn - Current Connection
     * @param exportCmd - EXPORT command with columns and export location
     * @param exportPath - Used to merge files - it is the directory containing all the files
     * @param exportFileFinal - Used to merge files - indicates what the file final name should be
     * @throws SQLException
     */
    private static void exportData(Connection conn, String exportCmd, String exportPath, String exportFileFinal) throws SQLException {
        LOG.info("Export Command: " + exportCmd);
        Statement stmt = conn.createStatement();
        stmt.executeQuery(exportCmd);
        if(stmt != null) {
            stmt.close();
        }
    }

}