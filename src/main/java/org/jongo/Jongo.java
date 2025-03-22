package org.jongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.jongo.bson.BsonDBDecoder;
import org.jongo.bson.BsonDBEncoder;
import org.jongo.query.Query;
import static org.jongo.marshall.jackson.JacksonMapper.Builder.jacksonMapper;

public class Jongo {
  private final DB database;

  private final Mapper mapper;

  @Deprecated public Jongo(DB database) {
    this(database, jacksonMapper().build());
  }

  @Deprecated public Jongo(DB database, Mapper mapper) {
    this.database = database;
    this.mapper = mapper;
  }

  public MongoCollection getCollection(String name) {
    DBCollection dbCollection = database.getCollection(name);
    dbCollection.setDBDecoderFactory(BsonDBDecoder.FACTORY);
    dbCollection.setDBEncoderFactory(BsonDBEncoder.FACTORY);
    dbCollection.setReadConcern(database.getReadConcern());
    return new MongoCollection(dbCollection, mapper);
  }

  public DB getDatabase() {
    return database;
  }

  public Mapper getMapper() {
    return mapper;
  }

  public Query createQuery(String query, Object... parameters) {
    return mapper.getQueryFactory().createQuery(query, parameters);
  }

  public Command runCommand(String query) {
    return runCommand(query, new Object[0]);
  }

  public Command runCommand(String query, Object... parameters) {
    return new Command(database, mapper.getUnmarshaller(), mapper.getQueryFactory(), query, parameters);
  }

  public static JongoNative useNative() {
    return new JongoNative();
  }

  public static JongoNative useNative(Mapper mapper) {
    return new JongoNative(mapper);
  }
}