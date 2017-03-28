create schema IRIS;

drop table if exists IRIS.TRAINING_DATA;
drop table if exists IRIS.TESTING_DATA;
drop table if exists IRIS.LIVE_DATA;
drop table if exists IRIS.LIVE_DATA_EXPECTED;

create table IRIS.TRAINING_DATA (
	SEPAL_LENGTH FLOAT,
	SEPAL_WIDTH FLOAT,
	PETAL_LENGTH FLOAT,
	PETAL_WIDTH FLOAT,
	IRIS_CLASS VARCHAR(100),
	IRIS_CLASS_CODE INTEGER
);

create table IRIS.TEST_DATA (
	SEPAL_LENGTH FLOAT,
	SEPAL_WIDTH FLOAT,
	PETAL_LENGTH FLOAT,
	PETAL_WIDTH FLOAT,
	IRIS_CLASS VARCHAR(100),
	IRIS_CLASS_CODE INTEGER
);

create table IRIS.LIVE_DATA (
	ID INTEGER,
	SEPAL_LENGTH FLOAT,
	SEPAL_WIDTH FLOAT,
	PETAL_LENGTH FLOAT,
	PETAL_WIDTH FLOAT,
	IRIS_CLASS VARCHAR(100),
	IRIS_CLASS_CODE INTEGER
);

create table IRIS.LIVE_DATA_EXPECTED (
	ID INTEGER,
	EXPECTED_RESULT VARCHAR(100),
	IRIS_CLASS_CODE INTEGER
);