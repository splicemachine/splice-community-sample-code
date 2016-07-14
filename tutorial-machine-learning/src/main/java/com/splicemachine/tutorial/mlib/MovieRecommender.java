package com.splicemachine.tutorial.mlib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.api.java.function.Function;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;
import com.splicemachine.db.iapi.types.SQLInteger;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedConnection;
import com.splicemachine.db.impl.jdbc.EmbedResultSet40;
import com.splicemachine.db.impl.sql.GenericColumnDescriptor;
import com.splicemachine.db.impl.sql.execute.IteratorNoPutResultSet;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.derby.impl.SpliceSpark;

import scala.Tuple2;

/**
 * @author jramineni
 * 
 */
public class MovieRecommender {
	private static final Logger LOG = Logger.getLogger(MovieRecommender.class);

	// Columns in Recommendations List
	private static final ResultColumnDescriptor[] MOVIE_RECOMMENDATIONS_COLUMNS = new GenericColumnDescriptor[] {
			new GenericColumnDescriptor("ID",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.INTEGER)),
			new GenericColumnDescriptor("NAME",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.VARCHAR)),

	};

	/**
	 * This procedure, uses the Rating Data to build model for use in Movie
	 * Recommender. The process involves building the mode, getting predictions,
	 * and evaluating the Mean Squared Error and save the model
	 */
	public static void buildMovieRecommender() {
		try {
			JavaSparkContext spliceSparkContext = SpliceSpark.getContext();
			SQLContext sqlContext = new SQLContext(spliceSparkContext);
			Connection conn = DriverManager
					.getConnection("jdbc:default:connection");

			// load train ratings from database. Here we are loading all
			// rating data. THe data can be split to be used as
			// training/evaluation/test data
			Map<String, String> options = new HashMap<String, String>();
			options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
			options.put(
					"url",
					"jdbc:splice://localhost:1527/splicedb;user=splice;password=admin;useSpark=true");
			options.put("dbtable", "MOVIELENS.USER_RATING");
			DataFrame ratingDF = sqlContext.read().format("jdbc")
					.options(options).load();

			// Convert the Ratings dataFrame to JavaRDD, with columns UserId,
			// ProductId and Rating
			JavaRDD<Rating> ratings = ratingDF.toJavaRDD().map(
					new Function<Row, Rating>() {
						public Rating call(Row row) {
							return new Rating(row.getInt(0), row.getInt(1),
									new Double(row.getInt(2)).doubleValue());
						}
					});

			// Load the Evaluation data. Here all the data in ratings table is
			// loaded
			JavaRDD<Tuple2<Object, Object>> userProducts = ratings
					.map(new Function<Rating, Tuple2<Object, Object>>() {
						public Tuple2<Object, Object> call(Rating r) {
							return new Tuple2<Object, Object>(r.user(), r
									.product());
						}
					});

			// Build the recommendation model using ALS
			// Note: Here some random values are specified for Rank, Number of
			// Iterations and Lambda
			// to build the model once.
			// Normally, would build model with different combinations, to find
			// the best fit model
			int rank = 10;
			int numIterations = 10;
			double lambda = 0.01;

			// Build model using ALS
			MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratings),
					rank, numIterations, lambda);

			// Lets Evaluate the model on rating data
			// First get predictions from model for the Evaluation data set
			JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD
					.fromJavaRDD(model
							.predict(JavaRDD.toRDD(userProducts))
							.toJavaRDD()
							.map(new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
								public Tuple2<Tuple2<Integer, Integer>, Double> call(
										Rating r) {
									return new Tuple2<>(new Tuple2<>(r.user(),
											r.product()), r.rating());
								}
							}));

			// To calculate mean get the Actual and Predicted values for the
			// Ratings
			// Create a RDD with the Actual and Predicted Rating
			JavaRDD<Tuple2<Double, Double>> ratesAndPreds = JavaPairRDD
					.fromJavaRDD(
							ratings.map(new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
								public Tuple2<Tuple2<Integer, Integer>, Double> call(
										Rating r) {
									return new Tuple2<>(new Tuple2<>(r.user(),
											r.product()), r.rating());
								}
							})).join(predictions).values();

			// Calculate the Mean Square Error
			double MSE = JavaDoubleRDD.fromRDD(
					ratesAndPreds.map(
							new Function<Tuple2<Double, Double>, Object>() {
								public Object call(Tuple2<Double, Double> pair) {
									Double err = pair._1() - pair._2();
									return err * err;
								}
							}).rdd()).mean();

			System.out.println("MSE :" + MSE);

			// Save Model
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyMMddHHmmss");
			String pathPost = simpleDateFormat.format(new Date());

			String modPath = "/tmp/movielensRecommender_" + pathPost;
			model.save(spliceSparkContext.sc(), modPath);

			// insert latest model to database
			PreparedStatement pstmt = conn
					.prepareStatement("INSERT INTO MOVIELENS.MODEL (model_path, create_date) values (?,CURRENT_TIMESTAMP)");
			pstmt.setString(1, modPath);
			int rowCount = pstmt.executeUpdate();

		} catch (SQLException sqle) {
			LOG.error("Exception in getColumnStatistics", sqle);
			sqle.printStackTrace();
		}

	}

	public static void getMovieRecommendations(int userId, int num,
			ResultSet[] resultSets) {

		try {

			JavaSparkContext spliceSparkContext = SpliceSpark.getContext();
			SQLContext sqlContext = new SQLContext(spliceSparkContext);
			Connection conn = DriverManager
					.getConnection("jdbc:default:connection");

			// Read the latest models path form database
			String modelPath = "tmp/movielensRecommender";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT model_path from MOVIELENS.MODEL ORDER BY create_date DESC {limit 1}");
			if (rs.next()) {
				modelPath = rs.getString(1);
			}
			rs.close();
			stmt.close();

			// Load the model
			MatrixFactorizationModel sameModel = MatrixFactorizationModel.load(
					spliceSparkContext.sc(), modelPath);

			// Get recommendation for the specified user and number of items
			Rating[] recom = sameModel.recommendProducts(userId, num);

			// Load the Movies table to get the details of the Movies
			Map<String, String> options = new HashMap<String, String>();
			options.put("driver", "com.splicemachine.db.jdbc.ClientDriver");
			options.put(
					"url",
					"jdbc:splice://localhost:1527/splicedb;user=splice;password=admin;useSpark=true");
			options.put("dbtable", "MOVIELENS.MOVIES");
			DataFrame moviesDF = sqlContext.read().format("jdbc")
					.options(options).load();

			moviesDF.registerTempTable("TEMP_MOVIES");

			// Collect the Movied Ids from Recommendations
			StringBuffer sFilter = new StringBuffer();
			for (Rating rate : recom) {
				if (sFilter.length() > 0)
					sFilter.append(", ");
				sFilter.append(rate.product());
			}

			// Apply filter to select only the recommended Movies
			DataFrame filteredMoviesDF = sqlContext
					.sql("Select * from TEMP_MOVIES where MOVIE_ID in ("
							+ sFilter.toString() + ")");

			List<Row> recMovieList = filteredMoviesDF.collectAsList();

			// Collect the details to build Result Set to return with the
			// details
			int movId = 0;
			String movTitle = "";
			List<ExecRow> rows = new ArrayList();

			for (Row movie : recMovieList) {
				ExecRow row = new ValueRow(9);
				row.setColumn(1, new SQLInteger(movie.getInt(0)));
				row.setColumn(2, new SQLVarchar(movie.getString(1)));
				rows.add(row);
			}

			// Convert the List of ExecRows to Result Set

			Activation lastActivation = ((EmbedConnection) conn)
					.getLanguageConnection().getLastActivation();
			IteratorNoPutResultSet resultsToWrap = new IteratorNoPutResultSet(
					rows, MOVIE_RECOMMENDATIONS_COLUMNS, lastActivation);

			resultsToWrap.openCore();

			// Set the Return resultset
			resultSets[0] = new EmbedResultSet40((EmbedConnection) conn,
					resultsToWrap, false, null, true);

		} catch (StandardException e) {
			LOG.error("Exception in getColumnStatistics", e);
			e.printStackTrace();
		} catch (SQLException sqle) {
			LOG.error("Exception in getColumnStatistics", sqle);
			sqle.printStackTrace();
		}
	}
}
