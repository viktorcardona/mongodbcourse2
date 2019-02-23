package mflix.api.daos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mongodb.client.model.Projections.include;

@Component
public class MovieDao extends AbstractMFlixDao {

  public static String MOVIES_COLLECTION = "movies";

  private MongoCollection<Document> moviesCollection;

  @Autowired
  public MovieDao(
      MongoClient mongoClient, @Value("${spring.mongodb.database}") String databaseName) {
    super(mongoClient, databaseName);
    moviesCollection = db.getCollection(MOVIES_COLLECTION);
  }

  @SuppressWarnings("unchecked")
  private Bson buildLookupStage() {
    return null;

  }

  /**
   * movieId needs to be a hexadecimal string value. Otherwise it won't be possible to translate to
   * an ObjectID
   *
   * @param movieId - Movie object identifier
   * @return true if valid movieId.
   */
  private boolean validIdValue(String movieId) {
    //TODO> Ticket: Handling Errors - implement a way to catch a
    //any potential exceptions thrown while validating a movie id.
    //Check out this method's use in the method that follows.
    return ObjectId.isValid(movieId);//TODO: I do on Feb 22 but it should be later
  }

  /**
   * Gets a movie object from the database.
   *
   * @param movieId - Movie identifier string.
   * @return Document object or null.
   */
  @SuppressWarnings("UnnecessaryLocalVariable")
  public Document getMovie_original(String movieId) {
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

    //https://stackoverflow.com/questions/47584665/mongodb-apply-sort-to-lookup-results?rq=1
    //https://stackoverflow.com/questions/22932364/mongodb-group-values-by-multiple-fields
    //https://discourse.university.mongodb.com/t/ticket-create-update-comments-and-delete-comments-did-someone-get-the-validation-code/10439/25

    Document movie = moviesCollection.aggregate(pipeline).first();

    return movie;
  }

  public Document getMovie_works_01(String movieId) {
    if (!validIdValue(movieId)) {
      return null;
    }

    List<Bson> pipeline = new ArrayList<>();
    // match stage to find movie
    Bson match = Aggregates.match(Filters.eq("_id", new ObjectId(movieId)));
    pipeline.add(match);
    // TODO> Ticket: Get Comments - implement the lookup stage that allows the comments to retrieved with Movies.

    Document movie = moviesCollection.aggregate(pipeline).first();

    if (Objects.isNull(movie)) {
      return null;
    }

    //TODO: it is not the way. This sort task should be done in the pipeline
    List<Document> commentDocs = db.getCollection("comments")
            .find(new Document("movie_id", new ObjectId(movieId)))
            .sort(Sorts.descending("date"))
            .into(new ArrayList<>());

    if (Objects.nonNull(commentDocs)) {
      movie.put("comments", commentDocs);
    }

    return movie;
  }

  public Document getMovie(String movieId) {
    if (!validIdValue(movieId)) {
      return null;
    }

    List<Bson> pipeline = new ArrayList<>();
    // match stage to find movie
    Bson match = Aggregates.match(Filters.eq("_id", new ObjectId(movieId)));
    pipeline.add(match);

    Bson lookupFields = new Document("from", "comments");
    ((Document) lookupFields).put("localField", "_id");
    ((Document) lookupFields).put("foreignField", "movie_id");
    ((Document) lookupFields).put("as", "comments");
    Bson lookup = new Document("$lookup", lookupFields);
    pipeline.add(lookup);

    Document movie = moviesCollection.aggregate(pipeline).first();

    if (Objects.isNull(movie)) {
      return null;
    }

    //TODO: it is not the way. This sort task should be done in the pipeline
    List<Document> commentDocs = (List<Document>) movie.get("comments");
    if (Objects.nonNull(commentDocs)) {
      commentDocs.sort((doc1, doc2) -> compareByDate(doc1, doc2));
    }

    return movie;
  }

  private int compareByDate(Document doc1, Document doc2) {
    Date date1 = (Date) doc1.get("date");
    Date date2 = (Date) doc2.get("date");
    return date2.compareTo(date1);//descending
  }

  /**
   * Returns all movies within the defined limit and skip values using a default descending sort key
   * `tomatoes.viewer.numReviews`
   *
   * @param limit - max number of returned documents.
   * @param skip - number of documents to be skipped.
   * @return list of documents.
   */
  @SuppressWarnings("UnnecessaryLocalVariable")
  public List<Document> getMovies(int limit, int skip) {
    String defaultSortKey = "tomatoes.viewer.numReviews";
    List<Document> movies =
        new ArrayList<>(getMovies(limit, skip, Sorts.descending(defaultSortKey)));
    return movies;
  }

