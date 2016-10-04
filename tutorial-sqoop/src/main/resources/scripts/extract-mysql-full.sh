#!/bin/bash

RUNDATE=$(date +"%m-%d-%Y_%T")
CONFIG=$TUTORIAL_HOME/src/main/resources/scripts/mysql/mysql-config.txt
TABLES=$TUTORIAL_HOME/src/main/resources/scripts/mysql/mysql-tables.txt
SCHEMA=test
IMPORTDIR=$TUTORIAL_HOME/src/main/resources/scripts/mysql/import
QUERYDIR=$TUTORIAL_HOME/src/main/resources/scripts/mysql/query
LOGFILE=$TUTORIAL_HOME/src/main/resources/scripts/mysql/logs/extract-mysql-$RUNDATE.log
HADOOPSBIN=$HADOOP_HOME/sbin
SPLICEBIN=$SPLICE_HOME/bin

./run-sqoop-full.sh $CONFIG $TABLES $SCHEMA $IMPORTDIR $QUERYDIR $LOGFILE $HADOOPSBIN $SPLICEBIN > $LOGFILE 2>&1

if [ $? -gt 0 ]; then
	echo run-sqoop-full.sh failed
	exit 1
else
	echo run-sqoop-full.sh successful
	exit 0
fi
