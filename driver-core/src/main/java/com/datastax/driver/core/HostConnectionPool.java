/*
 *      Copyright (C) 2012-2014 DataStax Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.datastax.driver.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
/**
 * A set of connections to a live host.
 *
 * We use different strategies depending of the protocol version in use.
 */
import com.google.common.collect.Lists;

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
<<<<<<< /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/left.java
||||||| /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/base.java

        this.newConnectionTask = new Runnable() {
            @Override
            public void run() {
                addConnectionIfUnderMaximum();
                scheduledForCreation.decrementAndGet();
            }
        };

        // Create initial core connections
        List<PooledConnection> l = new ArrayList<PooledConnection>(options().getCoreConnectionsPerHost(hostDistance));
        try {
            for (int i = 0; i < options().getCoreConnectionsPerHost(hostDistance); i++)
                l.add(manager.connectionFactory().open(this));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // If asked to interrupt, we can skip opening core connections, the pool will still work.
            // But we ignore otherwise cause I'm not sure we can do much better currently.
        }
        this.connections = new CopyOnWriteArrayList<PooledConnection>(l);
        this.open = new AtomicInteger(connections.size());

        logger.trace("Created connection pool to host {}", host);
=======

        this.newConnectionTask = new Runnable() {
            @Override
            public void run() {
                addConnectionIfUnderMaximum();
                scheduledForCreation.decrementAndGet();
            }
        };

        // Create initial core connections
        List<PooledConnection> l = new ArrayList<PooledConnection>(options().getCoreConnectionsPerHost(hostDistance));
        try {
            for (int i = 0; i < options().getCoreConnectionsPerHost(hostDistance); i++)
                l.add(manager.connectionFactory().open(this));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // If asked to interrupt, we can skip opening core connections, the pool will still work.
            // But we ignore otherwise cause I'm not sure we can do much better currently.
        } catch (RuntimeException e) {
            // Rethrow but make sure to properly close the connections in our temporary list.
            for (PooledConnection connection : l) {
                connection.closeAsync().force();
            }
            throw e;
        }
        this.connections = new CopyOnWriteArrayList<PooledConnection>(l);
        this.open = new AtomicInteger(connections.size());

        logger.trace("Created connection pool to host {}", host);
>>>>>>> /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java
    }

    abstract PooledConnection borrowConnection(long timeout, TimeUnit unit) throws ConnectionException, TimeoutException;

    abstract void returnConnection(PooledConnection connection);

    abstract void ensureCoreConnections();

    abstract void replaceDefunctConnection(final PooledConnection connection);

    abstract int opened();

    abstract int inFlightQueriesCount();

    protected abstract CloseFuture makeCloseFuture();

    public final boolean isClosed() {
        return closeFuture.get() != null;
    }

    public final CloseFuture closeAsync() {

        CloseFuture future = closeFuture.get();
        if (future != null)
            return future;

        future = makeCloseFuture();

        return closeFuture.compareAndSet(null, future)
<<<<<<< /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/left.java
            ? future
            : closeFuture.get(); // We raced, it's ok, return the future that was actually set
||||||| /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/base.java
             ? future
             : closeFuture.get(); // We raced, it's ok, return the future that was actually set
    }

    public int opened() {
        return open.get();
    }

    private List<CloseFuture> discardAvailableConnections() {

        List<CloseFuture> futures = new ArrayList<CloseFuture>(connections.size());
        for (final PooledConnection connection : connections) {
            CloseFuture future = connection.closeAsync();
            future.addListener(new Runnable() {
                public void run() {
                    if (connection.markForTrash.compareAndSet(false, true))
                        open.decrementAndGet();
                }
            }, MoreExecutors.sameThreadExecutor());
            futures.add(future);
        }
        return futures;
    }

    // This creates connections if we have less than core connections (if we
    // have more than core, connection will just get trash when we can).
    public void ensureCoreConnections() {
        if (isClosed())
            return;

        // Note: this process is a bit racy, but it doesn't matter since we're still guaranteed to not create
        // more connection than maximum (and if we create more than core connection due to a race but this isn't
        // justified by the load, the connection in excess will be quickly trashed anyway)
        int opened = open.get();
        for (int i = opened; i < options().getCoreConnectionsPerHost(hostDistance); i++) {
            // We don't respect MAX_SIMULTANEOUS_CREATION here because it's only to
            // protect against creating connection in excess of core too quickly
            scheduledForCreation.incrementAndGet();
            manager.blockingExecutor().submit(newConnectionTask);
        }
=======
             ? future
             : closeFuture.get(); // We raced, it's ok, return the future that was actually set
    }

    public int opened() {
        return open.get();
    }

    private List<CloseFuture> discardAvailableConnections() {
        // This can happen if creating the connections in the constructor fails
        if (connections == null)
            return Lists.newArrayList(CloseFuture.immediateFuture());

        List<CloseFuture> futures = new ArrayList<CloseFuture>(connections.size());
        for (final PooledConnection connection : connections) {
            CloseFuture future = connection.closeAsync();
            future.addListener(new Runnable() {
                public void run() {
                    if (connection.markForTrash.compareAndSet(false, true))
                        open.decrementAndGet();
                }
            }, MoreExecutors.sameThreadExecutor());
            futures.add(future);
        }
        return futures;
    }

    // This creates connections if we have less than core connections (if we
    // have more than core, connection will just get trash when we can).
    public void ensureCoreConnections() {
        if (isClosed())
            return;

        // Note: this process is a bit racy, but it doesn't matter since we're still guaranteed to not create
        // more connection than maximum (and if we create more than core connection due to a race but this isn't
        // justified by the load, the connection in excess will be quickly trashed anyway)
        int opened = open.get();
        for (int i = opened; i < options().getCoreConnectionsPerHost(hostDistance); i++) {
            // We don't respect MAX_SIMULTANEOUS_CREATION here because it's only to
            // protect against creating connection in excess of core too quickly
            scheduledForCreation.incrementAndGet();
            manager.blockingExecutor().submit(newConnectionTask);
        }
>>>>>>> /usr/src/app/output/datastax/java-driver/340f92891056fec83dc87a2b0d88dcbd4e66c6e9/driver-core/src/main/java/com/datastax/driver/core/HostConnectionPool.java/right.java
    }

    static class PoolState {
        volatile String keyspace;

        public void setKeyspace(String keyspace) {
            this.keyspace = keyspace;
        }
    }
}
