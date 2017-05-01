#!/usr/bin/env bash

#The broker for the kafka URL
KAFKA_BROKER=srv105:9092

#The kafka topic
TOPIC=spliceload

#The file that is being processed
FILE=/tmp/iot_fd001.txt

#The number of messages that should be put on the queue per second
NUM_MESSAGES_PER_SEC=200

#The number of milliseconds you should wait between each group of messages 
#That are put on the queue
PAUSE_BETWEEN_MESSAGES=500

#The number of time cycles for which the iot data is placed on queue
TIME_DURATION_CYCLES=500

#The starting number for unit id.
STRAT_UNIT_ID=1
/tmp/runKafkaIOTProducer.sh ${KAFKA_BROKER} ${TOPIC} ${FILE} ${NUM_MESSAGES_PER_SEC} ${PAUSE_BETWEEN_MESSAGES} ${TIME_DURATION_CYCLES} ${STRAT_UNIT_ID}