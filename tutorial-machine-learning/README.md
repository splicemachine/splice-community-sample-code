#Overview
This project contains the sample code for interfacing to the Spark Machine Learning library (MLlib) in Splice Machine.
The code demonstrates the following
	- Using of the Summary Column Statistics to understand the to get Data Features of a table. 
	- Build Model for Movie Recomendations using ALS algorithm.
	- Use Model to Recommend Movies for an User.
	
They are implemented as Stored Procedures, making it easy to be invoked from client applications.

#Prerequisites
You must have Splice Machine installed. There is dependency on jul-to-slf4j-1.7.21.jar. This needs to be deployed on all Region Servers by coping this jar to /opt/splice/default/lib/ on all region servers.

#How to run the code
1.  Pull the source code from github
2.  Run: mvn clean compile package
3.  Copy the ./target/splice-tutorial-machine-learning-2.0.jar to each of your servers /opt/splice/default/lib/ directory
4.  Restart the HBase Master and Region Servers
6.  Start the splice command prompt
7.  Create the tables for MovieLens . Run these scripts
	- src/main/resources/ddl/create-tables.sql
8. Import Movielens data  : The data files are in /src/main/resources/data. Use the splice import command to import 
	data from the data files. For instructions refer to the Splice Machine Import Util tutorial.
9. Create the stored Procedures by running the following scripts
		- /src/main/resources/ddl/create-procedures.sql
8.  To get the table features for USER_DEMOGRAPHICS table call the stored procedure 
		call MOVIELENS.ContinuousFeatureReport('movielens.user_demographics');
	This will list details for Column AGE.
9. To build Model for Movie Recommender call stored procedure
	 call MOVIELENS.BuildMovieRecommender ( );
	This will create the model and save it. The MODEL table will be udpdated with the  path of the latest model created.
10. To get Movie Recommendations for User with user_id 1, and get 10 recommendations, run the stored procedure 
	call MOVIELENS.GetMovieRecommendations (1, 10);
	
	
## Notes
The current build process is setup to be compiled and executed on a MapR environment.  You will need to change the properties section in your pom.xml in order to compile and run it against other distributions.
