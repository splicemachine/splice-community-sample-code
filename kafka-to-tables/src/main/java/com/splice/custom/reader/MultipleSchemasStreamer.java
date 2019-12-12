package com.splice.custom.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.spark.splicemachine.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;


public class MultipleSchemasStreamer {

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's EXECUTOR log
     */
    private static final Logger LOG = Logger
            .getLogger(MultipleSchemasStreamer.class);


    /**
     * The parameters that get passed in as follows:
     *
     *     1. kafkaBroker (required) - Kafka Broker
     *     2. JDBC Url (required) - JDBC Url
     *     3. maxRate (required) - Max Rate
     *     4. groupId (required) - kafka consumers group id
     *     5. topicMappings (required) in the form of a list with this format "topicname:offset>schemaname.tablename,topicname:offset>schemaname.tablename,..." required, list of topic to schema mappings
     *
     *
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {

        if(args.length < 5) {
            System.err.println("Incorrect number of parameters.");
            System.err.println("Parameters:");
            System.err.println("  Kafka Broker URL (i.e. \"localhost:9192\")");
            System.err.println("  JDBC URL (i.e. \"jdbc:splice://localhost:1527/splicedb;user=splice=password=admin\" )");
            System.err.println("  Max Rate : max mini-batch size (i.e. 30000)");
            System.err.println("  Kafka Consumer Group Id (i.e. \"ConsumerGroup1\")");
            System.err.println("  Topic Mappings (i.e. \"sales-topic:earliest>RETAIL.SALESTRANSACTION,customer-topic:earliest>RETAIL.CUSTOMER\")");
            return;
        }

        final String kafkaBroker = args[0];
        final String inJdbcUrl = args[1];
        final String maxRate = args[2];
        final String groupId = args[3];
        final String topicMappings = args[4];

        ArrayList<TopicMapping> mappings = new ArrayList<TopicMapping>();




        LOG.info("**** Parameters:");
        LOG.info("Kafka Broker: " + kafkaBroker);
        LOG.info("Kafka Consumer Group Id: " + groupId);
        LOG.info("JDBC URL: " + inJdbcUrl);
        LOG.info("Kafka / Spark MaxRate: " + maxRate);
        LOG.info("Topic Mapping String: "+ topicMappings);

        mappings = parseTopicMappings(topicMappings);

        // Initalize Kafka config settings
        Properties props = new Properties();
        SparkConf conf = new SparkConf().setAppName("stream-kafka-2-tables");

        //When a connection is initially made to kafka, it pulls
        //All the records down from the queue which causes a huge
        //delay in processing the first set of records
        //There is supposed to be two ways to control this.  The first
        //is with dynamic allocation the second is with back pressure
        conf.set("spark.streaming.backpressure.enabled","true");
        conf.set("spark.streaming.kafka.maxRate",maxRate);  //For some reason the initial records are 10,000
        conf.set("spark.streaming.kafka.maxRatePerPartition",maxRate);

        LOG.info("************ Spark configuration:" + conf.toDebugString());

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));


        for ( TopicMapping topicMapping: mappings) {

            LOG.info("<<<<<TOPIC MAP>>>>>\nKafka Topic: " + topicMapping.getTopicName());
            LOG.info("Target Table: " + topicMapping.getTableName());
            LOG.info("Target Schema: " + topicMapping.getSchemaName());
            LOG.info("Kafka Offset: " + topicMapping.getOffset());

            // read table metadata and prep row builder
            TargetTable rowBuilder = new TargetTable( inJdbcUrl, topicMapping.getSchemaName(), topicMapping.getTableName());
            LOG.info("Row Builder: "+rowBuilder.toString());

            topicMapping.setTableRowBuilder( rowBuilder);

            // build Dstream for each topic -> table mapping
            Map<String, Object> kafkaParams = new HashMap<>();
            kafkaParams.put("bootstrap.servers", kafkaBroker);
            kafkaParams.put("key.deserializer", StringDeserializer.class);
            kafkaParams.put("value.deserializer", StringDeserializer.class);
            kafkaParams.put("group.id", groupId);
            kafkaParams.put("auto.offset.reset", topicMapping.getOffset());
            kafkaParams.put("enable.auto.commit", false);

            Collection<String> topics = Arrays.asList(topicMapping.getTopicName());

            JavaInputDStream<ConsumerRecord<String, String>> stream =
                    KafkaUtils.createDirectStream(
                            jssc,
                            LocationStrategies.PreferConsistent(),
                            ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

            JavaPairDStream<String, String> resultRDD = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

            topicMapping.setStreamRDD(resultRDD);
        }

        doWork(mappings, inJdbcUrl, jssc);


    }

    /***
     *
     * @param topicMappings is a string in the format topicName1[:offset1]>schemaName1.tableName1,topicName2[:offset2]>schemaName2:tableName2
     * @return ArrayList of TopicMapping objects containing the topic mapping settings
     */
    private static ArrayList<TopicMapping> parseTopicMappings(String topicMappings)
    {
        ArrayList<TopicMapping> result = new ArrayList<>();

        String[] topics = topicMappings.split(",");
        for(String topicMap:topics)
        {
            TopicMapping mapping = new TopicMapping();

            String[] topicMapSplit = topicMap.split(">");
            if (topicMapSplit.length!=2)
            {
                throw new IllegalArgumentException("Format should be 'topicName1[:offset1]>schemaName1.tableName1,topicName2[:offset2]>schemaName2:tableName2...' ");
            }

            // parse "topicName[:offset]"
            String[] nameAndOffset = topicMapSplit[0].split(":");
            mapping.setTopicName(nameAndOffset[0]);
            if (nameAndOffset.length>1)
            {
                mapping.setOffset(nameAndOffset[1]);
            }

            //parse "schema.table"
            String[] schemaAndTable = topicMapSplit[1].split("[.]+");
            if (schemaAndTable.length!=2)
            {
                throw new IllegalArgumentException("Format should be 'topicName1[:offset1]>schemaName1.tableName1,topicName2[:offset2]>schemaName2:tableName2...' ");
            }
            mapping.setSchemaName(schemaAndTable[0].toUpperCase());
            mapping.setTableName(schemaAndTable[1].toUpperCase());

            result.add(mapping);
        }

        return result;
    }

