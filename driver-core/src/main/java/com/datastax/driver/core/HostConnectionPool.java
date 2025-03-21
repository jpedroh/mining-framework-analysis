package com.datastax.driver.core;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.collect.Lists;

/**
 * A set of connections to a live host.
 *
 * We use different strategies depending of the protocol version in use.
 */
abstract class HostConnectionPool {
  static HostConnectionPool newInstance(Host host, HostDistance hostDistance, SessionManager manager, ProtocolVersion version) throws ConnectionException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
    switch (version) {
      case V1:
      case V2:
      return new DynamicConnectionPool(host, hostDistance, manager);
      case V3:
      return new SingleConnectionPool(host, hostDistance, manager);
      default:
      throw version.unsupported();
    }
  }

  final Host host;

  volatile HostDistance hostDistance;

  protected final SessionManager manager;

  protected final AtomicReference<CloseFuture> closeFuture = new AtomicReference<CloseFuture>();

  protected HostConnectionPool(Host host, HostDistance hostDistance, SessionManager manager) {
    assert hostDistance != HostDistance.IGNORED;
    this.host = host;
    this.hostDistance = hostDistance;
    this.manager = manager;

<<<<<<< Unknown file: This is a bug in JDime.
=======
    try {
      for (int i = 0; i < options().getCoreConnectionsPerHost(hostDistance); i++) {
        l.add(manager.connectionFactory().open(this));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (RuntimeException e) {
      for (PooledConnection connection : l) {
        connection.closeAsync().force();
      }
      throw e;
    }
>>>>>>> /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java
  }

  abstract PooledConnection borrowConnection(long timeout, TimeUnit unit) throws ConnectionException, TimeoutException;

  abstract void returnConnection(PooledConnection connection);

  abstract void ensureCoreConnections();

  abstract void replaceDefunctConnection(final PooledConnection connection);

  abstract int opened();

  public final boolean isClosed() {
    return closeFuture.get() != null;
  }

  abstract int inFlightQueriesCount();

  protected abstract CloseFuture makeCloseFuture();

  public final CloseFuture closeAsync() {
    CloseFuture future = closeFuture.get();
    if (future != null) {
      return future;
    }
    future = makeCloseFuture();
    return closeFuture.compareAndSet(null, future) ? future : closeFuture.get();
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private List<CloseFuture> discardAvailableConnections() {
    if (connections == null) {
      return Lists.newArrayList(CloseFuture.immediateFuture());
    }
    List<CloseFuture> futures = new ArrayList<CloseFuture>(connections.size());
    for (final PooledConnection connection : connections) {
      CloseFuture future = connection.closeAsync();
      future.addListener(new Runnable() {
        public void run() {
          if (connection.markForTrash.compareAndSet(false, true)) {
            open.decrementAndGet();
          }
        }
      }, MoreExecutors.sameThreadExecutor());
      futures.add(future);
    }
    return futures;
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java


  static class PoolState {
    volatile String keyspace;

    public void setKeyspace(String keyspace) {
      this.keyspace = keyspace;
    }
  }
}