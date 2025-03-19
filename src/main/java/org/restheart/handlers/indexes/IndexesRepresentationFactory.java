package org.restheart.handlers.indexes;
import com.mongodb.DBObject;
import org.restheart.Configuration;
import org.restheart.hal.Link;
import org.restheart.hal.Representation;
import static org.restheart.hal.Representation.HAL_JSON_MEDIA_TYPE;
import org.restheart.handlers.IllegalQueryParamenterException;
import org.restheart.handlers.RequestContext;
import org.restheart.utils.URLUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class IndexesRepresentationFactory {
  private static final Logger logger = LoggerFactory.getLogger(IndexesRepresentationFactory.class);

  /**
     *
     * @param exchange
     * @param context
     * @param embeddedData
     * @param size
     * @throws IllegalQueryParamenterException
     */
  static public void sendHal(HttpServerExchange exchange, RequestContext context, List<DBObject> embeddedData, long size) throws IllegalQueryParamenterException {
    String requestPath = URLUtils.removeTrailingSlashes(context.getMappedRequestUri());
    String queryString = exchange.getQueryString() == null || exchange.getQueryString().isEmpty() ? "" : "?" + URLUtils.decodeQueryString(exchange.getQueryString());
    Representation rep = new Representation(requestPath + queryString);
    rep.addProperty("_type", context.getType().name());
    if (size >= 0) {
      rep.addProperty("_size", size);
    }
    if (embeddedData != null) {
      long count = embeddedData.stream().filter((props) -> props.keySet().stream().anyMatch((k) -> k.equals("id") || k.equals("_id"))).count();
      rep.addProperty("_returned", count);
      if (!embeddedData.isEmpty()) {
        embeddedDocuments(embeddedData, requestPath, rep);
      }
    }
    if (context.isParentAccessible()) {
      rep.addLink(new Link("rh:coll", URLUtils.getParentPath(requestPath)));
    }
    rep.addLink(new Link("rh", "curies", Configuration.RESTHEART_ONLINE_DOC_URL + "/#api-indexes-{rel}", false), true);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HAL_JSON_MEDIA_TYPE);
    exchange.getResponseSender().send(rep.toString());
  }

  private static void embeddedDocuments(List<DBObject> embeddedData, String requestPath, Representation rep) {
    embeddedData.stream().forEach((d) -> {
      Object _id = d.get("_id");
      if (_id != null && (_id instanceof String || _id instanceof ObjectId)) {
        Representation nrep = new Representation(requestPath + "/" + _id.toString());
        nrep.addProperty("_type", RequestContext.TYPE.INDEX.name());
        nrep.addProperties(d);
        rep.addRepresentation("rh:index", nrep);
      } else {
        logger.error("index missing string _id field", d);
      }
    });
  }
}