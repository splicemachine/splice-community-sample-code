# Background
The goal of this project is to develop a framework where we can create multiple machine learning models using different machine learning platforms (Tensorflow, Spark MLib, R, etc) and algorithms where the complexity of machine learning is hidden from from the end user.  We've started with genericizing the process of creating a model using a supervised machine learning algorithm.  In supervised machine learning a machine learning algorithm is able to learn a model of the relationship between a set of descriptive features and the target feature.  

Before going through the code, it is helpful to have a high level understanding of what a simple machine learning project involves.

This process starts with a data set that has a known value for the target feature (the thing you are trying to predict).  That data set is broken out into two groups one for training the model and one for testing the model.

Next each descriptive feature (column of your data) is categorized.  Each column in the data can be one of the following: a descriptive feature, the target feature, or not applicable.  Any column that is identified as a descriptive feature would then further be broken out into that column is Categorical or Continuous.

Categorical columns can be any of the following:

* **Ordinal**: Values that allow ordering and but do not permit arithmetic (size: small, medium, large)
* **Binary**: A set of just two values (gender)
* **Categorical**: A finite set of values that cannot be ordered and do not allow arithmetic (country, product type)
* **Textual**: Free-form, usually short, text data (name, address)

Continuous columns can be any of the following:

* **Numeric**: Numeric values (price, age)
* **Interval**: Values that allow ordering and subtraction but do not allow other arithemtic operations (date, time)

The next part of the analysis is to look at the features and see if a combination of features is more relevant than those features by themselves.  If you find that two features are meaningful together you define a feature cross.

You can choose to create buckets in order to convert a continuous column into a categorical column.  For example, if you have an age column, it might be more meaningful if ages were grouped into ranges (ie 0-18, 19-25, 26-30, ...)

Finally you can choose to assign a numerical value (index) to categorical data where the number of unique values are small.  

Once you are done with the evaluation of the descriptive features (columns of data) the next step is to decide which algorithm to use.  Finally you determine which combination of parameters provides the most optimal result.

# How the process works in Splice Machine

## 1. Create Training and Test tables in Splice Machine
You should have two tables for each data set:  One that contains the training data and one that contains the test data.  In those tables you should know what column you are trying to predict.  

## 2. Populate the tables with some initial values
We have a simple stored procedure SPLICE.CREATE_INITIAL_PROJECT_STRUCTURE (code is in com.splicemachine.tutorial.machinelearning.MachineLearningSetup) which will insert data into the MACHINE_LEARNING_PROJECT, DATASET, DATASET_FEATURE_DEFINITION, and DATASET_MACHINE_LEARNING_METHODS tables.  That stored procedure makes some high level assumptions about the feature type (continuous or categorical) based on the column's data type.

## 3. Update Feature Setup
Once the initial feature type has been created, a person familiar with the data and SQL will need to manually update the column definitions in the DATASET_FEATURE_DEFINITION table and add entries to the DATASET_FEATURE_CROSS if applicable.

## 4.  Create the Model
Next, we will create a model using the default setup by calling the stored procedure CREATE_MODEL_FOR_DATASET (code is in com.splicemachine.tutorial.tensorflow.ModelDefintion - method createModelForDataSet).  This will do the following:

* Retrieve the default machine learning algorithm (currently tensorflow), 
* Retrieve the default parameters for that algorithm
* Export the data to the filesystem or hdfs
* Build a JSON object that has the: label column, continuous features, categorical features, cross features and bucketized features
* Call a python script (/docker/template/handlers/TensorFlowWideNDeepHug.py) via a web service (using Hug and uwsgi) running on a Docker container - which will create the model.  The tensorflow model is written to the file system or hdfs.
* The MODEL_CREATION_RESULTS and MODEL_CREATION_RESULTS_DETAILS tables are populated with the results of creating the model

## 5. Use the Model
Finally, you can use the model to predict a value by calling the stored procedure PREDICT_MODEL (code is in com.splicemachine.tutorial.tensorflow.ModelDefintion - method predictModel).  This will do the following:

* Retrieve the machine learning algorithm and parameter set based on the values passed in
* Retrieve the record to be predicted
* Build a JSON object that has the: label column, continuous features, categorical features, cross features and bucketized features
* Call a python script (/docker/template/handlers/TensorFlowWideNDeepHug.py) via a web service (using Hug and uwsgi) running on a Docker container - which will create the model.  The tensorflow model is written to the file system or hdfs.
* Update the label column in the record with the predicted value


