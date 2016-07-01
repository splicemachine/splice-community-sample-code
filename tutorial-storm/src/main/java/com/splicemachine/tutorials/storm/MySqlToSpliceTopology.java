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
    	
        //tableName is the name of the table in splice to insert records to
    		//server is the server instance running splice
        String tableName = "students";
        String server = "localhost";
        
        TopologyBuilder builder = new TopologyBuilder();

        // set the spout for the topology
        builder.setSpout("seedDataFromMySql", new MySqlSpout());
        
        // dump the stream data into splice       
        builder.setBolt("dbRowProcessing", new MySqlSpliceBolt(server,tableName), 1).shuffleGrouping("seedDataFromMySql");

		Config conf = new Config();
        conf.setDebug(true);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("mysql-splice-topology", conf, builder.createTopology());
        Utils.sleep(3000);
        cluster.shutdown();
    }
}