    private static void doWork(ArrayList<TopicMapping> topicMappings, String jdbcUrl, JavaStreamingContext jssc) throws IOException, InterruptedException {

        SparkConf conf = new SparkConf();
        SparkSession spark = SparkSession.builder().appName("Reader").config(conf).getOrCreate();

        // Create Splice's Spark Session
        SpliceSpark.setContext(spark.sparkContext());

        SparkConf sparkConf = spark.sparkContext().getConf();

        // Create a SplicemachineContext based on the provided DB connection
        SplicemachineContext splicemachineContext = new SplicemachineContext(jdbcUrl);


        for( TopicMapping mapping : topicMappings) {

            JavaPairDStream<String,String> resultRDD = mapping.getStreamRDD();
            TargetTable rowBuilder = mapping.getTableRowBuilder();


            StructType schema = TargetTable.getSchema(rowBuilder);

            // setup each RDDs processing pipeline
            resultRDD.foreachRDD((RDD, time) ->
            {
                JavaRDD<String> rdd = RDD.values();

                JavaRDD<Row> rowJavaRDD = rdd.filter(new Function<String, Boolean>() {
                    public Boolean call(String line) {
                        return line != null && line.length() > 0;
                    }
                }).map(new Function<String, Map<String, Object>>() { //JSON string to Attribute:Object MAP
                    @Override
                    public Map<String, Object> call(String line) throws Exception {
                        return new ObjectMapper().readValue(line, HashMap.class);
                    }
                }).map(new Function<Map<String, Object>, Row>() {  // Attribute/Object Map to target table Row
                    @Override
                    public Row call(Map<String, Object> r) throws Exception {
                        return TargetTable.getRow( rowBuilder, r);
                    }
                });

                Dataset<Row> ds = spark.createDataFrame(rowJavaRDD, schema);
                splicemachineContext.upsert(ds, mapping.getFullTableName());

            });
        }

        jssc.start();              // Start the computation
        jssc.awaitTermination();   // Wait for the computation to terminate

    }
}
