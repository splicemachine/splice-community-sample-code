set schema iot;
create procedure processSensorStreamSP(IN zkQuorum varchar(200),IN topicgroup varchar(200),IN topicList varchar(200),IN numThreads integer) 
language java 
parameter style java 
reads sql data 
external name 'com.splicemachine.tutorials.sparkstreaming.sp.SparkStreaming.processSensorStream'
;

create procedure stopSensorStreamSP()
language java 
parameter style java 
reads sql data 
external name 'com.splicemachine.tutorials.sparkstreaming.sp.SparkStreaming.stopSensorStream'
;

create procedure processSensorStreamVTI(IN zkQuorum varchar(200),IN topicgroup varchar(200),IN topicList varchar(200),IN numThreads integer) 
language java 
parameter style java 
reads sql data 
external name 'com.splicemachine.tutorials.sparkstreaming.vti.SparkStreaming.processSensorStream'
;

create procedure stopSensorStreamVTI()
language java 
parameter style java 
reads sql data 
external name 'com.splicemachine.tutorials.sparkstreaming.vti.SparkStreaming.stopSensorStream'
;