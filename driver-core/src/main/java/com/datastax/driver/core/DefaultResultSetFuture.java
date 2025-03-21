package com.datastax.driver.core;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.exceptions.*;

/**
 * Internal implementation of ResultSetFuture.
 */
class DefaultResultSetFuture extends AbstractFuture<ResultSet> implements ResultSetFuture, RequestHandler.Callback {
  private static final Logger logger = LoggerFactory.getLogger(ResultSetFuture.class);

  private final SessionManager session;

  private final ProtocolVersion protocolVersion;

  private final Message.Request request;

  private volatile RequestHandler handler;

  DefaultResultSetFuture(SessionManager session, ProtocolVersion protocolVersion, Message.Request request) {
    this.session = session;
    this.protocolVersion = protocolVersion;
    this.request = request;
  }

  @Override public void register(RequestHandler handler) {
    this.handler = handler;
  }

  @Override public Message.Request request() {
    return request;
  }

  @Override public void onSet(Connection connection, Message.Response response, ExecutionInfo info, Statement statement, long latency) {
    try {
      switch (response.type) {
        case RESULT:
        Responses.Result rm = (Responses.Result) response;
        switch (rm.kind) {
          case SET_KEYSPACE:
          session.poolsState.setKeyspace(((Responses.Result.SetKeyspace) rm).keyspace);
          set(ArrayBackedResultSet.fromMessage(rm, session, protocolVersion, info, statement));
          break;
          case SCHEMA_CHANGE:
          Responses.Result.SchemaChange scc = (Responses.Result.SchemaChange) rm;
          ResultSet rs = ArrayBackedResultSet.fromMessage(rm, session, protocolVersion, info, statement);
          switch (scc.change) {
            case CREATED:
            switch (scc.target) {
              case KEYSPACE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null, null);
              break;
              case TABLE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, scc.name, null);
              break;
              case TYPE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null, scc.name);
              break;
            }
            break;
            case DROPPED:

<<<<<<< /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/main/java/com/datastax/driver/core/DefaultResultSetFuture.java/left.java
            KeyspaceMetadata keyspace;
=======
            if (scc.columnFamily.isEmpty()) {
              session.cluster.manager.metadata.removeKeyspace(scc.keyspace);
            } else {
              KeyspaceMetadata keyspace = session.cluster.manager.metadata.getKeyspaceInternal(scc.keyspace);
              if (keyspace == null) {
                logger.warn("Received a DROPPED notification for {}.{}, but this keyspace is unknown in our metadata", scc.keyspace, scc.columnFamily);
              } else {
                keyspace.removeTable(scc.columnFamily);
              }
            }
>>>>>>> /usr/src/app/output/datastax/java-driver/6af4247c0b94538f16fbac838eb9d6c0af4098b8/driver-core/src/main/java/com/datastax/driver/core/DefaultResultSetFuture.java/right.java

            switch (scc.target) {
              case KEYSPACE:
              session.cluster.manager.metadata.removeKeyspace(scc.keyspace);
              break;
              case TABLE:
              keyspace = session.cluster.manager.metadata.getKeyspace(scc.keyspace);
              if (keyspace == null) {
                logger.warn("Received a DROPPED notification for table {}.{}, but this keyspace is unknown in our metadata", scc.keyspace, scc.name);
              } else {
                keyspace.removeTable(scc.name);
              }
              break;
              case TYPE:
              keyspace = session.cluster.manager.metadata.getKeyspace(scc.keyspace);
              if (keyspace == null) {
                logger.warn("Received a DROPPED notification for UDT {}.{}, but this keyspace is unknown in our metadata", scc.keyspace, scc.name);
              } else {
                keyspace.removeUserType(scc.name);
              }
              break;
            }
            this.setResult(rs);
            break;
            case UPDATED:
            switch (scc.target) {
              case KEYSPACE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null, null);
              break;
              case TABLE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, scc.name, null);
              break;
              case TYPE:
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null, scc.name);
              break;
            }
            break;
            default:
            logger.info("Ignoring unknown schema change result");
            break;
          }
          break;
          default:
          set(ArrayBackedResultSet.fromMessage(rm, session, protocolVersion, info, statement));
          break;
        }
        break;
        case ERROR:
        setException(((Responses.Error) response).asException(connection.address));
        break;
        default:
        connection.defunct(new ConnectionException(connection.address, String.format("Got unexpected %s response", response.type)));
        setException(new DriverInternalError(String.format("Got unexpected %s response from %s", response.type, connection.address)));
        break;
      }
    } catch (RuntimeException e) {
      setException(new DriverInternalError("Unexpected error while processing response from " + connection.address, e));
    }
  }

  @Override public void onSet(Connection connection, Message.Response response, long latency, int retryCount) {
    onSet(connection, response, null, null, latency);
  }

  @Override public void onException(Connection connection, Exception exception, long latency, int retryCount) {
    setException(exception);
  }

  @Override public boolean onTimeout(Connection connection, long latency, int retryCount) {
    setException(new OperationTimedOutException(connection.address));
    return true;
  }

  void setResult(ResultSet rs) {
    set(rs);
  }

  /**
     * Waits for the query to return and return its result.
     *
     * This method is usually more convenient than {@link #get} because it:
     * <ul>
     *   <li>Waits for the result uninterruptibly, and so doesn't throw
     *   {@link InterruptedException}.</li>
     *   <li>Returns meaningful exceptions, instead of having to deal
     *   with ExecutionException.</li>
     * </ul>
     * As such, it is the preferred way to get the future result.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, that is an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query is invalid (syntax error,
     * unauthorized or any other validation problem).
     */
  public ResultSet getUninterruptibly() {
    try {
      return Uninterruptibles.getUninterruptibly(this);
    } catch (ExecutionException e) {
      throw extractCauseFromExecutionException(e);
    }
  }

  /**
     * Waits for the provided time for the query to return and return its
     * result if available.
     *
     * This method is usually more convenient than {@link #get} because it:
     * <ul>
     *   <li>Waits for the result uninterruptibly, and so doesn't throw
     *   {@link InterruptedException}.</li>
     *   <li>Returns meaningful exceptions, instead of having to deal
     *   with ExecutionException.</li>
     * </ul>
     * As such, it is the preferred way to get the future result.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, that is an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     * @throws TimeoutException if the wait timed out (Note that this is
     * different from a Cassandra timeout, which is a {@code
     * QueryExecutionException}).
     */
  public ResultSet getUninterruptibly(long timeout, TimeUnit unit) throws TimeoutException {
    try {
      return Uninterruptibles.getUninterruptibly(this, timeout, unit);
    } catch (ExecutionException e) {
      throw extractCauseFromExecutionException(e);
    }
  }

  /**
     * Attempts to cancel the execution of the request corresponding to this
     * future. This attempt will fail if the request has already returned.
     * <p>
     * Please note that this only cancels the request driver side, but nothing
     * is done to interrupt the execution of the request Cassandra side (and that even
     * if {@code mayInterruptIfRunning} is true) since  Cassandra does not
     * support such interruption.
     * <p>
     * This method can be used to ensure no more work is performed driver side
     * (which, while it doesn't include stopping a request already submitted
     * to a Cassandra node, may include not retrying another Cassandra host on
     * failure/timeout) if the ResultSet is not going to be retried. Typically,
     * the code to wait for a request result for a maximum of 1 second could
     * look like:
     * <pre>
     *   ResultSetFuture future = session.executeAsync(...some query...);
     *   try {
     *       ResultSet result = future.get(1, TimeUnit.SECONDS);
     *       ... process result ...
     *   } catch (TimeoutException e) {
     *       future.cancel(true); // Ensure any resource used by this query driver
     *                            // side is released immediately
     *       ... handle timeout ...
     *   }
     * <pre>
     *
     * @param mayInterruptIfRunning the value of this parameter is currently
     * ignored.
     * @return {@code false} if the future could not be cancelled (it has already
     * completed normally); {@code true} otherwise.
     */
  @Override public boolean cancel(boolean mayInterruptIfRunning) {
    if (!super.cancel(mayInterruptIfRunning)) {
      return false;
    }
    handler.cancel();
    return true;
  }

  static RuntimeException extractCauseFromExecutionException(ExecutionException e) {
    if (e.getCause() instanceof DriverException) {
      throw ((DriverException) e.getCause()).copy();
    } else {
      throw new DriverInternalError("Unexpected exception thrown", e.getCause());
    }
  }

  @Override public int retryCount() {
    return 0;
  }
}