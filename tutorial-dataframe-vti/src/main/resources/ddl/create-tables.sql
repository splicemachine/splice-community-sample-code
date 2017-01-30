CREATE TABLE SENSOR_MESSAGES_SOURCE (
  sensor_id   BIGINT,
  query_cycle INTEGER,
  recorded_time TIMESTAMP,
  humidity DECIMAL(12, 5),
  light DECIMAL(12, 5),
  temperature DECIMAL(12, 5),
  horizontal_acceleration DECIMAL(12, 5),
  vertical_acceleration DECIMAL(12, 5),
  horizontal_magnetometer DECIMAL(12, 5),
  vertical_magnetometer DECIMAL(12, 5),
  voice DECIMAL(12, 5),
  tone DECIMAL(12, 5),
  original_voice DECIMAL(12, 5),
  original_tone DECIMAL(12, 5)
);

CREATE TABLE SENSOR_MESSAGES_TARGET (
  sensor_id    BIGINT,
  temperature  DECIMAL(12, 5),
  humidity     DECIMAL(12, 5),
  recordedtime TIMESTAMP
);