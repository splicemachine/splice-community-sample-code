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
package com.splicemachine.tutorials.storm;

import java.sql.SQLException;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/*
 * Sample Topology using the MySqlSpliceBolt
 * This example pulls data from a mysql database and inserts it into splice
 */
public class MySqlToSpliceTopology {

    public static void main(String[] args) throws SQLException {

        // tableName is the name of the table in splice to insert records to
        // server is the server instance running splice
        String tableName = "students";
        String server = "localhost";
        TopologyBuilder builder = new TopologyBuilder();

        // set the spout for the topology
        builder.setSpout("seedDataFromMySql", new MySqlSpout());

        // dump the stream data into splice       
        builder.setBolt("dbRowProcessing", new MySqlSpliceBolt(server, tableName), 1).shuffleGrouping("seedDataFromMySql");

        Config conf = new Config();
        conf.setDebug(true);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("mysql-splice-topology", conf, builder.createTopology());
        Utils.sleep(3000);
        cluster.shutdown();
    }
}
