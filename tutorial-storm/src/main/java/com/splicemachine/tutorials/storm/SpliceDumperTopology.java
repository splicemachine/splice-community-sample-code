package com.splicemachine.tutorials.storm;

import java.sql.SQLException;
import java.util.ArrayList;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/*
 * Sample Topology using the SpliceDumperBolt
 * In order for this to work, the table column names should match the fields of the input stream tuples
 * For eg. the table used below should have word and number as the columns.
 */
public class SpliceDumperTopology {

    public static void main(String[] args) throws SQLException {
    	
        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<String> columnTypes = new ArrayList<String>();
        //this table must exist in splice
        //create table testTable (word varchar(100), number int);
        String tableName = "testTable";
        String server = "localhost";
        
        // add the column names and the respective types in the two arraylists
        columnNames.add("word");
        columnNames.add("number");
        
        // add the types
        columnTypes.add("varchar (100)");
        columnTypes.add("int");
        
        TopologyBuilder builder = new TopologyBuilder();
        
        // set the spout for the topology
        builder.setSpout("spout", new SpliceIntegerSpout(), 10);
        
        // dump the stream data into splice       
        SpliceDumperBolt dumperBolt = new SpliceDumperBolt(server, tableName);
        builder.setBolt("dumperBolt", dumperBolt, 1).shuffleGrouping("spout");
        Config conf = new Config();
        conf.setDebug(true);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("splice-topology", conf, builder.createTopology());
        Utils.sleep(10000);
        cluster.shutdown();
    }
}
