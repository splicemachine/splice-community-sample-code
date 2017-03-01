--Load the census data for training, testing and the live data
CALL SYSCS_UTIL.IMPORT_DATA( 'CENSUS','TRAINING_DATA','AGE,WORKCLASS,FNLWGT,EDUCATION,EDUCATION_NUM,MARITAL_STATUS,OCCUPATION,RELATIONSHIP,RACE,GENDER,CAPITAL_GAIN,CAPITAL_LOSS,HOURS_PER_WEEK,NATIVE_COUNTRY,INCOME_BRACKET','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/data/training.data.txt',null,null,null,null,null,-1,null, true, null);
CALL SYSCS_UTIL.IMPORT_DATA( 'CENSUS','TESTING_DATA','AGE,WORKCLASS,FNLWGT,EDUCATION,EDUCATION_NUM,MARITAL_STATUS,OCCUPATION,RELATIONSHIP,RACE,GENDER,CAPITAL_GAIN,CAPITAL_LOSS,HOURS_PER_WEEK,NATIVE_COUNTRY,INCOME_BRACKET','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/data/testing.data.txt',null,null,null,null,null,-1,null, true, null);
CALL SYSCS_UTIL.IMPORT_DATA( 'CENSUS','LIVE_DATA','AGE,WORKCLASS,FNLWGT,EDUCATION,EDUCATION_NUM,MARITAL_STATUS,OCCUPATION,RELATIONSHIP,RACE,GENDER,CAPITAL_GAIN,CAPITAL_LOSS,HOURS_PER_WEEK,NATIVE_COUNTRY,INCOME_BRACKET','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/data/live.data.csv',null,null,null,null,null,-1,null, true, null);

--Fix some issues with the data
update census.TESTING_DATA set LABEL = '0'  where (census.TESTING_DATA.INCOME_BRACKET LIKE '%<=50K%');
update census.TESTING_DATA set LABEL = '1' where (census.TESTING_DATA.INCOME_BRACKET LIKE '%>50K%');

update census.TRAINING_DATA set LABEL = '0'  where (census.TRAINING_DATA.INCOME_BRACKET LIKE '%<=50K%');
update census.TRAINING_DATA set LABEL = '1' where (census.TRAINING_DATA.INCOME_BRACKET LIKE '%>50K%');

-- Insert the definitions for the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS

insert into MODEL (MODEL_ID,NAME,DESCRIPTION) values (1,'CENSUS','Predict whether income is greater than 50,000');

insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'AGE','AGE','CONTINUOUS',null,'INTEGER','18, 25, 30, 35, 40, 45, 50, 55, 60, 65',null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'WORKCLASS','WORKCLASS','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'FNLWGT','FNLWGT',null,null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'EDUCATION','EDUCATION','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'EDUCATION_NUM','EDUCATION_NUM','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'MARITAL_STATUS','MARITAL_STATUS','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'OCCUPATION','OCCUPATION','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'RELATIONSHIP','RELATIONSHIP','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'RACE','RACE','CATEGORICAL','Amer-Indian-Eskimo, Asian-Pac-Islander, Black, Other, White',null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'GENDER','GENDER','CATEGORICAL','Female, Male',null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'CAPITAL_GAIN','CAPITAL_GAIN','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'CAPITAL_LOSS','CAPITAL_LOSS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'HOURS_PER_WEEK','HOURS_PER_WEEK','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'NATIVE_COUNTRY','NATIVE_COUNTRY','CATEGORICAL',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'INCOME_BRACKET','INCOME_BRACKET',null,null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (1,'LABEL','LABEL',null,null,null,null,TRUE);

insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'EDUCATION_OCCUPATION','EDUCATION');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'EDUCATION_OCCUPATION','OCCUPATION');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'AGE_EDUCATION_OCCUPATION','AGE_BUCKETS');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'AGE_EDUCATION_OCCUPATION','EDUCATION');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'AGE_EDUCATION_OCCUPATION','OCCUPATION');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'COUNTRY_OCCUPATION','NATIVE_COUNTRY');
insert into MODEL_FEATURE_CROSS (MODEL_ID,FEATURE_CROSS_NAME,FEATURE_NAME) values (1,'COUNTRY_OCCUPATION','OCCUPATION');

