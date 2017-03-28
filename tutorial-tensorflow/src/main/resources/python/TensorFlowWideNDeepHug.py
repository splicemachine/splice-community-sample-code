# Copyright (c) 2012 - 2017 Splice Machine, Inc.
#
# This file is part of Splice Machine.
# Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
# GNU Affero General Public License as published by the Free Software Foundation, either
# version 3, or (at your option) any later version.
# Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Affero General Public License for more details.
# You should have received a copy of the GNU Affero General Public License along with Splice Machine.
# If not, see <http://www.gnu.org/licenses/>.
#

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import hug
import sys
if sys.version_info[0] < 3:
    from StringIO import StringIO
else:
    from io import StringIO

import json
import tempfile
from six.moves import urllib
from collections import Counter

import pandas as pd
import tensorflow as tf

DEBUG=True

COLUMNS=None
LABEL_COLUMN=None
CATEGORICAL_COLUMNS=None
CONTINUOUS_COLUMNS=None
CROSSED_COLUMNS=None
BUCKETIZED_COLUMNS=None
DNN_HIDDEN_UNITS=None


def setVariableValues(body):
	global COLUMNS
	global LABEL_COLUMN
	global CATEGORICAL_COLUMNS
	global CONTINUOUS_COLUMNS
	global CROSSED_COLUMNS
	global BUCKETIZED_COLUMNS
	global DNN_HIDDEN_UNITS
	global LABEL_COLUMN

	LABEL_COLUMN=body['label_column']
	COLUMNS=body['columns']
	if 'categorical_columns' in body:
		CATEGORICAL_COLUMNS = body['categorical_columns']
	if 'continuous_columns' in body:
		CONTINUOUS_COLUMNS = body['continuous_columns']
	if 'crossed_columns' in body:
		CROSSED_COLUMNS = body['crossed_columns'];
	if 'bucketized_columns' in body:
		BUCKETIZED_COLUMNS = body['bucketized_columns'];
	DNN_HIDDEN_UNITS=[int(s) for s in body['dnn_hidden_units'].split(',')] 


def printVariables():
	if DEBUG:
		print("COLUMNS=%s" % COLUMNS)
		print("LABEL_COLUMN=%s" % LABEL_COLUMN)
		print("CATEGORICAL_COLUMNS=%s" % CATEGORICAL_COLUMNS)
		print("CONTINUOUS_COLUMNS=%s" % CONTINUOUS_COLUMNS)
		print("CROSSED_COLUMNS=%s" % CROSSED_COLUMNS)
		print("BUCKETIZED_COLUMNS=%s" % BUCKETIZED_COLUMNS)
		print("DNN_HIDDEN_UNITS=%s" % DNN_HIDDEN_UNITS)

def maybe_download(body):
	"""May be downloads training data and returns train and test file names."""
	if body['train_data']:
		train_file_name = body['train_data']
	else:
		train_file = tempfile.NamedTemporaryFile(delete=False)
		urllib.request.urlretrieve(body['train_data'], train_file.name)  # pylint: disable=line-too-long
		train_file_name = train_file.name
		train_file.close()
	if DEBUG:
		print("Training data is downloaded to %s" % train_file_name)

	if body['test_data']:
		test_file_name = body['test_data']
	else:
		test_file = tempfile.NamedTemporaryFile(delete=False)
		urllib.request.urlretrieve(body['test_data'], test_file.name)  # pylint: disable=line-too-long
		test_file_name = test_file.name
		test_file.close()
	if DEBUG:
		print("Test data is downloaded to %s" % test_file_name)

	return train_file_name, test_file_name

def prepare_sparse_columns(cols,body):
	"""Creates tf sparse columns with hash buckets"""
	# Sparse base columns.
	# TBD: allow keyed columns and hash bucket size as input
	tf_cols ={}
	if(cols != None):
		for col in cols :
			tf_cols[col] = tf.contrib.layers.sparse_column_with_hash_bucket(
				col, hash_bucket_size=int(body['hash_bucket_size']))
	return tf_cols

def prepare_continuous_columns(cols):
	"""Creates tf.contrib.layers.real_valued_columns"""
	#Continuous base columns
	tf_cols ={}
	if(cols != None):
		for col in cols :
			tf_cols[col] = (tf.contrib.layers.real_valued_column(col))
	return tf_cols

