package org.restheart.handlers.aggregation;
import com.mongodb.MongoCommandException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MapReduceIterable;
import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.restheart.Bootstrapper;
import org.restheart.representation.Resource;
import org.restheart.handlers.IllegalQueryParamenterException;
import org.restheart.handlers.PipedHttpHandler;
import org.restheart.handlers.RequestContext;
import org.restheart.handlers.metadata.InvalidMetadataException;
import org.restheart.utils.HttpStatus;
import org.restheart.utils.ResponseHelper;

/**
 *
 * @author Andrea Di Cesare {@literal <andrea@softinstigate.com>}
 */
public class GetAggregationHandler extends PipedHttpHandler {
  /**
     * Default ctor
     */
  public GetAggregationHandler() {
    super();
  }

  /**
     * Default ctor
     *
     * @param next
     */
  public GetAggregationHandler(PipedHttpHandler next) {
    super(next);
  }

  /**
     *
     * @param exchange
     * @param context
     * @throws Exception
     */
  @Override public void handleRequest(HttpServerExchange exchange, RequestContext context) throws Exception {
    if (context.isInError()) {
      next(exchange, context);
      return;
    }
    String queryUri = context.getAggregationOperation();
    List<AbstractAggregationOperation> aggregations = AbstractAggregationOperation.getFromJson(context.getCollectionProps());
    Optional<AbstractAggregationOperation> _query = aggregations.stream().filter((q) -> q.getUri().equals(queryUri)).findFirst();
    if (!_query.isPresent()) {
      ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_NOT_FOUND, "query does not exist");
      next(exchange, context);
      return;
    }
    ArrayList<BsonDocument> data = new ArrayList<>();
    int size;
    AbstractAggregationOperation query = _query.get();
    if (null == query.getType()) {
      ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_INTERNAL_SERVER_ERROR, "unknown query type");
      next(exchange, context);
      return;
    } else {
      switch (query.getType()) {
        case MAP_REDUCE:
        MapReduceIterable<BsonDocument> mrOutput;
        MapReduce mapReduce = (MapReduce) query;
        try {
          mrOutput = getDatabase().getCollection(context.getDBName(), context.getCollectionName()).mapReduce(mapReduce.getResolvedMap(context.getAggreationVars()), mapReduce.getResolvedReduce(context.getAggreationVars())).filter(mapReduce.getResolvedQuery(context.getAggreationVars())).maxTime(Bootstrapper.getConfiguration().getAggregationTimeLimit(), TimeUnit.MILLISECONDS);
        } catch (MongoCommandException | InvalidMetadataException ex) {
          ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_INTERNAL_SERVER_ERROR, "error executing mapReduce", ex);
          next(exchange, context);
          return;
        } catch (QueryVariableNotBoundException qvnbe) {
          ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_BAD_REQUEST, "error executing mapReduce: " + qvnbe.getMessage());
          next(exchange, context);
          return;
        }
        for (BsonDocument obj : mrOutput) {
          data.add(obj);
        }
        size = data.size();
        break;
        case AGGREGATION_PIPELINE:
        AggregateIterable<BsonDocument> agrOutput;
        AggregationPipeline pipeline = (AggregationPipeline) query;
        try {
          agrOutput = getDatabase().getCollection(context.getDBName(), context.getCollectionName()).aggregate(pipeline.getResolvedStagesAsList(context.getAggreationVars())).maxTime(Bootstrapper.getConfiguration().getAggregationTimeLimit(), TimeUnit.MILLISECONDS).allowDiskUse(pipeline.getAllowDiskUse().getValue());
        } catch (MongoCommandException | InvalidMetadataException ex) {
          ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_INTERNAL_SERVER_ERROR, "error executing aggreation pipeline", ex);
          next(exchange, context);
          return;
        } catch (QueryVariableNotBoundException qvnbe) {
          ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_BAD_REQUEST, "error executing aggreation pipeline: " + qvnbe.getMessage());
          next(exchange, context);
          return;
        }
        for (BsonDocument obj : agrOutput) {
          data.add(obj);
        }
        size = data.size();
        break;
        default:
        ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_INTERNAL_SERVER_ERROR, "unknown query type");
        next(exchange, context);
        return;
      }
    }
    if (exchange.isComplete()) {
      return;
    }
    try {
      context.setResponseContent(new AggregationResultRepresentationFactory().getRepresentation(exchange, context, applyPagination(data, context), size).asBsonDocument());
      context.setResponseContentType(Resource.HAL_JSON_MEDIA_TYPE);
      context.setResponseStatusCode(HttpStatus.SC_OK);
      next(exchange, context);
    } catch (IllegalQueryParamenterException ex) {
      ResponseHelper.endExchangeWithMessage(exchange, context, HttpStatus.SC_BAD_REQUEST, ex.getMessage(), ex);
      next(exchange, context);
    }
  }

  private List<BsonDocument> applyPagination(List<BsonDocument> data, RequestContext context) {
    if (data == null) {
      return data;
    }
    int start = context.getPagesize() * (context.getPage() - 1);
    int end = start + context.getPagesize();
    if (data.size() < start) {
      return Collections.emptyList();
    } else {
      return data.subList(start, end > data.size() ? data.size() : end);
    }
  }
}