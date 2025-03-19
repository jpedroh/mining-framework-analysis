package org.restheart.handlers.document;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.utils.HttpStatus;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.RequestHelper;
import org.restheart.utils.ResponseHelper;
import org.restheart.utils.URLUtils;
import io.undertow.server.HttpServerExchange;
import java.time.Instant;
import org.bson.types.ObjectId;
import org.restheart.db.Database;
import org.restheart.db.DbsDAO;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class GetDocumentHandler extends PipedHttpHandler {
  private final Database dbsDAO;

  /**
     * Default ctor
     */
  public GetDocumentHandler() {
    this(new DbsDAO());
  }

  /**
     * Creates a new instance of GetDocumentHandler
     */
  public GetDocumentHandler(Database dbsDAO) {
    super(null);
    this.dbsDAO = dbsDAO;
  }

  /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
  @Override public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
    BasicDBObject query = new BasicDBObject("_id", context.getDocumentId());
    ;
    DBObject document = dbsDAO.getCollection(context.getDBName(), context.getCollectionName()).findOne(query);
    if (document == null) {
      ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_FOUND, "document does not exist");
      return;
    }
    Object etag = document.get("_etag");
    if (etag != null && ObjectId.isValid("" + etag)) {
      ObjectId _etag = new ObjectId("" + etag);
      document.put("_lastupdated_on", Instant.ofEpochSecond(_etag.getTimestamp()).toString());
      if (RequestHelper.checkReadEtag(exchange, etag.toString())) {
        ResponseHelper.endExchange(exchange, HttpStatus.SC_NOT_MODIFIED);
        return;
      }
    }
    String requestPath = URLUtils.removeTrailingSlashes(exchange.getRequestPath());
    ResponseHelper.injectEtagHeader(exchange, document);
    exchange.setResponseCode(HttpStatus.SC_OK);
    DocumentRepresentationFactory.sendDocument(requestPath, exchange, context, document);
    exchange.endExchange();
  }
}