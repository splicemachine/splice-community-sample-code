# Overview
The Census Income data set is from the UCI Machine Learning Repository (https://archive.ics.uci.edu/ml/datasets/Census+Income).  It contains data that can be used to predict whether income exceeds $50K per year based on census data.

# Data Set Properties
|Property|Value|
|----|----|
|Data Set Characteristics|Multivariate|
|Attribute Characteristics|Categorical, Integer|
|Associated Tasks|Classification|
|Number of Attributes|14|

# Tables
The census data is comprised of 3 tables: 

* **CENSUS.TRAINING_DATA**: Contains the census data that is used to train the model - 28,361 records
* **CENSUS.TESTING_DATA**: Contains the census data that is used to test the model - 16,281 records
* **CENSUS.LIVE_DATA**: Contains the data that is used when running the model to predict values in real time. - 16,062 records

## Data / Table Structure
All three tables have the same definition:

|Column Number|Source Name|Valid Values|Splice Machine Column|Splice Machine Data Type|
|----|----|----|----|----|
|1|Age||AGE|INT|
|2|Workclass|Private, Self-emp-not-inc, Self-emp-inc, Federal-gov, Local-gov, State-gov, Without-pay, Never-worked.Ê|WORKCLASS|VARCHAR(20)|
|3|Sampling Weight||FNLWGT|INT|
|4|Education|Bachelors, Some-college, 11th, HS-grad, Prof-school, Assoc-acdm, Assoc-voc, 9th, 7th-8th, 12th, Masters, 1st-4th, 10th, Doctorate, 5th-6th, Preschool.Ê|EDUCATION|VARCHAR(20)|
|5|Education Num||EDUCATION_NUM|INT|
|6|Marital Status|Married-civ-spouse, Divorced, Never-married, Separated, Widowed, Married-spouse-absent, Married-AF-spouse.Ê|MARITAL_STATUS|VARCHAR(20)|
|7|Occupation|Tech-support, Craft-repair, Other-service, Sales, Exec-managerial, Prof-specialty, Handlers-cleaners, Machine-op-inspct, Adm-clerical, Farming-fishing, Transport-moving, Priv-house-serv, Protective-serv, Armed-Forces.Ê|OCCUPATION|VARCHAR(20)|
|8|Relationship|Wife, Own-child, Husband, Not-in-family, Other-relative, Unmarried.Ê|RELATIONSHIP|VARCHAR(20)|
|9|Race|White, Asian-Pac-Islander, Amer-Indian-Eskimo, Other, Black|RACE|INT|
|10|Sex|Female, Male|GENDER|INT|
|11|Capital Gain||CAPITAL_GAIN|INT|
|12|Capital Loss||CAPITAL_LOSS|INT|
|13|Hours Per Week||HOURS_PER_WEEK|INT|
|14|Native Country|United-States, Cambodia, England, Puerto-Rico, Canada, Germany, Outlying-US(Guam-USVI-etc), India, Japan, Greece, South, China, Cuba, Iran, Honduras, Philippines, Italy, Poland, Jamaica, Vietnam, Mexico, Portugal, Ireland, France, Dominican-Republic, Laos, Ecuador, Taiwan, Haiti, Columbia, Hungary, Guatemala, Nicaragua, Scotland, Thailand, Yugoslavia, El-Salvador, Trinadad&Tobago, Peru, Hong, Holand-Netherlands.|NATIVE_COUNTRY|VARCHAR(20)|
|15|Income Bracket||INCOME_BRACKET|VARCHAR(20)|
|16|N/A||LABEL|VARCHAR(20)|


## Sample Data

|AGE|WORKCLASS|FNLWGT|EDUCATION|EDUCATION_NUM|MARITAL_STATUS|OCCUPATION|RELATIONSHIP|RACE|GENDER|CAPITAL_GAIN|CAPITAL_LOSS|HOURS_PER_WEEK|NATIVE_COUNTRY|INCOME_BRACKET|LABEL|
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
|25|Private|226802|11th|7|Never-married|Machine-op-inspct|Own-child|Black|Male|0|0|40|United-States|<=50K|
|38|Private|89814|HS-grad|9|Married-civ-spouse|Farming-fishing|Husband|White|Male|0|0|50|United-States|<=50K|
|28|Local-gov|336951|Assoc-acdm|12|Married-civ-spouse|Protective-serv|Husband|White|Male|0|0|40|United-States|>50K|


# Splice Machine Objects

## Data Files

* **/data/live.data.csv**: Contains the data that will be used when running the process to PREDICT a value
* **/data/testing.data.csv**: Contains the data that will be used when running the process to TEST the model
* **/data/training.data.csv**: Contains the data that will be used when running the process to TRAIN the model

## Setup Files

* **/ddl/create-tables.sql**: Creates the schema CENSUS and the tables CENSUS.TRAINING_DATA, CENSUS.TESTING_DATA and CENSUS.LIVE_DATA 
* **/ddl/create-data.sql**: Populates the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS tables with the data needed to generate the models and also populates the CENSUS.TRAINING_DATA, CENSUS.TESTING_DATA and CENSUS.LIVE_DATA tables.

## Stored Procedure Call

* **/queries/create_model.sql**: Query that calls the CREATE_MODEL stored procedure to generate the model.
* **/queries/predict-true.sql**: Query that calls the PREDICT_MODEL stored procedure to generate the predictions in the live data where the expected output is true
* **/queries/predict-false.sql**: Query that calls the PREDICT_MODEL stored procedure to generate the predictions in the live data where the expected output is false

## Python Scripts
* **/python/manually-create-model-using-python-only.sh**: A script to manually call the python code without splice machine to create the model.  Useful for debugging purposes
* **/python/manually-predict-using-python-only.sh**: A script to manually call the python code without splice machine to predict the output.  Useful for debugging purposes

