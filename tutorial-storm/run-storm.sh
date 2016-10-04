#!/usr/bin/env bash

# LIB_JAR_LIST=$(find target/classes/lib -name \*.jar)
# CP=$(echo ${LIB_JAR_LIST} | sed 's/ /:/g')
# CLASSPATH="target/splice-tutorial-storm-2.0.1.18.jar:${CP}"

java -cp target/splice-tutorial-storm-2.0.1.18-jar-with-dependencies.jar \
com.splicemachine.tutorials.storm.SpliceDumperTopology
