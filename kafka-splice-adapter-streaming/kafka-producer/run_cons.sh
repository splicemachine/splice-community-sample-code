#!/bin/bash

KAFKA=/home/splice/kafka_2.10-0.10.0.1
java -cp "$KAFKA/libs/*":. SimpleConsumer test-k


