#!/usr/bin/env bash

###############################################################################
#  this is an example script the will require edits to make it work in any
#  environment.
###############################################################################
## THIS IS CLUSTER/PLATFORM DEPENDENT
# The Kafka lib directory
# MapR or HDP
#KAFKA_LIB_DIR="/opt/kafka/default/libs"
# Cloudera
#KAFKA_LIB_DIR="/opt/cloudera/parcels/KAFKA/lib/kafka/libs/"
KAFKA_LIB_DIR="/opt/kafka/default/libs/"

#The location where you placed the custom jar created by the maven process
SPLICE_KAFKA_JAR="/tmp/splice-tutorial-iot-kafka-2.5.0.1707.jar"

APPENDSTRING=`echo ${KAFKA_LIB_DIR}/*.jar | sed 's/ /:/g'`

java -cp ${SPLICE_KAFKA_JAR}:${APPENDSTRING} \
    com.splicemachine.tutorials.kafka.PutIOTDataOnKafkaQueueFromFile \
    $@
