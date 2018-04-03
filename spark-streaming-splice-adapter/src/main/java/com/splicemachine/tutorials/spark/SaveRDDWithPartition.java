/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package main.java.com.splicemachine.tutorials.spark;

import com.splicemachine.spark.splicemachine.SplicemachineContext;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This processes each RDD that is read in from the spark stream
 *
 * @author Erin Driggers
 */
public class SaveRDDWithPartition implements VoidFunction<JavaRDD<String>>, Externalizable {

    /**
     * Log object to log messages.  The log messages in this class will appear
     * in the spark's DRIVER log
     */
    private static final Logger LOG = Logger
            .getLogger(SaveRDDWithPartition.class);

    /**
     * The full JDBC URL for the splice machine database
     */
    private String spliceJdbcUrl;

    /**
     * For this initial code, we will pass in the schema where the tables reside.  In the future the
     * requirement will be to use the tenant id for the schema.
     */
    private String spliceSchema;

    /**
     * This is the table where we want to insert data
     */
    private String spliceTable;


    private String spliceFullTable;

    /**
     * The import parameters
     */
    private HashMap<String, String> importParms = null;


    private String INGESTION_COUNT_TABLE = "SPLICE.INGESTION_COUNT";

    /**
     * Stored the schema for the ingestion count
     */
    StructType ingestion_schema_item = null;


    private StructType splice_schema_item = null;


    private AtomicLong numRcdsProcessed = new AtomicLong(0);


    /**
     * Constructor for the SaveRDDWithPartions which allows you to pass in
     * the Splice JDBC url and the default splice schema.
     *
     * @param spliceJdbcUrl
     * @param spliceSchema
     * @param spliceTable
     */
    public SaveRDDWithPartition(String spliceJdbcUrl, String spliceSchema, String spliceTable, HashMap<String, String> importParms) {
        LOG.info("In constructor");

        this.spliceJdbcUrl = spliceJdbcUrl;
        this.spliceSchema = spliceSchema;
        this.spliceTable = spliceTable;
        this.importParms = importParms;

        this.spliceFullTable = this.spliceSchema + "." + this.spliceTable;
    }

    /**
     * A RDD is created on the driver for the blocks created during the batchInterval.
     * The blocks generated during the batchInterval are partitions of the RDD. Each
     * partition is a task in spark.
     * <p>
     * This takes the incoming RDD's partitions and for each partition
     * it calls SavePartition.  The SavePartition processes is distributed to the
     * non-driver executors to perform their operations and result in
     * Parallelization.
     * <p>
     * To understand this in more detail, read the section 'Level of
     * Parallelism in Data Receiving' in the Spark Streaming programming
     * guide
     *
     * @param messagesRdd - an RDD that is comprised of JSON strings describing various thing types
     * @return
     * @throws Exception
     */
    @Override
    public void call(JavaRDD<String> messagesRdd) throws Exception {
        LOG.info("In call");
        if (messagesRdd != null) {
            LOG.info("messagesRdd is not null");

            SparkContext context = messagesRdd.context();
            SparkConf conf = context.getConf();
            SparkSession session = SparkSession.builder().config(conf).getOrCreate();
            SplicemachineContext splicemachineContext = new SplicemachineContext(spliceJdbcUrl);


            JavaRDD<Row> messagesRowsRDD = messagesRdd.mapPartitions(new MapPartition(this.getSpliceStructType(session, splicemachineContext, this.spliceFullTable), this.importParms));
            if(messagesRowsRDD != null) {
                LOG.info("messagesRowsRDD is not null");
                Dataset<Row> saveData = session.createDataFrame(messagesRowsRDD, splice_schema_item);
                long rcdsToSave = saveData.count();
                if (rcdsToSave > 0) {
                    LOG.info("SaveRDDWithPartition.java: ************ Insert Kafka messages to splice DB:" + rcdsToSave);
                    splicemachineContext.insert(saveData, this.spliceFullTable);
                    numRcdsProcessed.addAndGet(rcdsToSave);
                    insertIngestionCount(session, splicemachineContext);
                }
            }
        }
    }

    public StructType getSpliceStructType(SparkSession session, SplicemachineContext splicemachineContext, String spliceFullTable) {
        if(this.splice_schema_item == null) {
            try {
                this.splice_schema_item = splicemachineContext.getSchema(spliceFullTable);
            } catch (Exception e) {
                this.splice_schema_item = splicemachineContext.getSchema(spliceFullTable);
            }
        }
        return this.splice_schema_item;
    }

    public void insertIngestionCount(SparkSession session, SplicemachineContext splicemachineContext) {

        if(ingestion_schema_item == null) {
            try {
                ingestion_schema_item = splicemachineContext.getSchema(INGESTION_COUNT_TABLE);
            } catch (Exception e) {
                ingestion_schema_item = splicemachineContext.getSchema(INGESTION_COUNT_TABLE);
            }
        }

        List<Row> rowsToInsert = new ArrayList();
        Object[] vals = new Object[3];
        vals[0] = "" + Thread.currentThread().getId(); //A unique id for this thread
        vals[1] = new Timestamp(System.currentTimeMillis()); //The current timestamp
        vals[2] = numRcdsProcessed.get();  //Number of records inserted
        rowsToInsert.add(RowFactory.create(vals));

        Dataset<Row> saveData = session.createDataFrame(rowsToInsert, ingestion_schema_item);
        splicemachineContext.insert(saveData, INGESTION_COUNT_TABLE);

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}

