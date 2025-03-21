package com.datastax.driver.core;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.datastax.driver.core.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


<<<<<<< /usr/src/app/output/datastax/java-driver/aede0e8d32b985ff839f607714f128c528a91e2f/driver-core/src/main/java/com/datastax/driver/core/ResultSet.java/left.java
/**
 * The result of a query.
 *
 * Note that this class is not thread-safe.
 */
public class ResultSet implements Iterable<Row> {
  private static final Logger logger = LoggerFactory.getLogger(ResultSet.class);

  private static final Queue<List<ByteBuffer>> EMPTY_QUEUE = new ArrayDeque<List<ByteBuffer>>(0);

  private final ColumnDefinitions metadata;

  private final Queue<List<ByteBuffer>> rows;

  private final List<ExecutionInfo> infos;

  private volatile FetchingState fetchState;

  private final Session.Manager session;

  private final Statement statement;

  private ResultSet(ColumnDefinitions metadata, Queue<List<ByteBuffer>> rows, ExecutionInfo info, ByteBuffer initialPagingState, Session.Manager session, Statement statement) {
    this.metadata = metadata;
    this.rows = rows;
    this.session = session;
    if (initialPagingState == null) {
      this.fetchState = null;
      this.infos = Collections.<ExecutionInfo>singletonList(info);
    } else {
      this.fetchState = new FetchingState(initialPagingState, null);
      this.infos = new ArrayList<ExecutionInfo>();
      this.infos.add(info);
    }
    this.statement = statement;
    assert fetchState == null || (session != null && statement != null);
  }

  static ResultSet fromMessage(Responses.Result msg, Session.Manager session, ExecutionInfo info, Statement statement) {
    UUID tracingId = msg.getTracingId();
    info = tracingId == null || info == null ? info : info.withTrace(new QueryTrace(tracingId, session));
    switch (msg.kind) {
      case VOID:
      return empty(info);
      case ROWS:
      Responses.Result.Rows r = (Responses.Result.Rows) msg;
      ColumnDefinitions columnDefs;
      if (r.metadata.columns == null) {
        assert statement instanceof BoundStatement;
        columnDefs = ((BoundStatement) statement).statement.resultSetMetadata;
        assert columnDefs != null;
      } else {
        columnDefs = r.metadata.columns;
      }
      return new ResultSet(columnDefs, r.data, info, r.metadata.pagingState, session, statement);
      case SET_KEYSPACE:
      case SCHEMA_CHANGE:
      return empty(info);
      case PREPARED:
      throw new RuntimeException("Prepared statement received when a ResultSet was expected");
      default:
      logger.error("Received unknow result type \'{}\'; returning empty result set", msg.kind);
      return empty(info);
    }
  }

  private static ResultSet empty(ExecutionInfo info) {
    return new ResultSet(ColumnDefinitions.EMPTY, EMPTY_QUEUE, info, null, null, null);
  }

  /**
     * Returns the columns returned in this ResultSet.
     *
     * @return the columns returned in this ResultSet.
     */
  public ColumnDefinitions getColumnDefinitions() {
    return metadata;
  }

  /**
     * Returns whether this ResultSet has more results.
     *
     * @return whether this ResultSet has more results.
     */
  public boolean isExhausted() {
    if (!rows.isEmpty()) {
      return false;
    }
    fetchMoreResultsBlocking();
    assert !rows.isEmpty() || isFullyFetched();
    return rows.isEmpty();
  }

  /**
     * Returns the the next result from this ResultSet.
     *
     * @return the next row in this resultSet or null if this ResultSet is
     * exhausted.
     */
  public Row one() {
    List<ByteBuffer> nextRow = rows.poll();
    if (nextRow != null) {
      return Row.fromData(metadata, nextRow);
    }
    fetchMoreResultsBlocking();
    return Row.fromData(metadata, rows.poll());
  }

  /**
     * Returns all the remaining rows in this ResultSet as a list.
     *
     * @return a list containing the remaining results of this ResultSet. The
     * returned list is empty if and only the ResultSet is exhausted.
     */
  public List<Row> all() {
    if (isExhausted()) {
      return Collections.emptyList();
    }
    List<Row> result = new ArrayList<Row>(rows.size());
    for (Row row : this) {
      result.add(row);
    }
    return result;
  }

