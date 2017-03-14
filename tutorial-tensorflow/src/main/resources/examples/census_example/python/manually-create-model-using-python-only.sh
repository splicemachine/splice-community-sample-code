MODEL_DIR="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/census_out"
MODEL_TYPE="wide_n_deep"
TRAIN_STEPS=200
TRAIN_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python/data/train/part-r-00000.csv"
TEST_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python/data/test/part-r-00000.csv"
HASH_BUCKET_SIZE=100
DIMENSION=8
DNN_HIDDEN_UNITS="100, 50"
INPUT_DATA="{\"columns\": [\"age\", \"capital_gain\", \"capital_loss\", \"education\", \"education_num\", \"fnlwgt\", \"gender\", \"hours_per_week\", \"income_bracket\", \"label\", \"marital_status\", \"native_country\", \"occupation\", \"race\", \"relationship\", \"workclass\"], \"continuous_columns\": [\"age\", \"education_num\", \"capital_gain\", \"capital_loss\", \"hours_per_week\"], \"categorical_columns\": [\"workclass\", \"education\", \"marital_status\", \"occupation\", \"relationship\", \"race\", \"gender\", \"native_country\"], \"crossed_columns\": [[\"age_buckets\", \"education\", \"occupation\"], [\"native_country\", \"occupation\"], [\"education\", \"occupation\"]], \"bucketized_columns\": {\"age_buckets\": {\"age\": [18, 25, 30, 35, 40, 45, 50, 55, 60, 65]}}, \"label_column\": \"label\"}"


cd /Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python

python  Tensor-Demo.py \
	--model_type=$MODEL_TYPE \
	--hash_bucket_size=$HASH_BUCKET_SIZE \
	--dimension=$DIMENSION \
	--train_data=$TRAIN_DATA \
	--test_data=$TEST_DATA \
	--dnn_hidden_units="$DNN_HIDDEN_UNITS" \
	--model_dir=$MODEL_DIR \
	--inputs="$INPUT_DATA"
