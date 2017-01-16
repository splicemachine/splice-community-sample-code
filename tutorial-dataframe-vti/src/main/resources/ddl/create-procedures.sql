DROP PROCEDURE populateSensorTargetTable;

CREATE PROCEDURE SPLICE.populateSensorTargetTable()
LANGUAGE JAVA
EXTERNAL NAME 'com.splicemachine.tutorials.spark.SimpleSparkDataFrameCallToVTI.populateSensorTargetTable'
PARAMETER STYLE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 1;
