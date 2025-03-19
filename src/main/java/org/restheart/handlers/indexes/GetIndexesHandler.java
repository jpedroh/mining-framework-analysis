package org.restheart.handlers.indexes;
import com.mongodb.DBObject;
import org.restheart.db.IndexDAO;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.HttpStatus;
import io.undertow.server.HttpServerExchange;
import java.util.List;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class GetIndexesHandler extends PipedHttpHandler {
  /**
     * Creates a new instance of GetIndexesHandler
     */
  public GetIndexesHandler() {
    super(null);
  }

  /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
  @Override public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
    final IndexDAO indexDAO = new IndexDAO();
    List<DBObject> indexes = indexDAO.getCollectionIndexes(context.getDBName(), context.getCollectionName());
    exchange.setResponseCode(HttpStatus.SC_OK);
    IndexesRepresentationFactory.sendHal(exchange, context, indexes, indexes.size());
    exchange.endExchange();
  }
}