set schema movielens;

DROP PROCEDURE MOVIELENS.ContinuousFeatureReport ;
DROP PROCEDURE MOVIELENS.BuildMovieRecommender;
DROP PROCEDURE MOVIELENS.GetMovieRecommendations;

CREATE PROCEDURE MOVIELENS.ContinuousFeatureReport ( IN  TableName VARCHAR(100) )
LANGUAGE JAVA
EXTERNAL NAME 'com.splicemachine.tutorial.mlib.FeatureReport.continuousFeatureReport'
PARAMETER STYLE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 1
;



CREATE PROCEDURE MOVIELENS.BuildMovieRecommender (  )
LANGUAGE JAVA
EXTERNAL NAME 'com.splicemachine.tutorial.mlib.MovieRecommender.buildMovieRecommender'
PARAMETER STYLE JAVA
READS SQL DATA
;

CREATE PROCEDURE MOVIELENS.GetMovieRecommendations ( IN UserID INT, IN NoOfRecom INT )
LANGUAGE JAVA
EXTERNAL NAME 'com.splicemachine.tutorial.mlib.MovieRecommender.getMovieRecommendations'
PARAMETER STYLE JAVA
READS SQL DATA
DYNAMIC RESULT SETS 1
;