  /**
   * Finds a limited amount of movies documents, for a given sort order.
   *
   * @param limit - max number of documents to be returned.
   * @param skip - number of documents to be skipped.
   * @param sort - result sorting criteria.
   * @return list of documents that sorted by the defined sort criteria.
   */
  public List<Document> getMovies(int limit, int skip, Bson sort) {

    List<Document> movies = new ArrayList<>();

    moviesCollection
        .find()
        .limit(limit)
        .skip(skip)
        .sort(sort)
        .iterator()
        .forEachRemaining(movies::add);

    return movies;
  }

  /**
   * For a given a country, return all the movies that match that country.
   *
   * @param country - Country string value to be matched.
   * @return List of matching Document objects.
   */
  public List<Document> getMoviesByCountry(String... country) {

    Bson queryFilter = Filters.in("countries", country);
    Bson projection = Projections.fields(Projections.include("title"));
    //TODO> Ticket: Projection - implement the query and projection required by the unit test
    List<Document> movies = new ArrayList<>();

    moviesCollection.find(queryFilter)
            .projection(projection)
            .into(movies);

    return movies;
  }

  /**
   * This method will execute the following mongo shell query: db.movies.find({"$text": { "$search":
   * `keywords` }}, {"score": {"$meta": "textScore"}}).sort({"score": {"$meta": "textScore"}})
   *
   * @param limit - integer value of number of documents to be limited to.
   * @param skip - number of documents to be skipped.
   * @param keywords - text matching keywords or terms
   * @return List of query matching Document objects
   */
  public List<Document> getMoviesByText(int limit, int skip, String keywords) {
    Bson textFilter = Filters.text(keywords);
    Bson projection = Projections.metaTextScore("score");
    Bson sort = Sorts.metaTextScore("score");
    List<Document> movies = new ArrayList<>();
    moviesCollection
        .find(textFilter)
        .projection(projection)
        .sort(sort)
        .skip(skip)
        .limit(limit)
        .iterator()
        .forEachRemaining(movies::add);
    return movies;
  }

  /**
   * Finds all movies that contain any of the `casts` members, sorted in descending by the `sortKey`
   * field.
   *
   * @param sortKey - sort key.
   * @param limit - number of documents to be returned.
   * @param skip - number of documents to be skipped.
   * @param cast - cast selector.
   * @return List of documents sorted by sortKey that match the cast selector.
   */
  public List<Document> getMoviesByCast(String sortKey, int limit, int skip, String... cast) {
    Bson castFilter = Filters.in("cast", cast);
    Bson sort = Sorts.descending(sortKey);
    //TODO> Ticket: Subfield Text Search - implement the expected cast
    // filter and sort
    List<Document> movies = new ArrayList<>();
    moviesCollection
        .find(castFilter)
        .sort(sort)
        .limit(limit)
        .skip(skip)
        .iterator()
        .forEachRemaining(movies::add);
    return movies;
  }

  /**
   * Finds all movies that match the provide `genres`, sorted descending by the `sortKey` field.
   *
   * @param sortKey - sorting key string.
   * @param limit - number of documents to be returned.
   * @param skip - number of documents to be skipped
   * @param genres - genres matching string vargs.
   * @return List of matching Document objects.
   */
  public List<Document> getMoviesByGenre(String sortKey, int limit, int skip, String... genres) {
    // query filter
    Bson castFilter = Filters.in("genres", genres);
    // sort key
    Bson sort = Sorts.descending(sortKey);
    List<Document> movies = new ArrayList<>();
    // TODO > Ticket: Paging - implement the necessary cursor methods to support simple
    // pagination like skip and limit in the code below
    moviesCollection.find(castFilter)
            .sort(sort)
            .skip(skip)
            .limit(limit)
            .iterator()
            .forEachRemaining(movies::add);
    return movies;
  }

  private ArrayList<Integer> runtimeBoundaries() {
    ArrayList<Integer> runtimeBoundaries = new ArrayList<>();
    runtimeBoundaries.add(0);
    runtimeBoundaries.add(60);
    runtimeBoundaries.add(90);
    runtimeBoundaries.add(120);
    runtimeBoundaries.add(180);
    return runtimeBoundaries;
  }

  private ArrayList<Integer> ratingBoundaries() {
    ArrayList<Integer> ratingBoundaries = new ArrayList<>();
    ratingBoundaries.add(0);
    ratingBoundaries.add(50);
    ratingBoundaries.add(70);
    ratingBoundaries.add(90);
    ratingBoundaries.add(100);
    return ratingBoundaries;
  }

