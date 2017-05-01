
insert into IOT.TRAIN_DATA
SELECT  1, b.*, cast(null as int) FROM new com.splicemachine.derby.vti.SpliceFileVTI(
  '/iotdemo/train_FD001.txt','',' ') AS b
   (unit int,time bigint,op_setting_1 decimal (19,6),op_setting_2 decimal (19,6),op_setting_3 decimal (19,6),sensor_measure_1 decimal (19,6),sensor_measure_2 decimal (19,6),sensor_measure_3 decimal (19,6),sensor_measure_4 decimal (19,6),sensor_measure_5 decimal (19,6),sensor_measure_6 decimal (19,6),sensor_measure_7 decimal (19,6),sensor_measure_8 decimal (19,6),sensor_measure_9 decimal (19,6),sensor_measure_10 decimal (19,6),sensor_measure_11 decimal (19,6),sensor_measure_12 decimal (19,6),sensor_measure_13 decimal (19,6),sensor_measure_14 decimal (19,6),sensor_measure_15 decimal (19,6),sensor_measure_16 decimal (19,6),sensor_measure_17 decimal (19,6),sensor_measure_18 decimal (19,6),sensor_measure_19 decimal (19,6),sensor_measure_20 decimal (19,6),sensor_measure_21 decimal (19,6));



insert into IOT.TEST_DATA
SELECT  1, b.*, cast(null as int) FROM new com.splicemachine.derby.vti.SpliceFileVTI(
  '/iotdemo/test_FD001.txt','',' ') AS b
   (unit int,time bigint,op_setting_1 decimal (19,6),op_setting_2 decimal (19,6),op_setting_3 decimal (19,6),sensor_measure_1 decimal (19,6),sensor_measure_2 decimal (19,6),sensor_measure_3 decimal (19,6),sensor_measure_4 decimal (19,6),sensor_measure_5 decimal (19,6),sensor_measure_6 decimal (19,6),sensor_measure_7 decimal (19,6),sensor_measure_8 decimal (19,6),sensor_measure_9 decimal (19,6),sensor_measure_10 decimal (19,6),sensor_measure_11 decimal (19,6),sensor_measure_12 decimal (19,6),sensor_measure_13 decimal (19,6),sensor_measure_14 decimal (19,6),sensor_measure_15 decimal (19,6),sensor_measure_16 decimal (19,6),sensor_measure_17 decimal (19,6),sensor_measure_18 decimal (19,6),sensor_measure_19 decimal (19,6),sensor_measure_20 decimal (19,6),sensor_measure_21 decimal (19,6));
   
insert into IOT.TEST_RUL_VALUES 
SELECT 1, NEXT VALUE FOR IOT.RUL_ENGINE_1_SEQ, b.* FROM new com.splicemachine.derby.vti.SpliceFileVTI(
  '/iotdemo/RUL_FD001.txt','',' ') AS b
  (rul int);
   
   
   
   
   