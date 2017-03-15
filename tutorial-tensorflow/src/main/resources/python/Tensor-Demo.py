
# Copyright 2016 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================
"""Example code for TensorFlow Wide & Deep Tutorial using TF.Learn API."""
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

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



# In[3]:

# This will error if you run more than once

DEBUG=True

flags = tf.app.flags
FLAGS = flags.FLAGS

flags.DEFINE_string("model_dir", "", "Base directory for output models.")
flags.DEFINE_string("model_type", "wide_n_deep","Valid model types: {'wide', 'deep', 'wide_n_deep'}.")
flags.DEFINE_integer("train_steps", 200, "Number of training steps.")
flags.DEFINE_string("train_data","","Path to the training data.")
flags.DEFINE_string("test_data","","Path to the test data.")
flags.DEFINE_string("inputs","","Input data dictionary")
flags.DEFINE_string("input_record", "","Comma delimited input record")
flags.DEFINE_string("predict", "false","Indicates if we are predicting or building the model")
flags.DEFINE_integer("hash_bucket_size", 1000,"The hash bucket size")
flags.DEFINE_integer("dimension", 8,"The dimension")
flags.DEFINE_string("dnn_hidden_units", "100,50","List of hidden units per DNN layer")
flags.DEFINE_string("comparison_column", "income_bracket","The column the value will be compared against")
flags.DEFINE_string("criteria", ">50K","The binary classification criteria")

if DEBUG:
	print("comparison_column=%s" % FLAGS.comparison_column)
	print("criteria=%s" % FLAGS.criteria)
	print("hash_bucket_size=%s" % FLAGS.hash_bucket_size)
	print("dimension=%s" % FLAGS.dimension)
	print("dnn_hidden_units=%s" % FLAGS.dnn_hidden_units)
	print("model_dir=%s" % FLAGS.model_dir)
	print("model_type=%s" % FLAGS.model_type)
	print("train_steps=%s" % FLAGS.train_steps)
	print("train_data=%s" % FLAGS.train_data)
	print("test_data=%s" % FLAGS.test_data)
	print("inputs=%s" % FLAGS.inputs)

# In[4]:


## TBD: The dict below should be input to teh script and constructed by a stored procedure
## The Stored Procedure can construct JSON and then this script can decode the JSON into the dict
## The paths to the files below should be paths constructed by a Splice Machine Export


INPUT_DICT=json.loads(FLAGS.inputs)
COLUMNS = INPUT_DICT['columns'];
LABEL_COLUMN = INPUT_DICT['label_column'];
CATEGORICAL_COLUMNS=None
if 'categorical_columns' in INPUT_DICT:
	CATEGORICAL_COLUMNS = INPUT_DICT['categorical_columns'];
CONTINUOUS_COLUMNS=None
if 'continuous_columns' in INPUT_DICT:
	CONTINUOUS_COLUMNS = INPUT_DICT['continuous_columns'];
CROSSED_COLUMNS=None
if 'crossed_columns' in INPUT_DICT:
	CROSSED_COLUMNS = INPUT_DICT['crossed_columns'];
BUCKETIZED_COLUMNS=None
if 'bucketized_columns' in INPUT_DICT:
	BUCKETIZED_COLUMNS = INPUT_DICT['bucketized_columns'];
DNN_HIDDEN_UNITS=[int(s) for s in FLAGS.dnn_hidden_units.split(',')] 

if DEBUG:
	print("INPUT_DICT=%s" % INPUT_DICT)
	print("COLUMNS=%s" % COLUMNS)
	print("LABEL_COLUMN=%s" % LABEL_COLUMN)
	print("CATEGORICAL_COLUMNS=%s" % CATEGORICAL_COLUMNS)
	print("CONTINUOUS_COLUMNS=%s" % CONTINUOUS_COLUMNS)
	print("CROSSED_COLUMNS=%s" % CROSSED_COLUMNS)
	print("BUCKETIZED_COLUMNS=%s" % BUCKETIZED_COLUMNS)
	print("DNN_HIDDEN_UNITS=%s" % DNN_HIDDEN_UNITS)


# In[5]:


