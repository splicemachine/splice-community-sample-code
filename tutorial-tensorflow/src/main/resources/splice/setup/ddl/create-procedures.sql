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



createInitialProjectStructure(String projectName, String projectDescription,
            String trainingTable, String testTable, String labelColumn, ResultSet[] returnResultset)


CREATE PROCEDURE SPLICE.CREATE_MODEL_FOR_ALL_INPUT_SETS(
	scriptPathAndName VARCHAR(300), 
	modelOutputPath  VARCHAR(300),
	modelId INTEGER
	)
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.generateModelsForAllInputSets';

CREATE PROCEDURE SPLICE.CREATE_MODEL_FOR_INPUT_SET(
	scriptPathAndName VARCHAR(300), 
	modelOutputPath  VARCHAR(300),
	modelInputId INTEGER
	)
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.generateModelForInputSet';

CREATE PROCEDURE SPLICE.CREATE_MODEL(
	scriptPathAndName VARCHAR(300), 
	type VARCHAR(50), 
	modelName VARCHAR(50), 
	trainingDataTable VARCHAR(50), 
	testDataTable VARCHAR(50),
	modelOutputPath  VARCHAR(300),
	trainingSteps INTEGER,
	hashBucketSize INTEGER,
    dimensions INTEGER,
    hiddenUnits  VARCHAR(300),
    modelId INTEGER,
    modelInputId INTEGER
	)
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.generateModel';
      
CREATE PROCEDURE SPLICE.PREDICT_MODEL(
	scriptPathAndName VARCHAR(300), 
	modelOutputPath  VARCHAR(300),
	modelId INTEGER,
    modelInputId INTEGER,
	sourceTable VARCHAR(50), 
	recordId INTEGER,
	comparisonColumn VARCHAR(100), 
	criteria VARCHAR(100)
   )
   PARAMETER STYLE JAVA
   READS SQL DATA
   LANGUAGE JAVA
   DYNAMIC RESULT SETS 1
   EXTERNAL NAME 'com.splicemachine.tutorial.tensorflow.ModelDefinition.predictModel';