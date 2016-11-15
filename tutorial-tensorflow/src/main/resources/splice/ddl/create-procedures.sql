CREATE PROCEDURE SPLICE.CREATE_MODEL(scriptPathAndName VARCHAR(300), type VARCHAR(50), modelName VARCHAR(50), trainingDataTable VARCHAR(50), testDataTable VARCHAR(50))
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.CreateInputDictionary.generateModel';

CREATE PROCEDURE SPLICE.PREDICT_MODEL(scriptPathAndName VARCHAR(300), type VARCHAR(50), modelName VARCHAR(50), sourceTable VARCHAR(50), recordId INTEGER)
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.CreateInputDictionary.predictModel';