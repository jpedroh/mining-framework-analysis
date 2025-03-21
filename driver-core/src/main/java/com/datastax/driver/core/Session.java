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


<<<<<<< /usr/src/app/output/datastax/java-driver/aede0e8d32b985ff839f607714f128c528a91e2f/driver-core/src/main/java/com/datastax/driver/core/Session.java/left.java
/**
 * A session holds connections to a Cassandra cluster, allowing it to be queried.
 *
 * Each session maintains multiple connections to the cluster nodes,
 * provides policies to choose which node to use for each query (round-robin on
 * all nodes of the cluster by default), and handles retries for failed query (when
 * it makes sense), etc...
 * <p>
 * Session instances are thread-safe and usually a single instance is enough
 * per application. However, a given session can only be set to one keyspace
 * at a time, so one instance per keyspace is necessary.
 */
public class Session {
  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  final Manager manager;

  Session(Cluster cluster, Collection<Host> hosts) {
    this.manager = new Manager(cluster, hosts);
  }

  /**
     * Executes the provided query.
     *
     * This is a convenience method for {@code execute(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     */
  public ResultSet execute(String query) {
    return execute(new SimpleStatement(query));
  }

  /**
     * Executes the provided query using the provided value.
     *
     * This is a convenience method for {@code execute(new SimpleStatement(query, values))}.
     *
     * @param query the CQL query to execute.
     * @param values values required for the execution of {@code query}. See
     * {@link SimpleStatement#SimpleStatement(String, Object...)} for more detail.
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     */
  public ResultSet execute(String query, Object... values) {
    return execute(new SimpleStatement(query, values));
  }

  /**
     * Executes the provided query.
     *
     * This method blocks until at least some result has been received from the
     * database. However, for SELECT queries, it does not guarantee that the
     * result has been received in full. But it does guarantee that some
     * response has been received from the database, and in particular
     * guarantee that if the request is invalid, an exception will be thrown
     * by this method.
     *
     * @param statement the CQL query to execute (that can be any {@code Statement}).
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     */
  public ResultSet execute(Statement statement) {
    return executeAsync(statement).getUninterruptibly();
  }

  /**
     * Executes the provided query asynchronously.
     *
     * This is a convenience method for {@code executeAsync(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return a future on the result of the query.
     */
  public ResultSetFuture executeAsync(String query) {
    return executeAsync(new SimpleStatement(query));
  }

  /**
     * Executes the provided query asynchronously using the provided values.
     *
     * This is a convenience method for {@code executeAsync(new SimpleStatement(query, values))}.
     *
     * @param query the CQL query to execute.
     * @param values values required for the execution of {@code query}. See
     * {@link SimpleStatement#SimpleStatement(String, Object...)} for more detail.
     * @return a future on the result of the query.
     */
  public ResultSetFuture executeAsync(String query, Object... values) {
    return executeAsync(new SimpleStatement(query, values));
  }

  /**
     * Executes the provided query asynchronously.
     *
     * This method does not block. It returns as soon as the query has been
     * passed to the underlying network stack. In particular, returning from
     * this method does not guarantee that the query is valid or has even been
     * submitted to a live node. Any exception pertaining to the failure of the
     * query will be thrown when accessing the {@link ResultSetFuture}.
     *
     * Note that for queries that doesn't return a result (INSERT, UPDATE and
     * DELETE), you will need to access the ResultSetFuture (that is call one of
     * its get method to make sure the query was successful.
     *
     * @param statement the CQL query to execute (that can be either any {@code Statement}.
     * @return a future on the result of the query.
     */
  public ResultSetFuture executeAsync(Statement statement) {
    return manager.executeQuery(manager.makeRequestMessage(statement, null), statement);
  }

  /**
     * Prepares the provided query string.
     *
     * @param query the CQL query string to prepare
     * @return the prepared statement corresponding to {@code query}.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to prepare this query.
     */
  public PreparedStatement prepare(String query) {
    Connection.Future future = new Connection.Future(new Requests.Prepare(query));
    manager.execute(future, Statement.DEFAULT);
    return toPreparedStatement(query, future);
  }

  /**
     * Prepares the provided query.
     * <p>
     * This method is essentially a shortcut for {@code prepare(statement.getQueryString())},
     * but note that the resulting {@code PreparedStamenent} will inherit the query properties
     * set on {@code statement}. Concretely, this means that in the following code:
     * <pre>
     *   RegularStatement toPrepare = new SimpleStatement("SELECT * FROM test WHERE k=?").setConsistencyLevel(ConsistencyLevel.QUORUM);
     *   PreparedStatement prepared = session.prepare(toPrepare);
     *   session.execute(prepared.bind("someValue"));
     * </pre>
     * the final execution will be performed with Quorum consistency.
     *
     * @param statement the statement to prepare
     * @return the prepared statement corresponding to {@code statement}.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to prepare this statement.
     * @throws IllegalArgumentException if {@code statement.getValues() != null}
     * (values for executing a prepared statement should be provided after preparation
     * though the {@link PreparedStatement#bind} method or through a corresponding
     * {@link BoundStatement}).
     */
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

  /**
     * Initiates a shutdown of this session instance.
     *
     * This method is asynchronous and return a future on the completion
     * of the shutdown process. As soon a the session is shutdown, no
     * new request will be accepted, but already submitted queries are
     * allowed to complete. Shutdown closes all connections of this
     * session  and reclaims all resources used by it.
     * <p>
     * If for some reason you wish to expedite this process, the
     * {@link ShutdownFuture#force} can be called on the result future.
     * <p>
     * This method has no particular effect if the session was already shut
     * down (in which case the returned future will return immediately).
     * <p>
     * Note that if you want to shut down the full {@code Cluster} instance
     * this session is part of, you should use {@link Cluster#shutdown} instead
     * (which will call this method for all sessions but also release some
     * additional resources).
     *
     * @return a future on the completion of the shutdown process.
     */
  public ShutdownFuture shutdown() {
    return manager.shutdown();
  }

