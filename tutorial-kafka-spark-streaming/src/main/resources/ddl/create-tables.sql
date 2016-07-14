create schema iot;
set schema iot;
create table SENSOR_MESSAGES (
	id varchar(20),
	location varchar(50),
	temperature decimal(12,5),
	humidity decimal(12,5),
	recordedTime timestamp
);
