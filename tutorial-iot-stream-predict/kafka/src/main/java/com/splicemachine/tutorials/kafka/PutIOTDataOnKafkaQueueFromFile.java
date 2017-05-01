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
package com.splicemachine.tutorials.kafka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * This is a utility class to put messages on a Kafka queue.
 * This reads a file containing sensor readings. Each row in the file is
 * sensor reading for a particular engine at a given time cycle. The in the file is expected
 * to be order by TIme cycle and engine (also referred to as unit).
 * For ex the data looks as shown below
 * Unit		Time 	Op1		OP2 	Op3		Sen1		Sen2 ....
 * 1		1		1.2		12.0	14		.001		1.233 ....   
 * 2		1		1.2		12.0	14		.001		1.233 ....   
 * 3		1		1.2		12.0	14		.001		1.233 ....  
 * ..... 
 * 100		1		1.2		12.0	14		.001		1.233 ....   
 * 1		2		1.2		12.0	14		.001		1.233 ....  
 * 2		2		1.2		12.0	14		.001		1.233 ....  
 * 3		2		1.2		12.0	14		.001		1.233 ....  
 * 
 * The training file used has data for 100 units, and number of time cycles will vary 
 * for each unit, for ex, unit1 may go upto 130, unit 2 upto 170
 * 
 * The data in the file is loaded into data structures.
 *Since the file only has 100 units, so for a given time cycle there are only 100 records
 *to be placed on queue. To get more volume, the units data is replicated, 
 *say if volume of 200 is required, then after the first 100 units, next hundred will 
 *created same as first 100, with different unit numbers. So the new data will have units 1 thru 200.
 *And sensor data of unit 1 and unit 101 are same, similarly that of unit 2 and unit 102 is same.
 *
 *Also since max time cycles is only around 300, this data will not run long enough, so to simulate
 *continuous flow, once the last time cycle is placed, the time cycle is reset to 1, and a new
 *unit number is assigned and the data is repeated. SO when unit 1 completes at time cycle130, 
 *for 131 time cycle, a new unit 201(after replicationg for volume), and data of time cycel1 of unit 1
 *is placed on queue. Similarly for time cycle 132, unit 201 withe timecycle 2 's data for unit 1 
 *is placed on queue. 
 *
 *  
 * @author Jyotsna Ramineni
 * 
 */
public class PutIOTDataOnKafkaQueueFromFile {

    /**
     * The kafka broker server and port (ie 52.209.245.34:9092)
     */
    private String kafkaBrokerServer = null;
    
    /**
     * The kafka topic for the queue
     */
    private String kafkaTopic = null;

    /**
	 * The number of messages that should
	 * be sent before pausing for a second 
	 */
    private int batchSize = 100;

    /**
     * Name of the file or path that contains the data to be read in to 
     * be placed on the queue
     */
    private String dataInputFileName = null;
    
    /**
     * The duration to wait between batches
     */
    long pauseDurationInMilliseconds = 1000;
    long timeDurationInCycles = 100;
    
    long recordsWritten = 0;
    //inidcates the number of units in the file
    long numberOfUnitsInfile = 0;
    
    
    //Data structure to save sensor data by unit and time cycle read from file
    private HashMap<Integer, HashMap<Long, String>> fileData = new HashMap<Integer, 
    		HashMap<Long, String>>();

   
    //To save the last time cycle for each unit for with data is available in file
    private HashMap <Integer, Long> unitLastTime = new HashMap<Integer, Long>();
    
    //Array of data to be placed on Queue for next time cycle.
    private ArrayList<IOTDataInfo> currentBatchData = new ArrayList<IOTDataInfo>();
    
    //Counter of last unit number used. The units are replicated both for 
    // desired volume and time duration
    private int lastUnit = 1;

    /**
     * Adds records to a Kafka queue
     * 
     * @param args
     *            args[0] - (required) Kafka Broker URL 
     *            args[1] - (required) Kafka Topic Name 
     *            args[2] - (required) Data file path or name and name (ie /tmp/myfolder or /tmp/myfolder/myfile.csv)
     *            args[3] - (optional) Number of messages per sec.  Defaults to 100
     *            args[4] - (optional) Pause Duration in Milliseconds - defaults to 1000
     *            args[5] - (optional) Total duration in Time Cycles to add to queue.  Defaults to 100
     *            args[6] - (optional) Starting Unit Id to allow multiple runs
     * 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	PutIOTDataOnKafkaQueueFromFile kp = new PutIOTDataOnKafkaQueueFromFile();
        kp.kafkaBrokerServer = args[0];
        kp.kafkaTopic = args[1];
        kp.dataInputFileName = args[2];
        if (args.length > 3)
            kp.batchSize = Integer.parseInt(args[3]);
        if (args.length > 4)
            kp.pauseDurationInMilliseconds = Integer.parseInt(args[4]);
        if (args.length > 5)
            kp.timeDurationInCycles = Long.parseLong(args[5]);
        if (args.length > 6)
            kp.lastUnit = Integer.parseInt(args[6]);

        kp.generateMessages();

    }

    /**
     * Puts messages on the kafka queue
     */
    public void generateMessages() {
        
        System.out.println("kafkaBrokerServer:" + kafkaBrokerServer);
        System.out.println("topic:" + kafkaTopic);
        System.out.println("file:" + dataInputFileName);
        System.out.println("batchSize:" + batchSize);
        System.out.println("pauseDurationInMilliseconds:" + pauseDurationInMilliseconds);

        // Define the properties for the Kafka Connection
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaBrokerServer); // kafka server
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        // Create a KafkaProducer using the Kafka Connection properties
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(
                props);
        
