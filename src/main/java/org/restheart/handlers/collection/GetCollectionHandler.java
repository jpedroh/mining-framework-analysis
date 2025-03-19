package org.restheart.handlers.collection;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSONParseException;
import org.restheart.utils.HttpStatus;
import org.restheart.handlers.IllegalQueryParamenterException;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.ResponseHelper;
import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import org.restheart.db.Database;
import org.restheart.db.DbsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class GetCollectionHandler extends PipedHttpHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCollectionHandler.class);

  private final Database dbsDAO;

  /**
     * Creates a new instance of GetCollectionHandler
     */
  public GetCollectionHandler() {
    this(new DbsDAO());
  }

  /**
     * Creates a new instance of GetCollectionHandler
     */
  public GetCollectionHandler(Database dbsDAO) {
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
    DBCollection coll = this.dbsDAO.getCollection(context.getDBName(), context.getCollectionName());
    long size = -1;
    if (context.isCount()) {
      size = this.dbsDAO.getCollectionSize(coll, exchange.getQueryParameters().get("filter"));
    }
    ArrayList<DBObject> data = null;
    try {
      data = this.
<<<<<<< /usr/src/app/output/softinstigate/restheart/0747dae87d95db10cd2f8c43757649542e6c6d62/src/main/java/org/restheart/handlers/collection/GetCollectionHandler.java/left.java
      getCollectionData(coll, context.getPage(), context.getPagesize(), context.getSortBy(), context.getFilter(), context.getCursorAllocationPolicy(), context.isDetectObjectIds())
=======
      dbsDAO.getCollectionData(coll, context.getPage(), context.getPagesize(), context.getSortBy(), context.getFilter(), context.getCursorAllocationPolicy())
>>>>>>> /usr/src/app/output/softinstigate/restheart/0747dae87d95db10cd2f8c43757649542e6c6d62/src/main/java/org/restheart/handlers/collection/GetCollectionHandler.java/right.java
      ;
    } catch (JSONParseException jpe) {
      LOGGER.error("invalid filter expression {}", context.getFilter(), jpe);
      ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_BAD_REQUEST, "wrong request, filter expression is invalid", jpe);
      return;
    } catch (MongoException me) {
      if (me.getMessage().matches(".*Can\'t canonicalize query.*")) {
        LOGGER.error("invalid filter expression {}", context.getFilter(), me);
        ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_BAD_REQUEST, "wrong request, filter expression is invalid", me);
        return;
      } else {
        throw me;
      }
    }
    if (exchange.isComplete()) {
      return;
    }
    if (data.isEmpty() && (context.getCollectionProps() == null || context.getCollectionProps().keySet().isEmpty())) {
      ResponseHelper.endExchange(exchange, HttpStatus.SC_NOT_FOUND);
      return;
    }
    try {
      exchange.setResponseCode(HttpStatus.SC_OK);
      new CollectionRepresentationFactory().sendHal(exchange, context, data, size);
      exchange.endExchange();
    } catch (IllegalQueryParamenterException ex) {
      ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_BAD_REQUEST, ex.getMessage(), ex);
    }
  }
}