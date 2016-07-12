create schema iot;
set schema iot;

create table RFID (
	ASSET_NUMBER varchar(50),
	ASSET_DESCRIPTION varchar(100),
	RECORDED_TIME TIMESTAMP,
	ASSET_TYPE VARCHAR(50),
	ASSET_LOCATION VARCHAR(50)
);


