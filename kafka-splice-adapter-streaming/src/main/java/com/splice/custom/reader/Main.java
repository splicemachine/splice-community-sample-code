package com.splice.custom.reader;

import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.security.token.TokenUtil;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;

import org.apache.hadoop.hbase.TableName;

import com.splicemachine.access.hbase.HBaseConnectionFactory;

import com.splicemachine.access.HConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import java.security.PrivilegedExceptionAction;
import com.splicemachine.client.SpliceClient;
import org.apache.spark.SparkConf;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.*;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Row;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.FileSystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import com.splicemachine.derby.impl.SpliceSpark;
import com.splicemachine.spark.splicemachine.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.apache.spark.streaming.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Tuple2;


public class Main {

    public static void main(String[] args) throws Exception {

        if(args.length < 7) {
            System.err.println("Incorrect number of params ");
            return;
        }
        final String inTargetTable = args[0];
        final String inTargetSchema = args[1];
        final String inHostName = args[2];
        final String inHostPort = args[3];
        final String inUserName = args[4];
        final String inUserPassword = args[5];
        final String kafkaBroker = args[6];
        final String kafkaTopicName = args[7];

	String inKbrPrincipal = System.getProperty("spark.yarn.principal");
	String inKbrKeytab = System.getProperty("spark.yarn.keytab");

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();

        System.out.println("Logged in as: " + ugi);
        System.out.println("Has credentials: " + ugi.hasKerberosCredentials());
        System.out.println("credentials: " + ugi.getCredentials());
        System.out.println("Kafka Broker: " + kafkaBroker);
        System.out.println("Kafka TopicName: " + kafkaTopicName);

        System.out.println(inKbrPrincipal);
        System.out.println(inKbrKeytab);

        // Initalize Kafka config settings
        Properties props = new Properties();
        SparkConf conf = new SparkConf().setAppName("stream");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));
  
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", kafkaBroker+":9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "test");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
       
        Collection<String> topics = Arrays.asList(kafkaTopicName);
       
        JavaInputDStream<ConsumerRecord<String, String>> stream =
            KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));
       
        JavaPairDStream<String, String> resultRDD = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));


        doWork(inTargetTable, inTargetSchema, inHostName, inHostPort, inUserName, inUserPassword, inKbrPrincipal, inKbrKeytab, resultRDD, jssc);


    }

    private static void doWork(String inTargetTable, String inTargetSchema, String inHostName, String inHostPort, String inUserName, String inUserPassword, String inKbrPrincipal, String inKbrKeytab, JavaPairDStream<String, String> resultRDD, JavaStreamingContext jssc) throws IOException, InterruptedException {

        SparkConf conf = new SparkConf();
        SparkSession spark = SparkSession.builder().appName("Reader").config(conf).getOrCreate();

        // Create Splice's Spark Session
        SpliceSpark.setContext(spark.sparkContext());

        SparkConf sparkConf = spark.sparkContext().getConf();
        String principal = sparkConf.get("spark.yarn.principal");
        String keytab = sparkConf.get("spark.yarn.keytab");
        System.out.println("spark.yarn.principal = " + sparkConf.get("spark.yarn.principal"));
        System.out.println("spark.yarn.keytab = " + sparkConf.get("spark.yarn.keytab"));
        System.out.print("principal: " + inKbrPrincipal);
        System.out.print("keytab: " + inKbrKeytab);

        String dbUrl = "jdbc:splice://" + inHostName + ":" + inHostPort + "/splicedb;user=" + inUserName + ";" + "password=" + inUserPassword;
        
        // Create a SplicemachineContext based on the provided DB connection
        SplicemachineContext splicemachineContext = new SplicemachineContext(dbUrl);

        // Set target tablename and schemaname
        String SPLICE_TABLE_ITEM = inTargetSchema + "." + inTargetTable;

        resultRDD.foreachRDD((RDD, time) -> {
          JavaRDD<String> rdd = RDD.values();

          JavaRDD<Row> rowJavaRDD = rdd.map(new Function<String, String[]>() {
                @Override
                public String[] call(String line) throws Exception {
                    return line.split(",");
                }
          }).map(new Function<String[], Row>() {
                @Override
                public Row call(String[] r) throws Exception {
                    return RowFactory.create(r[0], Integer.parseInt(r[1]), Boolean.parseBoolean(r[2]));
                }
          });

          Dataset<Row> ds = spark.createDataFrame(rowJavaRDD, createSchema());
          splicemachineContext.insert(ds, SPLICE_TABLE_ITEM);

        });

        jssc.start();              // Start the computation
        jssc.awaitTermination();   // Wait for the computation to terminate

    }

    // Match the test_table schema
    private static StructType createSchema() {
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("COL1", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("COL2", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("COL3", DataTypes.BooleanType, true));

        StructType schema = DataTypes.createStructType(fields);
        return (schema);
    }

}
