#!/bin/bash

set -e

mkdir /var/tmp/tmp_conf_files
curl -kLs  "http://hmaster-0-node.${FRAMEWORK}.mesos:16010/logs/conf.tar.gz" | tar -xz -C  /var/tmp/tmp_conf_files
cp  /var/tmp/tmp_conf_files/conf/core-site.xml $SPARK_HOME/conf/
cp  /var/tmp/tmp_conf_files/conf/fairscheduler.xml $SPARK_HOME/conf/
cp  /var/tmp/tmp_conf_files/conf/hbase-site.xml $SPARK_HOME/conf/
cp  /var/tmp/tmp_conf_files/conf/hdfs-site.xml $SPARK_HOME/conf/


rm -r /var/tmp/tmp_conf_files
exec "$@"