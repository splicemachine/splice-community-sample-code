JAR="/opt/splice/default/lib/splice-tutorial-tsdb-analytics-2.0.jar"
GSONJAR="/opt/splice/default/lib/gson-2.7.jar"
KAFKALIBDIR="/opt/kafka/default/libs"
APPENDSTRING=`echo $KAFKALIBDIR/*.jar | sed 's/ /:/g'`

#java -cp $JAR:$APPENDSTRING:$GSONJAR -Dlog4j.configuration=file:///tmp/log4j.properties com.splicemachine.tutorials.tsdbanalytics.kafkaStreamGenerator localhost:9092 $@
java -cp $JAR:$APPENDSTRING:$GSONJAR  com.splicemachine.tutorials.tsdbanalytics.kafkaStreamGenerator localhost:9092 tsdb $@
