# Splice Machine Tables
In Splice Machine we created several tables to store the project, machine learning, data set and feature definitions, and model creation results.  Please note that several columns / tables are not currently being used, but have been created for future use.  This document describes the purpose of each table.

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
|1|CENSUS|Predict whether income is greater than 50,000|A|
|2|INSURANCE|Predict whether a customer has a caravan insurance policy|A|
|3|IRIS|Predict the class of an iris based on the petal dimensions and the sepal dimensions|A|


## Table: DATASET
The table DATASET contains the name of the training table and the test table that will be used as inputs for the model creation process.

* **DATASET_ID**: A unique identifier for the record
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
|1|1|T|CENSUS.TRAINING_DATA|CENSUS.TESTING_DATA|null|null|
|2|2|T|INSURANCE.TRAINING_DATA|INSURANCE.TESTING_DATA|null|null|
|3|3|T|IRIS.TRAINING_DATA|IRIS.TEST_DATA|null|null|



## Table: DATASET_FEATURE_DEFINITION
The table DATASET_FEATURE_DEFINITION represents a single feature / attribute in your data set. This table stores all the features for your dataset with the properties needed to generically generate the machine learning input.

* **DATASET_FEATURE_ID**: A unique identifier for the record
* **DATASET_ID**: The data set associated with the data set feature.  It links to the DATASET table
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
|1|1|AGE|AGE|CONTINUOUS|null|INTEGER|18, 25, 30, 35, 40, 45, 50, 55, 60, 65|null|
|2|1|WORKCLASS|WORKCLASS|CATEGORICAL|null|null|null|null|
|3|1|FNLWGT|FNLWGT|null|null|null|null|null|
|4|1|EDUCATION|EDUCATION|CATEGORICAL|null|null|null|null|
|5|1|EDUCATION_NUM|EDUCATION_NUM|CONTINUOUS|null|null|null|null|
|6|1|MARITAL_STATUS|MARITAL_STATUS|CATEGORICAL|null|null|null|null|
|7|1|OCCUPATION|OCCUPATION|CATEGORICAL|null|null|null|null|
|8|1|RELATIONSHIP|RELATIONSHIP|CATEGORICAL|null|null|null|null|
|9|1|RACE|RACE|CATEGORICAL|Amer-Indian-Eskimo, Asian-Pac-Islander, Black, Other, White|null|null|null|
|10|1|GENDER|GENDER|CATEGORICAL|Female, Male|null|null|null|
|11|1|CAPITAL_GAIN|CAPITAL_GAIN|CONTINUOUS|null|null|null|null|
|12|1|CAPITAL_LOSS|CAPITAL_LOSS|CONTINUOUS|null|null|null|null|
|13|1|HOURS_PER_WEEK|HOURS_PER_WEEK|CONTINUOUS|null|null|null|null|
|14|1|NATIVE_COUNTRY|NATIVE_COUNTRY|CATEGORICAL|null|null|null|null|
|15|1|INCOME_BRACKET|INCOME_BRACKET|null|null|null|null|null|
|16|1|LABEL|LABEL|null|null|null|null|true|

## Table: DATASET_FEATURE_CROSS
The table DATASET_FEATURE_CROSS is used when you need to combine features together in order for it to have more meaning.  The value of this feature for a given record is just the concatenation of the values of the two source features

* **DATASET_FEATURE_CROSS_ID**: A unique identifier for the record
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
|1|1|EDUCATION_OCCUPATION|EDUCATION|
|2|1|EDUCATION_OCCUPATION|OCCUPATION|
|3|1|AGE_EDUCATION_OCCUPATION|AGE_BUCKETS|
|4|1|AGE_EDUCATION_OCCUPATION|EDUCATION|
|5|1|AGE_EDUCATION_OCCUPATION|OCCUPATION|
|6|1|COUNTRY_OCCUPATION|NATIVE_COUNTRY|
|7|1|COUNTRY_OCCUPATION|OCCUPATION|


## Table: DATASET_COLUMN_STATISTICS
The table DATASET_COLUMN_STATISTICS contains the statistics for the training data set.  This would be used for an advanced user to look at the details about the training data set.  The expectation is that this table would be populated by a call to stored procedure.  As of this time, there is no process to populate this table.

* **DATASET_COLUMN_STATISTICS_ID**: A unique identifier for the record
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


## Table: MACHINE_LEARNING_METHOD
The table MACHINE_LEARNING_METHOD defines the available machine learning processes and provides the details about how to instantiate the machine learning process

