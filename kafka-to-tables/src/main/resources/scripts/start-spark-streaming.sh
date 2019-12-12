#!/usr/bin/env bash
. ./setenv.sh

./run-kafka-spark-streaming.sh  "$KAFKA_BROKER" "$SPLICE_JDBC_URL" "$MAX_RATE" "$KAFKA_CONSUMER_GROUP" "$TOPICMAPPING"
