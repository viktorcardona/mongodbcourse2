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

