MODEL_DIR="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/census_out/test-1"
MODEL_TYPE="wide_n_deep"
TRAIN_STEPS=1000
TRAIN_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/census_out/test-1/data/train/part-r-00000.csv"
TEST_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/census_out/test-1/data/test/part-r-00000.csv"
HASH_BUCKET_SIZE=1000
DIMENSION=8
DNN_HIDDEN_UNITS="100, 50"
INPUT_DATA="{\"columns\": [\"age\", \"capital_gain\", \"capital_loss\", \"education\", \"education_num\", \"fnlwgt\", \"gender\", \"hours_per_week\", \"income_bracket\", \"label\", \"marital_status\", \"native_country\", \"occupation\", \"race\", \"relationship\", \"workclass\"], \"continuous_columns\": [\"age\", \"education_num\", \"capital_gain\", \"capital_loss\", \"hours_per_week\"], \"categorical_columns\": [\"workclass\", \"education\", \"marital_status\", \"occupation\", \"relationship\", \"race\", \"gender\", \"native_country\"], \"crossed_columns\": [[\"age_buckets\", \"education\", \"occupation\"], [\"native_country\", \"occupation\"], [\"education\", \"occupation\"]], \"bucketized_columns\": {\"age_buckets\": {\"age\": [18, 25, 30, 35, 40, 45, 50, 55, 60, 65]}}, \"label_column\": \"label\"}"
COMPARISON_COLUMN=income_bracket
CRITERIA=">50K"
#PREDICT FALSE - 0
INPUT_RECORD="32,0,0, HS-grad,9,287229, Female,35, <=50K,null, Never-married, United-States, Adm-clerical, White, Not-in-family, Private"
#PREDICT TRUE - 1
#INPUT_RECORD="38,99999,0, Doctorate,16,203988, Male,55, >50K,null, Married-civ-spouse, United-States, Prof-specialty, White, Husband, Self-emp-not-inc"

cd /Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python

python  Tensor-Demo.py \
	--predict=true \
	--model_type=$MODEL_TYPE \
	--train_steps=$TRAIN_STEPS \
	--hash_bucket_size=$HASH_BUCKET_SIZE \
	--dimension=$DIMENSION \
	--dnn_hidden_units="$DNN_HIDDEN_UNITS" \
	--comparison_column=$COMPARISON_COLUMN \
	--criteria="$CRITERIA" \
	--train_data=$TRAIN_DATA \
	--test_data=$TEST_DATA \
	--model_dir=$MODEL_DIR \
	--input_record="$INPUT_RECORD" \
	--inputs="$INPUT_DATA"


