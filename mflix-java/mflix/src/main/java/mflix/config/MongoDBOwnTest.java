package mflix.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.connection.SslSettings;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoDBOwnTest {

    public static void main_(String[] args) {
        String URI = "mongodb+srv://mflixAppUser:mflixAppPwd@mflix-cxjdm.mongodb.net/test?retryWrites=true&maxPoolSize=50&connectTimeoutMS=2000";

        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(URI)).build();
        MongoClient mongoClient = MongoClients.create(settings);
        System.out.printf("#docs: " + mongoClient.getDatabase("mflix").getCollection("movies").count());

        SslSettings sslSettings = settings.getSslSettings();
        ReadPreference readPreference = settings.getReadPreference();
        ReadConcern readConcern = settings.getReadConcern();
        WriteConcern writeConcern = settings.getWriteConcern();

        System.out.println();
        System.out.println("writeConcern: " + writeConcern.asDocument().toString());
        System.out.println("readPreference: " + readPreference.toString());
        System.out.println("readConcern: " + readConcern.asDocument().toString());
        System.out.println("sslSettings.isInvalidHostNameAllowed: " + sslSettings.isInvalidHostNameAllowed());
        System.out.println("sslSettings.isEnabled: " + sslSettings.isEnabled());

    }

    public static void main_Q5(String[] args) {
        String URI = "mongodb+srv://mflixAppUser:mflixAppPwd@mflix-cxjdm.mongodb.net/test?retryWrites=true&maxPoolSize=50&connectTimeoutMS=2000";
        String DATABASE = "mflix";

        MongoClient mongoClient = MongoClients.create(URI);
        MongoDatabase db = mongoClient.getDatabase(DATABASE);
        MongoCollection employeesCollection = db.getCollection("employees");

        Document doc1 = new Document("_id", 11)//TODO: Inserted
                .append("name", "Edgar Martinez")
                .append("salary", "8.5M");
        Document doc2 = new Document("_id", 3)//TODO: Inserted
                .append("name", "Alex Rodriguez")
                .append("salary", "18.3M");
        Document doc3 = new Document("_id", 24)//TODO: Inserted
                .append("name", "Ken Griffey Jr.")
                .append("salary", "12.4M");
        Document doc4 = new Document("_id", 11)
                .append("name", "David Bell")
                .append("salary", "2.5M");
        Document doc5 = new Document("_id", 19)
                .append("name", "Jay Buhner")
                .append("salary", "5.1M");

        List<WriteModel> requests = Arrays.asList(
                new InsertOneModel<>(doc1),
                new InsertOneModel<>(doc2),
                new InsertOneModel<>(doc3),
                new InsertOneModel<>(doc4),
                new InsertOneModel<>(doc5));
        try {
            System.out.println();
            System.out.println("bulkWrite!");
            employeesCollection.bulkWrite(requests);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
    }


    public static void main(String[] args) {
        String URI = "mongodb+srv://mflixAppUser:mflixAppPwd@mflix-cxjdm.mongodb.net/test?retryWrites=true&maxPoolSize=50&connectTimeoutMS=2000";
        String DATABASE = "mflix";

        MongoClient mongoClient = MongoClients.create(URI);
        MongoDatabase db = mongoClient.getDatabase(DATABASE);
        MongoCollection employeesCollection = db.getCollection("employees");

        try {
            System.out.println();
            System.out.println("# of employees: " + employeesCollection.countDocuments());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

}
