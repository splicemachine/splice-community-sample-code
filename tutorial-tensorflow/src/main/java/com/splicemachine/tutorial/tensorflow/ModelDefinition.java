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
import java.util.LinkedHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



public class ModelDefinition {
    
    private static Logger LOG = Logger.getLogger(ModelDefinition.class);
    
    public static String CTLF = "\r\n";
    
    private static int getModelId(Connection conn, String modelName) throws SQLException {
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
        return modelId;
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
    private static StringBuilder buildDictionaryObject(Connection conn, int modelId, JsonObject inputDict, StringBuilder sbLabel) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
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
            String labelField = rs.getString(1);
            inputDict.addProperty("label_column", labelField.toLowerCase());
            sbLabel.append(labelField);
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
     * Generates all the models for each entry in the MODEL_INPUT table for a specific model id
     * 
     * @param fullModelPath - Full path and name of the python script
     * @param rootOutputDirectory - Full path to the output directory for the model
     * @param modelId - The id of the model to generate.  Corresponds to the MODEL_ID in the MODEL table
     * @param returnResultset
     */
    public static void generateModelsForAllInputSets(
            String fullModelPath, 
            String rootOutputDirectory,
            int modelId,  
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
            
            //Get a list of the model input ids
            ArrayList<Integer> modelInputIds = new ArrayList<Integer>();
            PreparedStatement pstmt = conn.prepareStatement("select MODEL_INPUT_ID from MODEL_INPUTS where MODEL_ID = ? ");
            pstmt.setInt(1, modelId);
            rs = pstmt.executeQuery();        
            while(rs.next()) {            
                modelInputIds.add(rs.getInt(1));
            }
            
            for(Integer modelInputId : modelInputIds) {
                generateModelForInputSet (fullModelPath,rootOutputDirectory,modelInputId,returnResultset);
            }
            
            pstmt = conn.prepareStatement("select * from MODEL_CREATION_RESULTS where MODEL_ID = ? and START_TIME >= ?");
            pstmt.setInt(1, modelId);
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
     * Generates a model for a specific entry in the MODEL_INPUT table 
     * 
     * @param fullModelPath - Full path and name of the python script
     * @param rootOutputDirectory - Full path to the output directory for the model
     * @param modelInputId - The model input id to retrieve the model creation parameters.  
     *          Corresponds to the MODEL_INPUT_ID column in the  MODEL_INPUTS table.
     * @param returnResultset
     */
    public static void generateModelForInputSet (
            String fullModelPath, 
            String rootOutputDirectory,          
            int modelInputId,
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
            
            long startTime = System.currentTimeMillis();
            
            PreparedStatement pstmt = conn.prepareStatement("select * from MODEL_INPUTS where MODEL_INPUT_ID = ?");
            pstmt.setInt(1, modelInputId);
            rs = pstmt.executeQuery();    
            int modelId;
            if(rs.next()) {   
                modelId = rs.getInt("MODEL_ID");
                generateModel(
                    fullModelPath, 
                    rs.getString("MODEL_TYPE"), 
                    null, 
                    rs.getString("TRAINING_TABLE"), 
                    rs.getString("TEST_TABLE"), 
                    rootOutputDirectory + "/" + rs.getString("MODEL_OUTPUT_PATH"),
                    rs.getInt("TRAINING_STEPS"),
                    rs.getInt("HASH_BUCKET_SIZE"),
                    rs.getInt("DIMENSIONS"),
                    rs.getString("HIDDEN_UNITS"),
                    modelId,
                    modelInputId,
                    returnResultset);
                
                LOG.error("about to set the MODEL_ID=" + modelId);
                LOG.error("about execute query=" + modelId);
                
                pstmt = conn.prepareStatement("select * from MODEL_CREATION_RESULTS where MODEL_ID = ? and START_TIME >= ?");
                pstmt.setInt(1, modelId);
                pstmt.setTimestamp(2, new Timestamp(startTime));
                returnResultset[0] = pstmt.executeQuery(); 
                
                LOG.error("done executing query=" + modelId);
                
            } else {
                try {returnResultset[0] = stmt.executeQuery("values(-1, 'No entry found in MODEL_INPUTS for MODEL_INPUT_ID" + modelInputId + "')");} catch (Exception e1){}
            }
        }catch(Exception e){
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
            LOG.error("Exception generating model file.", e);
        } finally {
            if(conn != null) { try {conn.close();} catch (Exception e){}} 
        }
        
    }


    /**
     * Generates a model for a with the defined parameters for the model generation process
     * 
     * @param fullModelPath - The full path and name of the python file
     * @param modelType - the type of model. Valid model types: {'wide', 'deep', 'wide_n_deep'}.
     * @param modelName - The name of the model to be used to retrieve the list of columns.  It should correspond to
     *  a value in the NAME column in the MODEL table.  This can be null if the modelId is passed in.
     * @param trainTable - The table containing the training data
     * @param testTable - The table containing the test data
     * @param modelOutputDirectory - Base directory for output models.
     * @param trainSteps - Number of training steps for tensorflow model creation process
     * @param hashBucketSize - bucket size for tensorflow model creation process
     * @param dimensions - dimensions for tensorflow model creation process
     * @param hiddenUnits - hidden units for tensorflow model creation process
     * @param modelId - The model id.  If the modelId is not known set it to -1 and pass n the modelName
     * @param modelInputId - The model input id, set to -1 if not known
     */
    public static void generateModel(
            String fullModelPath, 
            String modelType, 
            String modelName, 
            String trainTable, 
            String testTable, 
            String modelOutputDirectory,
            int trainSteps,
            int hashBucketSize,
            int dimensions,
            String hiddenUnits,
            int modelId,
            int modelInputId,
            ResultSet[] returnResultset) { 
        
        Statement stmt = null;
        Connection conn = null;
        Connection conn2 = null;
        
        long startTime = System.currentTimeMillis();
        String status = "inprogress";
        String accuracy = null;
        String baselineTargetMean = null;
        String thresholdMean = null;
        String auc = null;
        String globalStep = null;
        String actualTargetMean = null;
        String predictionMean = null;
        String loss = null;
        String precisionPositiveThresholdMean = null;
        String recallPositiveThresholdMean = null;
        String trainingDataResults = null;
        String testDataResults = null;

        try{
            //For now, keep logging on for this stored procedure
            LOG.setLevel(Level.INFO);
            
            //There is a defect currently in base code which requires that we
            //use the full jdbc connection see: string: https://splice.atlassian.net/browse/SPLICE-1103
            conn = DriverManager.getConnection("jdbc:splice://localhost:1527/splicedb;user=splice;password=admin");
            
            //This second connection is to be able to return a resultset
            conn2 = DriverManager.getConnection("jdbc:default:connection");            
            stmt = conn2.createStatement();              
            
            //Confirm the model type that was passed in
            if(!"wide".equalsIgnoreCase(modelType) &&
                    !"deep".equalsIgnoreCase(modelType) && !"wide_n_deep".equalsIgnoreCase(modelType)) {
                returnResultset[0] = stmt.executeQuery("values(-1, 'Invalid modelType:" + modelType + ".  Valid values are: wide, deep, or wide_n_deep')");
                return;
            }         
            
            //Create the output directory for the model
            //There will be sub directories under this folder
            //For the data as well as the model output
            File fRootModelOutputDir = new File(modelOutputDirectory);
            if(!fRootModelOutputDir.exists()) {
                fRootModelOutputDir.mkdirs();
            }
            
            //Create the model directory.  If it already exists
            //clean out the files in that directory
            File fModelOutputDir = new File(fRootModelOutputDir, "model");
            if(fModelOutputDir.exists()) {
                purgeDirectory(fModelOutputDir); 
            } else {
                fModelOutputDir.mkdirs();
            }
            
            //Create the data directory.  If it already exists
            //clean out the files in that directory
            File fDataOutputDir = new File(fRootModelOutputDir, "data");
            if(fDataOutputDir.exists()) {
                purgeDirectory(fDataOutputDir); 
            } else {
                fDataOutputDir.mkdirs();
            }
            
            //Get the modelId
            if(modelId == -1) {
                modelId = getModelId(conn, modelName);
            }

            //A JSON Object is passed into the python script so that it
            //know the structure of the data and corresponding groupings
            JsonObject inputDict = new JsonObject();
            StringBuilder labelField = new StringBuilder();
            StringBuilder exportColumns = buildDictionaryObject(conn, modelId, inputDict,labelField);           
            
            //Export the training data
            String exportPathTrain = fDataOutputDir.getAbsolutePath() + "/train";
            String trainingFilePath = exportPathTrain + "/part-r-00000.csv";
            //inputDict.addProperty("train_data_path", exportPathTrain + "/part-r-00000.csv");
            String exportCmd = "EXPORT('" + exportPathTrain + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + trainTable;
            exportData(conn, exportCmd, exportPathTrain + "/* ", exportPathTrain + "/traindata.txt");        
            
            //Export the training data
            String exportPathTest = fDataOutputDir.getAbsolutePath() + "/test";
            String testFilePath = exportPathTest + "/part-r-00000.csv";
            //inputDict.addProperty("test_data_path", exportPathTest + "/part-r-00000.csv");
            exportCmd = "EXPORT('" + exportPathTest + "', false, null, null, null, null) SELECT " + exportColumns.toString() + " FROM " + testTable;
            exportData(conn, exportCmd, exportPathTest + "/* ", exportPathTest + "/testdata.txt");

            //Build JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String jsonData = gson.toJson(inputDict);
            
            LOG.info("Python Script: " + fullModelPath);
            LOG.info("Model Type (model_type): " + modelType);
            LOG.info("Model Output Directory (model_dir): " + modelOutputDirectory);
            LOG.info("Model Train Steps (train_steps): " + trainSteps);
            LOG.info("Training data File (train_data): " + trainingFilePath);
            LOG.info("Hash Bucket Size (hash_bucket_size): " + hashBucketSize);
            LOG.info("Dimension (dimension): " + dimensions);
            LOG.info("Hidden Units (dnn_hidden_units): " + hiddenUnits);
            LOG.info("Test data file (test_data): " + testFilePath);
            LOG.info("Model Inputs (inputs): " + jsonData);
                       
             //Call the python script
            ProcessBuilder pb = new ProcessBuilder("python",fullModelPath,
                    "--model_type=" + modelType,
                    "--model_dir=" + modelOutputDirectory,
                    "--train_steps=" + trainSteps,
                    "--hash_bucket_size=" + hashBucketSize,
                    "--dimension=" + dimensions,
                    "--dnn_hidden_units=" + hiddenUnits,
                    "--train_data=" + trainingFilePath,
                    "--test_data=" + testFilePath,
                    "--inputs=" + jsonData);
            
            //Variables to store in the results table
            startTime = System.currentTimeMillis();

            Process p = pb.start();

            LinkedHashMap<String,String> results = new LinkedHashMap<String,String>();
             
            //Process the output and write it the the log file
            BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            int exitCode = p.waitFor();
            boolean collectResults = false;
            while ((line = bfr.readLine()) != null){
                LOG.info("Python Output: " + line);
                if(line.startsWith("Begin Results")) {
                    collectResults = true;
                } else if (line.indexOf("] End Results") > -1) {
                    collectResults = false;
                } else if (collectResults) {
                    int colonPos = line.indexOf(":");
                    if(colonPos > -1) {
                        String fieldValue = line.substring(colonPos+1).trim();
                        if(line.contains("accuracy/baseline_target_mean")) {
                            baselineTargetMean = fieldValue;
                        } else if (line.contains("accuracy/threshold")) {
                            thresholdMean = fieldValue;
                        } else if (line.contains("labels/actual_target_mean")) {
                            actualTargetMean = fieldValue;
                        } else if (line.contains("labels/prediction_mean")) {
                            predictionMean = fieldValue;
                        } else if (line.contains("precision/positive_threshold")) {
                            precisionPositiveThresholdMean = fieldValue;
                        } else if (line.contains("recall/positive_threshold")) {
                            recallPositiveThresholdMean = fieldValue;
                        } else if (line.contains("global_step")) {
                            globalStep = fieldValue;
                        } else if (line.contains("auc")) {
                            auc = fieldValue;
                        }  else if (line.contains("loss")) {
                            loss = fieldValue;
                        }  else if (line.contains("accuracy")) {
                            accuracy = fieldValue;
                        }
                        
                        results.put(line.substring(0,colonPos), line.substring(colonPos+1));
                    }
                } else if (line.startsWith("Testing data predictions")) {     
                    //Training data predictions=[Counter({0: 820, 1: 2})]
                    int equalsPos = line.indexOf("=");
                    if(equalsPos > -1)
                        testDataResults = line.substring(equalsPos+1);
                } else if (line.startsWith("Training data predictions=")) {
                    //Testing data predictions=[Counter({0: 820, 1: 2})]
                    int equalsPos = line.indexOf("=");
                    if(equalsPos > -1)
                        trainingDataResults = line.substring(equalsPos+1);                    
                }
            }
            
            long endTime = System.currentTimeMillis();
            PreparedStatement pstmt = null;
            if(exitCode != 0) {
                status = "Python Script Failed";
                String insertStatement = "INSERT INTO MODEL_CREATION_RESULTS " + 
                        "(MODEL_ID,MODEL_INPUT_ID,START_TIME,END_TIME,STATUS)" +
                        "values (?,?,?,?,?)";
                pstmt = conn2.prepareStatement(insertStatement);   
                pstmt.setInt(1, modelId);
                pstmt.setInt(2, modelInputId);
                pstmt.setTimestamp(3, new Timestamp(startTime));
                pstmt.setTimestamp(4, new Timestamp(endTime));
                pstmt.setString(5, status);
                pstmt.executeUpdate();
            } else {
                status = "Success";
                String insertStatement = "INSERT INTO MODEL_CREATION_RESULTS " + 
                        "(MODEL_ID,MODEL_INPUT_ID,START_TIME,END_TIME,STATUS,ACCURACY,BASELINE_TARGET_MEAN,THRESHOLD_MEAN,AUC,GLOBAL_STEP,ACTUAL_TARGET_MEAN,PREDICTION_MEAN,LOSS,PRECISION_POSITIVE_THRESHOLD_MEAN,RECALL_POSITIVE_THRESHOLD_MEAN,TRAINING_DATA_RESULTS,TEST_DATA_RESULTS) " +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pstmt = conn2.prepareStatement(insertStatement);
                pstmt.setInt(1, modelId);
                pstmt.setInt(2, modelInputId);
                pstmt.setTimestamp(3, new Timestamp(startTime));
                pstmt.setTimestamp(4, new Timestamp(endTime));
                pstmt.setString(5, status);
                pstmt.setFloat(6, parseFloat(accuracy));
                pstmt.setFloat(7, parseFloat(baselineTargetMean));
                pstmt.setFloat(8, parseFloat(thresholdMean));
                pstmt.setFloat(9, parseFloat(auc));
                pstmt.setInt(10, parseInt(globalStep));
                pstmt.setFloat(11, parseFloat(actualTargetMean));
                pstmt.setFloat(12, parseFloat(predictionMean));
                pstmt.setFloat(13, parseFloat(loss));
                pstmt.setFloat(14, parseFloat(precisionPositiveThresholdMean));
                pstmt.setFloat(15, parseFloat(recallPositiveThresholdMean));
                pstmt.setString(16, trainingDataResults);
                pstmt.setString(17, testDataResults);
                pstmt.executeUpdate();
            
            }
               
            //Get the entry to be returned
            String selectStmt = "select * from MODEL_CREATION_RESULTS where MODEL_ID = ? and MODEL_INPUT_ID = ? and START_TIME = ? and END_TIME = ?";
            pstmt = conn2.prepareStatement(selectStmt);
            pstmt.setInt(1, modelId);
            pstmt.setInt(2, modelInputId);
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
    
    private static float parseFloat(String val) {
        float myVal = -1;
        if(val != null) {
            try {
                myVal = Float.parseFloat(val);
            } catch (Exception e) {
                //ignore
            }
        }
        return myVal;
    }

    private static int parseInt(String val) {
        int myVal = -1;
        try {
            myVal = Integer.parseInt(val);
        } catch (Exception e) {
            //ignore
        }
        return myVal;
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
     * @param rootOutputDirectory - the root output directory for data and models
     * @param modelId - The id of the model to be used to retrieve the list of columns.  It should correspond to
     *       a value in the MODEL_ID column in the MODEL table.
     * @param modelInputId - The id of the model inputs to be used to retrieve the details and location of the model.  It should
     *      correspond to the MODEL_INPUT_ID in the MODEL_INPUTS table
     * @param sourceTable - Table containing the live data
     * @param sourceId - Key to the record you want to predict
     * @param comparisonColumn - Comparison Column (NOT SURE THIS IS NEEDED)
     * @param criteria - Criteria (NOT SURE THIS IS NEEDED)
     * @param returnResultset
     */
    public static void predictModel(
            String fullModelPath, 
            String rootOutputDirectory,
            int modelId,
            int modelInputId,
            String sourceTable,
            int sourceId, 
            String comparisonColumn,
            String criteria,
            ResultSet[] returnResultset) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{

            //For right now keep logging on
            LOG.setLevel(Level.INFO);
            
            conn = DriverManager.getConnection("jdbc:default:connection");
            PreparedStatement pstmt = conn.prepareStatement("select * from MODEL_INPUTS where MODEL_INPUT_ID = ?");
            pstmt.setInt(1, modelInputId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                String modelType = rs.getString("MODEL_TYPE");
                String modelOutputDirectory = rootOutputDirectory + "/" + rs.getString("MODEL_OUTPUT_PATH") + "/model";
                int trainSteps = rs.getInt("TRAINING_STEPS");
                int hashBucketSize =  rs.getInt("HASH_BUCKET_SIZE");
                int dimensions = rs.getInt("DIMENSIONS");
                String hiddenUnits = rs.getString("HIDDEN_UNITS");
                rs.close();
                
                JsonObject inputDict = new JsonObject();
                StringBuilder labelField = new StringBuilder();
                StringBuilder exportColumns = buildDictionaryObject(conn, modelId, inputDict,labelField);
                
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
                LOG.info("Model Type (model_type): " + modelType);
                LOG.info("Comparison Column: " + comparisonColumn);
                LOG.info("Criteria: " + criteria);
                LOG.info("Model Output Directory (model_dir): " + modelOutputDirectory);
                LOG.info("Data Input Record (input_record): " + recordAsCSV);
                LOG.info("Model Inputs (inputs): " + jsonData);
                
                ProcessBuilder pb = new ProcessBuilder("python",fullModelPath,
                        "--predict=true",
                        "--model_type=" + modelType,
                        "--train_steps=" + trainSteps,
                        "--hash_bucket_size=" + hashBucketSize,
                        "--dimension=" + dimensions,
                        "--dnn_hidden_units=" + hiddenUnits,
                        "--comparison_column=" + comparisonColumn,
                        "--criteria=" + criteria,
                        "--model_dir=" + modelOutputDirectory,
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
                        String updateSql = "UPDATE " + sourceTable + " set " + labelField.toString() + " = ? where ID = ?";
                        pstmt = conn.prepareStatement(updateSql);
                        pstmt.setObject(1, returnVal);
                        pstmt.setInt(2, sourceId);
                        pstmt.execute();
                        returnResultset[0] = stmt.executeQuery("select * from "+ sourceTable + " where ID = " + sourceId);
                    }
                } else {
                    returnResultset[0] = stmt.executeQuery("values(-1, 'Exception running the Python model.')");
                }
            } else {
                stmt = conn.createStatement();
                returnResultset[0] = stmt.executeQuery("values(-1, 'No entry found in MODEL_INPUTS for MODEL_INPUT_ID" + modelInputId + "')");
            }            
        } catch (Exception e) {
            LOG.error("Exception calling pythong script.", e);
            try {returnResultset[0] = stmt.executeQuery("values(-1, 'Unexpected Exception')");} catch (Exception e1){}
        } finally {
            if(rs != null) { try {rs.close();} catch (Exception e){}}  
        }
    }

}