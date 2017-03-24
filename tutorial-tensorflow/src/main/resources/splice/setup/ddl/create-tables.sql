create table MACHINE_LEARNING_PROJECT (
	PROJECT_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	NAME VARCHAR(100) NOT NULL,
	DESCRIPTION VARCHAR(200),
	STATUS CHAR(1) DEFAULT 'A',
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30) DEFAULT 'SPLICE',
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30)  DEFAULT 'SPLICE',
	PRIMARY KEY (PROJECT_ID)
);

create table DATASET (
	DATASET_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	PROJECT_ID int,
	TYPE CHAR(1) DEFAULT 'T',
	TRAINING_TABLE VARCHAR(200),
	TEST_TABLE VARCHAR(200),
	TRAINING_QUERY VARCHAR(500),
	TEST_QUERY VARCHAR(500),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30) DEFAULT 'SPLICE',
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30)  DEFAULT 'SPLICE',
	PRIMARY KEY (DATASET_ID)
);

create table DATASET_FEATURE_DEFINITION (
	DATASET_FEATURE_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	DATASET_ID int,
	DATABASE_COLUMN_NAME VARCHAR(100),
	FEATURE_NAME VARCHAR(100),
	FEATURE_TYPE varchar(20),
	FEATURE_KEYS VARCHAR(500),
	FEATURE_BUCKET_DATA_TYPE VARCHAR(20),
	FEATURE_BUCKETS VARCHAR(500),
	IS_LABEL BOOLEAN,
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30) DEFAULT 'SPLICE',
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30)  DEFAULT 'SPLICE',
	PRIMARY KEY (DATASET_FEATURE_ID)
);

create table DATASET_FEATURE_CROSS (
	DATASET_FEATURE_CROSS_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	DATASET_ID int,	
	FEATURE_CROSS_NAME VARCHAR(50),
	FEATURE_NAME varchar(50),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (DATASET_FEATURE_CROSS_ID)
);

create table DATASET_COLUMN_STATISTICS (
	DATASET_COLUMN_STATISTICS_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	DATASET_ID int,	
	DATABASE_COLUMN_NAME VARCHAR(100),
	DATA_TYPE VARCHAR(50),
	COUNT BIGINT,
	CARDINALITY BIGINT,
	MISSING BIGINT,
	MEAN FLOAT,
	STD_DEVIATION FLOAT,
	MEDIAN FLOAT,
	MIN_VALUE FLOAT,
	MAX_VALUE FLOAT,
	PERCENTITLE_25_MEAN FLOAT,
	PERCENTITLE_25_MEDIAN FLOAT,
	PERCENTITLE_75_MEAN FLOAT,
	PERCENTITLE_75_MEDIAN FLOAT,	
	MODE VARCHAR(200),
	MODE_FREQUENCY BIGINT,
	MODE_PERCENT FLOAT,
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (DATASET_COLUMN_STATISTICS_ID)
);

create table MACHINE_LEARNING_PROCESS (
	MACHINE_LEARNING_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	NAME VARCHAR(100) NOT NULL,
	DESCRIPTION VARCHAR(200),
	PLATFORM VARCHAR(100) NOT NULL,
	ALGORITHM VARCHAR(100),
	ALGORITHM_TYPE CHAR(1),
	STATUS CHAR(1) DEFAULT 'A',
	INTEGRATION_TYPE VARCHAR(50),
	URL VARCHAR(200),
	CREATION_PROCESS VARCHAR(200),
	PREDICT_PROCESS VARCHAR(200),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MACHINE_LEARNING_ID)
);

create table MACHINE_LEARNING_PARAMETERS (
	MACHINE_LEARNING_PARAMETER_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	MACHINE_LEARNING_ID int,
	PARAMETER_NAME VARCHAR(100) NOT NULL,
	DISPLAY_NAME VARCHAR(100),
	DEFAULT_VALUE VARCHAR(100),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MACHINE_LEARNING_PARAMETER_ID)
);

CREATE TABLE MODEL_CREATION_GROUP (
	MODEL_CREATION_GROUP_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	NAME VARCHAR(100) NOT NULL,
	STATUS CHAR(1) DEFAULT 'A',
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MODEL_CREATION_GROUP_ID)
);

CREATE TABLE MODEL_CREATION_GROUP_DETAILS (
	MODEL_CREATION_GROUP_DETAIL_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	MODEL_CREATION_GROUP_ID int,
	MACHINE_LEARNING_ID int,
	STATUS CHAR(1) DEFAULT 'A',
	IS_DEFAULT BOOLEAN,
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MODEL_CREATION_GROUP_DETAIL_ID)
);

CREATE TABLE MODEL_CREATION_PARAMETER_VALUES (
	MODEL_CREATION_PARAMETER_VALUE_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	MODEL_CREATION_GROUP_DETAIL_ID int,
	MACHINE_LEARNING_PARAMETER_ID int,
	PARAMETER_VALUE VARCHAR(100) NOT NULL,
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MODEL_CREATION_PARAMETER_VALUE_ID)
);

CREATE TABLE MODEL_CREATION_RUN (
	MODEL_CREATION_RUN_ID int generated by default as identity (START WITH 1, INCREMENT BY 1),
	MODEL_CREATION_GROUP_ID int,
	TYPE CHAR(1) DEFAULT 'T',
	DATASET_ID int,
	TRAINING_TABLE VARCHAR(200),
	TEST_TABLE VARCHAR(200),
	TRAINING_QUERY VARCHAR(500),
	TEST_QUERY VARCHAR(500),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30),
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30),
	PRIMARY KEY (MODEL_CREATION_RUN_ID)
);

create table MODEL_CREATION_RESULTS (
	MODEL_RESULTS_ID int generated by default as identity (START WITH 2, INCREMENT BY 1),
	MODEL_CREATION_RUN_ID int NOT NULL,
	START_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	END_TIME TIMESTAMP,
	STATUS VARCHAR(50),
	ACCURACY FLOAT,
	BASELINE_TARGET_MEAN FLOAT,
	THRESHOLD_MEAN FLOAT,
	AUC FLOAT,
	GLOBAL_STEP INTEGER,
	ACTUAL_TARGET_MEAN FLOAT,
	PREDICTION_MEAN FLOAT,
	LOSS FLOAT,
	PRECISION_POSITIVE_THRESHOLD_MEAN FLOAT,
	RECALL_POSITIVE_THRESHOLD_MEAN FLOAT,
	TRAINING_DATA_RESULTS VARCHAR(500),
	TEST_DATA_RESULTS VARCHAR(500),
	CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CREATE_USER VARCHAR(30) DEFAULT 'SPLICE',
	UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UPDATE_USER VARCHAR(30)  DEFAULT 'SPLICE',
	PRIMARY KEY (MODEL_RESULTS_ID)
);