  /**
   * This method is the java implementation of the following mongo shell aggregation pipeline {
   * "$bucket": { "groupBy": "$runtime", "boundaries": [0, 60, 90, 120, 180], "default": "other",
   * "output": { "count": {"$sum": 1} } } }
   */
  private Bson buildRuntimeBucketStage() {

    BucketOptions bucketOptions = new BucketOptions();
    bucketOptions.defaultBucket("other");
    BsonField count = new BsonField("count", new Document("$sum", 1));
    bucketOptions.output(count);
    return Aggregates.bucket("$runtime", runtimeBoundaries(), bucketOptions);
  }

  /*
  This method is the java implementation of the following mongo shell aggregation pipeline
  {
   "$bucket": {
     "groupBy": "$metacritic",
     "boundaries": [0, 50, 70, 90, 100],
     "default": "other",
     "output": {
     "count": {"$sum": 1}
     }
    }
   }
   */
  private Bson buildRatingBucketStage() {
    BucketOptions bucketOptions = new BucketOptions();
    bucketOptions.defaultBucket("other");
    BsonField count = new BsonField("count", new Document("$sum", 1));
    bucketOptions.output(count);
    return Aggregates.bucket("$metacritic", ratingBoundaries(), bucketOptions);
  }

  /**
   * This method is the java implementation of the following mongo shell aggregation pipeline
   * pipeline.aggregate([ {$match: {cast: {$in: ... }}}, {$sort: {tomatoes.viewer.numReviews: -1}},
   * {$skip: ... }, {$limit: ... }, {$facet:{ runtime: {$bucket: ...}, rating: {$bucket: ...},
   * movies: {$addFields: ...}, }} ])
   */
  public List<Document> getMoviesCastFaceted(int limit, int skip, String... cast) {
    List<Document> movies = new ArrayList<>();
    String sortKey = "tomatoes.viewer.numReviews";
    Bson skipStage = Aggregates.skip(skip);
    Bson matchStage = Aggregates.match(Filters.in("cast", cast));
    Bson sortStage = Aggregates.sort(Sorts.descending(sortKey));
    Bson limitStage = Aggregates.limit(limit);
    Bson facetStage = buildFacetStage();
    // Using a LinkedList to ensure insertion order
    List<Bson> pipeline = new LinkedList<>();

    // TODO > Ticket: Faceted Search - build the aggregation pipeline by adding all stages in the
    // correct order
    // Your job is to order the stages correctly in the pipeline.
    // Starting with the `matchStage` add the remaining stages.
    pipeline.add(matchStage);
    pipeline.add(sortStage);//added
    pipeline.add(skipStage);//added
    pipeline.add(limitStage);//added
    pipeline.add(facetStage);//added

    moviesCollection.aggregate(pipeline).iterator().forEachRemaining(movies::add);
    return movies;
  }

  /**
   * This method is the java implementation of the following mongo shell aggregation pipeline
   * pipeline.aggregate([ ..., {$facet:{ runtime: {$bucket: ...}, rating: {$bucket: ...}, movies:
   * {$addFields: ...}, }} ])
   *
   * @return Bson defining the $facet stage.
   */
  private Bson buildFacetStage() {

    return Aggregates.facet(
        new Facet("runtime", buildRuntimeBucketStage()),
        new Facet("rating", buildRatingBucketStage()),
        new Facet("movies", Aggregates.addFields(new Field("title", "$title"))));
  }

  /**
   * Counts the total amount of documents in the `movies` collection
   *
   * @return number of documents in the movies collection.
   */
  public long getMoviesCount() {
    return this.moviesCollection.countDocuments();
  }

  /**
   * Counts the number of documents matched by this text query
   *
   * @param keywords - set of keywords that match the query
   * @return number of matching documents.
   */
  public long getTextSearchCount(String keywords) {
    return this.moviesCollection.countDocuments(Filters.text(keywords));
  }

  /**
   * Counts the number of documents matched by this cast elements
   *
   * @param cast - cast string vargs.
   * @return number of matching documents.
   */
  public long getCastSearchCount(String... cast) {
    return this.moviesCollection.countDocuments(Filters.in("cast", cast));
  }

  /**
   * Counts the number of documents match genres filter.
   *
   * @param genres - genres string vargs.
   * @return number of matching documents.
   */
  public long getGenresSearchCount(String... genres) {
    return this.moviesCollection.countDocuments(Filters.in("genres", genres));
  }
}
