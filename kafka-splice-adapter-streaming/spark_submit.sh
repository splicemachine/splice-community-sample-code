#!/bin/bash

TargetTable=TEST_TABLE
TargetSchema=SPLICE
RSHostName=localhost
SpliceConnectPort=1527
UserName=splice
UserPassword=admin
SparkDeployMode=cluster
ResourceManager=stl-colo-srv056
NameNodes=stl-colo-srv056
KafkaBroker=stl-colo-srv056
KafkaTopic=test-k
KrbPrincipal=hbase/stl-colo-srv056.splicemachine.colo@SPLICEMACHINE.COLO
KrbKeytab=/tmp/hbase.keytab

export SPARK_HOME="/opt/cloudera/parcels/SPARK2/lib/spark2"
export APPJARS=$(echo /tmp/dependency/*.jar | sed 's/ /,/g' )
export COLON_APPJARS=$(echo /tmp/dependency/*.jar | sed 's/ /:/g' )

$SPARK_HOME/bin/spark-submit --class com.splice.custom.reader.Main \
--master yarn \
--conf "spark.executor.extraClassPath=/etc/hadoop/conf:/etc/hbase/conf:/tmp/dependency/*:/opt/cloudera/parcels/CDH/lib/hbase/*:$COLON_APPJARS" \
--jars $APPJARS \
--deploy-mode $SparkDeployMode \
--principal $KrbPrincipal \
--keytab $KrbKeytab \
--driver-java-options  -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=4002 \
--driver-memory 4g \
--conf "spark.yarn.executor.extraJavaOptions=-Dspark.yarn.appMasterEnv.HADOOP_JAAS_DEBUG=true -Dsun.security.krb5.debug=true -Dsun.security.spnego.debug=true" \
--executor-memory 2g \
--executor-cores 1 \
--name DataGen \
--driver-cores 4 \
--num-executors 40 \
--driver-library-path /opt/cloudera/parcels/CDH/lib/hadoop/lib/native \
--driver-class-path /etc/hadoop/conf:/etc/hbase/conf:/tmp/dependency/*:/opt/cloudera/parcels/CDH/lib/hbase/*:$COLON_APPJARS \
/home/splice/stream-app/target/reader-1.0-SNAPSHOT.jar \
$TargetTable $TargetSchema $RSHostName $SpliceConnectPort $UserName $UserPassword $KafkaBroker $KafkaTopic

