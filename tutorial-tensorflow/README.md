# Overview
This is a framework where we can create multiple machine learning models using different machine learning platforms (Tensorflow, Spark MLib, R, etc) and algorithms where the complexity of machine learning is hidden from from the end user.  We are in the beginning phase and have developed the process for using a classifier for TensorFlow Linear and DNN joined training models.  At a high level, you use SQL statements (for now) to specify the database table that contains the training data set, the test data set and some other parameters and then you call a stored procedure to either generate the model or to predict an outcome.  

The first machine learning setup was done for TensorFlow.  The [tensorflow](http://www.tensorflow.org) framework is an open source software library for machine learning across a range of tasks.  It is a framework for building Deep Learning Neural Networks.  We took the '[TensorFlow Wide & Deep Learning Tutorial](https://www.tensorflow.org/tutorials/wide_and_deep)'  which trains a model to predict the probability that an individual has an annual income over 50,000 dollars using Census Income data set and created a method to generically create a model using any data set.

We use docker to run the machine learning process.  Docker is a tool that automates the deployment of applications inside software containers.  Dockerfiles enable you to create images that contain the software that is needed to run an application.  A Dockerfile describes the software that makes up an image and contains the set of instructions that specify what environment to use and which commands to run.  For more information goto [Docker's website](https://www.docker.com/)


# Setup

# 1.  Install Splice Machine
These instructions assume you have access to a running instance of Splice Machine 2.5.  If you don't goto [Splice Machine's website](https://www.splicemachine.com/get-started/) and install the standalone edition.

# 2.  Pull the Splice Machine Tensorflow Example

All of the community code for Splice Machine resides in the Splice Machine Community Sample Code github repository.  The code for this particular example can be found under tutorial-tensorflow.  Clone the Splice Machine repository containing the Tensorflow Code

	git clone https://github.com/splicemachine/splice-community-sample-code.git

# 3.  Compile the code

We use maven as our build process.  You must have maven installed in order to compile the code.  If you don't currently have it installed, please go to [maven's website](https://maven.apache.org/index.html) and follow the instructions on their site for installing and configuring maven.  Next, navigate to the tutorial-tensorflow folder and compile the code:

	mvn clean compile package -Pcdh5.8.3

# 4.  Copy the generated JAR file to your install
If you are running in standalone mode, copy the jar file to your <SPLICEMACHINE_HOME>/lib directory.  The command will look something like the following assuming you installed Splice Machine to your ~/Downloads directory:

	cp ./target/splice-tutorial-tensorflow-2.5.0.1708-SNAPSHOT.jar ~/Downloads/splicemachine/lib
	
# 5. Update paths in SQL files
The Splice Machine SQL scripts for importing data contain path references.  These paths need to be updated.  Modify the paths in the following files to to match the location where you cloned the git repository. 

* /resources/examples/census_example/ddl/create-data.sql - update the path on lines 2, 3 and 4
* /resources/examples/insurance_example/ddl/create-data.sql - update the path on lines 3 - 6
* /resources/examples/iris_example/ddl/create-data.sql - update the paths on lines 3 - 6
* /resources/splice/load-all.sql - update the path of all lines

# 6. Start Splice Machine
In standalone mode the command to start Splice Machine is:

	./bin/start-splice.sh
	
# 7. Launch the Splice Machine Command Prompt
In standalone mode the command to start Splice Machine is:

	./bin/sqlshell.sh

# 8. Run the script to create the data and tables
In the splice command prompt, run the /resources/splice/load-all.sql script.  Your command will look something like the following:

	run '/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/splice/load-all.sql';
	

# 9.  Install Docker

Install Docker on the environment where you plan on running the code.  The installation is straight forward and well documented on [Docker's web site](http://docs.docker.com/engine/installation/).  Check your installation by running:

	docker run hello-world


# 10.  Build the Splice Machine Tensorflow Docker Image
Now we need to create the Splice Machine Tensorflow Image.  Make sure you are in the tutorial-tensorflow/docker folder and run the following command:

	docker build -t splice-machine-tensorflow .

That will create the docker image to run the machine learning process.  If you are not familiar with Dockerfile files, take a look at the section [Splice Machine Dockerfile](#splice-machine-dockerfile) below.


# 11.  For standalone mode - create shared directory
If you are running Splice Machine in standalone mode we do not have assess to an HDFS system, so we need to create a directory where Splice Machine will export data to and Tensorflow will store the model output.  Create a directory /tmp/splicemachine-tensorflow.

	mkdir /tmp/splicemachine-tensorflow
	
# 12.  Run the Image
The run command creates a container using the specified image 'splice-machine-tensorflow'.  The command will be different if you are running Splice Machine in standalone versus a cluster.  In standalone, we are going to share a directory so we will need to specify the -v option, the command to start the docker container will be the following:

	docker run -p 8000:8000 -v /tmp/splicemachine-tensorflow:/tmp/splicemachine-tensorflow splice-machine-tensorflow

In a cluster mode, we will start the container with the -d option which will detach the docker process and run in the background

	docker run -p 8000:8000 -d splice-machine-tensorflow
	
# 13. For cluster mode - update the URLs for the docker integration
The database table MACHINE_LEARNING_METHOD contains two database columns with the URL for the Create Model and Predict Outcome processed.  Run the following command in the splice command prompt, replacing localhost with the server name:

	update MACHINE_LEARNING_METHOD set CREATION_PROCESS = 'http://localhost:8000/train_and_eval', PREDICT_PROCESS = 'http://localhost:8000/predict_outcome';


# 14.  Confirm you can connect to the web server running on your container
A simple get request is available to confirm your setup is correct.  It will return "Hello world!" when called.  Prior to running the tensorflow machine learning process, confirm that you can connect to the web server running on your container.  Open a browser a and navigate to the following url:

	http://localhost:8000/hello
	
# 15. Create the census model
We are ready to create our model.  Open a splice command prompt.  We will run a script that calls a stored procedure to create the model.  The command you run will look similar to the following - make sure to update the path to match your environment.

	run '/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/queries/create-model.sql';

# 16. Predict an outcome with the census model
Once the model is created we can use it to predict an outcome.  In a splice command prompt run a script that calls a stored procedure to predict an outcome.  The command you run will look similar to the following - make sure to update the path to match your environment.

	run '/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/examples/census_example/queries/predict-true.sql';


# Additional Resources
Here are some additional resources to help understand the contents of this solution:

* **[DATABASE_TABLES.md](DATABASE_TABLES.md)**:  This file contains an overview of the database tables that are a part of this solution
* **[CODE_OVERVIEW.md](CODE_OVERVIEW.md)**:  This file contains an overview of the code used in this solution
* **[NEW_MODEL_SETUP.md](NEW_MODEL_SETUP.md)**: Provides instructions on setting up your own dataset

# Docker Tips

## Splice Machine Dockerfile
If you are new to Dockerfiles this section explains the code found in the Dockerfile file.  Once you have pulled the code navigate to the tutorial-tensorflow folder and then to the docker folder.  In this folder you will see a file called Dockerfile.  In this section we will go over the contents of that file for informational purposes, but you will not need to make any changes to this file. 

	FROM python:3

That means that we are starting with a basic Python 3 image.  Next we want to install several python packages using pip.  We will install pandas, tensorflow, bug and gunicorn.  

	RUN pip install pandas
	RUN pip install tensorflow
	RUN pip install hug
	RUN pip install uwsgi
	
Next we create an environment variable using the ENV statement.  That environment variable has the name template and is going to be used to point to the template folder under the docker folder.

	ENV app=template

Next we are going to copy the template folder referenced using the 'app' environment variable to the app directory on our image.  The ADD command copies files from your local (or could be remote urls) and adds them to the image being created

	ADD $app app
	
We will interact with this image using http and connect on port 8000.  To tell the container to listen on a port use the EXPOSE instruction:

	EXPOSE 8000

Finally we are going to tell the container to start uwsgi

	CMD ["uwsgi", "--http","0.0.0.0:8000", "--wsgi-file", "/app/handlers/TensorFlowWideNDeepHug.py", "--callable", "__hug_wsgi__", "--worker-reload-mercy", "300", "--http-timeout","600"]
	

## Display a list of containers
The following command will display the list of containers in your environment.  The -a option also shows the containers that are not running.

	docker ps -a
	
## How to stop a running container
If you are not able to stop a running container using ctrl-c or ctrl-d, then open another command prompt, get a list of the running containers using the command above, and then run the 'stop' command passing in the container id like the following:

	docker stop ff4fd2e00d24

## Display container details
If you want to see the details of 

	docker inspect 7bf83406aec2

## Delete a container
If you want to delete a container, run the command above to get the list of containers and get the container id for the one that you want to delete.  It should be the first column and is an alphanumeric value.  Then run the 'rm' command specifying the container id like the following:

	docker rm 44d37af9b266
	
## Delete all containers 
If you want to delete all the containers, run the following command:

	docker ps -q -a | xargs docker rm
	
## Delete an image
If you want to delete an image, run the command above to get the list of containers and get the image name for the one that you want to delete.  It should be the second column.  Then run the 'rmi' command specifying the container id like the following:

	docker rmi hello-world 

# Using Hug
If you want to run the python script on your local environment - assuming you have all the tensorflow libraries installed you can run it with the hug library as follows:

	hug -f first_step_1.py

