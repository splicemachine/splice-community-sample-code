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
package com.splicemachine.tutorials.tsdbanalytics;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.VoidFunction;

import com.splicemachine.tutorials.tsdbanalytics.dataobjects.AggregationResult;

/**
 * This saves the AggregationResult records to a Splice table and returns AggregationResult
 *
 * @author Jyotsna Ramineni
 */

public class SaveLogAggPartition implements VoidFunction<Iterator<AggregationResult>>, Externalizable {
    private static final Logger LOG = Logger.getLogger(SaveLogAggPartition.class);
    // -AMM- TODO this should at least be a passed arg, if not obtained form somewhere else
    public String splicehost = "localhost";

    @Override
    public void call(Iterator<AggregationResult> aggResItr) throws Exception {

        if (aggResItr != null && aggResItr.hasNext()) {
            LOG.info("Data to process in partition:");

            //Create List of AggregationResult objects from Iterator
            List<AggregationResult> aggLogs = new ArrayList<AggregationResult>();
            while (aggResItr.hasNext()) {
                aggLogs.add(aggResItr.next());
            }

            int numRcds = aggLogs.size();
            LOG.info(" Number of records to process in partition:" + numRcds);
            if (numRcds > 0) {

                //Get Connection to Splice Database
                Connection con = DriverManager.getConnection("jdbc:splice://" + splicehost + ":1527/splicedb;user=splice;password=admin");

                // Insert Statement Using VTI to convert the AggregationResult
                // Object list to Splice format for insert statement
                String vtiStatement = "INSERT INTO LOGAGG.AggregateResults "
                        + "select s.* from new com.splicemachine.tutorials.tsdbanalytics.AggregationResultVTI(?) s ("
                        + AggregationResult.getTableDefinition() + ")";

                PreparedStatement ps = con.prepareStatement(vtiStatement);

                ps.setObject(1, aggLogs);
                try {
                    ps.execute();
                } catch (Exception e) {
                    LOG.error("Exception inserting data:" + e.getMessage(), e);
                } finally {
                    if (ps != null)
                        ps.close();
                    LOG.info("Inserted Complete");

                }
                if (con != null)
                    con.close();
            }
        } else
            LOG.info("No Records to Process in Partition");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        // TODO Auto-generated method stub
    }
}
