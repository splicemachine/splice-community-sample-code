select * from new com.splicemachine.tutorials.vti.PropertiesFileVTI('sample.properties') as b (KEY_NAME VARCHAR(200), VALUE VARCHAR(200));
select * from table (PROPERTIESFILE('sample.properties')) b;