def prepare_buckets(cols,REAL_TF_COLUMNS):
	"""Creates tf bucketed columns"""
	new_cols = {}
	if(cols != None):
		for newCol in cols:
			keyvalues = cols[newCol]
			for colname in keyvalues:
				orig_col = REAL_TF_COLUMNS[colname]
				bound = keyvalues[colname]
				new_cols[newCol] = tf.contrib.layers.bucketized_column(orig_col, boundaries=bound)
	return new_cols

def prepare_embedded_columns(cols,body):
	"""Create tf.contrib.layers.embedding_columns for the sparse entries"""
	tf_cols = {}
	if(cols != None):
		for col in cols:
			tf_cols[col] = tf.contrib.layers.embedding_column(col, dimension=body['dimensions'])
	return tf_cols

def prepare_crossed(cols,BUCKETIZED_TF_COLUMNS,SPARSE_TF_COLUMNS,REAL_TF_COLUMNS):
	"""Creates tf crossed columns"""
	new_cols = [];
	if(cols != None):
		for tuple in cols:
			list_of_cols = []
			for var in tuple:
				b = BUCKETIZED_TF_COLUMNS.get(var,False)
				s = SPARSE_TF_COLUMNS.get(var,False)
				r = REAL_TF_COLUMNS.get(var,False)
				if b : tf_var = b
				else :
					if s : tf_var = s
					else :
						if r : tf_var = r
				list_of_cols.append(tf_var)
			new_cols.append(tf.contrib.layers.crossed_column(list_of_cols,hash_bucket_size=int(1e6)))
	return new_cols

def build_estimator(model_dir, model_type,wide_tf_columns,deep_tf_columns):
	"""Build an estimator."""
	if model_type == "wide":
		m = tf.contrib.learn.LinearClassifier(model_dir=model_dir,feature_columns=wide_tf_columns)
	elif model_type == "deep":
		m = tf.contrib.learn.DNNClassifier(model_dir=model_dir,feature_columns=deep_tf_columns,hidden_units=DNN_HIDDEN_UNITS)
	else:
		m = tf.contrib.learn.DNNLinearCombinedClassifier(
			model_dir=model_dir,
			n_classes=2,
			linear_feature_columns=wide_tf_columns,
			dnn_feature_columns=deep_tf_columns,
			dnn_hidden_units=DNN_HIDDEN_UNITS
		)
	return m

def input_fn(df):
	"""Input builder function."""
	# Creates a dictionary mapping from each continuous feature column name (k) to
	# the values of that column stored in a constant Tensor.
	continuous_cols = {k: tf.constant(df[k].values, shape=[df[k].size, 1]) for k in CONTINUOUS_COLUMNS}
	# Creates a dictionary mapping from each categorical feature column name (k)
	# to the values of that column stored in a tf.SparseTensor.
	categorical_cols = {}
	if(CATEGORICAL_COLUMNS != None):
		categorical_cols = {
			k: tf.SparseTensor(
				indices=[[i, 0] for i in range(df[k].size)],
				values=df[k].values,
				dense_shape=[df[k].size, 1])
			for k in CATEGORICAL_COLUMNS}
	# Merges the two dictionaries into one.
	feature_cols = dict(continuous_cols)
	feature_cols.update(categorical_cols)
	# Converts the label column into a constant Tensor.
	label = tf.constant(df[LABEL_COLUMN].values, shape=[df[LABEL_COLUMN].size, 1])
  
	if DEBUG:
		print('Labels: {}'.format(str(label)))
  
	# Returns the feature columns and the label.
	return feature_cols, label

def jdefault(o):
	return o.__dict__

