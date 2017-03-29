set schema IRIS;

CALL SYSCS_UTIL.IMPORT_DATA( 'IRIS','TRAINING_DATA','SEPAL_LENGTH,SEPAL_WIDTH,PETAL_LENGTH,PETAL_WIDTH,IRIS_CLASS','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/iris_example/data/training.data.txt',',',null,null,null,null,-1,null, true, null);
CALL SYSCS_UTIL.IMPORT_DATA( 'IRIS','TEST_DATA','SEPAL_LENGTH,SEPAL_WIDTH,PETAL_LENGTH,PETAL_WIDTH,IRIS_CLASS','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/iris_example/data/testing.data.txt',',',null,null,null,null,-1,null, true, null);
CALL SYSCS_UTIL.IMPORT_DATA( 'IRIS','LIVE_DATA','SEPAL_LENGTH,SEPAL_WIDTH,PETAL_LENGTH,PETAL_WIDTH,IRIS_CLASS','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/iris_example/data/live.data.txt',',',null,null,null,null,-1,null, true, null);
CALL SYSCS_UTIL.IMPORT_DATA( 'IRIS','LIVE_DATA_EXPECTED','ID,EXPECTED_RESULT','/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/iris_example/data/live-expected.txt',',',null,null,null,null,-1,null, true, null);

update IRIS.TRAINING_DATA set IRIS_CLASS_CODE = 1 where IRIS_CLASS = 'Iris-versicolor';
update IRIS.TRAINING_DATA set IRIS_CLASS_CODE = 2 where IRIS_CLASS = 'Iris-setosa';
update IRIS.TRAINING_DATA set IRIS_CLASS_CODE = 3 where IRIS_CLASS = 'Iris-virginica';

update IRIS.TEST_DATA set IRIS_CLASS_CODE = 1 where IRIS_CLASS = 'Iris-versicolor';
update IRIS.TEST_DATA set IRIS_CLASS_CODE = 2 where IRIS_CLASS = 'Iris-setosa';
update IRIS.TEST_DATA set IRIS_CLASS_CODE = 3 where IRIS_CLASS = 'Iris-virginica';

update IRIS.LIVE_DATA_EXPECTED set IRIS_CLASS_CODE = 1 where EXPECTED_RESULT = 'Iris-versicolor';
update IRIS.LIVE_DATA_EXPECTED set IRIS_CLASS_CODE = 2 where EXPECTED_RESULT = 'Iris-setosa';
update IRIS.LIVE_DATA_EXPECTED set IRIS_CLASS_CODE = 3 where EXPECTED_RESULT = 'Iris-virginica';

