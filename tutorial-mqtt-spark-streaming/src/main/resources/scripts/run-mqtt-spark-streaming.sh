#!/usr/bin/env bash

export LD_LIBRARY_PATH=/opt/mapr/hadoop/hadoop-2.7.0/lib/native/:$LD_LIBRARY_PATH
export HADOOP_CONF_DIR=/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop/
export SPARK_HOME=/opt/mapr/spark/spark-1.6.1

#The jar file containing the spark job
export APPLICATION_JAR="/opt/splice/default/lib/splice-tutorial-mqtt-2.0.jar"
#Comma delimited list of jars the application is dependent on
export ADDITIONAL_JARS="/opt/splice/default/lib/spark-streaming-mqtt_2.10-1.6.1.jar,/opt/splice/default/lib/org.eclipse.paho.client.mqttv3-1.1.0.jar"

EXAMPLE_CLASS="com.splicemachine.tutorials.sparkstreaming.mqtt.SparkStreamingMQTT"

exec "${SPARK_HOME}"/bin/spark-submit \
  --name TutorialMQTT \
  --master yarn \
  --deploy-mode cluster \
  --executor-memory 2G \
  --num-executors 3 \
  --class $EXAMPLE_CLASS \
  --jars $ADDITIONAL_JARS \
  "$APPLICATION_JAR" \
  "$@"