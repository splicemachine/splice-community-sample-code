# Spark with Splice Machine
This maven project provides a simple example of how to save data to Splice Machine using spark. The intent is to provide an example of how to structure maven dependencies and a simple java class to write to splice machine.  

## To Build this code
```
mvn clean compile package
```

## To Run this code in Spark
```
./bin/spark-submit --class com.splicemachine.tutorials.spark.SparkToSpliceExample /path/to/tutorial-spark-to-splice-2.5.0.1803.jar
```
