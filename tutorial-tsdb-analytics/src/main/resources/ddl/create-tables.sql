CREATE SCHEMA LOGAGG;

CREATE TABLE LOGAGG.AggregateResults (
	LOGDATE timestamp, 
    PUBLISHER varchar(50), 
    GEO varchar(50),  
    IMPS integer, 
    UNIQUES integer, 
    AVGBIDS decimal(10,5) 
);


