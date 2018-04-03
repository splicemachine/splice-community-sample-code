#!/bin/bash -f

#
## Build Streaming Example as a docker container in splicemachine
## NOTE: It requires 'docker login' to splicemachine Docker Hub repo.
#

VERSION=2.0.3

curl https://s3.amazonaws.com/splicemachine/2.6.1.1736-1.0.777/artifacts/spark-2.1.1-bin-hadoop2.6.tgz -o spark-2.1.1-bin-hadoop2.6.tgz
tar -xf spark-2.1.1-bin-hadoop2.6.tgz

cp ../src/resources/scripts/run-kafka-spark-streaming.sh .
cp ../src/resources/scripts/start-spark-streaming.sh .
cp ../src/resources/spark/conf/spark-defaults.conf .

docker build -t tutorial-spark-kafka-consumer:$VERSION .
docker push splicemachine/tutorial-spark-kafka-consumer:$VERSION

rm splice-tutorial-file-spark-2.6.1.1736.jar
rm scripts/run-kafka-spark-streaming.sh
rm start-spark-streaming.sh
rm spark-defaults.conf
