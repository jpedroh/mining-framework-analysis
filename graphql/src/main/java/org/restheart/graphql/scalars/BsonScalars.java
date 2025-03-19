package org.restheart.graphql.scalars;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.restheart.graphql.scalars.bsonCoercing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;

public class BsonScalars {
  public static final GraphQLScalarType GraphQLBsonObjectId = GraphQLScalarType.newScalar().name("ObjectId").description("BSON ObjectId scalar").coercing(new GraphQLBsonObjectIdCoercing()).build();

  public static final GraphQLScalarType GraphQLBsonDecimal128 = GraphQLScalarType.newScalar().name("Decimal128").description("BSON Decimal128 scalar").coercing(new GraphQLBsonDecimal128Coercing()).build();

  public static final GraphQLScalarType GraphQLBsonTimestamp = GraphQLScalarType.newScalar().name("Timestamp").description("BSON Timestamp scalar").coercing(new GraphQLBsonTimestampCoercing()).build();

  public static final GraphQLScalarType GraphQLBsonDocument = GraphQLScalarType.newScalar().name("BsonDocument").description("BSON Document scalar").coercing(new GraphQLBsonDocumentCoercing()).build();

  public static final GraphQLScalarType 
<<<<<<< /usr/src/app/output/softinstigate/restheart/b196d4fc15bb19a3a3c21b8a39559ad4405acca3/graphql/src/main/java/org/restheart/graphql/scalars/BsonScalars.java/left.java
  GraphQLBsonArray = GraphQLScalarType.newScalar().name("BsonArray").description("BSON Array scalar").coercing(new GraphQLBsonArrayCoercing()).build()
=======
  GraphQLBsonInt64 = GraphQLScalarType.newScalar().name("Long").description("BSON Int64 scalar (Long)").coercing(new GraphQLBsonInt64Coercing()).build()
>>>>>>> /usr/src/app/output/softinstigate/restheart/b196d4fc15bb19a3a3c21b8a39559ad4405acca3/graphql/src/main/java/org/restheart/graphql/scalars/BsonScalars.java/right.java
  ;

  public static final GraphQLScalarType GraphQLBsonDate = GraphQLScalarType.newScalar().name("DateTime").description("BSON DateTime scalar").coercing(new GraphQLBsonDateCoercing()).build();

  public static final GraphQLScalarType GraphQLBsonRegularExpression = GraphQLScalarType.newScalar().name("Regex").description("Bson regular expression scalar").coercing(new GraphQLBsonRegexCoercing()).build();

  public static final Set<GraphQLScalarType> BSON_SCALARS = Sets.newHashSet(GraphQLBsonDocument, GraphQLBsonObjectId, GraphQLBsonDecimal128, GraphQLBsonTimestamp, GraphQLBsonDate, GraphQLBsonArray, GraphQLBsonRegularExpression, GraphQLBsonInt64);

  public static Map<String, GraphQLScalarType> getBsonScalars() {
    var bsonScalars = new HashMap<String, GraphQLScalarType>();
    for (var scalar : BSON_SCALARS) {
      bsonScalars.put(scalar.getName(), scalar);
    }
    return bsonScalars;
  }

  public static String getBsonScalarHeader() {
    var header = "";
    for (var scalar : BSON_SCALARS) {
      header += "scalar " + scalar.getName() + " ";
    }
    return header;
  }
}