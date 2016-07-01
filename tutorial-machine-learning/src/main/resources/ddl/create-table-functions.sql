set schema movielens;
CREATE FUNCTION continuousFeatureReport(TableName VARCHAR(100))
   RETURNS TABLE
     (
     	FEATURE_NAME varchar(100),
     	COUNT integer,
     	MISSING_PERCENT double,
     	CARDINALITY integer,
     	MINIMUM double,
     	QUARTILE_1 double,
     	MEAN double,
		MEDIAN double,
     	QUARTILE_3 double,
		MAXIMUM double,
		STD_DEVIATION double
     )
   LANGUAGE JAVA
   PARAMETER STYLE SPLICE_JDBC_RESULT_SET
   READS SQL DATA
   EXTERNAL NAME 'com.splicemachine.tutorial.mlib.TableFunctions.continuousFeatureReport';
   
CREATE FUNCTION discreteFeatureReport(TableName VARCHAR(100))
   RETURNS TABLE
     (
     	FEATURE_NAME varchar(100),
     	COUNT integer,
     	MISSING_PERCENT double,
     	CARDINALITY integer,
     	MODE double,
     	MODE_FREQUENCY double,
     	MODE_PERCENT double,
     	MODE_2 double,
     	MODE_2_FREQUENCY double,
     	MODE_2_PERCENT double
     )
   LANGUAGE JAVA
   PARAMETER STYLE SPLICE_JDBC_RESULT_SET
   READS SQL DATA
   EXTERNAL NAME 'com.splicemachine.tutorial.mlib.TableFunctions.discreteFeatureReport';
