CREATE SCHEMA iot;
SET SCHEMA iot;
CREATE TABLE SENSOR_MESSAGES (
  id           VARCHAR(20),
  location     VARCHAR(50),
  temperature  DECIMAL(12, 5),
  humidity     DECIMAL(12, 5),
  recordedTime TIMESTAMP
);
