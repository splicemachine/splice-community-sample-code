package com.splicemachine.tutorials.storm;

import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/*
 * Spout that generates random integers for words 
 * 
 */
public class SpliceIntegerSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;
	SpoutOutputCollector _collector;
    Random _rand;  
    int count = 0;
    
    public boolean isDistributed() {
        return true;
    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {                              
        _collector = collector;
        _rand = new Random();
    }
    
    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        String[] words = new String[] { "splice", "machine", "hadoop", "rdbms", "acid", "sql", "transactions"};
        Integer[] numbers = new Integer[] {
                1,2,3,4,5,6,7
        };
        if(count == numbers.length -1) {
            count = 0;
        }
        count ++;
        int number = numbers[count];
        String word = words[count];
        int randomNum = (int) (Math.random()*1000);
        System.out.println("Random Number: " +randomNum);
        System.out.println("SpliceIntegerSpout emitting: " + number);
        _collector.emit(new Values(word, number));       
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
        declarer.declare(new Fields("word", "number"));
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
