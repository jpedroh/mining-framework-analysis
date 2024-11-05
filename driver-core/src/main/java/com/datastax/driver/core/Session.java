package com.datastax.driver.core;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Uninterruptibles;
import com.datastax.driver.core.exceptions.*;
import com.datastax.driver.core.policies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session {
  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  final Manager manager;

  Session(Cluster cluster, Collection<Host> hosts) {
    this.manager = new Manager(cluster, hosts);
  }

  public ResultSet execute(String query) {
    return execute(new SimpleStatement(query));
  }

  public ResultSet execute(String query, Object... values) {
    return execute(new SimpleStatement(query, values));
  }

  public ResultSet execute(Statement statement) {
    return executeAsync(statement).getUninterruptibly();
  }

  public ResultSetFuture executeAsync(String query) {
    return executeAsync(new SimpleStatement(query));
  }

  public ResultSetFuture executeAsync(String query, Object... values) {
    return executeAsync(new SimpleStatement(query, values));
  }

  public ResultSetFuture executeAsync(Statement statement) {
    return manager.executeQuery(manager.makeRequestMessage(statement, null), statement);
  }

  public PreparedStatement prepare(String query) {
    Connection.Future future = new Connection.Future(new Requests.Prepare(query));
    manager.execute(future, Statement.DEFAULT);
    return toPreparedStatement(query, future);
  }

  public PreparedStatement prepare(RegularStatement statement) {
    if (statement.getValues() != null) {
      throw new IllegalArgumentException("A statement to prepare should not have values");
    }
    PreparedStatement prepared = prepare(statement.toString());
    ByteBuffer routingKey = statement.getRoutingKey();
    if (routingKey != null) {
      prepared.setRoutingKey(routingKey);
    }
    prepared.setConsistencyLevel(statement.getConsistencyLevel());
    if (statement.isTracing()) {
      prepared.enableTracing();
    }
    prepared.setRetryPolicy(statement.getRetryPolicy());
    return prepared;
  }

  public ShutdownFuture shutdown() {
    return manager.shutdown();
  }

  public Cluster getCluster() {
    return manager.cluster;
  }

  private PreparedStatement toPreparedStatement(String query, Connection.Future future) {
    try {
      Message.Response response = Uninterruptibles.getUninterruptibly(future);
      switch (response.type) {
        case RESULT:
        Responses.Result rm = (Responses.Result) response;
        switch (rm.kind) {
          case PREPARED:
          Responses.Result.Prepared pmsg = (Responses.Result.Prepared) rm;
          PreparedStatement stmt = PreparedStatement.fromMessage(pmsg, manager.cluster.getMetadata(), query, manager.poolsState.keyspace);
          try {
            manager.cluster.manager.prepare(stmt, future.getAddress());
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          return stmt;
          default:
          throw new DriverInternalError(String.format("%s response received when prepared statement was expected", rm.kind));
        }
        case ERROR:
        throw ((Responses.Error) response).asException(future.getAddress());
        break;
        default:
        throw new DriverInternalError(String.format("%s response received when prepared statement was expected", response.type));
      }
    } catch (ExecutionException e) {
      throw ResultSetFuture.extractCauseFromExecutionException(e);
    }
  }

  static class Manager {
    final Cluster cluster;

    final ConcurrentMap<Host, HostConnectionPool> pools;

    final HostConnectionPool.PoolState poolsState;

    final AtomicReference<ShutdownFuture> shutdownFuture = new AtomicReference<ShutdownFuture>();

    public Manager(Cluster cluster, Collection<Host> hosts) {
      this.cluster = cluster;
      this.pools = new ConcurrentHashMap<Host, HostConnectionPool>(hosts.size());
      this.poolsState = new HostConnectionPool.PoolState();
      for (Host host : hosts) {
        try {
          addOrRenewPool(host, false).get();
        } catch (ExecutionException e) {
          throw new DriverInternalError(e);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    public Connection.Factory connectionFactory() {
      return cluster.manager.connectionFactory;
    }

    public Configuration configuration() {
      return cluster.manager.configuration;
    }

    LoadBalancingPolicy loadBalancingPolicy() {
      return cluster.manager.loadBalancingPolicy();
    }

    ReconnectionPolicy reconnectionPolicy() {
      return cluster.manager.reconnectionPolicy();
    }

    public ListeningExecutorService executor() {
      return cluster.manager.executor;
    }

    public ListeningExecutorService blockingExecutor() {
      return cluster.manager.blockingTasksExecutor;
    }

    boolean isShutdown() {
      return shutdownFuture.get() != null;
    }

    private ShutdownFuture shutdown() {
      ShutdownFuture future = shutdownFuture.get();
      if (future != null) {
        return future;
      }
      List<ShutdownFuture> futures = new ArrayList<ShutdownFuture>(pools.size());
      for (HostConnectionPool pool : pools.values()) {
        futures.add(pool.shutdown());
      }
      future = new ShutdownFuture.Forwarding(futures);
      return shutdownFuture.compareAndSet(null, future) ? future : shutdownFuture.get();
    }

    ListenableFuture<Boolean> addOrRenewPool(final Host host, final boolean isHostAddition) {
      final HostDistance distance = cluster.manager.loadBalancingPolicy().distance(host);
      if (distance == HostDistance.IGNORED) {
        return Futures.immediateFuture(true);
      }
      return executor().submit(new Callable<Boolean>() {
        public Boolean call() {
          logger.debug("Adding {} to list of queried hosts", host);
          try {
            HostConnectionPool previous = pools.put(host, new HostConnectionPool(host, distance, Session.Manager.this));
            if (previous != null) {
              previous.shutdown();
            }
            return true;
          } catch (AuthenticationException e) {
            logger.error("Error creating pool to {} ({})", host, e.getMessage());
            cluster.manager.signalConnectionFailure(host, new ConnectionException(e.getHost(), e.getMessage()), isHostAddition);
            return false;
          } catch (ConnectionException e) {
            logger.debug("Error creating pool to {} ({})", host, e.getMessage());
            cluster.manager.signalConnectionFailure(host, e, isHostAddition);
            return false;
          }
        }
      });
    }

    ListenableFuture<?> removePool(Host host) {
      final HostConnectionPool pool = pools.remove(host);
      if (pool == null) {
        return Futures.immediateFuture(null);
      }
      return executor().submit(new Runnable() {
        public void run() {
          pool.shutdown();
        }
      });
    }

    void updateCreatedPools() {
      for (Host h : cluster.getMetadata().allHosts()) {
        HostDistance dist = loadBalancingPolicy().distance(h);
        HostConnectionPool pool = pools.get(h);
        if (pool == null) {
          if (dist != HostDistance.IGNORED && h.isUp()) {
            addOrRenewPool(h, false);
          }
        } else {
          if (dist != pool.hostDistance) {
            if (dist == HostDistance.IGNORED) {
              removePool(h);
            } else {
              pool.hostDistance = dist;
            }
          }
        }
      }
    }

    public void onDown(Host host) {
      removePool(host).addListener(new Runnable() {
        public void run() {
          updateCreatedPools();
        }
      }, MoreExecutors.sameThreadExecutor());
    }

    public void onRemove(Host host) {
      onDown(host);
    }

    public void setKeyspace(String keyspace) {
      long timeout = configuration().getSocketOptions().getConnectTimeoutMillis();
      try {
        Future<?> future = executeQuery(new Requests.Query("use " + keyspace), Statement.DEFAULT);
        Uninterruptibles.getUninterruptibly(future, timeout, TimeUnit.MILLISECONDS);
      } catch (TimeoutException e) {
        throw new DriverInternalError(String.format("No responses after %d milliseconds while setting current keyspace. This should not happen, unless you have setup a very low connection timeout.", timeout));
      } catch (ExecutionException e) {
        throw ResultSetFuture.extractCauseFromExecutionException(e);
      }
    }

    public Message.Request makeRequestMessage(Statement statement, ByteBuffer pagingState) {
      ConsistencyLevel consistency = statement.getConsistencyLevel();
      if (consistency == null) {
        consistency = configuration().getQueryOptions().getConsistencyLevel();
      }
      ConsistencyLevel serialConsistency = statement.getSerialConsistencyLevel();
      if (serialConsistency == null) {
        serialConsistency = configuration().getQueryOptions().getSerialConsistencyLevel();
      }
      return makeRequestMessage(statement, consistency, serialConsistency, pagingState);
    }

    public Message.Request makeRequestMessage(Statement statement, ConsistencyLevel cl, ConsistencyLevel scl, ByteBuffer pagingState) {
      int fetchSize = statement.getFetchSize();
      if (fetchSize <= 0) {
        fetchSize = configuration().getQueryOptions().getFetchSize();
      }
      if (fetchSize == Integer.MAX_VALUE) {
        fetchSize = -1;
      }
      if (statement instanceof RegularStatement) {
        RegularStatement rs = (RegularStatement) statement;
        ByteBuffer[] rawValues = rs.getValues();
        List<ByteBuffer> values = rawValues == null ? Collections.<ByteBuffer>emptyList() : Arrays.asList(rawValues);
        String qString = rs.getQueryString();
        Requests.QueryProtocolOptions options = new Requests.QueryProtocolOptions(cl, values, false, fetchSize, pagingState, scl);
        return new Requests.Query(qString, options);
      } else {
        if (statement instanceof BoundStatement) {
          BoundStatement bs = (BoundStatement) statement;
          boolean skipMetadata = bs.statement.resultSetMetadata != null;
          Requests.QueryProtocolOptions options = new Requests.QueryProtocolOptions(cl, Arrays.asList(bs.values), skipMetadata, fetchSize, pagingState, scl);
          return new Requests.Execute(bs.statement.id, options);
        } else {
          assert statement instanceof BatchStatement : statement;
          assert pagingState == null;
          BatchStatement bs = (BatchStatement) statement;
          BatchStatement.IdAndValues idAndVals = bs.getIdAndValues();
          return new Requests.Batch(bs.batchType, idAndVals.ids, idAndVals.values, cl);
        }
      }
    }

    public void execute(RequestHandler.Callback callback, Statement statement) {
      new RequestHandler(this, callback, statement).sendRequest();
    }

    public void prepare(String query, InetAddress toExclude) throws InterruptedException {
      for (Map.Entry<Host, HostConnectionPool> entry : pools.entrySet()) {
        if (entry.getKey().getAddress().equals(toExclude)) {
          continue;
        }
        Connection c = null;
        try {
          c = entry.getValue().borrowConnection(200, TimeUnit.MILLISECONDS);
          c.write(new Requests.Prepare(query)).get();
        } catch (ConnectionException e) {
        } catch (BusyConnectionException e) {
        } catch (TimeoutException e) {
        } catch (ExecutionException e) {
          logger.error(String.format("Unexpected error while preparing query (%s) on %s", query, entry.getKey()), e);
        } finally {
          if (c != null) {
            entry.getValue().returnConnection(c);
          }
        }
      }
    }

    public ResultSetFuture executeQuery(Message.Request msg, Statement statement) {
      if (statement.isTracing()) {
        msg.setTracingRequested();
      }
      ResultSetFuture future = new ResultSetFuture(this, msg);
      execute(future.callback, statement);
      return future;
    }
  }
}