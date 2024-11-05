package com.datastax.driver.core;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.exceptions.DriverInternalError;

abstract class ArrayBackedResultSet implements ResultSet {
  private static final Logger logger = LoggerFactory.getLogger(ResultSet.class);

  private static final Queue<List<ByteBuffer>> EMPTY_QUEUE = new ArrayDeque<List<ByteBuffer>>(0);

  protected final ColumnDefinitions metadata;

  private final boolean wasApplied;

  protected final ProtocolVersion protocolVersion;

  private ArrayBackedResultSet(ColumnDefinitions metadata, List<ByteBuffer> firstRow, ProtocolVersion protocolVersion) {
    this.metadata = metadata;
    this.protocolVersion = protocolVersion;
    this.wasApplied = checkWasApplied(firstRow, metadata);
  }

  static ArrayBackedResultSet fromMessage(Responses.Result msg, SessionManager session, ProtocolVersion protocolVersion, ExecutionInfo info, Statement statement) {
    info = update(info, msg, session);
    switch (msg.kind) {
      case VOID:
      return empty(info);
      case ROWS:
      Responses.Result.Rows r = (Responses.Result.Rows) msg;
      ColumnDefinitions columnDefs;
      if (r.metadata.columns == null) {
        assert statement instanceof BoundStatement;
        columnDefs = ((BoundStatement) statement).statement.getPreparedId().resultSetMetadata;
        assert columnDefs != null;
      } else {
        columnDefs = r.metadata.columns;
      }
      assert r.metadata.pagingState == null || info != null;
      return r.metadata.pagingState == null ? new SinglePage(columnDefs, protocolVersion, r.data, info) : new MultiPage(columnDefs, protocolVersion, r.data, info, r.metadata.pagingState, session, statement);
      case SET_KEYSPACE:
      case SCHEMA_CHANGE:
      return empty(info);
      case PREPARED:
      throw new RuntimeException("Prepared statement received when a ResultSet was expected");
      default:
      logger.error("Received unknown result type \'{}\'; returning empty result set", msg.kind);
      return empty(info);
    }
  }

  private static ExecutionInfo update(ExecutionInfo info, Responses.Result msg, SessionManager session) {
    UUID tracingId = msg.getTracingId();
    return tracingId == null || info == null ? info : info.withTrace(new QueryTrace(tracingId, session));
  }

  private static ArrayBackedResultSet empty(ExecutionInfo info) {
    return new SinglePage(ColumnDefinitions.EMPTY, null, EMPTY_QUEUE, info);
  }

  public ColumnDefinitions getColumnDefinitions() {
    return metadata;
  }

  public List<Row> all() {
    if (isExhausted()) {
      return Collections.emptyList();
    }
    List<Row> result = new ArrayList<Row>(getAvailableWithoutFetching());
    for (Row row : this) {
      result.add(row);
    }
    return result;
  }

