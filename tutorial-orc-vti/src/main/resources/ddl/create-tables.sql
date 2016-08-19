CREATE TABLE user_orc (
     id        BIGINT,
     name      VARCHAR(100),
     activity char(1),
     is_fte    BOOLEAN,
     test_tinyint  SMALLINT,
     test_smallint  SMALLINT,
     test_int  INT,
     test_float  FLOAT,
     test_double  DOUBLE,
     test_decimal DECIMAL(10,0),
     role      VARCHAR(64),
     salary    DECIMAL(8,2),
     START_DT  DATE,
     UPDATE_DT TIMESTAMP
)