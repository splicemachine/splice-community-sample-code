# environment dependent configuration
export SPARK_HOME="/opt/cloudera/parcels/SPARK2/lib/spark2"
export CLOUDERA_KAFKA="/opt/cloudera/parcels/SPARK2/lib/spark2/kafka-0.10/*"
export HBASE_JAR="/opt/cloudera/parcels/CDH/lib/hbase/lib/*"
export SPLICE_LIB_DIR="/opt/cloudera/parcels/SPLICEMACHINE/lib"

export SPARK_KAFKA_VERSION=0.10

# spark streaming application settings
export SPLICE_JDBC_URL="jdbc:splice://<hostname>:1527/splicedb;user=splice;password=admin"
export KAFKA_BROKER="<kafka host name>:9092"
export KAFKA_CONSUMER_GROUP="CONSUMER_GROUP1"
export MAX_RATE="30000"


# topic to table mapping list where each topic to table map has format topic:offset>schemaname.tablename
export TOPICMAPPING="topicName1:earliest>schemaname1.tablename1,topic2:earliest>schema2.table2"






