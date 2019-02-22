# mongodbcourse2
# M220J: MongoDB for Java Developers
 

# The code found in this repo is from:
# https://university.mongodb.com/mercury/M220J/2019_February/overview



## MongoDB Atlas Account:
https://cloud.mongodb.com/user#/atlas/register/accountProfile
Using Atlas means you won't have to worry about installing, configuring, or managing your database anymore.

URL:
https://cloud.mongodb.com/v2/5c661268cf09a28adbd3e64b#clusters/edit

New Cluster Name:
mflix

Project Name:
M220

DB User:

username: m220student
password: m220password

YOUR_CLUSTER_URI: 
mflix-cxjdm.mongodb.net/test?retryWrites=true

Connection:
mongodb+srv://m220student:<PASSWORD>@mflix-cxjdm.mongodb.net/test?retryWrites=true
mongodb+srv://m220student:m220password@mflix-cxjdm.mongodb.net/test?retryWrites=true

Run the docker container in order to have access to the mongorestore command.
docker run -p 27017:27017 -v /Users/THE-USER/dockerdata/mongo:/data/db -d mongo

Create a temporal folder named /_temp in the folder:
/Users/THE-USER/dockerdata/mongo/_temp


copy the folder with its content:
/mflix-java/mflix/data 

into the previous created folder:
/Users/THE-USER/dockerdata/mongo/_temp


Open the command line of the MongoDB docker container:
docker exec -it <docker-container-mongo-id> bash

Go to the /_temp folder:
cd /data/db/_temp


Execute the mongorestore tool to import the data located in the /Users/THE-USER/dockerdata/mongo/_temp/data folder into the Atlas MongoDB:

mongorestore --drop --gzip --uri mongodb+srv://m220student:m220password@<YOUR_CLUSTER_URI> data
mongorestore --drop --gzip --uri mongodb+srv://m220student:m220password@mflix-cxjdm.mongodb.net/test?retryWrites=true data

The Data is now imported into the mflix Atlas DB Cluster!

# URI to MongoDB called svr string
# mongodb+srv://<username>:<password>@<host>/<database>
# the srv record manages the info related to the cluster
# retryWrites=true Retries the connection if it fails

## Run the App

cd /mflix-java/mflix
mvn spring-boot:run

http://localhost:5000/

## Running the Unit Tests

cd mflix
mvn -Dtest=<TestClass> test

For instance:

cd mflix
mvn -Dtest=ConnectionTest test


## MongoClient

	Java MongoDB Driver Used in this Course:

	<dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>3.9.1</version>
    </dependency>


	Java MongoDB Driver for Asynchronous:    

	<dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-async</artifactId>
        <version>...</version>
    </dependency>

    Java MongoDB Driver Legacy
    	Which combines syn and asyn
    	Should not be used

	<dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-legacy</artifactId>
        <version>...</version>
    </dependency>

    MongoDB Java Driver Base Classes:

    	MongoClient
    	MongoDatabase
    	MongoCollection
    	Document
    	Bson

    	class Document implements Map<String, Object>, Serializable, Bson {

    	}

    API:
    	https://static.javadoc.io/org.mongodb/mongodb-driver-sync/3.9.0/index.html?com/mongodb/client/MongoClient.html


## Import Dataset

	All the data required for MFlix is contained in the data/ directory in the handout. To import this data into Atlas, use the following command (with your Atlas URI string filled in):

		mongorestore --drop --gzip --uri <your-atlas-uri> data

	A few tips when running mongorestore:

		The --username and --password flags cannot be used with and SRV string. Instead, include the username and password in the SRV string (i.e. mongodb+srv://m220student:m220password@<your-cluster-address>)
		In order to work properly, this command must be run from the top-level of the mflix-<language>/ directory, where <language> is your chosen programming language.
	
	You can verify that the data was imported by connecting to Atlas:

		mongo <your-atlas-uri>


## MongoDB Compass

	https://www.mongodb.com/download-center/compass?jmp=university

	MongoDB Compass
	As the GUI for MongoDB, MongoDB Compass allows you to make smarter decisions about document structure, querying, indexing, document validation, and more. 

	https://docs.mongodb.com/compass/master/aggregation-pipeline-builder/

	https://docs.mongodb.com/compass/master/export-pipeline-to-language/


## Query Builders

	Filters
	Projections
	Sorts
	Aggregation
	Updates
	Indexes


## Java POJO 

	http://mongodb.github.io/mongo-java-driver/3.6/driver/getting-started/quick-start-pojo/
	http://mongodb.github.io/mongo-java-driver/3.2/bson/codecs/


## Aggregation Framework

	Dedicated Course:
	M121: The MongoDB Aggregation Framework
	https://university.mongodb.com/courses/M121/about

	This lesson uses unit test file src/test/java/mflix/lessons/UsingAggregationBuilders.java

	Operators:
		https://docs.mongodb.com/manual/reference/operator/aggregation/

	Stages:
		https://docs.mongodb.com/manual/reference/operator/aggregation-pipeline/

	$facet Stage:
		Check the example:
		https://docs.mongodb.com/manual/reference/operator/aggregation/facet/

	Method Example:

	MovieDao.getMoviesCastFaceted


## Basic Writes - Inserts

	Docs:
	http://mongodb.github.io/mongo-java-driver/3.9/driver/tutorials/perform-write-operations/

	BasicWrites.java in the mflix/src/test/java/mflix/lessons directory

## Write Concerns

	w: 1 			It is the default writeConcern - it ensures the write has been committed by at least 1 node

	w: majority 	Ensures writes are committed by a majority of nodes - slower but very durable

	w: 0 			Does not ensure that write was committed by any nodes - very fast but less durable

## Update Operators

	Docs:
	http://mongodb.github.io/mongo-java-driver/3.9/driver/tutorials/perform-write-operations/

	lesson UpdateOperators.java in the mflix/src/test/java/mflix/lessons directory

## Basic Joins
	
	From the movies collection joining with the comments collection:

	{
	  from: 'comments',
	  localField: '_id',
	  foreignField: 'movie_id',
	  as: 'movie_comments'
	}

	Java Method:

	public Document getMovie(String movieId) {
	    if (!validIdValue(movieId)) {
	      return null;
	    }

	    List<Bson> pipeline = new ArrayList<>();
	    // match stage to find movie
	    Bson match = Aggregates.match(Filters.eq("_id", new ObjectId(movieId)));
	    pipeline.add(match);
	    // TODO> Ticket: Get Comments - implement the lookup stage that allows the comments to retrieved with Movies.

	    Bson lookupFields = new Document("from", "comments");
	    ((Document) lookupFields).put("localField", "_id");
	    ((Document) lookupFields).put("foreignField", "movie_id");
	    ((Document) lookupFields).put("as", "comments");
	    Bson lookup = new Document("$lookup", lookupFields);
	    pipeline.add(lookup);

	    Document movie = moviesCollection.aggregate(pipeline).first();

	    return movie;
  	}

  	