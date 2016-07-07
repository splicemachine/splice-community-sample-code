CREATE FUNCTION propertiesFile(propertyFilename VARCHAR(200))
   RETURNS TABLE
     (
     	KEY_NAME varchar(100),
     	VALUE varchar(200)
     )
   LANGUAGE JAVA
   PARAMETER STYLE SPLICE_JDBC_RESULT_SET
   READS SQL DATA
   EXTERNAL NAME 'com.splicemachine.tutorials.vti.PropertiesFileVTI.getPropertiesFileVTI';
