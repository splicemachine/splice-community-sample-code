/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.splicemachine.tutorial.mlib;

import java.sql.Connection;
import java.sql.DriverManager;

import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.Activation;
import com.splicemachine.db.iapi.sql.ResultColumnDescriptor;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataTypeDescriptor;
import com.splicemachine.db.iapi.types.SQLDouble;
import com.splicemachine.db.iapi.types.SQLLongint;
import com.splicemachine.db.iapi.types.SQLVarchar;
import com.splicemachine.db.impl.jdbc.EmbedConnection;
import com.splicemachine.db.impl.jdbc.EmbedResultSet40;
import com.splicemachine.db.impl.sql.GenericColumnDescriptor;
import com.splicemachine.db.impl.sql.execute.IteratorNoPutResultSet;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.example.SparkMLibUtils;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary;
import org.apache.spark.mllib.stat.Statistics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FeatureReport {
	private static final Logger LOG = Logger.getLogger(FeatureReport.class);

	//Columns in the return resultset
	private static final ResultColumnDescriptor[] STATEMENT_STATS_OUTPUT_COLUMNS = new GenericColumnDescriptor[] {
			new GenericColumnDescriptor("COLUMN_NAME",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.VARCHAR)),
			new GenericColumnDescriptor("MIN",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			new GenericColumnDescriptor("MAX",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			new GenericColumnDescriptor("COUNT",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.BIGINT)),
			new GenericColumnDescriptor("NUM_NONZEROS",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			/*new GenericColumnDescriptor("VARIANCE",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
							*/
			new GenericColumnDescriptor("STANDARD_DEVIATION",
									DataTypeDescriptor
											.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			new GenericColumnDescriptor("MEAN",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			/* new GenericColumnDescriptor("NORML1",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
			new GenericColumnDescriptor("NORML2",
					DataTypeDescriptor
							.getBuiltInDataTypeDescriptor(Types.DOUBLE)),
							*/

	};

	public static void continuousFeatureReport(String tableName,
			ResultSet[] resultSets) throws StandardException {

		try {

			Connection conn = DriverManager
					.getConnection("jdbc:default:connection");

			// Get a list of the columns / features that will be included
			// for the table in the continuous feature report.
			PreparedStatement ps = conn
					.prepareStatement("select COLUMN_NAME, PARSE_METHOD FROM MOVIELENS.DATA_FEATURE where TABLE_NAME = ? and CONTINUOUS = ?");
			ps.setString(1, tableName);
			ps.setString(2, "Y");
			ResultSet rs = ps.executeQuery();

			// Process the columns that should be included in the report
			StringBuilder columns = new StringBuilder();
			int numColumns = 0;
			while (rs.next()) {
				String columnName = rs.getString(1);
				if (numColumns > 0)
					columns.append(", ");
				columns.append(columnName);
				numColumns++;
			}

			// If columns found, process the table
			if (numColumns > 0) {

				// Get the data for those columns
				PreparedStatement ps2 = conn.prepareStatement("select "
						+ columns.toString() + " FROM " + tableName);
				ResultSet rs2 = ps2.executeQuery();

				// Convert the resultset into a JavaRDD
				JavaRDD<LocatedRow> resultSetRDD = ResultSetToRDD(rs2);

				int[] fieldsToConvert = getFieldsToConvert(ps2);
				JavaRDD<Vector> vectorJavaRDD = SparkMLibUtils
						.locatedRowRDDToVectorRDD(resultSetRDD, fieldsToConvert);

				//Make MlLib call
				MultivariateStatisticalSummary summary = Statistics
						.colStats(vectorJavaRDD.rdd());
				
				IteratorNoPutResultSet resultsToWrap = wrapResults(
						(EmbedConnection) conn,
						getColumnStatistics(ps2, summary, fieldsToConvert));
				
				resultSets[0] = new EmbedResultSet40((EmbedConnection) conn,
						resultsToWrap, false, null, true);

			}

		} catch (Exception e) {
			LOG.error("Exception in continuousFeatureReport", e);
			throw StandardException.newException(e.getLocalizedMessage());
		}
	}

	/*
	 * Convert a ResultSet to JavaRDD
	 */
	private static JavaRDD<LocatedRow> ResultSetToRDD(ResultSet resultSet)
			throws StandardException {
		EmbedResultSet40 ers = (EmbedResultSet40) resultSet;

		com.splicemachine.db.iapi.sql.ResultSet rs = ers
				.getUnderlyingResultSet();
		JavaRDD<LocatedRow> resultSetRDD = SparkMLibUtils.resultSetToRDD(rs);

		return resultSetRDD;
	}

	private static int[] getFieldsToConvert(PreparedStatement ps)
			throws SQLException {
		ResultSetMetaData metaData = ps.getMetaData();
		int columnCount = metaData.getColumnCount();
		int[] fieldsToConvert = new int[columnCount];
		for (int i = 0; i < columnCount; ++i) {
			fieldsToConvert[i] = i + 1;
		}
		return fieldsToConvert;
	}

	/*
	 * Convert column statistics to an iterable row source
	 */
	private static Iterable<ExecRow> getColumnStatistics(PreparedStatement ps,
			MultivariateStatisticalSummary summary, int[] fieldsToConvert)
			throws StandardException {
		try {

			List<ExecRow> rows = new ArrayList();
			ResultSetMetaData metaData = ps.getMetaData();

			double[] min = summary.min().toArray();
			double[] max = summary.max().toArray();
			double[] mean = summary.mean().toArray();
			double[] nonZeros = summary.numNonzeros().toArray();
			double[] variance = summary.variance().toArray();
			double[] normL1 = summary.normL1().toArray();
			double[] normL2 = summary.normL2().toArray();

			long count = summary.count();

			for (int i = 0; i < fieldsToConvert.length; ++i) {
				int columnPosition = fieldsToConvert[i];
				String columnName = metaData.getColumnName(columnPosition);
				ExecRow row = new ValueRow(9);
				row.setColumn(1, new SQLVarchar(columnName));
				row.setColumn(2, new SQLDouble(min[columnPosition - 1]));
				row.setColumn(3, new SQLDouble(max[columnPosition - 1]));
				row.setColumn(4, new SQLLongint(count));
				row.setColumn(5, new SQLDouble(nonZeros[columnPosition - 1]));
				row.setColumn(6, new SQLDouble(Math.sqrt(variance[columnPosition - 1])));
				//row.setColumn(7, new SQLDouble(variance[columnPosition - 1]));	
				row.setColumn(7, new SQLDouble(mean[columnPosition - 1]));
				//row.setColumn(9, new SQLDouble(normL1[columnPosition - 1]));
				//row.setColumn(10, new SQLDouble(normL2[columnPosition - 1]));

				rows.add(row);
			}
			return rows;
		} catch (Exception e) {
			LOG.error("Exception in getColumnStatistics", e);
			throw StandardException.newException(e.getLocalizedMessage());
		}
	}

	private static IteratorNoPutResultSet wrapResults(EmbedConnection conn,
			Iterable<ExecRow> rows) throws StandardException {
		Activation lastActivation = conn.getLanguageConnection()
				.getLastActivation();
		IteratorNoPutResultSet resultsToWrap = new IteratorNoPutResultSet(rows,
				STATEMENT_STATS_OUTPUT_COLUMNS, lastActivation);
		resultsToWrap.openCore();
		return resultsToWrap;
	}

}
