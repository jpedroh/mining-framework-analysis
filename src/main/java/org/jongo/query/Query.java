package org.jongo.query;
import com.mongodb.DBObject;
import org.bson.conversions.Bson;

public interface Query {
  DBObject toDBObject();

  Bson toBson();
}