* **MACHINE_LEARNING_ID**: A unique identifier for the record
* **NAME**: The name of the machine learning process
* **DESCRIPTION**: A description for the process
* **PLATFORM**: The platform for the machine learning process (ie tensorflow, R, H2O)
* **ALGORITHM**: The name of the machine learning algorithm
* **ALGORITHM_TYPE**: The algorithm type 'S' for supervised learning, 'R' for reinforcement learning or 'U' for unsupervised learning
* **STATUS**: The status of the machine learning process either A for active or I for inactive
* **INTEGRATION_TYPE**: The type of integration 'HTTP', 'API', etc
* **CREATION_PROCESS**: In the case of an integration type of HTTP, this is the URL for creating a model
* **PREDICT_PROCESS**: In the case of an integration type of HTTP, this is the URL for predicting a model
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MACHINE_LEARNING_ID|NAME|DESCRIPTION|PLATFORM|ALGORITHM|ALGORITHM_TYPE|STATUS|INTEGRATION_TYPE|URL|CREATION_PROCESS|PREDICT_PROCESS|
|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
|1|Wide-n-Deep|Jointly train a wide linear model and a deep feed-forward neural network|TENSORFLOW|DNNLinearCombinedClassifier|S|A|HTTP|null|http://localhost:8000/train_and_eval|http://localhost:8000/predict_outcome|


## Table: MACHINE_LEARNING_PARAMETERS
The table MACHINE_LEARNING_PARAMETERS contains the parameters that need to be passed into the model creation process.  This table contains the default values as well. 

* **MACHINE_LEARNING_PARAMETER_ID**: A unique identifier for the model input record
* **MACHINE_LEARNING_ID**: The machine learning process.  Links to the MACHINE_LEARNING_METHOD table
* **PARAMETER_NAME**: The name of the parameter as it would be referenced in code
* **DISPLAY_NAME**: The display name of the parameter for the user interface
* **DEFAULT_VALUE**: The default value for the parameter in case it is not provided in the group details
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns.  In the sample below you have a total of 5 parameters that are required when calling the Machine Learning Method id 1 (Wide-n-Deep|Jointly train a wide) - training_steps, hash_bucket_size, dimensions, dnn_hidden_units and model_type.  Each Machine Learning process may require different parameters.

|MACHINE_LEARNING_PARAMETER_ID|MACHINE_LEARNING_ID|PARAMETER_NAME|DISPLAY_NAME|DEFAULT_VALUE|
|--------|--------|--------|--------|--------|
|1|1|training_steps|Training Steps|5000|
|2|1|hash_bucket_size|Hash Bucket Size|1000|
|3|1|dimensions|Dimensions|8|
|4|1|dnn_hidden_units|Hidden Units|100, 50|
|5|1|model_type|Model Type|wide_n_deep|


## Table: DATASET_MACHINE_LEARNING_METHODS
The table DATASET_MACHINE_LEARNING_METHODS contains the association between the dataset and a particular machine learning method.  It is possible, down the line, that you may want to use different Algorithms for a particular dataset to determine which dataset provides the fastest and most accurate result.

* **DATASET_MACHINE_LEARNING_ID**: A unique identifier for the record
* **DATASET_ID**: The data set id.  Links to the DATASET table
* **MACHINE_LEARNING_ID**: The machine learning process.  Links to the MACHINE_LEARNING_METHOD table
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|DATASET_MACHINE_LEARNING_ID|DATASET_ID|MACHINE_LEARNING_ID|
|--------|--------|--------|
|1|1|1|
|2|1|2|
|3|1|3|

## Table: PARAMETER_SET
The table PARAMETER_SET stores the set of parameters that can be passed in to the machine learning method when creating the model.  The parameter set is a means of grouping different combinations of values for a particular machine learning process without requiring a set of parameters be defined for each data set.  This table is used in combination with the MACHINE_LEARNING_PARAMETERS and PARAMETER_SET_VALUES tables to derive the parameters that should be passed into the model creation and prediction process.

* **PARAMETER_SET_ID**: A unique identifier for the record
* **MACHINE_LEARNING_ID**: The machine learning process.  Links to the MACHINE_LEARNING_METHOD table. 
* **NAME**: A nice name for the parameter set
* **STATUS**: The status for the parameter set either A for active or I for inactive
* **IS_DEFAULT**: Indicates if the parameter set is the default parameter set for the machine learning method
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|PARAMETER_SET_ID|MACHINE_LEARNING_ID|NAME|STATUS|IS_DEFAULT|
|--------|--------|--------|--------|--------|
|1|1|Default Tensorflow Group|A|true|
|2|1|Step 3000 / Dimension 6|A|false|
|3|1|Step 2000 / Dimension 6|A|false|


