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
package com.splicemachine.tutorials.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import com.splicemachine.db.iapi.sql.ResultSet;
import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.example.SparkMLibUtils;

/**
 * @author jramineni
 * Stored Procedure methods to Builr Model and Make PRedictions
 * 
 */



public class RULPredictiveModel {
	private static final Logger LOG = Logger
			.getLogger(RULPredictiveModel.class);

	/**
	 * Build and Test Model
	 */
	public static void buildRULModel(String trainTableName,
			String testTableName, String saveModelPath) {
		try {

			if (trainTableName == null || trainTableName.length() == 0)
				trainTableName = "IOT.TRAIN_AGG_1_VIEW";
			if (testTableName == null || testTableName.length() == 0)
				testTableName = "IOT.TEST_AGG_1_VIEW";
			if (saveModelPath == null || saveModelPath.length() == 0)
				saveModelPath = "/tmp";
			if (!saveModelPath.endsWith("/"))
				saveModelPath = saveModelPath + "/";

			String jdbcUrl = "jdbc:splice://localhost:1527/splicedb;user=splice;password=admin;useSpark=true";

			SparkSession sparkSession = SpliceSpark.getSession();

			// LOAD TRAIN AND TEST DATA
			Map<String, String> options = new HashMap<String, String>();
			options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
			options.put("url", jdbcUrl);

			// Train Data
			options.put("dbtable", trainTableName);
			Dataset<Row> trainds = sparkSession.read().format("jdbc")
					.options(options).load();
			// Test Data
			options.put("dbtable", testTableName);
			Dataset<Row> testds = sparkSession.read().format("jdbc")
					.options(options).load();

			/********
			Connection con = DriverManager.getConnection("jdbc:default:connection");
	        PreparedStatement ps = con.prepareStatement(statement);
	        ResultSet rs = ps.executeQuery();
	        // Convert result set to Java RDD
	        JavaRDD<LocatedRow> resultSetRDD = SparkMLibUtils.resultSetToRDD(rs) ;
	        ******/
			
			// Prepare data

			// Fill in nulls
			trainds = trainds.na().fill(0);
			testds = trainds.na().fill(0);

			// Relabel Label Column
			trainds = trainds.withColumnRenamed("PREDICTION", "label");
			testds = testds.withColumnRenamed("PREDICTION", "label");

			// Create Features Vector
			VectorAssembler assembler = new VectorAssembler()
					.setInputCols(
							new String[] { "OP_SETTING_1", "OP_SETTING_2",
									"OP_SETTING_3", "SENSOR_MEASURE_1",
									"SENSOR_MEASURE_2", "SENSOR_MEASURE_3",
									"SENSOR_MEASURE_4", "SENSOR_MEASURE_5",
									"SENSOR_MEASURE_6", "SENSOR_MEASURE_7",
									"SENSOR_MEASURE_8", "SENSOR_MEASURE_9",
									"SENSOR_MEASURE_10", "SENSOR_MEASURE_11",
									"SENSOR_MEASURE_12", "SENSOR_MEASURE_13",
									"SENSOR_MEASURE_14", "SENSOR_MEASURE_15",
									"SENSOR_MEASURE_16", "SENSOR_MEASURE_17",
									"SENSOR_MEASURE_18", "SENSOR_MEASURE_19",
									"SENSOR_MEASURE_20", "SENSOR_MEASURE_21",
									"AVG_SENSOR_1", "AVG_SENSOR_2",
									"AVG_SENSOR_3", "AVG_SENSOR_4",
									"AVG_SENSOR_5", "AVG_SENSOR_6",
									"AVG_SENSOR_7", "AVG_SENSOR_8",
									"AVG_SENSOR_9", "AVG_SENSOR_10",
									"AVG_SENSOR_11", "AVG_SENSOR_12",
									"AVG_SENSOR_13", "AVG_SENSOR_14",
									"AVG_SENSOR_15", "AVG_SENSOR_16",
									"AVG_SENSOR_17", "AVG_SENSOR_18",
									"AVG_SENSOR_19", "AVG_SENSOR_20",
									"AVG_SENSOR_21" }).setOutputCol("features");

			//Algorithm
			DecisionTreeClassifier dt = new DecisionTreeClassifier();

			//Create Pipeline
			Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {
					assembler, dt }); // removed normalizer

			//Create parameters for iteration for validator
			ParamMap[] paramGrid = new ParamGridBuilder()
					.addGrid(dt.maxDepth(), new int[] { 2, 4, 6 })
					.addGrid(dt.maxBins(), new int[] { 20, 60 }).build();

			//Create Validator
			CrossValidator cv = new CrossValidator().setEstimator(pipeline)
					.setEvaluator(new BinaryClassificationEvaluator())
					.setEstimatorParamMaps(paramGrid).setNumFolds(2);

			//Create Model - This process generates Multiple models, iterating thru the 
			// range of parameter values, and finds the best model
			CrossValidatorModel cvModel = cv.fit(trainds);

			//Test the generated Model
			Dataset<Row> predictionsDF = cvModel.transform(testds).select(
					"rawPrediction", "probability", "label", "prediction");

			//Calculate accuracy of the model
			Long correctPredCnt = predictionsDF.filter(
					predictionsDF.col("label").equalTo(
							predictionsDF.col("prediction"))).count();
			Float totalCnt = new Float(predictionsDF.count());
			float acc = correctPredCnt / totalCnt;

			System.out.println("ACCURACY = " + acc);

			//Save the pipeline and Model. Model will be used for predictions and pipeline can be
			// generate model on different set of data.
			pipeline.write().overwrite().save(saveModelPath + "pipeline/");
			cvModel.write().overwrite().save(saveModelPath + "model/");

		} catch (IOException sqle) {
			LOG.error("Exception in getColumnStatistics", sqle);
			sqle.printStackTrace();
		}

	}

	/**
	 * Stored Procedure for Predictions
	 */
	public static void predictRUL(String sensorTableName,
			String resultsTableName, String savedModelPath, int loopinterval) {
		
		try {
			
			//Initialize variables
			if (sensorTableName == null || sensorTableName.length() == 0)
				sensorTableName = "IOT.SENSOR_AGG_1_VIEW";
			if (resultsTableName == null || resultsTableName.length() == 0)
				resultsTableName = "IOT.PREDICTION_EXT";
			if (savedModelPath == null || savedModelPath.length() == 0)
				savedModelPath = "/tmp";
			if (!savedModelPath.endsWith("/"))
				savedModelPath = savedModelPath + "/";
			savedModelPath += "model/";

			String jdbcUrl = "jdbc:splice://localhost:1527/splicedb;user=splice;password=admin;useSpark=true";
			Connection conn = DriverManager.getConnection(jdbcUrl);
			
			SparkSession sparkSession = SpliceSpark.getSession();
			
			
			//Specify the data for predictions
			Map<String, String> options = new HashMap<String, String>();
			options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
			options.put("url", jdbcUrl);
			options.put("dbtable", sensorTableName);
			

			//Load Model to use for predictins
			CrossValidatorModel cvModel = CrossValidatorModel
					.load(savedModelPath);
			
			//Keep checking for new data and make predictions
			while (loopinterval > 0) {
				//Sensor data requiring predictions
				Dataset<Row> sensords = sparkSession.read().format("jdbc")
						.options(options).load();

				//prepare data
				sensords = sensords.na().fill(0);

				//make predictions
				Dataset<Row> predictions = cvModel.transform(sensords)
						.select("ENGINE_TYPE", "UNIT", "TIME", "prediction")
						.withColumnRenamed("prediction", "PREDICTION");
				
				//Save predictions
				String fileName = "temp_pred_"
						+ RandomStringUtils.randomAlphabetic(6).toLowerCase();

				predictions.write().mode(SaveMode.Append)
						.csv("/tmp/data_pred/predictions");

				//Mark records for which predictions are made
				PreparedStatement pStmtDel = conn
						.prepareStatement("delete  from IOT.TO_PROCESS_SENSOR s where exists (select 1 from IOT.PREDICTIONS_EXT p where p.engine_type = s.engine_type and p.unit= s.unit and p.time=s.time )");
				pStmtDel.execute();
				pStmtDel.close();
			}

			
		} catch (SQLException sqle) {
			System.out.println("Error  :::::" + sqle.toString());
			LOG.error("Exception in getColumnStatistics", sqle);
			sqle.printStackTrace();
		}

	}

}
