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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



public class ModelDefinition {
    
    private static int fileSuffix = 0;
    
    private static Logger LOG = Logger.getLogger(ModelDefinition.class);
    
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
    private static StringBuilder buildDictionaryObject(Connection conn, String modelName, JsonObject inputDict) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        //Get the Model Id
        int modelId = -1;
        pstmt = conn.prepareStatement("select MODEL_ID from MODEL where NAME = ? and STATUS = ?");
        pstmt.setString(1, modelName);
        pstmt.setString(2, "A");
        rs = pstmt.executeQuery();
        if(rs.next()) {
            modelId = rs.getInt(1);
        }
        
        //Get Columns
        pstmt = conn.prepareStatement("select FEATURE_NAME from MODEL_FEATURES where MODEL_ID = ? and DATABASE_COLUMN_NAME IS NOT NULL order by FEATURE_NAME");
        pstmt.setInt(1, modelId);
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
        pstmt = conn.prepareStatement("select FEATURE_NAME from MODEL_FEATURES where MODEL_ID = ? and MODEL_DATA_TYPE = ?");
        pstmt.setInt(1, modelId);
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
        pstmt.setInt(1, modelId);
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
        pstmt = conn.prepareStatement("select FEATURE_NAME from MODEL_FEATURES where MODEL_ID = ? and IS_LABEL = ?");
        pstmt.setInt(1, modelId);
        pstmt.setBoolean(2, true);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            inputDict.addProperty("label_column", rs.getString(1).toLowerCase());
        }
        
        //Get bucketized_columns
        JsonObject buckets = new JsonObject();
        pstmt = conn.prepareStatement("select FEATURE_NAME, FEATURE_BUCKETS, FEATURE_BUCKET_DATA_TYPE from MODEL_FEATURES where MODEL_ID = ? and FEATURE_BUCKETS IS NOT NULL ");
        pstmt.setInt(1, modelId);
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
        pstmt = conn.prepareStatement("select FEATURE_CROSS_NAME, FEATURE_NAME from MODEL_FEATURE_CROSS where MODEL_ID = ? order by FEATURE_CROSS_NAME");
        pstmt.setInt(1, modelId);
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
    
    /**
     * 
     * @param fullModelPath - The full path and name of the python file
     * @param type - the type of model, for now it is always wide_n_deep
     * @param modelName - The name of the model to be used to retrieve the list of columns.  It should correspond to
     *  a value in the MODEL column in the MODEL_DICTIONARY table.
     * @param trainTable - The table containing the training data.
     * @param testTable - The table containing the test data.
     */
    public static void generateModel(String fullModelPath, String type, String modelName, String trainTable, String testTable, ResultSet[] returnResultset) { 
        
        Statement stmt = null;
        Connection conn = null;
        Connection conn2 = null;
        boolean sucess = false;
        try{
            //For now, keep logging on for this stored procedure
            LOG.setLevel(Level.INFO);
            
            //There is a defect currently in base code which requires that we
            //use the full jdbc connection see: string: https://splice.atlassian.net/browse/SPLICE-1103
            conn = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
            
            //This second connection is to be able to return a resultset
            conn2 = DriverManager.getConnection("jdbc:default:connection");            
            stmt = conn2.createStatement();    
            
            File pythonFile = new File(fullModelPath);
            String parentDir = pythonFile.getParent();

            //Directory where the data is exported.  Python expects one file
            //This will only work for an export that is in one region
            //Ideally this process should be changed to export the data
            //using a VTI that writes a file in TFRecord format
            String dataDir = parentDir + "/data";
            String trainingDataFile = dataDir + "/train/part-r-00000.csv";
            String testDataFile = dataDir + "/test/part-r-00000.csv";

            //This is the directory where the model output is placed
            //This is on the file system and contains the actual model files
            String modelOutputDir = parentDir + "/output";
            
            //We need to clean out the model output directory
            File fModelOutputDir = new File(modelOutputDir);
            if(fModelOutputDir.exists()) {
                purgeDirectory(fModelOutputDir); 
            } else {
                fModelOutputDir.mkdirs();
            }
            
            //A JSON Object is passed into the python script so that it
            //know the structure of the data and corresponding groupings
            JsonObject inputDict = new JsonObject();
            StringBuilder exportColumns = buildDictionaryObject(conn, modelName, inputDict);           
            
            //Export the training data
            String exportPathTrain = dataDir + "/train";
            inputDict.addProperty("train_data_path", exportPathTrain + "/part-r-00000.csv");
            String exportCmd = "EXPORT('" + exportPathTrain + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + trainTable;
            exportData(conn, exportCmd, exportPathTrain + "/* ", exportPathTrain + "/traindata.txt");        
            
            //Export the training data
            String exportPathTest = dataDir + "/test";
            inputDict.addProperty("test_data_path", exportPathTest + "/part-r-00000.csv");
            exportCmd = "EXPORT('" + exportPathTest + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + testTable;
            exportData(conn, exportCmd, exportPathTest + "/* ", exportPathTest + "/testdata.txt");

            //Build JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String jsonData = gson.toJson(inputDict);
            
            LOG.info("Python Script: " + fullModelPath);
            LOG.info("Model Type (model_type): " + type);
            LOG.info("Model Output Directory (model_dir): " + modelOutputDir);
            LOG.info("Model Inputs (inputs): " + jsonData);
            
            //Call the python script
            ProcessBuilder pb = new ProcessBuilder("python",fullModelPath,
                    "--model_type=" + type,
                    "--model_dir=" + modelOutputDir,
                    "--inputs=" + jsonData);
            Process p = pb.start();
             
            //Process the output and write it the the log file
            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            int exitCode = p.waitFor();
            while ((line = bfr.readLine()) != null){
                LOG.info("Python Output: " + line);
            }
               
            if(exitCode != 0) {
                returnResultset[0] = stmt.executeQuery("values(-1, 'Python Script Failed')");
                sucess = false;
                return;
            }

            returnResultset[0] = stmt.executeQuery("values(1, 'Model Successfully Generated')");

        }catch(Exception e){
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
            sucess = false;
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
     * @param fullModelPath - The full path and name of the python file
     * @param type - the type of model, for now it is always wide_n_deep
     * @param modelName - The name of the model to be used to retrieve the list of columns.  It should correspond to
     *       a value in the MODEL column in the MODEL_DICTIONARY table.
     * @param sourceTable - Table containing the live data
     * @param sourceId - Key to the record you want to predict
     * @param returnResultset
     */
    public static void predictModel(String fullModelPath, String type, String modelName, String sourceTable, int sourceId, ResultSet[] returnResultset) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{

            //For right now keep logging on
            LOG.setLevel(Level.INFO);
            
            conn = DriverManager.getConnection("jdbc:default:connection");
            
            File pythonFile = new File(fullModelPath);
            String parentDir = pythonFile.getParent();
            
            String modelOutputDir = parentDir + "/output";
            String dataDir = parentDir + "/data";
            
            JsonObject inputDict = new JsonObject();
            StringBuilder exportColumns = buildDictionaryObject(conn, modelName, inputDict);
            
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
            } else {
                returnResultset[0] = stmt.executeQuery("values(-1, 'Record not found in the database')");
                return;
            }

            //Build JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String jsonData = gson.toJson(inputDict);
            
            LOG.info("Python Script: " + fullModelPath);
            LOG.info("Model Type (model_type): " + type);
            LOG.info("Model Output Directory (model_dir): " + modelOutputDir);
            LOG.info("Data Input Record (input_record): " + recordAsCSV);
            LOG.info("Model Inputs (inputs): " + jsonData);
            
            ProcessBuilder pb = new ProcessBuilder("python",fullModelPath,
                    "--predict=true",
                    "--model_type=" + type,
                    "--model_dir=" + modelOutputDir,
                    "--input_record=" + recordAsCSV,                    
                    "--inputs=" + jsonData);

            Process p = pb.start();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String returnVal = "";
            String line = "";
            int exitCode = p.waitFor();
            while ((line = bfr.readLine()) != null){
                LOG.info("Python Output: " + line);
                returnVal = line;
            }
            
            if(exitCode == 0) {    
                int beginIndex = returnVal.indexOf("[");
                int endIndex = returnVal.indexOf("]");
                if(beginIndex > -1 && endIndex > -1) {
                    returnVal = returnVal.substring(beginIndex+1, endIndex);
                    stmt.executeUpdate("UPDATE " + sourceTable + " set LABEL = '" + returnVal +"' where ID = " + sourceId);
                    returnResultset[0] = stmt.executeQuery("select * from "+ sourceTable + " where ID = " + sourceId);
                }
            } else {
                returnResultset[0] = stmt.executeQuery("values(-1, 'Exception running the Python model.')");
            }
            
        } catch (Exception e) {
            LOG.error("Exception calling pythong script.", e);
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
        } finally {
            if(rs != null) { try {rs.close();} catch (Exception e){}}  
        }
    }

}