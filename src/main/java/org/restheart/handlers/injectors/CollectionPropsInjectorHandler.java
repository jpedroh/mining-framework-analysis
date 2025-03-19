package org.restheart.handlers.injectors;
import com.mongodb.DBObject;
import org.restheart.db.CollectionDAO;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.ResponseHelper;
import io.undertow.server.HttpServerExchange;

/**
 * this handler injects the collection properties in the RequestContext this
 * handler is also responsible of sending NOT_FOUND in case of requests
 * involving not existing collections (that are not PUT)
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class CollectionPropsInjectorHandler extends PipedHttpHandler {
  /**
     * Creates a new instance of MetadataInjecterHandler
     *
     * @param next
     */
  public CollectionPropsInjectorHandler(PipedHttpHandler next) {
    super(next);
  }

  /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
  @Override public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
    if (context.getDBName() != null && context.getCollectionName() != null) {
      DBObject collProps;
      if (!LocalCachesSingleton.isEnabled()) {
        final CollectionDAO collectionDAO = new CollectionDAO();
        collProps = collectionDAO.getCollectionProps(context.getDBName(), context.getCollectionName());
        if (collProps != null) {
          collProps.put("_collection-props-cached", false);
        } else {
          if (!(context.getType() == RequestContext.TYPE.COLLECTION && context.getMethod() == RequestContext.METHOD.PUT) && context.getType() != RequestContext.TYPE.ROOT && context.getType() != RequestContext.TYPE.DB) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_FOUND, "collection does not exist");
            return;
          }
        }
      } else {
        collProps = LocalCachesSingleton.getInstance().getCollectionProps(context.getDBName(), context.getCollectionName());
      }
      if (collProps == null && !(context.getType() == RequestContext.TYPE.COLLECTION && context.getMethod() == RequestContext.METHOD.PUT) && context.getType() != RequestContext.TYPE.ROOT && context.getType() != RequestContext.TYPE.DB) {
        ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_FOUND, "collection does not exist");
        return;
      }
      context.setCollectionProps(collProps);
    }
    next.handleRequest(exchange, context);
  }
}