  /**
     * Returns the {@code Cluster} object this session is part of.
     *
     * @return the {@code Cluster} object this session is part of.
     */
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

    /**
         * Execute the provided request.
         *
         * This method will find a suitable node to connect to using the
         * {@link LoadBalancingPolicy} and handle host failover.
         */
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
=======
/**
 * A session holds connections to a Cassandra cluster, allowing it to be queried.
 *
 * Each session maintains multiple connections to the cluster nodes,
 * provides policies to choose which node to use for each query (round-robin on
 * all nodes of the cluster by default), and handles retries for failed query (when
 * it makes sense), etc...
 * <p>
 * Session instances are thread-safe and usually a single instance is enough
 * per application. However, a given session can only be set to one keyspace
 * at a time, so one instance per keyspace is necessary.
 */
public interface Session {
  /**
     * Executes the provided query.
     *
     * This is a convenience method for {@code execute(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     */
  public ResultSet execute(String query);

  /**
     * Executes the provided query.
     *
     * This method blocks until at least some result has been received from the
     * database. However, for SELECT queries, it does not guarantee that the
     * result has been received in full. But it does guarantee that some
     * response has been received from the database, and in particular
     * guarantee that if the request is invalid, an exception will be thrown
     * by this method.
     *
     * @param query the CQL query to execute (that can be either a {@code
     * Statement} or a {@code BoundStatement}). If it is a {@code
     * BoundStatement}, all variables must have been bound (the statement must
     * be ready).
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     * @throws IllegalStateException if {@code query} is a {@code BoundStatement}
     * but {@code !query.isReady()}.
     */
  public ResultSet execute(Query query);

  /**
     * Executes the provided query asynchronously.
     *
     * This is a convenience method for {@code executeAsync(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return a future on the result of the query.
     */
  public ResultSetFuture executeAsync(String query);

  /**
     * Executes the provided query asynchronously.
     *
     * This method does not block. It returns as soon as the query has been
     * passed to the underlying network stack. In particular, returning from
     * this method does not guarantee that the query is valid or has even been
     * submitted to a live node. Any exception pertaining to the failure of the
     * query will be thrown when accessing the {@link ResultSetFuture}.
     *
     * Note that for queries that doesn't return a result (INSERT, UPDATE and
     * DELETE), you will need to access the ResultSetFuture (that is call one of
     * its get method to make sure the query was successful.
     *
     * @param query the CQL query to execute (that can be either a {@code
     * Statement} or a {@code BoundStatement}). If it is a {@code
     * BoundStatement}, all variables must have been bound (the statement must
     * be ready).
     * @return a future on the result of the query.
     *
     * @throws IllegalStateException if {@code query} is a {@code BoundStatement}
     * but {@code !query.isReady()}.
     */
  public ResultSetFuture executeAsync(Query query);

  /**
     * Prepares the provided query string.
     *
     * @param query the CQL query string to prepare
     * @return the prepared statement corresponding to {@code query}.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to prepare this query.
     */
  public PreparedStatement prepare(String query);

  /**
     * Prepares the provided query.
     * <p>
     * This method is essentially a shortcut for {@code prepare(statement.getQueryString())},
     * but note that the resulting {@code PreparedStamenent} will inherit the query properties
     * set on {@code statement}. Concretely, this means that in the following code:
     * <pre>
     *   Statement toPrepare = new SimpleStatement("SELECT * FROM test WHERE k=?").setConsistencyLevel(ConsistencyLevel.QUORUM);
     *   PreparedStatement prepared = session.prepare(toPrepare);
     *   session.execute(prepared.bind("someValue"));
     * </pre>
     * the final execution will be performed with Quorum consistency.
     *
     * @param statement the statement to prepare
     * @return the prepared statement corresponding to {@code statement}.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to prepare this statement.
     */
  public PreparedStatement prepare(Statement statement);

  /**
     * Shuts down this session instance.
     * <p>
     * This closes all connections used by this sessions. Note that if you want
     * to shut down the full {@code Cluster} instance this session is part of,
     * you should use {@link Cluster#shutdown} instead (which will call this
     * method for all session but also release some additional resources).
     * <p>
     * This method has no effect if the session was already shutdown.
     */
  public void shutdown();

  /**
     * Shutdown this session instance, only waiting a definite amount of time.
     * <p>
     * This closes all connections used by this sessions. Note that if you want
     * to shutdown the full {@code Cluster} instance this session is part of,
     * you should use {@link Cluster#shutdown} instead (which will call this
     * method for all session but also release some additional resources).
     * <p>
     * Note that this method is not thread safe in the sense that if another
     * shutdown is perform in parallel, it might return {@code true} even if
     * the instance is not yet fully shutdown.
     *
     * @param timeout how long to wait for the session to shutdown.
     * @param unit the unit for the timeout.
     * @return {@code true} if the session has been properly shutdown within
     * the {@code timeout}, {@code false} otherwise.
     */
  public boolean shutdown(long timeout, TimeUnit unit);

  /**
     * Returns the {@code Cluster} object this session is part of.
     *
     * @return the {@code Cluster} object this session is part of.
     */
  public Cluster getCluster();
}
>>>>>>> /usr/src/app/output/datastax/java-driver/aede0e8d32b985ff839f607714f128c528a91e2f/driver-core/src/main/java/com/datastax/driver/core/Session.java/right.java
