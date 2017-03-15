-- Insert the definitions for the MODEL, MODEL_FEATURES and MODEL_FEATURE_CROSS

insert into MODEL (MODEL_ID,NAME,DESCRIPTION) values (2,'INSURANCE','Predict whether a customer has a caravan insurance policy');

insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CUSTOMER_SUBTYPE','CUSTOMER_SUBTYPE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_HOUSES','NUM_HOUSES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'AVG_SIZE_HOUSEHOLD','AVG_SIZE_HOUSEHOLD','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'AVG_AGE','AVG_AGE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CUSTOMER_MAIN_TYPE','CUSTOMER_MAIN_TYPE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'ROMAN_CATHOLIC','ROMAN_CATHOLIC','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'PROTESTANT','PROTESTANT','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'OTHER_RELIGION','OTHER_RELIGION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NO_RELIGION','NO_RELIGION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'MARRIED','MARRIED','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'LIVING_TOGETHER','LIVING_TOGETHER','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'OTHER_RELATION','OTHER_RELATION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SINGLES','SINGLES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'HOUSEHOLD_WITHOUT_CHILDREN','HOUSEHOLD_WITHOUT_CHILDREN','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'HOUSEHOLD_WITH_CHILDREN','HOUSEHOLD_WITH_CHILDREN','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'HIGH_LEVEL_EDUCATION','HIGH_LEVEL_EDUCATION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'MEDIUM_LEVEL_EDUCATION','MEDIUM_LEVEL_EDUCATION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'LOWER_LEVEL_EDUCATION','LOWER_LEVEL_EDUCATION','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'HIGH_STATUS','HIGH_STATUS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'ENTREPRENEUR','ENTREPRENEUR','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'FARMER','FARMER','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'MIDDLE_MANAGEMENT','MIDDLE_MANAGEMENT','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SKILLED_LABOURERS','SKILLED_LABOURERS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'UNSKILLED_LABOURERS','UNSKILLED_LABOURERS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SOCIAL_CLASS_A','SOCIAL_CLASS_A','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SOCIAL_CLASS_B1','SOCIAL_CLASS_B1','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SOCIAL_CLASS_B2','SOCIAL_CLASS_B2','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SOCIAL_CLASS_C','SOCIAL_CLASS_C','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'SOCIAL_CLASS_D','SOCIAL_CLASS_D','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'RENTED_HOUSE','RENTED_HOUSE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'HOME_OWNERS','HOME_OWNERS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CARS_1','CARS_1','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CARS_2','CARS_2','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NO_CHAR','NO_CHAR','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NATIONAL_HEALTH_SERVICE','NATIONAL_HEALTH_SERVICE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'PRIVATE_HEALTH_INSURANCE','PRIVATE_HEALTH_INSURANCE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'INCOME_UNDER_30','INCOME_UNDER_30','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'INCOME_30_TO_45','INCOME_30_TO_45','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'INCOME_45_TO_75','INCOME_45_TO_75','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'INOCME_75_TO_112','INOCME_75_TO_112','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'INCOME_OVER_123','INCOME_OVER_123','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'AVERAGE_INCOME','AVERAGE_INCOME','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'PURCHASING_POWER_CLASS','PURCHASING_POWER_CLASS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_PRIVATE_3RD_PARTY_INSURANCE','CONTRIB_PRIVATE_3RD_PARTY_INSURANCE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_3RD_PARTY_INSURANCE_FIRMS','CONTRIB_3RD_PARTY_INSURANCE_FIRMS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_3RD_PARTY_INSURANCE_AGRICULTURE','CONTRIB_3RD_PARTY_INSURANCE_AGRICULTURE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_CAR_POLICIES','CONTRIB_CAR_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_DELIVERY_VAN_POLICIES','CONTRIB_DELIVERY_VAN_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_MOTORCYCLE_SCOOTER_POLICIES','CONTRIB_MOTORCYCLE_SCOOTER_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_LORRY_POLICIES','CONTRIB_LORRY_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_TRAILER_POLICIES','CONTRIB_TRAILER_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_TRACTOR_POLICIES','CONTRIB_TRACTOR_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_AGRICULTURAL_MACHINES_POLICIES','CONTRIB_AGRICULTURAL_MACHINES_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_MOPED_POLICIES','CONTRIB_MOPED_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_LIFE_INSURANCE_POLICIES','CONTRIB_LIFE_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_PRIVATE_ACCIDENT_INSURANCE_POLICIES','CONTRIB_PRIVATE_ACCIDENT_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_FAMILY_ACCIDENTS_INSURANCE_POLICIES','CONTRIB_FAMILY_ACCIDENTS_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_DISABILITY_INSURANCE_POLICIES','CONTRIB_DISABILITY_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_FIRE_POLICIES','CONTRIB_FIRE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_SURFACE_POLICIES','CONTRIB_SURFACE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_BOAT_POLICIES','CONTRIB_BOAT_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_BICYCLE_POLICIES','CONTRIB_BICYCLE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_PORPERTY_INSURANCE_POLICIES','CONTRIB_PORPERTY_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'CONTRIB_SOCIAL_SECURITY_INSURANCE_POLICIES','CONTRIB_SOCIAL_SECURITY_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_PRIVATE_3RD_PARTY_INSURANCE','NUM_PRIVATE_3RD_PARTY_INSURANCE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_PRIVATE_3RD_PARTY_INSURANCE_FIRMS','NUM_PRIVATE_3RD_PARTY_INSURANCE_FIRMS','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_PRIVATE_3RD_PARTY_INSURANCE_AGRICULTURE','NUM_PRIVATE_3RD_PARTY_INSURANCE_AGRICULTURE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_CAR_POLICIES','NUM_CAR_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_DELIVERY_VAN_POLICIES','NUM_DELIVERY_VAN_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_MOTORCYCLE_SCOOTER_POLICIES','NUM_MOTORCYCLE_SCOOTER_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_LORRY_POLICIES','NUM_LORRY_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_TRAILER_POLICIES','NUM_TRAILER_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_TRACTOR_POLICIES','NUM_TRACTOR_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_AGRICULTURAL_MACHINES_POLICIES','NUM_AGRICULTURAL_MACHINES_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_MOPED_POLICIES','NUM_MOPED_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_LIFE_INSURANCE','NUM_LIFE_INSURANCE','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_PRIVATE_ACCIDENT_INSURANCE_POLICIES','NUM_PRIVATE_ACCIDENT_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_FAMILY_ACCIDENT_INSURANCE_POLICIES','NUM_FAMILY_ACCIDENT_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_DISABILITY_INSURANCE_POLICIES','NUM_DISABILITY_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_FIRE_POLICIES','NUM_FIRE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_SURFBOARD_POLICIES','NUM_SURFBOARD_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_BOAT_POLICIES','NUM_BOAT_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_BICYCLE_POLICIES','NUM_BICYCLE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_PROPERTY_INSURANCE_POLICIES','NUM_PROPERTY_INSURANCE_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_SOCIAL_SECURITY_INS_POLICIES','NUM_SOCIAL_SECURITY_INS_POLICIES','CONTINUOUS',null,null,null,null);
insert into MODEL_FEATURES (MODEL_ID,FEATURE_NAME,DATABASE_COLUMN_NAME,MODEL_DATA_TYPE,FEATURE_KEYS,FEATURE_BUCKET_DATA_TYPE,FEATURE_BUCKETS,IS_LABEL) values (2,'NUM_MOBILE_HOME','NUM_MOBILE_HOME',null,null,null,null,TRUE);


