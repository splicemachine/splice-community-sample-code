# Overview

TensorFlow (www.tensorflow.org) is an open source software library for machine learning across a range of tasks.  It is a framework for building Deep Learning Neural Networks.  We took the 'TensorFlow Wide & Deep Learning Tutorial' (https://www.tensorflow.org/versions/r0.11/tutorials/wide_and_deep/index.html) which trains a model to predict the probability that an individual has an annual income over 50,000 dollars using Census Income data set and created a method to generically create a model using any data set.

## Dynamically Creating Variables
The original code provided by TensorFlow (https://github.com/tensorflow/tensorflow/blob/master/tensorflow/examples/learn/wide_n_deep_tutorial.py) created a model where the variables COLUMNS, LABEL_COLUMN, CATEGORICAL_COLUMNS and CONTINIOUS_COLUMNS were hard coded.  Before we explain how the process was generized let's explain the purpose for each of the variables:

* **COLUMNS**: This is a list of all of the columns / features in the data set.  For example age or marital status.
* **LABEL_COLUMN**: This is the name of the column in the data set that you are trying to predict.  In this example it is a flag that indicates the probability of having an annual income of over 50,000.
* **CATEGORICAL_COLUMNS**: Categorical columns encompass categorical, ordinal, binary and textual types.  When we use categorical columns there is a possible set of values for example Credit Rating or Gender.
* **CONTINUOUS_COLUMNS**: Continuous data encompasses numerical and interval types

# How the process works
The original tensorflow python code was modified to be more generic and provide the ability to not only create the model but also use the model after it has been created.  When creating the model, the columns, label, categorical columns, continuous columns, crossed columns and bucketized columns are passed into the python code via a JSON object as opposed to be hard coded in the model.  To do this, we call a stored procedure which will query the tables MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS and dynamically create a JSON object containing the data required by the python code.  This prevents the data from being hard coded and allows modelers to be able to quickly generate new models by adding entries to a database table.


# Splice Machine Tables
In Splice Machine we created several tables to store the model and feature definitions.  This section describes the purpose of each table.

## Table: MODEL
The model table contains the high level definition of a model and the MODEL_ID field is a key into other tables which define the data to include in the model creation.  The intent is that overtime you may have multiple models for the same dataset.

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

## Table: MODEL_INPUTS

This table will have the parameters that you want to send to the tensorflow model generation process.  For one model you will have multiple entries.
* **MODEL_INPUT_ID**: A unique identifier for the model input record
* **MODEL_ID**: Reference to the model id
* **TRAINING_TABLE**: The table containing the training data
* **TEST_TABLE**: The table containing the test data
* **MODEL_OUTPUT_PATH**: Name of the folder to place the generated model.  
* **MODEL_TYPE**: The type of model. Valid model types: {'wide', 'deep', 'wide_n_deep'}
* **TRAINING_STEPS**: Number of training steps
* **HASH_BUCKET_SIZE**: Hash Buket Size - An example of a valid value is 100
* **DIMENSIONS**: The higher the dimension of the embedding is, the more degrees of freedom the model will have to learn the representations of the features An example of a valid value is 8
* **HIDDEN_UNITS**: List of hidden units per DNN layer. An example of a valid value is '100, 50'
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
|MODEL_INPUT_ID|MODEL_ID|TRAINING_TABLE|TEST_TABLE|MODEL_OUTPUT_PATH|MODEL_TYPE|TRAINING_STEPS|HASH_BUCKET_SIZE|DIMENSIONS|HIDDEN_UNITS|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
|2|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-1|wide_n_deep|1000|1000|8|"100, 50"|
|3|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-2|wide_n_deep|2000|1000|8|"100, 50"|
|4|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-3|wide_n_deep|3000|1000|8|"100, 50"|
|5|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-1-wide|wide|1000|1000|8|"100, 50"|
|6|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-2-wide|wide|2000|1000|8|"100, 50"|
|7|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-3-wide|wide|3000|1000|8|"100, 50"|
|8|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-1-deep|deep|1000|1000|8|"100, 50"|
|9|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-2-deep|deep|2000|1000|8|"100, 50"|
|10|1|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|test-3-deep|deep|3000|1000|8|"100, 50"|
|11|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-1000-both|wide_n_deep|1000|1000|8|"100, 50"|
|12|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-2000-both|wide_n_deep|2000|1000|8|"100, 50"|
|13|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-3000-both|wide_n_deep|3000|1000|8|"100, 50"|
|14|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-1000-wide|wide|1000|1000|8|"100, 50"|
|15|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-2000-wide|wide|2000|1000|8|"100, 50"|
|16|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-3000-wide|wide|3000|1000|8|"100, 50"|
|17|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-1000-deep|deep|1000|1000|8|"100, 50"|
|18|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-2000-deep|deep|2000|1000|8|"100, 50"|
|19|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-3000-deep|deep|3000|1000|8|"100, 50"|
|20|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-2000-both|wide_n_deep|2000|1000|8|"100, 50"|
|21|2|TRAINING.TRAINING_DATA|TRAINING.TESTING_DATA|test-3000-both|wide_n_deep|3000|1000|8|"100, 50"|

## Table: MODEL_INPUTS

This table will have the results of creating the model in tensorflow.  For one model you will have multiple entries.
* **MODEL_RESULTS_ID**: A unique identifier for the model results record
* **MODEL_ID**: Reference to the model id
* **MODEL_INPUT_ID**: Reference to the model input id used when creating the model
* **START_TIME**: The time the model building process began
* **END_TIME**: The time the model building process completed
* **STATUS**: The status of the model creation process
* **ACCURACY**: 
* **BASELINE_TARGET_MEAN**: The mean of the class labels in your data
* **THRESHOLD_MEAN**: Examples for which the prediction is above the threshold of 0.5 are considered positive examples whereas those below 0.5 are negative
* **AUC**: Area Under Curve
* **GLOBAL_STEP**: 
* **ACTUAL_TARGET_MEAN**: 
* **PREDICTION_MEAN**: 
* **LOSS**: 
* **PRECISION_POSITIVE_THRESHOLD_MEAN**: 
* **RECALL_POSITIVE_THRESHOLD_MEAN**: 
* **TRAINING_DATA_RESULTS**: The results of validating the training data
* **TEST_DATA_RESULTS**: The results of validating the test data
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data

|MODEL_RESULTS_ID|MODEL_ID|MODEL_INPUT_ID|START_TIME|END_TIME|STATUS|ACCURACY|BASELINE_TARGET_MEAN|THRESHOLD_MEAN|AUC|GLOBAL_STEP|ACTUAL_TARGET_MEAN|PREDICTION_MEAN|LOSS|PRECISION_POSITIVE_THRESHOLD_MEAN|RECALL_POSITIVE_THRESHOLD_MEAN|TRAINING_DATA_RESULTS|TEST_DATA_RESULTS|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
|3|1|2|2017-03-15 13:50:13.388|2017-03-15 13:52:34.297|Success|0.851597011089325|0.23624099791049957|0.851597011089325|0.8810870051383972|2000|0.23624099791049957|0.21699200570583344|0.38453999161720276|0.746891975402832|0.5624020099639893|"[Counter({0: 23279| 1: 5082})]"|"[Counter({0: 13384| 1: 2896})]"|
|4|1|2|2017-03-15 14:06:21.896|2017-03-15 14:08:32.588|Success|0.8525180220603943|0.23624099791049957|0.8525180220603943|0.9046810269355774|3000|0.23624099791049957|0.2504279911518097|0.3783150017261505|0.7087550163269043|0.6378059983253479|"[Counter({0: 22277| 1: 6084})]"|"[Counter({0: 12819| 1: 3461})]"|
|5|1|3|2017-03-15 14:08:41.086|2017-03-15 14:12:21.804|Success|0.8466830253601074|0.23624099791049957|0.8466830253601074|0.8985949754714966|2000|0.23624099791049957|0.26568499207496643|0.48438701033592224|0.6834239959716797|0.6539260149002075|"[Counter({0: 21905| 1: 6456})]"|"[Counter({0: 12600| 1: 3680})]"|
|6|1|4|2017-03-15 14:12:30.29|2017-03-15 14:17:35.481|Success|0.8552209734916687|0.23624099791049957|0.8552209734916687|0.909250020980835|3000|0.23624099791049957|0.24973100423812866|0.32095199823379517|0.7195519804954529|0.6344249844551086|"[Counter({0: 22395| 1: 5966})]"|"[Counter({0: 12889| 1: 3391})]"|

# Code Structure

## Java
com.splicemachine.tutorial.tensorflow.ModelDefinition.java - Contains the code for the stored procedures.  All stored procedures builds a JSON object from the data in the tables and passes it into the python code.

## Python Scripts
These are the python scripts used when building models.  They are located under /resources/python

* **Tensor-Demo.py**: This is the python script that contains the logic for creating the model and predicting the outcome
* **Tensor-Demo.ipynb**: This is the same as the 'Tensor-Demo.py' except it is the notebook version of the script.

## Splice Machine Objects

### Setup Files
The ddl script for creating the tables and stored procedures can be found under /resources/splice/setup.

* **/ddl/create-tables.sql**: Creates the tables MODEL, MODEL_FEATURES, MODEL_FEATURE_CROSS, MODEL_INPUTS and MODEL_CREATION_RESULTS
* **/ddl/create-procedures.sql**: Creates the stored procedures 

#### Stored Procedure: CREATE_MODEL_FOR_ALL_INPUT_SETS
Used for creating the models for all the defined input sets
* **scriptPathAndName**: Full path and name to the python script
* **modelOutputPath**: Full path to the output directory for the model
* **modelId**: The id of the model to generate.  Corresponds to the MODEL_ID in the MODEL table

#### Stored Procedure: CREATE_MODEL_FOR_INPUT_SET
Used for creating the models for all the defined input sets
* **scriptPathAndName**: Full path and name to the python script
* **modelOutputPath**: Full path to the output directory for the model
* **modelInputId**: The id of the model input set that you want to generate.  Corresponds to the MODEL_INPUT_ID in the MODEL_INPUTS table

#### Stored Procedure: CREATE_MODEL
Used for creating a model
* **scriptPathAndName**: Full path and name to the python script
* **type**: The type of model. Valid model types: {'wide', 'deep', 'wide_n_deep'}
* **modelName**: Name of the model that is being created, it maps to an entry in the MODEL table and the column NAME
* **trainingDataTable**: The table containing the training data
* **testDataTable**: The table containing the test data
* **modelOutputDirectory**: Base directory for output models
* **trainingSteps**: Number of training steps for tensorflow model creation process
* **hashBucketSize**: Bucket size for tensorflow model creation process
* **dimension**: Dimensions for tensorflow model creation process
* **hiddenUnits**: List of hidden units per DNN layer. An example of a valid value is '100, 50'
* **modelId**: The model id.  If the modelId is not known set it to -1 and pass n the modelName
* **modelInputId**: The model input id, set to -1 if not known


#### Stored Procedure: PREDICT_MODEL
Used for predicting the outcome of a particular record
* **scriptPathAndName**: Full path and name to the python script
* **modelOutputDirectory**: Base directory for output models
* **modelId**: The model id.  If the modelId is not known set it to -1 and pass n the modelName
* **modelInputId**: The model input id, set to -1 if not known
* **sourceTable**: The table containing the live data
* **recordId**: ID of the record to perform the prediction on
* **comparisonColumn**: The column that is used for the binary comparison
* **criteria**: The criteria that the comparison column will evaluated against

### Examples
The examples folder has examples for dynamically creating models using different datasets.

#### Census Example
The folder /resources/examples/census_example contains the files needed to setup the census data.  The folders are as follows:

* **/data**: Contains the files for the training, test and live data sets.
* **/ddl**: Contains the scripts for creating the training, test and live tables as well as the script for populating the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS tables with data
* **/python**: This folder contains examples of running the python script outside of the stored procedure.
* **/queries**: This folder contains the sql statements to call the stored procedure to create the model and then use the model to predict the outcome.

An overview of the Census Example and the files can be found in the README.md file under the /resources/examples/census_example folder.



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

# How to Setup a New Model
Here are the high level steps for creating your own model

* Create the splice machine table definitions for your training, test and live data sets.  Make sure the definition of the live table has an ID column that uniquely identifies the row for use in the predict stored procedure.
* Import the data into each of the three tables above
* Insert a record to the MODEL table with the name of your model and a unique identifier, for example:  insert into MODEL (MODEL_ID,NAME,DESCRIPTION) values (2,'INSURANCE','Predict whether a customer has a caravan insurance policy');
* Review the columns in your dataset and identify the column that is the label, identify if the column is CONTINUOUS or CATEGORICAL.  If the column is a numeric column, please mark it as CONTINUOUS.  
* Create the insert statements MODEL_FEATURES table and if applicable the MODEL_FEATURE_CROSS table.
* Create the insert statements for the entries in the MODEL_INPUTS table.
* Call the stored procedure to create your model
* Call the stored procedure to predict an outcome.

Look at the /resources/examples/census_example and the /resources/examples/insurance_example for examples of how this was done.



