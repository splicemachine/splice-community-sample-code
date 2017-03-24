# Overview
This is a framework where we can create multiple machine learning models using different machine learning platforms (Tensorflow, Spark MLib, R, etc) and algorithms where the complexity of machine learning is hidden from from the end user.  We are in the beginning phase and have developed the process for using a classifier for TensorFlow Linear and DNN joined training models.  At a high level, you use SQL statements (for now) to specify the database table that contains the training data set, the test data set and some other parameters and then you call a stored procedure which will generate the model for you.  

The first machine learning setup was done for TensorFlow.  The tensorflow (www.tensorflow.org) framework is an open source software library for machine learning across a range of tasks.  It is a framework for building Deep Learning Neural Networks.  We took the 'TensorFlow Wide & Deep Learning Tutorial' (https://www.tensorflow.org/versions/r0.11/tutorials/wide_and_deep/index.html) which trains a model to predict the probability that an individual has an annual income over 50,000 dollars using Census Income data set and created a method to generically create a model using any data set.


## Dynamically Creating Variables
The original code provided by TensorFlow (https://github.com/tensorflow/tensorflow/blob/master/tensorflow/examples/learn/wide_n_deep_tutorial.py) created a model where the variables COLUMNS, LABEL_COLUMN, CATEGORICAL_COLUMNS and CONTINIOUS_COLUMNS were hard coded.  Before we explain how the process was generized let's explain the purpose for each of the variables:

* **COLUMNS**: This is a list of all of the columns / features in the data set.  For example age or marital status.
* **LABEL_COLUMN**: This is the name of the column in the data set that you are trying to predict.  In this example it is a flag that indicates the probability of having an annual income of over 50,000.
* **CATEGORICAL_COLUMNS**: Categorical columns encompass categorical, ordinal, binary and textual types.  When we use categorical columns there is a possible set of values for example Credit Rating or Gender.
* **CONTINUOUS_COLUMNS**: Continuous data encompasses numerical and interval types

# How the process works
The original tensorflow python code was modified to be more generic and provide the ability to not only create the model but also use the model after it has been created.  When creating the model, the columns, label, categorical columns, continuous columns, crossed columns and bucketized columns are passed into the python code via a JSON object as opposed to be hard coded in the model.  To do this, we call a stored procedure which will query the tables MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS and dynamically create a JSON object containing the data required by the python code.  This prevents the data from being hard coded and allows modelers to be able to quickly generate new models by adding entries to a database table.


# Splice Machine Tables
In Splice Machine we created several tables to store the project, machine learning, data set and feature definitions, and model creation results.  This section describes the purpose of each table.

## Table: MACHINE_LEARNING_PROJECT
The table MACHINE_LEARNING_PROJECT contains the high level definition of a machine learning project.  The intent is that overtime you may have multiple models for the same data set.

* **PROJECT_ID**: A unique identifier for the model input record
* **NAME**: The name of the project.  
* **DESCRIPTION**: The description of the project
* **STATUS**: The status of the project either A for active or I for inactive
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|PROJECT_ID|NAME|DESCRIPTION|STATUS|
|--------|--------|--------|--------|
|4|CENSUS|Predict whether income is greater than 50,000|A|
|5|INSURANCE|Predict whether a customer has a caravan insurance policy|A|
|6|IRIS|Predict the class of an iris based on the petal dimensions and the sepal dimensions|A|


## Table: DATASET
The table DATASET contains the data set definition.  

* **DATASET_ID**: A unique identifier for the model input record
* **PROJECT_ID**: The project associated with the data set.  It links to the MACHINE_LEARNING_PROJECT table.
* **TYPE**: The type of data set: 'T' for table and 'Q' for query.  If the type is 'T' then the fields TRAINING_TABLE and TEST_TABLE should be populated.  Otherwise the TRAINING_QUERY and TEST_QUERY columns should be populated.  At the present time only 'T' is valid.
* **TRAINING_TABLE**: The full schema and table name for the database table containing the training data set
* **TEST_TABLE**: The full schema and table name for the ddatabaes table containing the test data set
* **TRAINING_QUERY**: The SQL statement which builds the training data set
* **TEST_QUERY**: The SQL statement which builds the test data set
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|DATASET_ID|PROJECT_ID|TYPE|TRAINING_TABLE|TEST_TABLE|TRAINING_QUERY|TEST_QUERY|
|--------|--------|--------|--------|--------|--------|--------|
|4|4|T|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|null|null|
|5|5|T|INSURANCE.TRAINING_DATA|INSURANCE.TESTING_DATA|null|null|
|6|6|T|IRIS.TRAINING_DATA|IRIS.TEST_DATA|null|null|


## Table: DATASET_FEATURE_DEFINITION
The table DATASET_FEATURE_DEFINITION represents a single feature / attribute in your data set. This table stores all the features for your dataset with the properties needed to generically generate the machine learning input.

* **DATASET_FEATURE_ID**: A unique identifier for the model input record
* **DATASET_ID**: The data set associated with the data set feature.  It links to the DATASET table.
* **DATABASE_COLUMN_NAME**: The database column name
* **FEATURE_NAME**: Represents a single feature / attribute in your data such as color
* **FEATURE_TYPE**: The feature data type either CONTINUOUS or CATEGORICAL or NULL
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

|DATASET_FEATURE_ID|DATASET_ID|DATABASE_COLUMN_NAME|FEATURE_NAME|FEATURE_TYPE|FEATURE_KEYS|FEATURE_BUCKET_DATA_TYPE|FEATURE_BUCKETS|IS_LABEL|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|
|49|4|AGE|AGE|CONTINUOUS|null|INTEGER|18, 25, 30, 35, 40, 45, 50, 55, 60, 65|null|
|50|4|WORKCLASS|WORKCLASS|CATEGORICAL|null|null|null|null|
|51|4|FNLWGT|FNLWGT|null|null|null|null|null|
|52|4|EDUCATION|EDUCATION|CATEGORICAL|null|null|null|null|
|53|4|EDUCATION_NUM|EDUCATION_NUM|CONTINUOUS|null|null|null|null|
|54|4|MARITAL_STATUS|MARITAL_STATUS|CATEGORICAL|null|null|null|null|
|55|4|OCCUPATION|OCCUPATION|CATEGORICAL|null|null|null|null|
|56|4|RELATIONSHIP|RELATIONSHIP|CATEGORICAL|null|null|null|null|
|57|4|RACE|RACE|CATEGORICAL|Amer-Indian-Eskimo, Asian-Pac-Islander, Black, Other, White|null|null|null|
|58|4|GENDER|GENDER|CATEGORICAL|Female, Male|null|null|null|
|59|4|CAPITAL_GAIN|CAPITAL_GAIN|CONTINUOUS|null|null|null|null|
|60|4|CAPITAL_LOSS|CAPITAL_LOSS|CONTINUOUS|null|null|null|null|
|61|4|HOURS_PER_WEEK|HOURS_PER_WEEK|CONTINUOUS|null|null|null|null|
|62|4|NATIVE_COUNTRY|NATIVE_COUNTRY|CATEGORICAL|null|null|null|null|
|63|4|INCOME_BRACKET|INCOME_BRACKET|null|null|null|null|null|
|64|4|LABEL|LABEL|null|null|null|null|true|
|65|5|ID|ID|CONTINUOUS|null|null|null|null|
|66|5|CUSTOMER_SUBTYPE|CUSTOMER_SUBTYPE|CONTINUOUS|null|null|null|null|
|67|5|NUM_HOUSES|NUM_HOUSES|CONTINUOUS|null|null|null|null|
|68|5|AVG_SIZE_HOUSEHOLD|AVG_SIZE_HOUSEHOLD|CONTINUOUS|null|null|null|null|

## Table: DATASET_FEATURE_CROSS
The table DATASET_FEATURE_CROSS is used when you need to combine a combination of features together in order for it to have more meaning.  The value of this feature for a given record is just the concatenation of the values of the two source features

* **DATASET_FEATURE_CROSS_ID**: A unique identifier for the model input record
* **DATASET_ID**: The data set associated with the data set feature cross.  It links to the DATASET table.
* **FEATURE_CROSS_NAME**: The name of the feature cross.  This will be repeated for multiple records
* **FEATURE_NAME**: The name of the source feature from the MODEL_FEATURE
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record


### Sample Data
Here is some sample data for the non audit columns

|DATASET_FEATURE_CROSS_ID|DATASET_ID|FEATURE_CROSS_NAME|FEATURE_NAME|
|--------|--------|--------|--------|
|1|4|EDUCATION_OCCUPATION|EDUCATION|
|2|4|EDUCATION_OCCUPATION|OCCUPATION|
|3|4|AGE_EDUCATION_OCCUPATION|AGE_BUCKETS|
|4|4|AGE_EDUCATION_OCCUPATION|EDUCATION|
|5|4|AGE_EDUCATION_OCCUPATION|OCCUPATION|
|6|4|COUNTRY_OCCUPATION|NATIVE_COUNTRY|
|7|4|COUNTRY_OCCUPATION|OCCUPATION|


## Table: DATASET_COLUMN_STATISTICS
The table DATASET_COLUMN_STATISTICS contains the statistics for the training data set.  This would be used for an advanced user to look at the details about the dataset.  The expectation is that this table would be populated by a call to stored procedure.

* **DATASET_COLUMN_STATISTICS_ID**: A unique identifier for the model input record
* **DATASET_ID**: The data set associated with the data set statistics.  It links to the DATASET table.
* **DATABASE_COLUMN_NAME**: The database column name
* **DATAT_TYPE**: The data type of the column
* **COUNT**
* **CARDINALITY**
* **MISSING**
* **MEAN**
* **STD_DEVIATION**
* **MEDIAN**
* **MIN_VALUE**
* **MAX_VALUE**
* **PERCENTITLE_25_MEAN**
* **PERCENTITLE_25_MEDIAN**
* **PERCENTITLE_75_MEAN**
* **PERCENTITLE_75_MEDIAN**
* **MODE**
* **MODE_FREQUENCY**
* **MODE_PERCENT**
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|DATASET_COLUMN_STATISTICS_ID|DATASET_ID|DATABASE_COLUMN_NAME|DATA_TYPE|COUNT|CARDINALITY|MISSING|MEAN|STD_DEVIATION|MEDIAN|MIN_VALUE|MAX_VALUE|PERCENTITLE_25_MEAN|PERCENTITLE_25_MEDIAN|PERCENTITLE_75_MEAN|PERCENTITLE_75_MEDIAN|MODE|MODE_FREQUENCY|MODE_PERCENT|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|


## Table: MACHINE_LEARNING_PROCESS
The table MACHINE_LEARNING_PROCESS defines the available machine learning processes

* **MACHINE_LEARNING_ID**: A unique identifier for the model input record
* **NAME**: The name of the machine learning process
* **DESCRIPTION**: A description for the process
* **PLATFORM**: The platform for the machine learning process (ie tensorflow, R, H2O)
* **ALGORITHM**: The name of the machine learning algorithm
* **ALGORITHM_TYPE**: The algorithm type 'S' for supervised learning, 'R' for reinforcement learning or 'U' for unsupervised learning
* **STATUS**: The status of the machine learning process either A for active or I for inactive
* **INTEGRATION_TYPE**: The type of integration 'HTTP', 'API', etc
* **URL**: TBD - Need to see if the stored procedure would be enough or if we can to allow a non sp process??
* **CREATION_PROCESS**: TBD - was thinking it should be the stored procedure for executing the process???
* **PREDICT_PROCESS**: TBD - was thinking it should be the stored procedure for executing the process???
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MACHINE_LEARNING_ID|NAME|DESCRIPTION|PLATFORM|ALGORITHM|ALGORITHM_TYPE|STATUS|INTEGRATION_TYPE|URL|CREATION_PROCESS|PREDICT_PROCESS|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
|1|Wide-n-Deep|Jointly train a wide linear model and a deep feed-forward neural network|TENSORFLOW|DNNLinearCombinedClassifier|S|A|HTTP|/tensorflow/widendeep.py|null|null|


## Table: MACHINE_LEARNING_PARAMETERS
The table MACHINE_LEARNING_PARAMETERS contains the parameters that need to be passed into the model creation process.  This table contains the default values as well. 

* **MACHINE_LEARNING_PARAMETER_ID**: A unique identifier for the model input record
* **MACHINE_LEARNING_ID**: The machine learning process.  Links to the MACHINE_LEARNING_PROCESS table.
* **PARAMETER_NAME**: The name of the parameter as it would be referenced in code
* **DISPLAY_NAME**: The display name of the parameter for the user interface
* **DEFAULT_VALUE**: The default value for the parameter in case it is not provided in the group details
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MACHINE_LEARNING_PARAMETER_ID|MACHINE_LEARNING_ID|PARAMETER_NAME|DISPLAY_NAME|DEFAULT_VALUE|
|--------|--------|--------|--------|--------|
|1|1|TRAINING_STEPS|Training Steps|5000|
|2|1|HASH_BUCKET_SIZE|Hash Bucket Size|1000|
|3|1|DIMENSIONS|Dimensions|8|
|4|1|HIDDEN_UNITS|Hidden Units|100, 50|
|5|1|MODEL_TYPE|Model Type|wide_n_deep|


## Table: MODEL_CREATION_GROUP
The table MODEL_CREATION_GROUP allows you to group a differentWe want to be able to group a set of ML processes to test a particular dataset.  This grouping is independent of an actual run or dataset

* **MODEL_CREATION_GROUP_ID**: A unique identifier for the model input record
* **NAME**: The name of the model creation group
* **STATUS**: The status of the model creation group either A for active or I for inactive
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_CREATION_GROUP_ID|NAME|STATUS|
|--------|--------|--------|
|1|Default Tensorflow Group|A|



## Table: MODEL_CREATION_GROUP_DETAILS
The table MODEL_CREATION_GROUP_DETAILS associates the group of input parameters to a particular model creation group

* **MODEL_CREATION_GROUP_DETAIL_ID**: A unique identifier for the model input record
* **MODEL_CREATION_GROUP_ID**: The model creation group that this group detail is associated with
* **MACHINE_LEARNING_ID**: The machine learning model associated with the group details
* **STATUS**: The status of the model creation group detail either A for active or I for inactive
* **IS_DEFAULT**:  Indicates if this group is the default for a machine learning process.
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_CREATION_GROUP_DETAIL_ID|MODEL_CREATION_GROUP_ID|MACHINE_LEARNING_ID|STATUS|IS_DEFAULT|
|--------|--------|--------|--------|--------|
|1|1|1|A|true|
|2|1|1|A|false|
|3|1|1|A|false|


## Table: MODEL_CREATION_PARAMETER_VALUES
The table MODEL_CREATION_PARAMETER_VALUES you can choose to override the default values for the parameters in a model.  If an entry is not included, the default value from the MACHINE_LEARNING_PARAMETERS table will be used

* **MODEL_CREATION_PARAMETER_VALUE_ID**: A unique identifier for the model input record
* **MODEL_CREATION_GROUP_DETAIL_ID**: The model creation group detail this entry is associated with.  Links to the MODEL_CREATION_GROUP_DETAILS table
* **MACHINE_LEARNING_PARAMETER_ID**: The id of the machine learning parameter that this overrides
* **PARAMETER_VALUE**: The value for the parameter
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_CREATION_PARAMETER_VALUE_ID|MODEL_CREATION_GROUP_DETAIL_ID|MACHINE_LEARNING_PARAMETER_ID|PARAMETER_VALUE|
|--------|--------|--------|--------|
|1|1|1|3000|
|2|1|2|1000|
|3|2|1|3000|
|4|2|3|6|
|5|3|3|6|


## Table: MODEL_CREATION_RUN
The table MODEL_CREATION_RUN contains the actual execution of the group and provides the flexibility to use a different table / query than the original data set definition

* **MODEL_CREATION_RUN_ID**: A unique identifier for the model input record
* **MODEL_CREATION_GROUP_ID**: The model creation group for this run.  Either this column will have a value or the MODEL_CREATION_GROUP_DETAIL_ID will have a value.
* **MODEL_CREATION_GROUP_DETAIL_ID**: The model creation group detail for this run.
* **TYPE**: The type of data set: 'T' for table, 'Q' for query or 'D' for data set.  If the type is 'T' then the fields TRAINING_TABLE and TEST_TABLE should be populated.  If the type is 'Q' the TRAINING_QUERY and TEST_QUERY columns should be populated.  If the type is 'D' then the data set details will be used. 
* **DATASET_ID**: The data set id associated with this run
* **TRAINING_TABLE**: The override of the training table from the data set definition.
* **TEST_TABLE**: The override of the test table from the data set definition.
* **TRAINING_QUERY**: The override of the training query from the data set definition.
* **TEST_QUERY**: The override of the test query from the data set definition
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record



### Sample Data
Here is some sample data for the non audit columns

|MODEL_CREATION_RUN_ID|MODEL_CREATION_GROUP_ID|TYPE|DATASET_ID|TRAINING_TABLE|TEST_TABLE|TRAINING_QUERY|TEST_QUERY|
|--------|--------|--------|--------|--------|--------|--------|--------|


## Table: MODEL_CREATION_RESULTS
The table MODEL_CREATION_RESULTS contains the results of creating the model.

* **MODEL_RESULTS_ID**: A unique identifier for the model input record
* **MODEL_CREATION_RUN_ID**: Reference to the Model Creation Run.  Links to the MODEL_CREATION_RUN table
* **START_TIME**: The time the model building process began
* **END_TIME**: The time the model building process completed
* **STATUS**: The status of the model creation process
* **ACCURACY**
* **BASELINE_TARGET_MEAN**: The mean of the class labels in your data
* **THRESHOLD_MEAN**: Examples for which the prediction is above the threshold of 0.5 are considered positive examples whereas those below 0.5 are negative
* **AUC**: Area Under Curve
* **GLOBAL_STEP**
* **ACTUAL_TARGET_MEAN**
* **PREDICTION_MEAN**
* **LOSS**: Represents how far off our model is from our desired outcome
* **PRECISION_POSITIVE_THRESHOLD_MEAN**
* **RECALL_POSITIVE_THRESHOLD_MEAN**
* **TRAINING_DATA_RESULTS**: The results of validating the training data
* **TEST_DATA_RESULTS**: The results of validating the test data
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record


### Sample Data
Here is some sample data for the non audit columns

|MODEL_RESULTS_ID|MODEL_CREATION_RUN_ID|START_TIME|END_TIME|STATUS|ACCURACY|BASELINE_TARGET_MEAN|THRESHOLD_MEAN|AUC|GLOBAL_STEP|ACTUAL_TARGET_MEAN|PREDICTION_MEAN|LOSS|PRECISION_POSITIVE_THRESHOLD_MEAN|RECALL_POSITIVE_THRESHOLD_MEAN|TRAINING_DATA_RESULTS|TEST_DATA_RESULTS|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|


# Code Structure

## Java
* **com.splicemachine.tutorial.machinelearning.MachineLearningSetup.java**: Contains the code for creating the initial setup for a project  
* **com.splicemachine.tutorial.tensorflow.ModelDefinition.java**:  Contains the code for the stored procedures to call the model creation process and the prediction process

## Python Scripts
These are the python scripts used when building models.  They are located under /resources/python

* **Tensor-Demo.py**: This is the python script that contains the logic for creating the model and predicting the outcome

## Splice Machine Objects

### Setup Files
The ddl script for creating the tables and stored procedures can be found under /resources/splice/setup.

* **/ddl/create-tables.sql**: Creates the tables MODEL, MODEL_FEATURES, MODEL_FEATURE_CROSS, MODEL_INPUTS and MODEL_CREATION_RESULTS
* **/ddl/create-procedures.sql**: Creates the stored procedures 

The data script for populating the MACHINE_LEARNING_PROCESS, MACHINE_LEARNING_PARAMETERS, MODEL_CREATION_GROUP, MODEL_CREATION_GROUP_DETAILS and MODEL_CREATION_PARAMETER_VALUES tables.

* **/data/create-data.sql**: Inserts the initial data for running the machine learning process

#### Stored Procedure: CREATE_INITIAL_PROJECT_STRUCTURE
Used for the initial setup and population of a machine learning project.  Inserts records into the MACHINE_LEARNING_PROJECT, DATASET and DATASET_FEATURE_DEFINITION tables
* **projectName**: The name of the project.
* **projectDescription**: The description for the project
* **trainingTable**: The full name (SCHEMA.TABLENAME) of the table that contains the training data
* **testTable**: The full name (SCHEMA.TABLENAME) of the table that contains the test data
* **labelColumn**: The column that contains the label


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
* Run the stored procedure CREATE_INITIAL_PROJECT_STRUCTURE to populate the initial tables for a new data set
* Review the entries in the DATASET_FEATURE_DEFINITION table.
* Add any entries to the DATASET_FEATURE_CROSS
* Call the stored procedure to create your model
* Call the stored procedure to predict an outcome.

Look at the examples under /resources/examples for more details.



