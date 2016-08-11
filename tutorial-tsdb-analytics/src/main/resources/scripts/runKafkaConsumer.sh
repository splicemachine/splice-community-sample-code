#!/usr/bin/env bash

export LD_LIBRARY_PATH=/opt/mapr/hadoop/hadoop-2.7.0/lib/native/:$LD_LIBRARY_PATH
export HADOOP_CONF_DIR=/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/
export SPARK_HOME=/opt/mapr/spark/spark-1.6.1

#The jar file containing the spark job
export APPLICATION_JAR="/opt/splice/default/lib/splice-tutorial-tsdb-analytics-2.0.jar"
#Comma delimited list of jars the application is dependent on
export ADDITIONAL_JARS="/opt/splice/SPLICEMACHINE-2.0.1.18.mapr5.1.0.p0.75/lib/spark-streaming-kafka_2.10-1.6.1.jar,/opt/kafka/default/libs/kafka-clients-0.9.0.1.jar"
EXAMPLE_CLASS="com.splicemachine.tutorials.tsdbanalytics.LogAggregator"

exec "${SPARK_HOME}"/bin/spark-submit \
  --name TutorialTSBD \
  --master yarn \
  --deploy-mode cluster \
  --driver-memory 4g \
  --executor-memory 2G \
  --executor-cores 4 \
  --num-executors 8 \
  --class $EXAMPLE_CLASS \
  --jars $ADDITIONAL_JARS \
  "$APPLICATION_JAR" \
  "test" \
  "tsdb" \
  "$@"