@hug.post()
def predict_outcome(body):
	response = {}
	response['success'] = False

	setVariableValues(body)
	printVariables()

	model_dir = tempfile.mkdtemp() if not body['model_dir'] else body['model_dir']
	print('model_dir = %s' % model_dir);
	
	SPARSE_TF_COLUMNS = prepare_sparse_columns(CATEGORICAL_COLUMNS,body)

	if DEBUG:
		print("continuous_columns %s" %CONTINUOUS_COLUMNS)

	REAL_TF_COLUMNS = prepare_continuous_columns(CONTINUOUS_COLUMNS)


	if DEBUG:
		print("real_tf_columns: %s" % REAL_TF_COLUMNS);

	BUCKETIZED_TF_COLUMNS = prepare_buckets(BUCKETIZED_COLUMNS,REAL_TF_COLUMNS)
	EMBEDDED_TF_COLUMNS = prepare_embedded_columns(list(SPARSE_TF_COLUMNS.values()),body)
	DEEP_TF_COLUMNS =  list(EMBEDDED_TF_COLUMNS.values()) + list(REAL_TF_COLUMNS.values())
	CROSSED_TF_COLS = prepare_crossed(CROSSED_COLUMNS,BUCKETIZED_TF_COLUMNS,SPARSE_TF_COLUMNS,REAL_TF_COLUMNS)
	WIDE_TF_COLUMNS = list(SPARSE_TF_COLUMNS.values()) + list(BUCKETIZED_TF_COLUMNS.values()) + list(CROSSED_TF_COLS)

	m = build_estimator(model_dir, body['model_type'], WIDE_TF_COLUMNS,DEEP_TF_COLUMNS )
	indata=StringIO(body['input_record'])

	prediction_set = pd.read_csv(
		indata,
		names=COLUMNS,
		skipinitialspace=True,
		skiprows=0,
		engine="python")
	
	y=list(m.predict(input_fn=lambda: input_fn(prediction_set),as_iterable=True))
	#if DEBUG:
	#	print('Predictions: {}'.format(list(y)))
	response['success'] = True
	response['prediction'] = '{}'.format(str(y))
	return json.dumps(response,default=jdefault)

@hug.post()
def train_and_eval(body):
	response = {}
	response['success'] = False	

	setVariableValues(body)
	printVariables()

	"""Train and evaluate the model."""
	train_file_name, test_file_name = maybe_download(body)

	df_train = pd.read_csv(
		tf.gfile.Open(train_file_name),
		names=COLUMNS,
		skipinitialspace=True,
		engine="python")
	df_test = pd.read_csv(
		tf.gfile.Open(test_file_name),
		names=COLUMNS,
		skipinitialspace=True,
		engine="python")

	# remove NaN elements
	df_train = df_train.dropna(how='any', axis=0)
	df_test = df_test.dropna(how='any', axis=0)

	if DEBUG:  
		print("Training model label column values: %s:" %df_train[LABEL_COLUMN])
		print("Test model label column values: %s:" %df_test[LABEL_COLUMN])
  
	model_dir = tempfile.mkdtemp() if not body['model_dir'] else body['model_dir']
	if DEBUG:
		print("model directory = %s" % model_dir)

	SPARSE_TF_COLUMNS = prepare_sparse_columns(CATEGORICAL_COLUMNS,body)

	if DEBUG:
		print("continuous_columns %s" %CONTINUOUS_COLUMNS)

	REAL_TF_COLUMNS = prepare_continuous_columns(CONTINUOUS_COLUMNS)


	if DEBUG:
		print("real_tf_columns: %s" % REAL_TF_COLUMNS);

	BUCKETIZED_TF_COLUMNS = prepare_buckets(BUCKETIZED_COLUMNS,REAL_TF_COLUMNS)
	EMBEDDED_TF_COLUMNS = prepare_embedded_columns(list(SPARSE_TF_COLUMNS.values()),body)
	DEEP_TF_COLUMNS =  list(EMBEDDED_TF_COLUMNS.values()) + list(REAL_TF_COLUMNS.values())
	CROSSED_TF_COLS = prepare_crossed(CROSSED_COLUMNS,BUCKETIZED_TF_COLUMNS,SPARSE_TF_COLUMNS,REAL_TF_COLUMNS)
	WIDE_TF_COLUMNS = list(SPARSE_TF_COLUMNS.values()) + list(BUCKETIZED_TF_COLUMNS.values()) + list(CROSSED_TF_COLS)

	m = build_estimator(model_dir, body['model_type'], WIDE_TF_COLUMNS,DEEP_TF_COLUMNS )
	m.fit(input_fn=lambda: input_fn(df_train), steps=int(body['training_steps']))
	results = m.evaluate(input_fn=lambda: input_fn(df_test), steps=1)

	response['success'] = True

	for key in sorted(results):
		response[key] = str(results[key])
		print("%s: %s" % (key, results[key]))

	y=m.predict(input_fn=lambda: input_fn(df_train))
	print("Training data predictions=[%s]" % Counter(y))

	y=m.predict(input_fn=lambda: input_fn(df_test))
	print("Testing data predictions=[%s]" % Counter(y))

	return json.dumps(response,default=jdefault)