  /**
     * Returns an iterator over the rows contained in this ResultSet.
     *
     * The {@link Iterator#next} method is equivalent to calling {@link #one}.
     * So this iterator will consume results from this ResultSet and after a
     * full iteration, the ResultSet will be empty.
     *
     * The returned iterator does not support the {@link Iterator#remove} method.
     *
     * @return an iterator that will consume and return the remaining rows of
     * this ResultSet.
     */
  @Override public Iterator<Row> iterator() {
    return new Iterator<Row>() {
      @Override public boolean hasNext() {
        return !isExhausted();
      }

      @Override public Row next() {
        return ResultSet.this.one();
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
     * The number of rows that can be retrieved from this result set without
     * blocking to fetch.
     *
     * @return the number of rows readily available in this result set. If
     * {@link #isFullyFetched()}, this is the total number of rows remaining
     * in this result set (after which the result set will be exhausted).
     */
  public int getAvailableWithoutFetching() {
    return rows.size();
  }

  /**
     * Whether all results from this result set has been fetched from the
     * database.
     * <p>
     * Note that if {@code isFullyFetched()}, then {@link #getAvailableWithoutFetching}
     * will return how much rows remains in the result set before exhaustion. But
     * please note that {@code !isFullyFetched()} never guarantees that the result set
     * is not exhausted (you should call {@code isExhausted()} to make sure of it).
     *
     * @return whether all results have been fetched.
     */
  public boolean isFullyFetched() {
    return fetchState == null;
  }

  private void fetchMoreResultsBlocking() {
    try {
      Uninterruptibles.getUninterruptibly(fetchMoreResults());
    } catch (ExecutionException e) {
      throw ResultSetFuture.extractCauseFromExecutionException(e);
    }
  }

  /**
     * Force the fetching the next page of results for this result set, if any.
     * <p>
     * This method is entirely optional. It will be called automatically while
     * the result set is consumed (through {@link #one}, {@link #all} or iteration)
     * when needed (i.e. when {@code getAvailableWithoutFetching() == 0} and
     * {@code isFullyFetched() == false}).
     * <p>
     * You can however call this method manually to force the fetching of the
     * next page of results. This can allow to prefetch results before they are
     * stricly needed. For instance, if you want to prefetch the next page of
     * results as soon as there is less than 100 rows readily available in this
     * result set, you can do:
     * <pre>
     *   ResultSet rs = session.execute(...);
     *   Iterator&lt;Row&gt; iter = rs.iterator();
     *   while (iter.hasNext()) {
     *       if (rs.getAvailableWithoutFetching() == 100 && !rs.isFullyFetched())
     *           rs.fetchMoreResults();
     *       Row row = iter.next()
     *       ... process the row ...
     *   }
     * </pre>
     * This method is not blocking, so in the example above, the call to {@code
     * fetchMoreResults} will not block the processing of the 100 currently available
     * rows (but {@code iter.hasNext()} will block once those rows have been processed
     * until the fetch query returns, if it hasn't yet).
     * <p>
     * Only one page of results (for a given result set) can be
     * fetched at any given time. If this method is called twice and the query
     * triggered by the first call has not returned yet when the second one is
     * performed, then the 2nd call will simply return a future on the currently
     * in progress query.
     *
     * @return a future on the completion of fetching the next page of results.
     * If the result set is already fully retrieved ({@code isFullyFetched() == true}),
     * then the returned future will return immediately but not particular error will be
     * thrown (you should thus call {@code isFullyFetched() to know if calling this
     * method can be of any use}).
     */
  public ListenableFuture<Void> fetchMoreResults() {
    if (isFullyFetched()) {
      return Futures.immediateFuture(null);
    }
    if (fetchState.inProgress != null) {
      return fetchState.inProgress;
    }
    assert fetchState.nextStart != null;
    ByteBuffer state = fetchState.nextStart;
    SettableFuture<Void> future = SettableFuture.create();
    fetchState = new FetchingState(null, future);
    return queryNextPage(state, future);
  }

  private ListenableFuture<Void> queryNextPage(ByteBuffer nextStart, final SettableFuture<Void> future) {
    assert !(statement instanceof BatchStatement);
    final Message.Request request = session.makeRequestMessage(statement, nextStart);
    session.execute(new RequestHandler.Callback() {
      @Override public Message.Request request() {
        return request;
      }

      @Override public void register(RequestHandler handler) {
      }

      @Override public void onSet(Connection connection, Message.Response response, ExecutionInfo info, Statement statement, long latency) {
        try {
          switch (response.type) {
            case RESULT:
            Responses.Result rm = (Responses.Result) response;
            ResultSet tmp = ResultSet.fromMessage(rm, ResultSet.this.session, info, statement);
            ResultSet.this.rows.addAll(tmp.rows);
            ResultSet.this.fetchState = tmp.fetchState;
            ResultSet.this.infos.addAll(tmp.infos);
            future.set(null);
            break;
            case ERROR:
            future.setException(((Responses.Error) response).asException(connection.address));
            break;
            default:
            connection.defunct(new ConnectionException(connection.address, String.format("Got unexpected %s response", response.type)));
            future.setException(new DriverInternalError(String.format("Got unexpected %s response from %s", response.type, connection.address)));
            break;
          }
        } catch (RuntimeException e) {
          future.setException(new DriverInternalError("Unexpected error while processing response from " + connection.address, e));
        }
      }

      @Override public void onSet(Connection connection, Message.Response response, long latency) {
        onSet(connection, response, null, null, latency);
      }

      @Override public void onException(Connection connection, Exception exception, long latency) {
        future.setException(exception);
      }

      @Override public void onTimeout(Connection connection, long latency) {
        throw new UnsupportedOperationException();
      }
    }, statement);
    return future;
  }

  /**
     * Returns information on the execution of the last query made for this ResultSet.
     * <p>
     * Note that in most cases, a ResultSet is fetched with only one query, but large
     * result sets can be paged and thus be retrieved by multiple queries. If that is
     * the case, that method return that {@code ExecutionInfo} for the last query
     * performed. To retrieve the informations for all queries, use {@link #getAllExecutionInfo}.
     * <p>
     * The returned object includes basic information such as the queried hosts,
     * but also the Cassandra query trace if tracing was enabled for the query.
     *
     * @return the execution info for the last query made for this ResultSet.
     */
  public ExecutionInfo getExecutionInfo() {
    return infos.get(infos.size() - 1);
  }

  /**
     * Return the execution informations for all queries made to retrieve this
     * ResultSet.
     * <p>
     * Unless the ResultSet is large enough to get paged underneath, the returned
     * list will be singleton. If paging has been used however, the returned list
     * contains the {@code ExecutionInfo} for all the queries done to obtain this
     * ResultSet (at the time of the call) in the order those queries were made.
     *
     * @return a list of the execution info for all the queries made for this ResultSet.
     */
  public List<ExecutionInfo> getAllExecutionInfo() {
    return new ArrayList<ExecutionInfo>(infos);
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ResultSet[ exhausted: ").append(isExhausted());
    sb.append(", ").append(metadata).append("]");
    return sb.toString();
  }

  private static class FetchingState {
    public final ByteBuffer nextStart;

    public final ListenableFuture<Void> inProgress;

    FetchingState(ByteBuffer nextStart, ListenableFuture<Void> inProgress) {
      assert (nextStart == null) != (inProgress == null);
      this.nextStart = nextStart;
      this.inProgress = inProgress;
    }
  }
}
=======
/**
 * The result of a query.
 *
 * Note that this class is not thread-safe.
 */
public interface ResultSet extends Iterable<Row> {
  /**
     * Returns the columns returned in this ResultSet.
     *
     * @return the columns returned in this ResultSet.
     */
  public ColumnDefinitions getColumnDefinitions();

  /**
     * Returns whether this ResultSet has more results.
     *
     * @return whether this ResultSet has more results.
     */
  public boolean isExhausted();

  /**
     * Returns the the next result from this ResultSet.
     *
     * @return the next row in this resultSet or null if this ResultSet is
     * exhausted.
     */
  public Row one();

  /**
     * Returns all the remaining rows in this ResultSet as a list.
     *
     * @return a list containing the remaining results of this ResultSet. The
     * returned list is empty if and only the ResultSet is exhausted.
     */
  public List<Row> all();

  /**
     * Returns an iterator over the rows contained in this ResultSet.
     *
     * The {@link Iterator#next} method is equivalent to calling {@link #one}.
     * So this iterator will consume results from this ResultSet and after a
     * full iteration, the ResultSet will be empty.
     *
     * The returned iterator does not support the {@link Iterator#remove} method.
     *
     * @return an iterator that will consume and return the remaining rows of
     * this ResultSet.
     */
  @Override public Iterator<Row> iterator();

  /**
     * Returns information on the execution of this query.
     * <p>
     * The returned object includes basic information such as the queried hosts,
     * but also the Cassandra query trace if tracing was enabled for the query.
     *
     * @return the execution info for this query.
     */
  public ExecutionInfo getExecutionInfo();
}
>>>>>>> /usr/src/app/output/datastax/java-driver/aede0e8d32b985ff839f607714f128c528a91e2f/driver-core/src/main/java/com/datastax/driver/core/ResultSet.java/right.java