# Code Structure

## Java
* **com.splicemachine.tutorial.machinelearning.MachineLearningSetup.java**: Contains the code for creating the initial setup for a project  
* **com.splicemachine.tutorial.tensorflow.ModelDefinition.java**:  Contains the code for the stored procedures to call the model creation process and the prediction process

## Dockerfile
A dockerfile is used to create the image and container that will be used which contains the tensorflow libraries and the pythong scripts.  The dockerfile is located in the /docker folder

## Python Scripts
Any python scripts used to create the models are located under the /docker/template/handlers folder.  

* **TensorFlowWideNDeepHub.py**: This is the python script that contains the logic for creating the model and predicting the outcome

## Splice Machine Objects

### Setup Files
The SQL scripts for creating the tables, stored procedures and loading the initial meta data can be found under /resources/splice/setup.

* **/ddl/create-tables.sql**: Creates all the meta data tables
* **/ddl/create-procedures.sql**: Creates the stored procedures defintions in splice amchine
* **/data/create-data.sql**: Inserts the initial meta data for running the machine learning process

#### Stored Procedure: CREATE_INITIAL_PROJECT_STRUCTURE
Used for the initial setup and population of a machine learning project.  Inserts records into the MACHINE_LEARNING_PROJECT, DATASET and DATASET_FEATURE_DEFINITION tables
* **projectName**: The name of the project.
* **projectDescription**: The description for the project
* **trainingTable**: The full name (SCHEMA.TABLENAME) of the table that contains the training data
* **testTable**: The full name (SCHEMA.TABLENAME) of the table that contains the test data
* **labelColumn**: The column that contains the label

#### Stored Procedure: CREATE_MODEL_FOR_DATASET
Used for creating the models for all particular dataset
* **dataOutputPath**: Full path to the output directory for the data
* **modelOutputOath**: Full path to the output directory for the model
* **datasetId**: The id of the dataset.  Corresponds to the DATASET_ID in the DATASET table

#### Stored Procedure: PREDICT_MODEL
Used for predicting the outcome of a particular record
* **modelOutputOath**: Full path to the output directory for the model
* **datasetId**: The id of the dataset.  Corresponds to the DATASET_ID in the DATASET table
* **parameterSetId**: The parameter set id that you want to use for predicting the model
* **sourceTable**: The table containing the live data
* **recordId**: ID of the record to perform the prediction on

### Examples
The examples folder has examples for dynamically creating models using different datasets.

#### Census Example
The folder /resources/examples/census_example contains the files needed to setup the census data.  The folders are as follows:

* **/data**: Contains the files for the training, test and live data sets.
* **/ddl**: Contains the scripts for creating the training, test and live tables as well as the script for calling the stored procedure CREATE_INITIAL_PROJECT_STRUCTURE and statements to update the data after the defaults are created.  
* **/queries**: This folder contains the sql statements to call the stored procedure to create the model and then use the model to predict the outcome.

An overview of the Census Example and the files can be found in the README.md file under the /resources/examples/census_example folder.

#### Insurance Example
The folder /resources/examples/insurance_example contains the files needed to setup the insurance data.  The folders are as follows:

* **/data**: Contains the files for the training, test and live data sets.
* **/ddl**: Contains the scripts for creating the training, test and live tables as well as the script for calling the stored procedure CREATE_INITIAL_PROJECT_STRUCTURE and statements to update the data after the defaults are created.  
* **/queries**: This folder contains the sql statements to call the stored procedure to create the model and then use the model to predict the outcome.

An overview of the Insurance Example and the files can be found in the README.md file under the /resources/examples/insurance_example folder.

#### Iris Example
The folder /resources/examples/iris_example contains the files needed to setup the iris data.  The folders are as follows:

* **/data**: Contains the files for the training, test and live data sets.
* **/ddl**: Contains the scripts for creating the training, test and live tables as well as the script for calling the stored procedure CREATE_INITIAL_PROJECT_STRUCTURE and statements to update the data after the defaults are created.  
* **/queries**: This folder contains the sql statements to call the stored procedure to create the model and then use the model to predict the outcome.

