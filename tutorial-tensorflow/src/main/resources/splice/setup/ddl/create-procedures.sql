CREATE PROCEDURE SPLICE.CREATE_MODEL(scriptPathAndName VARCHAR(300), 
	type VARCHAR(50), 
	modelName VARCHAR(50), 
	trainingDataTable VARCHAR(50), 
	testDataTable VARCHAR(50),
	modelOutputPath  VARCHAR(300),
	trainingSteps INTEGER,
	hashBucketSize INTEGER,
    dimensions INTEGER,
    hiddenUnits  VARCHAR(300)
	)
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.generateModel';

CREATE PROCEDURE SPLICE.PREDICT_MODEL(scriptPathAndName VARCHAR(300), 
	type VARCHAR(50), 
	modelName VARCHAR(50), 
	sourceTable VARCHAR(50), 
	comparisonColumn VARCHAR(100), 
	criteria VARCHAR(100),
	recordId INTEGER,
	modelDirectory VARCHAR(300))
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.predictModel';