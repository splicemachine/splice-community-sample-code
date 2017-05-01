#!/usr/bin/env bash
BROKER=srv105:9092
CONSUMER_GROUP=kstest
TOPIC_LIST=spliceload
SPARK_BATCH_INTERVAL=5
JDBC_URL=jdbc:splice://srv107:1527/splicedb;user=splice;password=admin
VTI_TYPE=vtiname
VTI_CLASS_NAME=com.splicemachine.tutorials.vti.ArrayListOfStringVTI
SPLICE_SCHEMA=IOT
SPLICE_TABLE=SENSOR_DATA

#sudo -su mapr ./run-kafka-spark-streaming.sh "${BROKER}" ${CONSUMER_GROUP} ${TOPIC_LIST} ${SPARK_BATCH_INTERVAL} "${JDBC_URL}" ${VTI_TYPE} ${VTI_CLASS_NAME} ${SPLICE_SCHEMA} ${SPLICE_TABLE}
sudo -su mapr ./run-kafka-spark-streaming.sh "${BROKER}" ${CONSUMER_GROUP} ${TOPIC_LIST} ${SPARK_BATCH_INTERVAL} "jdbc:splice://srv107:1527/splicedb;user=splice;password=admin" ${VTI_TYPE} ${VTI_CLASS_NAME} ${SPLICE_SCHEMA} ${SPLICE_TABLE}