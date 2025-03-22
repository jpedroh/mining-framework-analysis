package org.jongo.query;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONCallback;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BsonDocumentWrapper;
import org.jongo.bson.Bson;
import org.jongo.bson.BsonDocument;
import org.jongo.marshall.Marshaller;
import org.jongo.marshall.MarshallingException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BsonQueryFactory implements QueryFactory {
  private static final String DEFAULT_TOKEN = "#";

  private static final String MARSHALL_OPERATOR = "$marshall";

  private final String token;

  private final Marshaller marshaller;

  private static class BsonQuery implements Query {
    private final DBObject dbo;

    public BsonQuery(DBObject dbo) {
      this.dbo = dbo;
    }

    public DBObject toDBObject() {
      return dbo;
    }

    public org.bson.conversions.Bson toBson() {
      return BsonDocumentWrapper.asBsonDocument(dbo, MongoClient.getDefaultCodecRegistry());
    }
  }

  public BsonQueryFactory(Marshaller marshaller) {
    this(marshaller, DEFAULT_TOKEN);
  }

  public BsonQueryFactory(Marshaller marshaller, String token) {
    this.token = token;
    this.marshaller = marshaller;
  }

  public Query createQuery(final String query, Object... parameters) {
    if (query == null) {
      return new BsonQuery((DBObject) JSON.parse(query));
    }
    if (parameters == null) {
      parameters = new Object[] { null };
    }
    StringBuilder sb = new StringBuilder();
    int paramIncrement = 0;
    int paramPos = 0;
    int start = 0;
    int pos;
    while ((pos = query.indexOf(token, start)) != -1) {
      if (paramPos >= parameters.length) {
        throw new IllegalArgumentException("Not enough parameters passed to query: " + query);
      }
      sb.append(query, start, pos);
      if (isValueToken(query, pos)) {
        sb.append("{\"").append(MARSHALL_OPERATOR).append("\":").append(paramIncrement).append("}");
        paramIncrement = 0;
      } else {
        sb.append(parameters[paramPos]);
        paramIncrement++;
      }
      paramPos++;
      start = pos + token.length();
    }
    sb.append(query, start, query.length());
    if (paramPos < parameters.length) {
      throw new IllegalArgumentException("Too many parameters passed to query: " + query);
    }
    final Object[] params = parameters;
    DBObject dbo;
    try {
      dbo = (DBObject) JSON.parse(sb.toString(), new JSONCallback() {
        int paramPos = 0;

        @Override public Object objectDone() {
          String name = curName();
          Object o = super.objectDone();
          if (o instanceof BSONObject && !(o instanceof List<?>)) {
            BSONObject dbo = (BSONObject) o;
            Object marshallValue = dbo.get(MARSHALL_OPERATOR);
            if (marshallValue != null) {
              paramPos += ((Number) marshallValue).intValue();
              if (paramPos >= params.length) {
                throw new IllegalArgumentException("Not enough parameters passed to query: " + query);
              }
              o = marshallParameter(params[paramPos++]);
              if (!isStackEmpty()) {
                _put(name, o);
              } else {
                o = !BSON.hasDecodeHooks() ? o : BSON.applyDecodingHooks(o);
                setRoot(o);
              }
            }
          }
          if (isStackEmpty()) {
          }
          return o;
        }
      });
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot parse query: " + query, e);
    }
    return new BsonQuery(dbo);
  }

  private boolean isValueToken(String query, int tokenIndex) {
    for (int pos = tokenIndex; pos >= 0; pos--) {
      char c = query.charAt(pos);
      if (c == ':') {
        return true;
      } else {
        if (c == '{' || c == '.') {
          return false;
        } else {
          if (c == ',') {
            return !isPropertyName(query, pos - 1);
          }
        }
      }
    }
    return true;
  }

  private boolean isPropertyName(String query, int tokenIndex) {
    for (int pos = tokenIndex; pos >= 0; pos--) {
      char c = query.charAt(pos);
      if (c == '[') {
        return false;
      } else {
        if (c == '{') {
          return true;
        }
      }
    }
    return false;
  }

  private Object marshallParameter(Object parameter) {
    try {
      if (parameter == null || Bson.isPrimitive(parameter)) {
        return parameter;
      }
      if (parameter instanceof Collection) {
        return marshallCollection((Collection<?>) parameter);
      }
      if (parameter instanceof Object[]) {
        return marshallArray((Object[]) parameter);
      }
      return marshallDocument(parameter);
    } catch (Exception e) {
      String message = String.format("Unable to marshall parameter: %s", parameter);
      throw new MarshallingException(message, e);
    }
  }

  private DBObject marshallArray(Object[] parameters) {
    BasicDBList list = new BasicDBList();
    for (final Object parameter : parameters) {
      list.add(marshallParameter(parameter));
    }
    return list;
  }

  private DBObject marshallCollection(Collection<?> parameters) {
    BasicDBList list = new BasicDBList();
    for (Object param : parameters) {
      list.add(marshallParameter(param));
    }
    return list;
  }

  private Object marshallDocument(Object parameter) {
    if (parameter instanceof Enum) {
      return marshallParameterAsPrimitive(parameter);
    } else {
      BsonDocument document = marshaller.marshall(parameter);
      if (hasBeenSerializedAsPrimitive(document)) {
        return marshallParameterAsPrimitive(parameter);
      } else {
        return document.toDBObject();
      }
    }
  }

  private boolean hasBeenSerializedAsPrimitive(BsonDocument document) {
    byte[] bytes = document.toByteArray();
    if (bytes.length > 4) {
      return bytes.length != document.getSize();
    }
    return true;
  }

  /**
     * The object may have been serialized to a primitive type with a
     * custom serializer, so try again after wrapping as an object property.
     * We do this trick only as a falllback since it causes Jackson to consider the parameter
     * as "Object" and thus ignore any annotations that may exist on its actual class.
     */
  private Object marshallParameterAsPrimitive(Object parameter) {
    Map<String, Object> primitiveWrapper = Collections.singletonMap("wrapped", parameter);
    BsonDocument document = marshaller.marshall(primitiveWrapper);
    return document.toDBObject().get("wrapped");
  }
}