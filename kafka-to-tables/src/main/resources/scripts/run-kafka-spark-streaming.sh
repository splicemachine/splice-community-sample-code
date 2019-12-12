#!/usr/bin/env bash

CLASS_NAME="com.splice.custom.reader.MultipleSchemasStreamer"
APPLICATION_JAR="kafka-to-tables-1.0-1.jar"

SPARK_JARS_DIR="${SPARK_HOME}/jars"




"${SPARK_HOME}"/bin/spark-submit \
  --class ${CLASS_NAME} \
  --jars "$SPLICE_LIB_DIR/*,$CLOUDERA_KAFKA, $HBASE_JAR" \
  --conf "spark.executor.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*" \
  --conf "spark.driver.extraClassPath=/etc/hadoop/conf/:/etc/hbase/conf/:/opt/cloudera/parcels/SPLICEMACHINE/lib/*:/opt/cloudera/parcels/SPARK2/lib/spark2/jars/*:/opt/cloudera/parcels/CDH/lib/hbase/lib/*" \
  "${APPLICATION_JAR}" \
  "$@"


