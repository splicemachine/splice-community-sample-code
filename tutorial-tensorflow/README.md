# Overview

TensorFlow (www.tensorflow.org) is an open source software library for machine learning across a range of tasks.  It is a framework for building Deep Learning Neural Networks.  We took the 'TensorFlow Wide & Deep Learning Tutorial' (https://www.tensorflow.org/versions/r0.11/tutorials/wide_and_deep/index.html) which trains a model to predict the probability that an individual has an annual income over 50,000 dollars using Census Income data set and created a method to generically create a model using any data set.

## Dynamically Creating Variables
The original code provided by TensorFlow (https://github.com/tensorflow/tensorflow/blob/master/tensorflow/examples/learn/wide_n_deep_tutorial.py) created a model where the variables COLUMNS, LABEL_COLUMN, CATEGORICAL_COLUMNS and CONTINIOUS_COLUMNS were hard coded.  Before we explain how the process was generized let's explain the purpose for each of the variables:

* **COLUMNS**: This is a list of all of the columns / features in the data set.  For example age or marital status.
* **LABEL_COLUMN**: This is the name of the column in the data set that you are trying to predict.  In this example it is a flag that indicates the probability of having an annual income of over 50,000.
* **CATEGORICAL_COLUMNS**: Categorical columns encompass categorical, ordinal, binary and textual types.  When we use categorical columns there is a possible set of values for example Credit Rating or Gender.
* **CONTINUOUS_COLUMNS**: Continuous data encompasses numerical and interval types

# How the process works
The original tensorflow model was modified to be more generic and provide the ability to not only create the model but also use the model after it has been created.  When creating the model, the columns, label, categorical columns, continuous columns, crossed columns and bucketized columns are passed into the python model via a JSON object as opposed to be hard coded in the model.  To do this, we call a stored procedure which will query the tables MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS and dynamically create a JSON object containing the fields into a python model.  This prevents the data from being hard coded and allows modelers to be able to quickly generate new models by adding entries to a database table.


# Splice Machine Tables
In Splice Machine we created several tables to store the model and feature definitions.  This section describes the purpose of each table.

## Table: MODEL
The model table contains the high level definition of a model and is used in other tables to define.  The intent is that overtime you may have multiple models.

* **MODEL_ID**: A unique identifier for the model
* **NAME**: The name of the model.  This is used when calling the stored procedure to generate the model
* **DESCRIPTION**: This is a description / explanation of the purpose of the model
* **STATUS**: The status of the model either A for active or I for inactive
* **VERSION**: This is for future features where you may have different versions of the same model for testing purposes
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_ID | NAME   | DESCRIPTION                                   | STATUS | VERSION |
|-------- | ------ | --------------------------------------------- | ------ | ------- |
|1        | CENSUS | Predict whether income is greater than 50,000 | A      | 1       |
|2        | INSURANCE | Predict whether a customer has a caravan insurance policy | A      | 1       |

## Table: MODEL_FEATURES 
A Feature represents a single feature / attribute in your data. This table stores all the features for your model with the properties needed to generically generate the model.

* **FEATURE_ID**: A unique identifier for the feature
* **MODEL_ID**: The model id that corresponds to this feature
* **FEATURE_NAME**: Represents a single feature / attribute in your data such as color
* **DATABASE_COLUMN_NAME**: The database column name that corresponds to that feature
* **MODEL_DATA_TYPE**: The feature data type either CONTINUOUS or CATEGORICAL or NULL
* **FEATURE_KEYS**: Used when you want to convert the categorical values into vectors automatically
* **FEATURE_BUCKET_DATA_TYPE**: The data type of a bucket, ie INTEGER
* **FEATURE_BUCKETS**: Used to convert a continuous column into a categorical column.  For example, it divides the range of possible values into subranges called buckets
* **IS_LABEL**: Indicates if the column is the target (to be predicted) column
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_ID |COLUMN_NAME	  |DATABASE_COLUMN_NAME	|MODEL_DATA_TYPE |FEATURE_KEYS                                                |FEATURE_BUCKET_DATA_TYPE |FEATURE_BUCKETS                        |IS_LABEL |
|-------- |---------------|---------------------|----------------|------------------------------------------------------------|-------------------------|---------------------------------------|---------|
|1	      |AGE	          | AGE	                |CONTINUOUS	   |                                                            |INTEGER	                |18, 25, 30, 35, 40, 45, 50, 55, 60, 65	|         |
|1	      |WORKCLASS	  | WORKCLASS	        |CATEGORICAL	   |                                                            |                         |                                       |         |                                                     
|1	      |FNLWGT	      | FNLWGT	            |                |                                                            |	                        |                                       |         |    
|1	      |EDUCATION	  | EDUCATION	        |CATEGORICAL	   |                                                            |                         |                                       |         |    
|1	      |EDUCATION_NUM  |	EDUCATION_NUM	    |CONTINUOUS	   |                                                            |                         |                                       |         |    
|1	      |MARITAL_STATUS |	MARITAL_STATUS	    |CATEGORICAL	   |                                                            |                         |                                       |         |    
|1	      |OCCUPATION	  | OCCUPATION	        |CATEGORICAL	   |                                                           |                         |                                       |         |    
|1	      |RELATIONSHIP   |	RELATIONSHIP	    |CATEGORICAL	   |                                                            |                         |                                       |         |    
|1	      |RACE	          | RACE	            |CATEGORICAL	   |Amer-Indian-Eskimo, Asian-Pac-Islander, Black, Other, White |                         |                                       |         |    
|1	      |GENDER	      | GENDER	            |CATEGORICAL	   |Female, Male                                      		  |                         |                                       |         |    
|1	      |CAPITAL_GAIN	  | CAPITAL_GAIN	    |CONTINUOUS	   |                                                           |	                        |                                       |         |    			
|1	      |CAPITAL_LOSS	  | CAPITAL_LOSS	    |CONTINUOUS	   |                                                            |                         |                                       |         |    				
|1	      |HOURS_PER_WEEK |	HOURS_PER_WEEK	    |CONTINUOUS	   |                                                            |                         |                                       |         |    				
|1	      |NATIVE_COUNTRY |	NATIVE_COUNTRY	    |CATEGORICAL	   |                                                            |                         |                                       |         |    				
|1	      |INCOME_BRACKET |	INCOME_BRACKET	    |                |                                                            |	                        |                                       |         |    				
|1	      |LABEL	      | LABEL	            |                |                                                            |                         |                                       | TRUE    |


## Table: MODEL_FEATURE_CROSS

A feature cross is when you need to combine a combination of features together in order for it to have more meaning.  The value of this feature for a given record is just the concatenation of the values of the two source features
* **MODEL_FEATURE_CROSS_ID**: A unique identifier for the feature cross record
* **MODEL_ID**: Reference to the model id
* **FEATURE_CROSS_NAME**: The name of the feature cross.  This will be repeated for multiple records
* **FEATURE_NAME**: The name of the source feature from the MODEL_FEATURE
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_FEATURE_CROSS_ID | FEATURE_CROSS_NAME       | FEATURE_NAME |
|---------------------- | ------------------------ | ------------ |
|1                      | EDUCATION_OCCUPATION     | EDUCATION    |
|2                      | EDUCATION_OCCUPATION     | OCCUPATION   |
|3                      | AGE_EDUCATION_OCCUPATION | AGE_BUCKETS  |
|4                      | AGE_EDUCATION_OCCUPATION | EDUCATION    |
|5                      | AGE_EDUCATION_OCCUPATION | OCCUPATION   |


# Code Structure

## Java
com.splicemachine.tutorial.tensorflow.ModelDefinition.java - Contains the code for the two stored procedures: CREATE_MODEL and PREDICT_MODEL.  In either case, it builds a JSON object from the data in the tables and passes it into the python code.

## Python Scripts
These are the python scripts used when building models.  They are located under /resources/python

* **Tensor-Demo.py**: This is the python script that contains the logic for creating the model and predicting the outcome
* **Tensor-Demo.ipynb**: This is the same as the 'Tensor-Demo.py' except it is the notebook version of the script.

## Splice Machine Objects

### Setup Files
The ddl script for creating the tables and stored procedures can be found under /resources/splice/setup.

* **/ddl/create-tables.sql**: Creates the tables MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS
* **/ddl/create-procedures.sql**: Creates the stored procedures CREATE_MODEL and PREDICT_MODEL

#### Stored Procedure: CREATE_MODEL
Used for creating a model
* scriptPathAndName: Full path and name to the python script
* type: The type of model. Valid model types: {'wide', 'deep', 'wide_n_deep'}
* modelName: Name of the model that is being created, it maps to an entry in the MODEL and the column NAME
* trainingDataTable: The table containing the training data
* testDataTable: The table containing the test data
* modelOutputDirectory: Base directory for output models
* trainSteps: Number of training steps

	
#### Stored Procedure: PREDICT_MODEL
Used for predicting the outcome of a particular record
* scriptPathAndName: Full path and name to the python script
* type: The type of model. Valid model types: {'wide', 'deep', 'wide_n_deep'}
* modelName: Name of the model that is being created, it maps to an entry in the MODEL and the column NAME
* sourceTable: The table containing the live data
* recordId: ID of the record to perform the prediction on
* modelDirectory: The directory where the model was created


### Census Example
There is an example of predicting a model for census data.  The files to create the tables, entries in the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS and to call the stored procedures can be found in the /resources/examples/census_example folder


* **/ddl/create-tables.sql**: Creates the schema CENSUS and the tables TRAINING_DATA and TESTING_DATA
* **/ddl/create-data.sql**: Populates the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS tables with the data needed to generate the models and also populates the TRAINING_DATA and TESTING_DATA tables.

* **/data/testing.data.txt**: Contains the data that is used to train the Census model
* **/data/training.data.txt**: Contains the data that is used to test the Census model


* **/queries/create_model.sql**: Query that calls the CREATE_MODEL stored procedure to generate the model.
* **/queries/predict.sql**: Query that calls the PREDICT_MODEL stored procedure to generate the predictions in the test data

* **/python/manually-test-python.sh**: A script to manually test the python code without splice machine.  Useful for debugging purposes


# How to Run the Tutorial 

## Prerequisites
In order to run this code you must have Python installed, TensorFlow libraries installed (https://www.tensorflow.org/versions/r0.11/get_started/os_setup) and Splice Machine installed.


## How to compile / deploy and run
In the instructions below the <SOURCE_DIRECTORY> variable should be replaced with the location where you downloaded the sample tutorial code.  <SPLICE_HOME_DIR> is the directory where splice machine is installed ie /Users/username/Downloads/splicemachine/lib


### Compile code and update references in *.sql files

* **Compile the code:** mvn clean compile package
* **Deploy the code:** Copy the compiled jar to your splice machine lib directory ./target/splice-tutorial-tensorflow-2.0.1.1703-SNAPSHOT.jar /Users/username/Downloads/splicemachine/lib
* **Update the import file references:** Modify the file /resources/examples/census_example/create-data.sql to point to the location of your data files - there are three lines to update
* **Update the script file references:** Modify the file /resources/splice/load-all.sql to point to the locations on your filesystem
* **Update the create-model.sql file references:** Modify the file /resources/splice/queries/create-model.sql and update the path to the python script to match your environment
* **Update the predict.sql file references:** Modify the file /resources/splice/queries/predict.sql and update the path to the python script to match your environment

### Create objects in slice machine

* Start Splice Machine
* Launch the splice command prompt
* Run the script to create the tables <SOURCE_DIRECTORY>/splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/load-all.sql

### Create the Census Example model
 This will run the process to create the model.  The data will be extracted to the /data/test and data/train directory
 
* In a splice machine command prompt run the <SOURCE_DIRECTORY>/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/queries/create-model.sql

### Use the model to predict data

* Query the CENSUS.LIVE_DATA and confirm that the LABEL column has no data 
* In a splice machine command prompt run the <SOURCE_DIRECTORY>/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/queries/predict.sql
* Query the CENSUS.LIVE_DATA and confirm that the LABEL column now has data
