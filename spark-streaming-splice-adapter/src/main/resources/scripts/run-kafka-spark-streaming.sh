#!/usr/bin/env bash

export CLASS_NAME="com.splicemachine.tutorials.spark.SparkStreamingKafka"
export APPLICATION_JAR="splice-tutorial-file-spark-2.6.1.1736.jar"
export SPARK_JARS_DIR="${SPARK_HOME}/jars"

CURRENT_IP=$(ifconfig eth0 | grep inet | awk '{print $2}')
SPARK_IMAGE=${SPARK_IMAGE:-"splicemachine/tutorial-spark-kafka-consumer:2.0.3"}

echo "spark.driver.host                 $CURRENT_IP" >> $SPARK_HOME/conf/spark-defaults.conf
echo "spark.mesos.executor.docker.image $SPARK_IMAGE" >> $SPARK_HOME/conf/spark-defaults.conf

exec "${SPARK_HOME}"/bin/spark-submit \
  --class ${CLASS_NAME} \
  --files ${SPARK_HOME}/conf/hbase-site.xml,${SPARK_HOME}/conf/core-site.xml,${SPARK_HOME}/conf/hdfs-site.xml \
  "${APPLICATION_JAR}" \
  "$@"