  @Override public Iterator<Row> iterator() {
    return new Iterator<Row>() {
      @Override public boolean hasNext() {
        return !isExhausted();
      }

      @Override public Row next() {
        return ArrayBackedResultSet.this.one();
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override public boolean wasApplied() {
    return wasApplied;
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ResultSet[ exhausted: ").append(isExhausted());
    sb.append(", ").append(metadata).append(']');
    return sb.toString();
  }

  private static class SinglePage extends ArrayBackedResultSet {
    private final Queue<List<ByteBuffer>> rows;

    private final ExecutionInfo info;

    private SinglePage(ColumnDefinitions metadata, ProtocolVersion protocolVersion, Queue<List<ByteBuffer>> rows, ExecutionInfo info) {
      super(metadata, rows.peek(), protocolVersion);
      this.info = info;
      this.rows = rows;
    }

    public boolean isExhausted() {
      return rows.isEmpty();
    }

    public Row one() {
      return ArrayBackedRow.fromData(metadata, protocolVersion, rows.poll());
    }

    public int getAvailableWithoutFetching() {
      return rows.size();
    }

    public boolean isFullyFetched() {
      return true;
    }

    public ListenableFuture<Void> fetchMoreResults() {
      return Futures.immediateFuture(null);
    }

    public ExecutionInfo getExecutionInfo() {
      return info;
    }

    public List<ExecutionInfo> getAllExecutionInfo() {
      return Collections.singletonList(info);
    }
  }

  private static class MultiPage extends ArrayBackedResultSet {
    private Queue<List<ByteBuffer>> currentPage;

    private final Queue<Queue<List<ByteBuffer>>> nextPages = new ConcurrentLinkedQueue<Queue<List<ByteBuffer>>>();

    private final Deque<ExecutionInfo> infos = new LinkedBlockingDeque<ExecutionInfo>();

    private volatile FetchingState fetchState;

    private final SessionManager session;

    private final Statement statement;

    private MultiPage(ColumnDefinitions metadata, ProtocolVersion protocolVersion, Queue<List<ByteBuffer>> rows, ExecutionInfo info, ByteBuffer pagingState, SessionManager session, Statement statement) {
      super(metadata, rows.peek(), protocolVersion);
      this.currentPage = rows;
      this.infos.offer(info);
      this.fetchState = new FetchingState(pagingState, null);
      this.session = session;
      this.statement = statement;
    }

    public boolean isExhausted() {
      prepareNextRow();
      return currentPage.isEmpty();
    }

    public Row one() {
      prepareNextRow();
      return ArrayBackedRow.fromData(metadata, protocolVersion, currentPage.poll());
    }

    public int getAvailableWithoutFetching() {
      int available = currentPage.size();
      for (Queue<List<ByteBuffer>> page : nextPages) {
        available += page.size();
      }
      return available;
    }

    public boolean isFullyFetched() {
      return fetchState == null;
    }

    private void prepareNextRow() {
      while (currentPage.isEmpty()) {
        FetchingState fetchingState = this.fetchState;
        Queue<List<ByteBuffer>> nextPage = nextPages.poll();
        if (nextPage != null) {
          currentPage = nextPage;
          continue;
        }
        if (fetchingState == null) {
          return;
        }
        try {
          Uninterruptibles.getUninterruptibly(fetchMoreResults());
        } catch (ExecutionException e) {
          throw DefaultResultSetFuture.extractCauseFromExecutionException(e);
        }
      }
    }

    public ListenableFuture<Void> fetchMoreResults() {
      return fetchMoreResults(this.fetchState);
    }

    private ListenableFuture<Void> fetchMoreResults(FetchingState fetchState) {
      if (fetchState == null) {
        return Futures.immediateFuture(null);
      }
      if (fetchState.inProgress != null) {
        return fetchState.inProgress;
      }
      assert fetchState.nextStart != null;
      ByteBuffer state = fetchState.nextStart;
      SettableFuture<Void> future = SettableFuture.create();
      this.fetchState = new FetchingState(null, future);
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
              info = update(info, rm, MultiPage.this.session);
              if (rm.kind == Responses.Result.Kind.ROWS) {
                Responses.Result.Rows rows = (Responses.Result.Rows) rm;
                MultiPage.this.nextPages.offer(rows.data);
                MultiPage.this.fetchState = rows.metadata.pagingState == null ? null : new FetchingState(rows.metadata.pagingState, null);
              } else {
                if (rm.kind == Responses.Result.Kind.VOID) {
                  MultiPage.this.fetchState = null;
                } else {
                  logger.error("Received unknown result type \'{}\' during paging: ignoring message", rm.kind);
                  connection.defunct(new ConnectionException(connection.address, String.format("Got unexpected %s result response", rm.kind)));
                  future.setException(new DriverInternalError(String.format("Got unexpected %s result response from %s", rm.kind, connection.address)));
                  return;
                }
              }
              MultiPage.this.infos.offer(info);
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

        @Override public void onSet(Connection connection, Message.Response response, long latency, int retryCount) {
          onSet(connection, response, null, null, latency);
        }

        @Override public void onException(Connection connection, Exception exception, long latency, int retryCount) {
          future.setException(exception);
        }

        @Override public boolean onTimeout(Connection connection, long latency, int retryCount) {
          throw new UnsupportedOperationException();
        }

        @Override public int retryCount() {
          return 0;
        }
      }, statement);
      return future;
    }

    public ExecutionInfo getExecutionInfo() {
      return infos.getLast();
    }

    public List<ExecutionInfo> getAllExecutionInfo() {
      return new ArrayList<ExecutionInfo>(infos);
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

  private static boolean checkWasApplied(List<ByteBuffer> firstRow, ColumnDefinitions metadata) {
    if (firstRow == null) {
      return true;
    }
    int[] is = metadata.findAllIdx("[applied]");
    if (is == null) {
      return true;
    }
    int i = is[0];
    if (!DataType.cboolean().equals(metadata.getType(i))) {
      return true;
    }
    ByteBuffer value = firstRow.get(i);
    if (value == null || value.remaining() == 0) {
      return false;
    }
    return TypeCodec.BooleanCodec.instance.deserializeNoBoxing(value);
  }
}