def maybe_download():
  """May be downloads training data and returns train and test file names."""
  if FLAGS.train_data:
    train_file_name = FLAGS.train_data
  else:
    train_file = tempfile.NamedTemporaryFile(delete=False)
    urllib.request.urlretrieve(FLAGS.train_data, train_file.name)  # pylint: disable=line-too-long
    train_file_name = train_file.name
    train_file.close()
    if DEBUG:
    	print("Training data is downloaded to %s" % train_file_name)

  if FLAGS.test_data:
    test_file_name = FLAGS.test_data
  else:
    test_file = tempfile.NamedTemporaryFile(delete=False)
    urllib.request.urlretrieve(FLAGS.test_data, test_file.name)  # pylint: disable=line-too-long
    test_file_name = test_file.name
    test_file.close()
    if DEBUG:
    	print("Test data is downloaded to %s" % test_file_name)

  return train_file_name, test_file_name


# In[6]:

def prepare_sparse_columns(cols):
    """Creates tf sparse columns with hash buckets"""
    # Sparse base columns.
    # TBD: allow keyed columns and hash bucket size as input
    tf_cols ={}
    if(cols != None):
	    for col in cols :
	        tf_cols[col] = tf.contrib.layers.sparse_column_with_hash_bucket(
	          col, hash_bucket_size=FLAGS.hash_bucket_size)
    return tf_cols


# In[7]:

SPARSE_TF_COLUMNS = prepare_sparse_columns(CATEGORICAL_COLUMNS)
if DEBUG:
	print(SPARSE_TF_COLUMNS)


# In[8]:

def prepare_continuous_columns(cols):
    """Creates tf.contrib.layers.real_valued_columns"""
    #Continuous base columns
    tf_cols ={}
    if(cols != None):
	    for col in cols :
	        tf_cols[col] = (tf.contrib.layers.real_valued_column(col))
    return tf_cols


# In[9]:

REAL_TF_COLUMNS = prepare_continuous_columns(CONTINUOUS_COLUMNS)
if DEBUG:
	print(REAL_TF_COLUMNS)


# In[10]:

def prepare_buckets(cols):
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


# In[11]:
if DEBUG:
	print(BUCKETIZED_COLUMNS)
BUCKETIZED_TF_COLUMNS = prepare_buckets(BUCKETIZED_COLUMNS)


# In[12]:

def prepare_embedded_columns(cols):
    """Create tf.contrib.layers.embedding_columns for the sparse entries"""
    tf_cols = {}
    if(cols != None):
	    for col in cols:
	        tf_cols[col] = tf.contrib.layers.embedding_column(col, dimension=FLAGS.dimension)
    return tf_cols


# In[13]:
if DEBUG:
	print(list(SPARSE_TF_COLUMNS.keys()))


# In[14]:

EMBEDDED_TF_COLUMNS = prepare_embedded_columns(list(SPARSE_TF_COLUMNS.values()))


# In[15]:
if DEBUG:
	print(EMBEDDED_TF_COLUMNS)


# In[16]:

DEEP_TF_COLUMNS =  list(EMBEDDED_TF_COLUMNS.values()) + list(REAL_TF_COLUMNS.values())
if DEBUG:
	print(DEEP_TF_COLUMNS)


# In[18]:

def prepare_crossed(cols):
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
	            print(tf_var)
	            list_of_cols.append(tf_var)
	        new_cols.append(tf.contrib.layers.crossed_column(list_of_cols,
	                      hash_bucket_size=int(1e6)))
    return new_cols


# In[19]:

CROSSED_TF_COLS = prepare_crossed(CROSSED_COLUMNS)


# In[20]:
if DEBUG:
	print(CROSSED_TF_COLS)
if(len(CROSSED_TF_COLS) > 0):
	CROSSED_TF_COLS[1]

# In[17]:

WIDE_TF_COLUMNS = list(SPARSE_TF_COLUMNS.values()) + list(BUCKETIZED_TF_COLUMNS.values()) + list(CROSSED_TF_COLS)
if DEBUG:
	print(WIDE_TF_COLUMNS)


# In[21]:

