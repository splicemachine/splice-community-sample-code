DROP PROCEDURE populateSensorTargetRowTable;

CREATE PROCEDURE SPLICE.populateSensorTargetRowTable()
LANGUAGE JAVA
EXTERNAL NAME 'com.splicemachine.tutorials.spark.SimpleSparkDataFrameCallToVTI.populateSensorPartitionTargetTable'
PARAMETER STYLE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 1;
