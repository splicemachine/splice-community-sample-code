package com.splice.custom.reader;

import org.apache.spark.streaming.api.java.JavaPairDStream;

public class TopicMapping {
    String topicName;
    String schemaName;
    String tableName;
    String offset;


    TargetTable tableRowBuilder;

    public JavaPairDStream<String, String> getStreamRDD() {
        return streamRDD;
    }

    public void setStreamRDD(JavaPairDStream<String, String> streamRDD) {
        this.streamRDD = streamRDD;
    }

    JavaPairDStream<String, String> streamRDD;

    public TargetTable getTableRowBuilder() {
        return tableRowBuilder;
    }

    public void setTableRowBuilder(TargetTable tableRowBuilder) {
        this.tableRowBuilder = tableRowBuilder;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getFullTableName()
    {
        return schemaName+"."+tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}