def build_estimator(model_dir, model_type):
  """Build an estimator."""
  if model_type == "wide":
    m = tf.contrib.learn.LinearClassifier(model_dir=model_dir,
                                          feature_columns=WIDE_TF_COLUMNS)
  elif model_type == "deep":
    m = tf.contrib.learn.DNNClassifier(model_dir=model_dir,
                                       feature_columns=DEEP_TF_COLUMNS,
                                       hidden_units=DNN_HIDDEN_UNITS)
  else:
	  m = tf.contrib.learn.DNNLinearCombinedClassifier(
	    model_dir=model_dir,
	    linear_feature_columns=WIDE_TF_COLUMNS,
	    dnn_feature_columns=DEEP_TF_COLUMNS,
	    dnn_hidden_units=DNN_HIDDEN_UNITS
	    )
  return m


# In[22]:

def input_fn(df):
  """Input builder function."""
  # Creates a dictionary mapping from each continuous feature column name (k) to
  # the values of that column stored in a constant Tensor.
  continuous_cols = {k: tf.constant(df[k].values) for k in CONTINUOUS_COLUMNS}
  # Creates a dictionary mapping from each categorical feature column name (k)
  # to the values of that column stored in a tf.SparseTensor.
  categorical_cols = {}
  if(CATEGORICAL_COLUMNS != None):
	  categorical_cols = {k: tf.SparseTensor(
	      indices=[[i, 0] for i in range(df[k].size)],
	      values=df[k].values,
	      shape=[df[k].size, 1])
	                      for k in CATEGORICAL_COLUMNS}
  # Merges the two dictionaries into one.
  feature_cols = dict(continuous_cols)
  feature_cols.update(categorical_cols)
  # Converts the label column into a constant Tensor.
  label = tf.constant(df[LABEL_COLUMN].values)
  
  print('Labels: {}'.format(str(label)))
  
  # Returns the feature columns and the label.
  return feature_cols, label


# In[25]:

def train_and_eval():
  """Train and evaluate the model."""
#  train_file_name, test_file_name = maybe_download()
  train_file_name=FLAGS.train_data;
  test_file_name=FLAGS.test_data;
  
  df_train = pd.read_csv(
      tf.gfile.Open(train_file_name),
      names=COLUMNS,
      skipinitialspace=True,
      engine="python")
  df_test = pd.read_csv(
      tf.gfile.Open(test_file_name),
      names=COLUMNS,
      skipinitialspace=True,
      skiprows=1,
      engine="python")
      
  
  model_dir = tempfile.mkdtemp() if not FLAGS.model_dir else FLAGS.model_dir
  if DEBUG:
  	print("model directory = %s" % model_dir)

  m = build_estimator(model_dir, FLAGS.model_type )
  m.fit(input_fn=lambda: input_fn(df_train), steps=FLAGS.train_steps)
  results = m.evaluate(input_fn=lambda: input_fn(df_test), steps=1)
  print("Begin Results [")
  for key in sorted(results):
    print("%s: %s" % (key, results[key]))
  print("] End Results")
    
  y=m.predict(input_fn=lambda: input_fn(df_train))
  print("Training data predictions=[%s]" % Counter(y))
  
  y=m.predict(input_fn=lambda: input_fn(df_test))
  print("Testing data predictions=[%s]" % Counter(y))

def predict_outcome():
  model_dir = tempfile.mkdtemp() if not FLAGS.model_dir else FLAGS.model_dir
  print('model_dir = %s' % model_dir);
  m = build_estimator(model_dir, FLAGS.model_type )

  indata=StringIO(FLAGS.input_record)

  prediction_set = pd.read_csv(
      indata,
      names=COLUMNS,
      skipinitialspace=True,
      skiprows=0,
      engine="python")

#  prediction_set[LABEL_COLUMN] = (
#      prediction_set[FLAGS.comparison_column].apply(lambda x: FLAGS.criteria in x)).astype(int)


  y=m.predict(input_fn=lambda: input_fn(prediction_set))
  print('Predictions: {}'.format(str(y)))
  return y
  
# In[ ]:

def main(_):
  if FLAGS.predict == "true":
    predict_outcome()
  else:
    train_and_eval()

if __name__ == "__main__":
  tf.app.run()

