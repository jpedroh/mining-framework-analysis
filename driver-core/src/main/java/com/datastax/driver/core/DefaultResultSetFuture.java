package com.datastax.driver.core;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.exceptions.*;

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
            if (scc.name.isEmpty()) {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, null, null);
            } else {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null);
            }
            break;
            case DROPPED:
            if (scc.name.isEmpty()) {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, null, null);
            } else {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null);
            }
            break;
            case UPDATED:
            if (scc.name.isEmpty()) {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, null);
            } else {
              session.cluster.manager.refreshSchemaAndSignal(connection, this, rs, scc.keyspace, scc.name);
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

  void setResult(ResultSet rs) {
    set(rs);
  }

  public ResultSet getUninterruptibly() {
    try {
      return Uninterruptibles.getUninterruptibly(this);
    } catch (ExecutionException e) {
      throw extractCauseFromExecutionException(e);
    }
  }

  public ResultSet getUninterruptibly(long timeout, TimeUnit unit) throws TimeoutException {
    try {
      return Uninterruptibles.getUninterruptibly(this, timeout, unit);
    } catch (ExecutionException e) {
      throw extractCauseFromExecutionException(e);
    }
  }

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

  @Override public void onSet(Connection connection, Message.Response response, long latency, int retryCount) {
    onSet(connection, response, null, null, latency);
  }

  @Override public void onException(Connection connection, Exception exception, long latency, int retryCount) {
    setException(exception);
  }

  @Override public boolean onTimeout(Connection connection, long latency, int retryCount) {
    setException(new ConnectionException(connection.address, "Operation timed out"));
    return true;
  }

  @Override public int retryCount() {
    return 0;
  }
}