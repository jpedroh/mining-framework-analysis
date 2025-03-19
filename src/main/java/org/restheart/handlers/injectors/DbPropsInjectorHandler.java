package org.restheart.handlers.injectors;
import com.mongodb.DBObject;
import org.restheart.db.DbsDAO;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.ResponseHelper;
import io.undertow.server.HttpServerExchange;

/**
 *
 * this handler injects the db properties in the RequestContext this handler is
 * also responsible of sending NOT_FOUND in case of requests involving not
 * existing dbs (that are not PUT)
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class DbPropsInjectorHandler extends PipedHttpHandler {
  /**
     * Creates a new instance of MetadataInjecterHandler
     *
     * @param next
     */
  public DbPropsInjectorHandler(PipedHttpHandler next) {
    super(next);
  }

  /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
  @Override public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
    if (context.getDBName() != null) {
      DBObject dbProps = null;
      if (!LocalCachesSingleton.isEnabled()) {
        final DbsDAO dbsDAO = new DbsDAO();
        dbProps = dbsDAO.getDbProps(context.getDBName());
        if (dbProps != null) {
          dbProps.put("_db-props-cached", false);
        } else {
          if (!(context.getType() == RequestContext.TYPE.DB && context.getMethod() == RequestContext.METHOD.PUT) && context.getType() != RequestContext.TYPE.ROOT) {
            ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_FOUND, "db does not exist");
            return;
          }
        }
      } else {
        dbProps = LocalCachesSingleton.getInstance().getDBProps(context.getDBName());
      }
      if (dbProps == null && !(context.getType() == RequestContext.TYPE.DB && context.getMethod() == RequestContext.METHOD.PUT) && context.getType() != RequestContext.TYPE.ROOT) {
        ResponseHelper.endExchangeWithMessage(exchange, HttpStatus.SC_NOT_FOUND, "db does not exis");
        return;
      }
      context.setDbProps(dbProps);
    }
    next.handleRequest(exchange, context);
  }
}