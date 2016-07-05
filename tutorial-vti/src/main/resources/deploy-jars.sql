mv /Users/erindriggers/git/customer-solutions/special-projects/kafka-spark-streaming-splice/target/splice-cs-kafka-0.0.1-SNAPSHOT.jar /tmp/
call SQLJ.INSTALL_JAR('/tmp/splice-tutorial-vti-2.0.jar', 'SPLICE.VTI_JAR', 0);
call SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath','SPLICE.VTI_JAR');

-- Only if this is the second time or you are updating a jar file
call SQLJ.REPLACE_JAR('/tmp/splice-tutorial-vti-2.0.jar','SPLICE.VTI_JAR');
