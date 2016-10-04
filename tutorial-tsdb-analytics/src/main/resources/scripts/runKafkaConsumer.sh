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
export SPARK_HOME="/opt/mapr/spark/spark-1.6.1"
export HADOOP_CONF_DIR=/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/
export LD_LIBRARY_PATH=/opt/mapr/hadoop/hadoop-2.7.0/lib/native/:${LD_LIBRARY_PATH}
# Cloudera
# SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"
# KAFKA_LIB_DIR="/opt/cloudera/parcels/KAFKA/lib/kafka/libs/"
# export SPARK_HOME="/opt/cloudera/parcels/CDH/lib/spark"
# export HADOOP_CONF_DIR=/etc/hadoop/conf
# export LD_LIBRARY_PATH=/opt/cloudera/parcels/CDH/lib/hadoop/lib/native:${LD_LIBRARY_PATH}

CLASS_NAME="com.splicemachine.tutorials.sparkstreaming.kafka.SparkStreamingKafka"
export ADDITIONAL_JARS="${SPLICE_LIB_DIR}/spark-streaming-kafka*.jar,${KAFKA_LIB_DIR}/kafka-clients-*.jar"
export APPLICATION_JAR="${SPLICE_LIB_DIR}/splice-tutorial-kafka-spark-streaming-2.0.1.18.jar"
export APPLICATION_JAR="${SPLICE_LIB_DIR}/splice-tutorial-tsdb-analytics-2.0.1.18.jar"
EXAMPLE_CLASS="com.splicemachine.tutorials.tsdbanalytics.LogAggregator"

exec "${SPARK_HOME}"/bin/spark-submit \
  --name TutorialTSBD \
  --master yarn \
  --deploy-mode cluster \
  --driver-memory 4g \
  --executor-memory 2G \
  --executor-cores 4 \
  --num-executors 8 \
  --class ${EXAMPLE_CLASS} \
  --jars ${ADDITIONAL_JARS} \
  "${APPLICATION_JAR}" \
  "test" \
  "tsdb" \
  "$@"