## Table: PARAMETER_SET_VALUES
The table PARAMETER_SET_VALUES contains the values for a set of parameter set and machine learning parameters combination.  Using the sample data, if for example we take the PARAMETER_SET with PARAMETER_SET_ID equal to 2, we would ultimately send the following parameters and values to the machine learning process: training_steps=3000, hash_bucket_size=1000, dimensions=8, dnn_hidden_units="100,50" and model_type=wide_n_deep

* **PARAMETER_SET_VALUE_ID**: A unique identifier for the record
* **PARAMETER_SET_ID**: The parameter set id that this parameter is associated with
* **MACHINE_LEARNING_PARAMETER_ID**: The machine learning parameter id this value is associated with
* **MACHINE_LEARNING_ID**: The machine learning process.  Links to the MACHINE_LEARNING_METHOD table. 
* **PARAMETER_VALUE**: the value for the machine learning parameter
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|PARAMETER_SET_VALUE_ID|PARAMETER_SET_ID|MACHINE_LEARNING_PARAMETER_ID|MACHINE_LEARNING_ID|PARAMETER_VALUE|
|--------|--------|--------|--------|--------|
|1|2|1|1|3000|
|3|3|1|1|2000|
|4|3|3|1|6|


## Table: MODEL_CREATION_GROUP
The table MODEL_CREATION_GROUP allows you to create groups of PARAMETER_SETs to run at one time in order to determine the optimal parameter set combination.  In essence you create a bucket of models to choose the best model for each problem.  This is also known as Cross-Validation Selection. This table is used in combination with the MODEL_CREATION_GROUP_DETAILS table.  

* **MODEL_CREATION_GROUP_ID**: A unique identifier for the record
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
|1|Tensorflow training step variations|A|


## Table: MODEL_CREATION_GROUP_DETAILS
The table MODEL_CREATION_GROUP_DETAILS associates the group of input parameters to a particular model creation group

* **MODEL_CREATION_GROUP_DETAIL_ID**: A unique identifier for the record
* **MODEL_CREATION_GROUP_ID**: The model creation group that this group detail is associated with
* **PARAMETER_SET_ID**: A link to the parameter set id for this particular group
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_CREATION_GROUP_DETAIL_ID|MODEL_CREATION_GROUP_ID|PARAMETER_SET_ID|
|--------|--------|--------|
|1|1|2|
|2|1|3|


## Table: MODEL_CREATION_RESULTS
The table MODEL_CREATION_RESULTS contains the result of the model creation process.  

* **MODEL_RESULTS_ID**: A unique identifier for the record
* **DATASET_ID**: The dataset id used to create the model
* **MACHINE_LEARNING_ID**: The machine learning method used when creating the model
* **PARAMETER_SET_ID**: The parameter set id associated with this run
* **MODEL_PATH**: The path to the model creation model files
* **START_TIME**: The time the model creation process started
* **END_TIME**: The time the model creation process completed
* **STATUS**: The status of the model creation process
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_RESULTS_ID|DATASET_ID|MACHINE_LEARNING_ID|PARAMETER_SET_ID|MODEL_PATH|START_TIME|END_TIME|STATUS|
|--------|--------|--------|--------|--------|--------|--------|--------|
|1|1|1|1|null|2017-03-27 17:22:49.24|2017-03-27 17:29:30.321|Success|


## Table: MODEL_CREATION_RESULTS_DETAILS
The table MODEL_CREATION_RESULTS_DETAILS contains the details of the returned response

* **MODEL_RESULTS_DETAIL_ID**: A unique identifier for the record
* **MODEL_RESULTS_ID**: A link to the model creation results table
* **FIELD**: The field returned by the create model process
* **FIELD_VALUE**: The value for the model creation field
* **CREATE_DATE**: Audit column that indicates the date the record was created 
* **CREATE_USER**: Audit column that indicates the user / system that created the record
* **UPDATE_DATE**: Audit column that indicates the date the record was last updated 
* **UPDATE_USER**: Audit column that indicates the user / system that last updated the record

### Sample Data
Here is some sample data for the non audit columns

|MODEL_RESULTS_DETAIL_ID|MODEL_RESULTS_ID|FIELD|FIELD_VALUE|
|--------|--------|--------|--------|
|1|1|loss|0.313589|
|2|1|precision/positive_threshold_0.500000_mean|0.748857|
|3|1|recall/positive_threshold_0.500000_mean|0.596204|
|4|1|success|true|
|5|1|accuracy/baseline_label_mean|0.236226|
|6|1|labels/actual_label_mean|0.236226|
|7|1|accuracy|0.85738|
|8|1|labels/prediction_mean|0.231706|
|9|1|accuracy/threshold_0.500000_mean|0.85738|
|10|1|auc|0.908026|
|11|1|global_step|5002|