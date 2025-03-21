package com.datastax.driver.core;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A connection that is associated to a pool.
 */
class PooledConnection extends Connection {
  private final HostConnectionPool pool;

  /** The instant when the connection should be trashed after being idle for too long */
  private volatile long trashTime = Long.MAX_VALUE;

  /** Used in {@link DynamicConnectionPool} to handle races between two threads trying to trash the same connection */
  final AtomicBoolean markForTrash = new AtomicBoolean();

  PooledConnection(String name, InetSocketAddress address, Factory factory, HostConnectionPool pool) throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException, ClusterNameMismatchException {
    super(name, address, factory);
    this.pool = pool;
  }

  /**
     * Return the pooled connection to its pool.
     * The connection should generally not be reused after that.
     */
  public void release() {
    if (pool == null) {
      return;
    }
    pool.returnConnection(this);
  }

  @Override protected void notifyOwnerWhenDefunct(boolean hostIsDown) {
    if (pool == null) {
      return;
    }
    if (hostIsDown) {
      pool.closeAsync().force();
    } else {
      pool.replaceDefunctConnection(this);
    }
  }

  long getTrashTime() {
    return trashTime;
  }

  void cancelTrashTime() {
    trashTime = Long.MAX_VALUE;
  }

  void setTrashTimeIn(int timeoutSeconds) {
    trashTime = System.currentTimeMillis() + 1000 * timeoutSeconds;
  }
}