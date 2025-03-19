package org.restheart.db;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import io.undertow.server.HttpServerExchange;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.RequestHelper;
import org.restheart.utils.URLUtils;
import io.undertow.util.HttpString;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import static jdk.nashorn.internal.runtime.Debug.id;
import org.bson.types.ObjectId;
import org.restheart.utils.IllegalDocumentIdException;
import org.restheart.utils.URLUtils.DOC_ID_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class DocumentDAO implements Repository {
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDAO.class);

  public DocumentDAO() {
  }

  /**
     * @param dbName
     * @param collName
     * @param documentId
     * @param content
     * @param requestEtag
     * @param patching
     * @return the HttpStatus code
     */
  @Override public int upsertDocument(String dbName, String collName, Object documentId, DBObject content, ObjectId requestEtag, boolean patching) {
    DB db = client.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
    ObjectId timestamp = new ObjectId();
    Instant now = Instant.ofEpochSecond(timestamp.getTimestamp());
    if (content == null) {
      content = new BasicDBObject();
    }
    content.put("_etag", timestamp);
    BasicDBObject idQuery = new BasicDBObject("_id", documentId);
    if (patching) {
      content.removeField("_created_on");
      DBObject oldDocument = coll.findAndModify(idQuery, null, null, false, new BasicDBObject("$set", content), false, false);
      if (oldDocument == null) {
        return HttpStatus.SC_NOT_FOUND;
      } else {
        return optimisticCheckEtag(coll, oldDocument, requestEtag, HttpStatus.SC_OK);
      }
    } else {
      content.put("_created_on", now.toString());
      DBObject oldDocument = coll.findAndModify(idQuery, null, null, false, content, false, true);
      if (oldDocument != null) {
        Object oldTimestamp = oldDocument.get("_created_on");
        if (oldTimestamp == null) {
          oldTimestamp = now.toString();
          LOGGER.warn("properties of document /{}/{}/{} had no @created_on field. set it to current time", dbName, collName, documentId);
        }
        BasicDBObject created = new BasicDBObject("_created_on", "" + oldTimestamp);
        created.markAsPartialObject();
        coll.update(idQuery, new BasicDBObject("$set", created), true, false);
        return optimisticCheckEtag(coll, oldDocument, requestEtag, HttpStatus.SC_OK);
      } else {
        return HttpStatus.SC_CREATED;
      }
    }
  }

  /**
     * @param exchange
     * @param dbName
     * @param collName
     * @param docId
     * @param content
     * @param requestEtag
     * @return
     */
  @Override public int upsertDocumentPost(HttpServerExchange exchange, String dbName, String collName, Object docId, DBObject content, ObjectId requestEtag) {
    DB db = client.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
    ObjectId timestamp = new ObjectId();
    Instant now = Instant.ofEpochSecond(timestamp.getTimestamp());
    if (content == null) {
      content = new BasicDBObject();
    }
    content.put("_etag", timestamp);
    content.put("_created_on", now.toString());
    Object _idInContent = content.get("_id");
    content.removeField("_id");
    if (_idInContent == null) {
      content.put("_id", docId);
      coll.insert(content);
      exchange.getResponseHeaders().add(HttpString.tryFromString("Location"), getReferenceLink(exchange.getRequestURL(), docId.toString()).toString());
      return HttpStatus.SC_CREATED;
    } else {
      exchange.getResponseHeaders().add(HttpString.tryFromString("Location"), getReferenceLink(exchange.getRequestURL(), _idInContent.toString()).toString());
    }
    BasicDBObject idQuery = new BasicDBObject("_id", docId);
    DBObject oldDocument = coll.findAndModify(idQuery, null, null, false, content, false, true);
    if (oldDocument != null) {
      Object oldTimestamp = oldDocument.get("_created_on");
      if (oldTimestamp == null) {
        oldTimestamp = now.toString();
        LOGGER.warn("properties of document /{}/{}/{} had no @created_on field. set it to current time", dbName, collName, _idInContent.toString());
      }
      BasicDBObject createdContent = new BasicDBObject("_created_on", "" + oldTimestamp);
      createdContent.markAsPartialObject();
      coll.update(idQuery, new BasicDBObject("$set", createdContent), true, false);
      return optimisticCheckEtag(coll, oldDocument, requestEtag, HttpStatus.SC_OK);
    } else {
      return HttpStatus.SC_CREATED;
    }
  }

  /**
     * @param dbName
     * @param collName
     * @param documentId
     * @param requestEtag
     * @return
     */
  @Override public int deleteDocument(String dbName, String collName, Object documentId, ObjectId requestEtag) {
    DB db = client.getDB(dbName);
    DBCollection coll = db.getCollection(collName);
    BasicDBObject idQuery = new BasicDBObject("_id", documentId);
    DBObject oldDocument = coll.findAndModify(idQuery, null, null, true, null, false, false);
    if (oldDocument == null) {
      return HttpStatus.SC_NOT_FOUND;
    } else {
      return optimisticCheckEtag(coll, oldDocument, requestEtag, HttpStatus.SC_NO_CONTENT);
    }
  }

  private int optimisticCheckEtag(DBCollection coll, DBObject oldDocument, ObjectId requestEtag, int httpStatusIfOk) {
    if (requestEtag == null) {
      coll.save(oldDocument);
      return HttpStatus.SC_CONFLICT;
    }
    Object oldEtag = RequestHelper.getEtagAsObjectId(oldDocument.get("_etag"));
    if (oldEtag == null) {
      return HttpStatus.SC_NO_CONTENT;
    } else {
      if (oldEtag.equals(requestEtag)) {
        return httpStatusIfOk;
      } else {
        coll.save(oldDocument);
        return HttpStatus.SC_PRECONDITION_FAILED;
      }
    }
  }

  private URI getReferenceLink(String parentUrl, String referencedName) {
    try {
      return new URI(URLUtils.removeTrailingSlashes(parentUrl) + "/" + referencedName);
    } catch (URISyntaxException ex) {
      LOGGER.error("error creating URI from {} + / + {}", parentUrl, referencedName, ex);
    }
    return null;
  }
}