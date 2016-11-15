# Overview
This is an example of using Splice Machine to build a tensorflow model and later use that model that was created to predict a particular outcome and while using the Splice Machine command prompt.  The process in this tutorial is based on the tensorflow tutorial: https://www.tensorflow.org/versions/r0.11/tutorials/wide_and_deep/index.html.  

The original tensorflow model was modified to be more generic and provide the ability to not only create the model but also use the model after it has been created.  When creating the model, the columns, label, categorical columns, continious columns, crossed columns and bucketized columns are passed into the python model via a JSON object as opposed to be hard coded in the model.

At the present time this can only be run in a standalone fashion.

# Prerequisites
In order to run this code you must have Python and Splice Machine installed.

# Code Structure

## Java
com.splicemachine.tutorial.tensorflow.CreateInputDictionary.java - Contains the code for the two stored procedures: CREATE_MODEL and PREDICT_MODEL.  In either case, it builds a JSON object from the data in the 

## Splice Machine Objects

### Schema
* **SCHEMA**: CENSUS

### Tables
* **TRAINING_DATA**: Contains the data that is used to train the model
* **TEST_DATA**: Contains the data that is used to test the model
* **LIVE_DATA**: Contains the data that is used when trying to predict an outcome
* **MODEL_DICTIONARY**: Contains the details about how the columns in the tables should be used when creating the model.

### Stored Procedure: CREATE_MODEL
Used for creating a model
* scriptPathAndName: Full path and name to the python script
* type: What type of
* modelName: Name of the model that is being created, it maps to an entry in MODEL_DICTIONARY
* trainingDataTable: The table containing the training data
* testDataTable: The table containing the test data
	
### Stored Procedure: PREDICT_MODEL
Used for predicting the outcome of a particular record
* scriptPathAndName: Full path and name to the python script
* type: What type of
* modelName: Name of the model that is being created, it maps to an entry in MODEL_DICTIONARY
* sourceTable: The table containing the live data
* recordId: ID of the record to perform the prediction on

## Python Scripts
These are the python scripts used when building models.

### Tensor-Demo.py
This is the python script that contains the logic for creating the model and predicting the outcome

### Tensor-Demo.ipynb
This is the same as the 'Tensor-Demo.py' except it is the notebook version of the script.

## How to compile / deploy and run
* Compile the code by running: mvn clean compile
* Copy the compiled jar to your splice machine lib directory ./target/splice-tutorial-tensorflow-2.0.1.32-SNAPSHOT.jar /Users/username/Downloads/splicemachine/lib
* Update the file Open a command prompt and run the splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/ddl/create-data.sql to have the path to your import files
* Update the file Open a command prompt and run the splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/queries/create-model.sql to point to the location of the /tutorial-tensorflow/src/main/resources/python/Tensor-Demo.py file
* Update the file Open a command prompt and run the splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/queries/predict.sql to point to the location of the /tutorial-tensorflow/src/main/resources/python/Tensor-Demo.py file
* Start Splice Machine
* Open a command prompt and run the splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/ddl/create-data.sql
* Open a command prompt and run the splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/ddl/create-procedures.sql
* 

