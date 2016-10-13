This section contains common Splice functions. 

Added functions are 
* RPAD  - This pads the characters on the right of a string.
* POWER - It consists of base and exponent arguments and gives the result for base ^ exponent (2^3 = 8)



How to download these functions to your standalone or cluster:

1. Download the JAR file (jar/splice-functions-0.0.1.jar)
2. upload the file in a location on cluster or local.
3. Open SQL shell and run : CALL SQLJ.INSTALL_JAR('<location of jar file>', 'SPLICE.MY_EXAMPLE_APP', 0);
4. CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'SPLICE.MY_EXAMPLE_APP');
5. CREATE FUNCTION POWER(BASE double,EXPONENT double) returns double LANGUAGE JAVA PARAMETER STYLE JAVA NO SQL EXTERNAL NAME 'com.splicemachine.utils.Power.Power';
6. Check the function by running the statement select POWER(2,3) from SYSIBM.SYSDUMMY1;

For detailed staps , please refer : http://doc.splicemachine.com/Developers/AdvancedTopics/ProcAndFcnExamples.html
