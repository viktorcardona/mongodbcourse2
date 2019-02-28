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


$match

  	{
  '_id': ObjectId('573a1390f29313caabcd413e')
}

$lookup

{
  from: 'comments',
  localField: '_id',
  foreignField: 'movie_id',
  as: 'comments'
}


$unwind

{
  path: '$comments'
}


$sort

{
  'comments.date': -1
}

$group

{
  "_id": '$_id',
  "title": {$first: "$title"},
  "year": {$first: "$year"},
  "runtime": {$first: "$runtime"},
  "cast": {$first: "$cast"},
  "plot": {$first: "$plot"},
  "fullplot": {$first: "$fullplot"},
  "lastupdated": {$first: "$lastupdated"},
  "type": {$first: "$type"},
  "poster": {$first: "$poster"},
  "directors": {$first: "$directors"},
  "writers": {$first: "$writers"},
  "imdb": {$first: "$imdb"},
  "countries": {$first: "$countries"},
  "genres": {$first: "$genres"},
  "tomatoes": {$first: "$tomatoes"},
  "num_mflix_comments": {$first: "$num_mflix_comments"},
  "comments": {
    "$push" : {
      "_id": "$comments._id",
      "name": "$comments.name",
      "email": "$comments.email",
      "movie_id": "$comments.movie_id",
      "text": "$comments.text",
      "date": "$comments.date"
    }
  }
}



https://github.com/cult-of-coders/grapher/issues/188

{
  from: 'comments',
  as: 'comments',
  let: { comment_movie_id: "$movie_id" },
  pipeline: [
        {
            $project: { _id: 1}
        },
        {
            $match: {
               $expr: {
                   $eq: ["$_id", "$$comment_movie_id"]
               }
            }
        }
  ]
}


## Basic Deletes

	BasicDeletes.java

## Admin Backend

	Review:
  /**
   * Ticket: User Report - produce a list of users that comment the most in the website. Query the
   * `comments` collection and group the users by number of comments. The list is limited to up most
   * 20 commenter.
   *
   * @return List {@link Critic} objects.
   */
  public List<Critic> mostActiveCommenters() {
    List<Critic> mostActive = new ArrayList<>();
    // // TODO> Ticket: User Report - execute a command that returns the
    // // list of 20 users, group by number of comments. Don't forget,
    // // this report is expected to be produced with an high durability
    // // guarantee for the returned documents. Once a commenter is in the
    // // top 20 of users, they become a Critic, so mostActive is composed of
    // // Critic objects.
    /*
      Query Implemented with MongoDB Compass:
      Arrays.asList(group("$email", sum("numComments", 1L)), sort(descending("numComments")), limit(3L))

      $group
      {
        _id: "$email",
        numComments: {
          $sum:1
        }
      }
      $sort
      {
        numComments: -1
      }
      $limit: 3
    */
    List<Bson> pipeline = new ArrayList<>();
    pipeline.add(Aggregates.group("$email", Accumulators.sum("count", 1)));
    pipeline.add(Aggregates.sort(Sorts.descending("count")));
    pipeline.add(Aggregates.limit(20));

    db.getCollection(COMMENT_COLLECTION, Critic.class)
            .withWriteConcern(WriteConcern.MAJORITY)//this report is expected to be produced with an high durability guarantee for the returned documents
            .withCodecRegistry(pojoCodecRegistry)
            .aggregate(pipeline)
            .into(mostActive);

    return mostActive;
  }


## Bulk Writes

	http://mongodb.github.io/mongo-java-driver/3.9/driver/tutorials/bulk-writes/
	

## Resilience & Robustness


## Resilience & Robustness: Connection Pooling
    
    Default size is 100 connections in a MongoDB's pool

    http://mongodb.github.io/mongo-java-driver/3.6/javadoc/com/mongodb/ConnectionString.html

    mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database.collection][?options]]


    http://mongodb.github.io/mongo-java-driver/3.9/driver/tutorials/connect-to-mongodb/

    connectTimeoutMS=ms: How long a connection can take to be opened before timing out.
    connectTimeoutMS=2000

    Modify the connection information for MongoClient to set a write concern timeout of 2500 milliseconds:

      ConnectionString connString = new ConnectionString(connectionString);
      MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .writeConcern(WriteConcern.MAJORITY.withWTimeout(2500, TimeUnit.MILLISECONDS))
            .build();
      MongoClient mongoClient = MongoClients.create(settings);

    Aside from the write concern timeout, you are also tasked to set the connectTimeoutMS configuration option to 2000 milliseconds:

      spring.mongodb.uri=mongodb+srv://myUser:myPassword@mflix-cxjdm.mongodb.net/test?retryWrites=true&maxPoolSize=50&connectTimeoutMS=2000



## Resilience & Robustness: Robust Client Configuration
  
    * Always use connection pooling
    * Always specify a wtimeout with majority writes
      - The primary reason to use a wtimeout is because by default, when 
        using Write Concern more durable than w: 1, there is no wtimeout, 
        so the server will wait indefinitely for operations to complete. 
      - Our application can use wtimeout to put a time limit on how long the server waits before a Write Concern is satisfied
    * Always handle serverSelectionTimeout errors. Those errors are cause by malfunctioning hardware or software.



## Resilience & Robustness: Error Handling



## Resilience & Robustness: Principle of Least Privilege:
  
  Add a new user on your Atlas cluster for the MFlix.


## Resilience & Robustness: Change Streams
  
  Change Streams: They can be used to log changes to a MongoDB collection.
  As of MongoDB 4.0, Change Streams can also be used to log changes at a database, and even a cluster level.

  https://docs.mongodb.com/manual/changeStreams/index.html
  ChangeStreams.java in the mflix/src/test/java/mflix/lessons
  https://mongodb.github.io/mongo-java-driver/3.9/javadoc/com/mongodb/client/model/changestream/ChangeStreamDocument.html


