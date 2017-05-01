#!/usr/bin/env bash

###############################################################################
#  this is an example script the will require edits to make it work in any
#  environment.
###############################################################################
## THIS IS CLUSTER/PLATFORM DEPENDENT

# MapR
# https://community.mapr.com/docs/DOC-1396
SPLICE_LIB_DIR="/opt/splice/default/lib"
KAFKA_LIB_DIR="/opt/kafka/default/libs"
export SPARK_HOME="/opt/spark/default"

#export HADOOP_CONF_DIR=/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/
#export LD_LIBRARY_PATH=/opt/mapr/hadoop/hadoop-2.7.0/lib/native/:${LD_LIBRARY_PATH}

# Cloudera
#SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"
# KAFKA_LIB_DIR="/opt/cloudera/parcels/KAFKA/lib/kafka/libs/"
# export SPARK_HOME="/opt/cloudera/parcels/CDH/lib/spark"
# export HADOOP_CONF_DIR=/etc/hadoop/conf

CLASS_NAME="com.splicemachine.tutorials.spark.SparkStreamingKafka"
export ADDITIONAL_JARS="/tmp/kafka-clients-0.10.2.0.jar,/tmp/db-engine-2.5.0.1707.jar,${SPLICE_LIB_DIR}/db-client-2.5.0.1707.jar"
export APPLICATION_JAR="/tmp/splice-tutorial-iot-spark-2.5.0.1707.jar"


exec "${SPARK_HOME}"/bin/spark-submit \
  --name TutorialKafka \
  --master spark://stl-colo-srv107.splicemachine.colo:7077 \
  --deploy-mode cluster \
  --executor-memory 2G \
  --num-executors 2 \
  --class ${CLASS_NAME} \
  --jars ${ADDITIONAL_JARS} \
  "${APPLICATION_JAR}" \
  "$@"