insert into MODEL_INPUTS (MODEL_ID, TRAINING_TABLE, TEST_TABLE, MODEL_OUTPUT_PATH, MODEL_TYPE, TRAINING_STEPS,HASH_BUCKET_SIZE,DIMENSIONS,HIDDEN_UNITS) values (2, 'INSURANCE.TRAINING_DATA', 'INSURANCE.TESTING_DATA', 'test-1000-both','wide_n_deep',1000,1000,8,'100, 50');
insert into MODEL_INPUTS (MODEL_ID, TRAINING_TABLE, TEST_TABLE, MODEL_OUTPUT_PATH, MODEL_TYPE, TRAINING_STEPS,HASH_BUCKET_SIZE,DIMENSIONS,HIDDEN_UNITS) values (2, 'INSURANCE.TRAINING_DATA', 'INSURANCE.TESTING_DATA', 'test-2000-both','wide_n_deep',2000,1000,8,'100, 50');
insert into MODEL_INPUTS (MODEL_ID, TRAINING_TABLE, TEST_TABLE, MODEL_OUTPUT_PATH, MODEL_TYPE, TRAINING_STEPS,HASH_BUCKET_SIZE,DIMENSIONS,HIDDEN_UNITS) values (2, 'INSURANCE.TRAINING_DATA', 'INSURANCE.TESTING_DATA', 'test-3000-both','wide_n_deep',3000,1000,8,'100, 50');
