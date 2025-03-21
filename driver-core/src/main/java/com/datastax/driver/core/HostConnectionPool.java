package com.datastax.driver.core;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

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
  }

  abstract PooledConnection borrowConnection(long timeout, TimeUnit unit) throws ConnectionException, TimeoutException;


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private PooledConnection waitForConnection(long timeout, TimeUnit unit) throws ConnectionException, TimeoutException {
    if (timeout == 0) {
      throw new TimeoutException();
    }
    long start = System.nanoTime();
    long remaining = timeout;
    do {
      try {
        awaitAvailableConnection(remaining, unit);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        timeout = 0;
      }
      if (isClosed()) {
        throw new ConnectionException(host.getSocketAddress(), "Pool is shutdown");
      }
      int minInFlight = Integer.MAX_VALUE;
      PooledConnection leastBusy = null;
      for (PooledConnection connection : connections) {
        int inFlight = connection.inFlight.get();
        if (inFlight < minInFlight) {
          minInFlight = inFlight;
          leastBusy = connection;
        }
      }
      if (leastBusy != null) {
        while (true) {
          int inFlight = leastBusy.inFlight.get();
          if (inFlight >= leastBusy.maxAvailableStreams()) {
            break;
          }
          if (leastBusy.inFlight.compareAndSet(inFlight, inFlight + 1)) {
            return leastBusy;
          }
        }
      }
      remaining = timeout - Cluster.timeSince(start, unit);
    } while(remaining > 0);
    throw new TimeoutException();
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/5a6a084a6ed84a2742a7c2211c1071193df4c2e7/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java


  abstract void returnConnection(PooledConnection connection) 
<<<<<<< Unknown file: This is a bug in JDime.
=======
  {
    if (isClosed()) {
      close(connection);
      return;
    }
    if (connection.isDefunct()) {
      return;
    }
    int inFlight = connection.inFlight.decrementAndGet();
    if (trash.contains(connection)) {
      if (inFlight == 0 && trash.remove(connection)) {
        close(connection);
      }
    } else {
      if (connections.size() > options().getCoreConnectionsPerHost(hostDistance) && inFlight <= options().getMinSimultaneousRequestsPerConnectionThreshold(hostDistance)) {
        connection.setTrashTimeIn(options().getIdleTimeoutSeconds());
      } else {
        if (connection.maxAvailableStreams() < MIN_AVAILABLE_STREAMS) {
          replaceConnection(connection);
        } else {
          connection.cancelTrashTime();
          signalAvailableConnection();
        }
      }
    }
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/5a6a084a6ed84a2742a7c2211c1071193df4c2e7/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java


  void trashIdleConnections(long now) {
    for (PooledConnection connection : connections) {
      if (connection.getTrashTime() < now) {
        trashConnection(connection);
      }
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private boolean trashConnection(PooledConnection connection) {
    if (connection.markForTrash.compareAndSet(false, true)) {
      for ( ; ; ) {
        int opened = open.get();
        if (opened <= options().getCoreConnectionsPerHost(hostDistance)) {
          connection.markForTrash.set(false);
          connection.cancelTrashTime();
          return false;
        }
        if (open.compareAndSet(opened, opened - 1)) {
          break;
        }
      }
      doTrashConnection(connection);
    }
    return true;
  }
>>>>>>> /usr/src/app/output/datastax/java-driver/5a6a084a6ed84a2742a7c2211c1071193df4c2e7/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java


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

  static class PoolState {
    volatile String keyspace;

    public void setKeyspace(String keyspace) {
      this.keyspace = keyspace;
    }
  }
}