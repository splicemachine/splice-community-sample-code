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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

//import com.splicemachine.cs.storm.example.SpliceCommunicator;
//import com.splicemachine.cs.storm.example.SpliceConnector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

/*
 * Bolt for dumping data into splice
 */
public class SpliceDumperBolt implements IBasicBolt {

    private static final long serialVersionUID = 1L;
    private static transient SpliceCommunicator communicator = null;
    private transient SpliceConnector connector = new SpliceConnector();
    private transient Connection con = null;
    private String tableName = null;
    private ArrayList<String> fields = new ArrayList<String>();
    private ArrayList<Object> fieldValues = new ArrayList<Object>();

    public SpliceDumperBolt(String server, String tableName) throws SQLException {
        super();
        this.tableName = tableName;
        try {
            con = connector.getConnection(server);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        communicator = new SpliceCommunicator(con);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        fields = new ArrayList<String>();
        fields = (ArrayList<String>) input.getFields().toList();
        fieldValues = new ArrayList<Object>();
        fieldValues = (ArrayList<Object>) input.getValues();
        try {
            communicator.insertRow(this.tableName, fields, fieldValues);
        } catch (SQLException e) {
            System.out.println("Exception occurred in adding a row");
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
    }

    @Override
    public void cleanup() {
    }

}
