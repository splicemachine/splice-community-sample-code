CREATE PROCEDURE SPLICE.CREATE_INITIAL_PROJECT_STRUCTURE(
	projectName VARCHAR(100), 
	projectDescription  VARCHAR(200),
	trainingTable VARCHAR(200),
	testTable VARCHAR(200),
	labelColumn VARCHAR(100)
	)
   PARAMETER STYLE JAVA
   MODIFIES SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.machinelearning.MachineLearningSetup.createInitialProjectStructure';

CREATE PROCEDURE SPLICE.CREATE_MODEL_FOR_DATASET(
	dataOutputPath VARCHAR(300), 
	modelOutputOath  VARCHAR(300),
	datasetId INTEGER
	)
   PARAMETER STYLE JAVA
   MODIFIES SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.createModelForDataSet';

CREATE PROCEDURE SPLICE.PREDICT_MODEL(
	modelOutputOath  VARCHAR(300),
	datasetId INTEGER,
	parameterSetId INTEGER,
	sourceTable VARCHAR(50), 
	recordId INTEGER
   )
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.predictModel';

