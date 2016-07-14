JAR="/opt/splice/default/lib/splice-tutorial-kafka-producer-2.0.jar"
KAFKALIBDIR="/opt/kafka/default/libs"
APPENDSTRING=`echo $KAFKALIBDIR/*.jar | sed 's/ /:/g'`

java -cp $JAR:$APPENDSTRING -Dlog4j.configuration=file:///tmp/log4j.properties com.splicemachine.tutorials.sparkstreaming.kafka.KafkaTopicProducer stl-colo-srv54:9092 $@
