#!/bin/bash -f

#
## Build Tutorial for a Kafka consumer using spark streaming
## This is a container that will run in splicemachine environment
## NOTE: It requires 'docker login' to splicemachine Docker Hub repo.
#

VERSION=${VERSION:-"2.0.3"}

CURRENT_DIRECTORY=$(pwd)

./compile.sh
if [ $? -eq 0 ]; then
    docker build --rm -f ./Docker/Dockerfile -t splicemachine/tutorial-spark-kafka-consumer:$VERSION .
    if [ $? -eq 0 ]; then
        docker push splicemachine/tutorial-spark-kafka-consumer:$VERSION
    fi
fi
