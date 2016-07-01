package com.splicemachine.tutorials.storm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/*
 * Spout that gets data from mysql
 */
public class MySqlSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;
	private TopologyContext _context;
	private SpoutOutputCollector _collector;
	private transient Connection con = null;
	static Queue<String> bufferQueue = new ConcurrentLinkedQueue<String>();
	static int index = 0;
	final static int SIZE = 10;
    
    public boolean isDistributed() {
        return true;
    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {   
    	_context = context;
        _collector = collector;
    }
    
	private void seedBufferQueue(String server, String db, String user, String pwd){
		try {
			final MySqlConnector connector = new MySqlConnector();
			con = connector.getConnection(server, db, user, pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		PreparedStatement pst = null;

		try {
			//sql statement used to extract data from mysql
			pst = con.prepareStatement("SELECT name FROM students");
			index+=SIZE;
			
			// execute the SQL
			ResultSet res = pst.executeQuery();
			while (res.next()) {
				//data to be extracted
				String name = res.getString("name");
				bufferQueue.add(name);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void nextTuple() {			
		if(bufferQueue.isEmpty()){
			//pass in mysql server, db name, user, password
			seedBufferQueue("localhost", "test", "root", "");
			Utils.sleep(100);
		} else {
			//Replace name with the data being extracted.  This example expects only name to be returned in the sql
			//and thus is only item outputed by the spout.  To add additional data add them to the values using new Values(value1, value2, etc)
			//then emit the values
			String name = bufferQueue.poll();	
			if (name != null) {
				Values values = new Values();				
				values.add(name);
				_collector.emit(values);
			}
			Utils.sleep(50);
		}	
	}
    
    @Override
    public void close() {        
    }
    
    @Override
    public void ack(Object id) {
    }
    
    @Override
    public void fail(Object id) {
    }
     
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("name"));
    }
    
    @Override
    public void activate() {}
    
    @Override
    public void deactivate() {}
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
    
}
