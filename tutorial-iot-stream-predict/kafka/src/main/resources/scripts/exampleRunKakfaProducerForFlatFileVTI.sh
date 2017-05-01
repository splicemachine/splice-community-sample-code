#!/usr/bin/env bash

#The broker for the kafka URL
KAFKA_BROKER=srv055:9092

#The kafka topic
TOPIC=spliceload

#The file that is being processed
FILE=/tmp/orders.tbl

#The number of messages that should be put on the queue per second
NUM_MESSAGES_PER_SEC=10000

#The number of milliseconds you should wait between each group of messages 
#That are put on the queue
PAUSE_BETWEEN_MESSAGES=2000

./runKafkaProducer.sh ${KAFKA_BROKER} ${TOPIC} ${FILE} ${NUM_MESSAGES_PER_SEC} ${PAUSE_BETWEEN_MESSAGES}