        File f = new File(dataInputFileName);
        try {
            if(f.exists()) {
            	
            	//load data from file into data structures
                if(f.isDirectory()) {
                    processDirectory( f);
                } else {
                    processFile( f);
                }

               // Initialize the total units required to meet the required volume
               int unitsCount = (int) ((batchSize-1)/numberOfUnitsInfile)+1;
               System.out.println("Batch Size =  " + batchSize +   "  unitsCount = "+ unitsCount);
               initBatch(unitsCount);
               
               //Start putting data on the queue
               putDataOnQueue(producer, timeDurationInCycles);
                
            } else {
                System.out.println("Filename or path does not exist:" + dataInputFileName);
            }
            producer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception:" + e);
        }
        
        
    }
    
    
    public void processDirectory(File fileDirectory) throws java.io.FileNotFoundException, java.io.IOException {
        File[] listOfFiles = fileDirectory.listFiles();
        for(File currentFile : listOfFiles) {
        	processFile( currentFile);
        }
    }
    
    
    
    public void putDataOnQueue(KafkaProducer<String, String> producer, long totalTimeCycles) throws java.io.FileNotFoundException, java.io.IOException {
    	try {
    		
    		System.out.println("putDataOnQueue Number of Cycles:" + totalTimeCycles);
            
	    	int i = 0;      
	        String line = null;      	
	        int curTime = 1;
	        int unitCnt = currentBatchData.size();
	       
	        //Continue until the desired duration (in Time Cycles)
	        while(  (totalTimeCycles==0) ? true: (curTime < totalTimeCycles+1))
	        { 
	        	//Get data all the units for required volume 
	        	for (int unitIt =0; unitIt < unitCnt; unitIt++){
	        		
	        		//Get the meesage from data structure
	        		IOTDataInfo curData = currentBatchData.get(unitIt);
	        		line = "1|"+ curData.unit + "|" + curTime + 
	        				fileData.get(curData.refunit).get(curData.reftime);
	        		
	        		//Put the meesage on the queue
	        		producer.send(new ProducerRecord<String, String>(kafkaTopic, line));
	        		recordsWritten ++;
	        		
	        		//Update teh pointers in data structure for next message
	        		IOTDataInfo nextData;
	        		if(curData.reftime >= unitLastTime.get(curData.refunit)){
	        			nextData = new IOTDataInfo(lastUnit++, curTime, curData.refunit, 1);
	        		}
	        		else {
	        			
	        			nextData = new IOTDataInfo(curData.unit, curTime, curData.refunit, curData.reftime+1);
	        		}
	        		currentBatchData.set(unitIt, nextData);
	        	
	        	}
	        	curTime++;
	        	//Sleep before each time cycle
	        	Thread.sleep(pauseDurationInMilliseconds);
	        }
	        System.out.println("Records Written:" + recordsWritten);
    		
        
    	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
    
    
    /*
     * REads the messages in the files, and loads into memory/data structures
     * 
     */
    public void processFile( File fileToProcess) throws java.io.FileNotFoundException, java.io.IOException {
         System.out.println("Processing file:" + fileToProcess.getAbsolutePath());
       
         FileInputStream fis = new FileInputStream(fileToProcess);
         BufferedReader br = new BufferedReader(new InputStreamReader(fis));

       
        String line = null;
        HashMap<Long, String> timeData = null;
      
        while ((line = br.readLine()) != null) {
            String[] lineComp = line.split(" ");
            int unit = Integer.parseInt(lineComp[0]);
            long time = Long.parseLong(lineComp[1]);
            StringBuffer msg = new StringBuffer();
            for(int i= 2; i <lineComp.length; i++ )
            	msg.append("|").append(lineComp[i]);
            
            
            if(fileData.containsKey(unit)){
            	timeData = fileData.get(unit);
            }
            else 
            	timeData = new HashMap<Long, String> ();
            
            timeData.put(time, msg.toString());
            fileData.put(unit, timeData);
            
            if(!unitLastTime.containsKey(unit))
            	unitLastTime.put(unit, time);
            else if( (unitLastTime.containsKey(unit)) && (time > unitLastTime.get(unit)))
            	unitLastTime.put(unit, time);          
            
        }
        numberOfUnitsInfile += unitLastTime.size();
        
        br.close();
    }
    
    
   
   /* 
    * INitialises array of current data to be placed  on queue for each of the unit.
    * Array length corresponds to the deisred volume, and the timecycle starts at 1
    * As the message is placed on queue, elelemnt in the array is updated to inidcate the next
    * value that needs to be placed on queue
    * 
    */
    public void initBatch( int unitsCount) throws java.io.FileNotFoundException, java.io.IOException {
    	
    
    	for(int i=0; i < unitsCount; i++) {  // This is for volume replication
    		for (int j=0; j< numberOfUnitsInfile; j++) // loop thru original units in file
    		//j will indicate the original unit from 
    		currentBatchData.add(new IOTDataInfo( lastUnit++, 1, j+1, 1));
    	}
    }
    	   
    
   /* Data structure for data to be placed on next time cycle. This will indicate
    * the current unit and time cycle number, and the original unit and time it is repeating.
   */
     class IOTDataInfo {
    	private int unit;
    	private long time;
    	private int refunit;
    	private long reftime;
    	public IOTDataInfo(int unit, long time, int refunit, long reftime) {
    		this.unit = unit;
    		this.time = time;
    		this.refunit = refunit;
    		this.reftime = reftime;		
    	}

		public int getUnit() {
			return unit;
		}

		public void setUnit(int unit) {
			this.unit = unit;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public int getRefunit() {
			return refunit;
		}

		public void setRefunit(int refunit) {
			this.refunit = refunit;
		}

		public long getReftime() {
			return reftime;
		}

		public void setReftime(long reftime) {
			this.reftime = reftime;
		}
    }
    
}