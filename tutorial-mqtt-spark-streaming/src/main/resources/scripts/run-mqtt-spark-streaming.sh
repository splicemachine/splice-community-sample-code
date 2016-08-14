#!/usr/bin/env bash

###############################################################################
#  this is an example script the will require edits to make it work in any
#  environment.
###############################################################################
## THIS IS CLUSTER/PLATFORM DEPENDENT
# MapR
SPLICE_LIB_DIR="/opt/splice/default/lib"
export SPARK_HOME="/opt/mapr/spark/spark-1.6.1"
export HADOOP_CONF_DIR=/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/
export LD_LIBRARY_PATH=/opt/mapr/hadoop/hadoop-2.7.0/lib/native/:${LD_LIBRARY_PATH}
# Cloudera
# SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"
# export SPARK_HOME="/opt/cloudera/parcels/CDH/lib/spark"
# export HADOOP_CONF_DIR=/etc/hadoop/conf
# export LD_LIBRARY_PATH=/opt/cloudera/parcels/CDH/lib/hadoop/lib/native:${LD_LIBRARY_PATH}

CLASS_NAME="com.splicemachine.tutorials.sparkstreaming.mqtt.SparkStreamingMQTT"
export ADDITIONAL_JARS="${SPLICE_LIB_DIR}/spark-streaming-mqtt_2.10-1.6.1.jar,${SPLICE_LIB_DIR}/org.eclipse.paho.client.mqttv3-1.1.0.jar"
export APPLICATION_JAR="${SPLICE_LIB_DIR}/splice-tutorial-mqtt-2.0.1.18.jar"

exec "${SPARK_HOME}"/bin/spark-submit \
  --name TutorialMQTT \
  --master yarn \
  --deploy-mode cluster \
  --executor-memory 2G \
  --num-executors 3 \
  --class ${CLASS_NAME} \
  --jars ${ADDITIONAL_JARS} \
  "${APPLICATION_JAR}" \
  "$@"