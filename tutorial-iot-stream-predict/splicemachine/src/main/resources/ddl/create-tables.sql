CREATE SCHEMA IOT;
SET SCHEMA IOT;


Create table IOT.TRAIN_DATA 
(	engine_type int,
	unit int,
	time bigint,
	op_setting_1 decimal (19,6),
	op_setting_2 decimal (19,6),
	op_setting_3 decimal (19,6),
	sensor_measure_1 decimal (19,6),
	sensor_measure_2 decimal (19,6),
	sensor_measure_3 decimal (19,6),
	sensor_measure_4 decimal (19,6),
	sensor_measure_5 decimal (19,6),
	sensor_measure_6 decimal (19,6),
	sensor_measure_7 decimal (19,6),
	sensor_measure_8 decimal (19,6),
	sensor_measure_9 decimal (19,6),
	sensor_measure_10 decimal (19,6),
	sensor_measure_11 decimal (19,6),
	sensor_measure_12 decimal (19,6),
	sensor_measure_13 decimal (19,6),
	sensor_measure_14 decimal (19,6),
	sensor_measure_15 decimal (19,6),
	sensor_measure_16 decimal (19,6),
	sensor_measure_17 decimal (19,6),
	sensor_measure_18 decimal (19,6),
	sensor_measure_19 decimal (19,6),
	sensor_measure_20 decimal (19,6),
	sensor_measure_21 decimal (19,6),
	prediction int
);
Create table IOT.TEST_DATA 
(	engine_type int,
	unit int,
	time bigint,
	op_setting_1 decimal (19,6),
	op_setting_2 decimal (19,6),
	op_setting_3 decimal (19,6),
	sensor_measure_1 decimal (19,6),
	sensor_measure_2 decimal (19,6),
	sensor_measure_3 decimal (19,6),
	sensor_measure_4 decimal (19,6),
	sensor_measure_5 decimal (19,6),
	sensor_measure_6 decimal (19,6),
	sensor_measure_7 decimal (19,6),
	sensor_measure_8 decimal (19,6),
	sensor_measure_9 decimal (19,6),
	sensor_measure_10 decimal (19,6),
	sensor_measure_11 decimal (19,6),
	sensor_measure_12 decimal (19,6),
	sensor_measure_13 decimal (19,6),
	sensor_measure_14 decimal (19,6),
	sensor_measure_15 decimal (19,6),
	sensor_measure_16 decimal (19,6),
	sensor_measure_17 decimal (19,6),
	sensor_measure_18 decimal (19,6),
	sensor_measure_19 decimal (19,6),
	sensor_measure_20 decimal (19,6),
	sensor_measure_21 decimal (19,6),
	prediction int
);

create table IOT.TEST_RUL_VALUES 
(
	engine_type int,
	unit int,
	rul int
);

create table IOT.PREDICTION_RESULTS 
(
	engine_type int,
	unit int,
	time bigint,
	prediction int
);


Create table IOT.SENSOR_DATA 
(	engine_type int,
	unit int,
	time bigint,
	op_setting_1 decimal (19,6),
	op_setting_2 decimal (19,6),
	op_setting_3 decimal (19,6),
	sensor_measure_1 decimal (19,6),
	sensor_measure_2 decimal (19,6),
	sensor_measure_3 decimal (19,6),
	sensor_measure_4 decimal (19,6),
	sensor_measure_5 decimal (19,6),
	sensor_measure_6 decimal (19,6),
	sensor_measure_7 decimal (19,6),
	sensor_measure_8 decimal (19,6),
	sensor_measure_9 decimal (19,6),
	sensor_measure_10 decimal (19,6),
	sensor_measure_11 decimal (19,6),
	sensor_measure_12 decimal (19,6),
	sensor_measure_13 decimal (19,6),
	sensor_measure_14 decimal (19,6),
	sensor_measure_15 decimal (19,6),
	sensor_measure_16 decimal (19,6),
	sensor_measure_17 decimal (19,6),
	sensor_measure_18 decimal (19,6),
	sensor_measure_19 decimal (19,6),
	sensor_measure_20 decimal (19,6),
	sensor_measure_21 decimal (19,6)
);

Create table IOT.TO_PROCESS_SENSOR
(engine_type int,
	unit int,
	time bigint);
	

CREATE EXTERNAL TABLE IOT.PREDICTIONS_EXT(
   engine_type INT, unit int, time bigint, prediction int )
   ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' ESCAPED BY '\\'
   LINES TERMINATED BY '\\n'
   STORED AS TEXTFILE
   LOCATION '/tmp/data_pred/predictions';


   
CREATE TRIGGER iot.add_sensor_to_process
   AFTER INSERT
   ON IOT.SENSOR_DATA 
   REFERENCING  NEW AS NEW_ROW
   FOR EACH ROW
      INSERT INTO IOT.TO_PROCESS_SENSOR VALUES (1,  NEW_ROW.unit, NEW_ROW.time);
     
     

CREATE SEQUENCE IOT.RUL_ENGINE_1_SEQ 
   START WITH 1
   MAXVALUE 100
   MINVALUE 1
   CYCLE;
