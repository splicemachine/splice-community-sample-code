# Stored Procedures in Splice Machine
This maven project provides an (extremely simplified) example of how to implement (java based) stored procedures in Splice Machine. The intent is to provide concrete examples of how to structure maven demepdencies and a simple java class to create a (set of) stored procedure(s).  

Additionally there sql scripts included `src/main/resources/stored-procs-{create,test,drop}.sql` that demonstrate the process for:
* installing the procedure jar on the cluster
* adding it to the database classpath
* defining the procedures within the database
* running the procedures
* dropping the procedures
* removing the installed from the database classpath
* uninstalling the jar from the cluster

Stored procedures in Splice (being java based) can be infinitely more complex than what is demonstrated here. The included procedures are only select statements that read data. Procedures can also modify data, and perform data transformations beyond build-in SQL functions.
See the [Splice Machine Online Documentation](http://doc.splicemachine.com/Developers/AdvancedTopics/WritingProcsAndFcns.html) for more. 
