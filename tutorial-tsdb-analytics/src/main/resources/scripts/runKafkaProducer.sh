#!/usr/bin/env bash

###############################################################################
#  this is an example script the will require edits to make it work in any
#  environment.
###############################################################################
HOST="servername.domain.internal"
## THIS IS CLUSTER/PLATFORM DEPENDENT
# MapR or HDP
SPLICE_LIB_DIR="/opt/splice/default/lib"
KAFKA_LIB_DIR="/opt/kafka/default/libs"
# Cloudera
# SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"
# KAFKA_LIB_DIR="/opt/cloudera/parcels/KAFKA/lib/kafka/libs/"

SPLICE_TUTORIAL_JAR="${SPLICE_LIB_DIR}/splice-tutorial-tsdb-analytics-2.0.1.18.jar"
GSON_JAR="${SPLICE_LIB_DIR}/gson-2.7.jar"
APPENDSTRING=`echo ${KAFKA_LIB_DIR}/*.jar | sed 's/ /:/g'`

java -cp ${SPLICE_TUTORIAL_JAR}:${APPENDSTRING}:${GSON_JAR} \
    -Dlog4j.configuration=file:///tmp/log4j.properties \
    com.splicemachine.tutorials.tsdbanalytics.kafkaStreamGenerator \
    ${HOST}:9092 tsdb $@
