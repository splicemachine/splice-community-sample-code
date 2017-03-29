# How to Setup a New Model
This document provides the steps for creating a new model for your dataset.


# 1.  Create Tables in Splice Machine
Create tables to store your training, test and live data.  Make sure that your live data table has an ID column that uniquely identifies a record.  This will be used by the predict stored procedure to retrieve the record and then later on update it.

# 2.  Import Data
Import data into each of the training, test and live database tables.

# 3.  Create the Initial Project definition
Run the stored procedure CREATE_INITIAL_PROJECT_STRUCTURE to populate the initial tables for a new project and dataset.  The call to the stored procedure will look something like the following:

	call SPLICE.CREATE_INITIAL_PROJECT_STRUCTURE('CENSUS','Predict whether income is greater than 50,000','CENSUS.TRAINING_DATA','CENSUS.TESTING_DATA','LABEL');
	
# 4.  Update the Dataset Features
Run a SQL statement and review the definition of the columns in the DATASET_FEATURE_DEFINITION table.  If there are any changes necessary, run SQL statements to add, delete or update records.

# 5.  Add Feature Cross 
If applicable, add entries to the DATASET_FEATURE_CROSS table for features that should be combined to be meaningful.

# 6.  Create the Model
Call the stored procedure to create the model.  The call to the stored procedure will look something like the following:

	call SPLICE.CREATE_MODEL_FOR_DATASET(
	'/tmp/splicemachine-tensorflow/census_out', 
	'/tmp/splicemachine-tensorflow/census_out',
	1
	);
	 
# 7.  Use the Model to Predict the Outcome
Call the stored procedure to predict the outcome of a particular record.  The call to the stored procedure will look something like the followng:

	call SPLICE.PREDICT_MODEL(
	'/tmp/splicemachine-tensorflow/census_out',
	1,
	1,
	'CENSUS.LIVE_DATA',
	6479
	);

Look at the examples under /resources/examples for examples.