package com.lambdaworks.redis.cluster;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.lambdaworks.redis.*;
import com.lambdaworks.redis.ReadFrom;
import com.lambdaworks.redis.RedisChannelHandler;
import com.lambdaworks.redis.RedisChannelWriter;
import com.lambdaworks.redis.RedisException;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.models.role.RedisInstance;
import com.lambdaworks.redis.models.role.RedisNodeDescription;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.*;
import java.util.concurrent.ExecutionException;
import com.google.common.collect.*;
import com.lambdaworks.redis.api.StatefulConnection;
import com.lambdaworks.redis.api.StatefulRedisConnection;

/**
 * Connection provider with built-in connection caching.
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class PooledClusterConnectionProvider<K, V> implements ClusterConnectionProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledClusterConnectionProvider.class);

    // Contains NodeId-identified and HostAndPort-identified connections.
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private final LoadingCache<ConnectionKey, RedisAsyncConnectionImpl<K, V>> connections;
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    private final LoadingCache<ConnectionKey, StatefulRedisConnection<K, V>> connections;
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    private final boolean debugEnabled;
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private final RedisAsyncConnectionImpl<K, V> writers[] = new RedisAsyncConnectionImpl[SlotHash.SLOT_COUNT];
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    private final StatefulRedisConnection<K, V> writers[] = new StatefulRedisConnection[SlotHash.SLOT_COUNT];
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private final RedisAsyncConnectionImpl<K, V> readers[][] = new RedisAsyncConnectionImpl[SlotHash.SLOT_COUNT][];
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    private final StatefulRedisConnection<K, V> readers[][] = new StatefulRedisConnection[SlotHash.SLOT_COUNT][];
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    private final RedisClusterClient redisClusterClient;
    private Partitions partitions;
    private boolean autoFlushCommands = true;
    private Object stateLock = new Object();
    private ReadFrom readFrom;
    public PooledClusterConnectionProvider(RedisClusterClient redisClusterClient, RedisChannelWriter<K, V> clusterWriter,
            RedisCodec<K, V> redisCodec) {
        this.redisClusterClient = redisClusterClient;
        this.debugEnabled = logger.isDebugEnabled();
        this.connections = CacheBuilder.newBuilder().build(
                new ConnectionFactory<K, V>(redisClusterClient, redisCodec, clusterWriter));
    }
    @Override
    public
    @Override
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @SuppressWarnings({ "unchecked", "hiding", "rawtypes" }) RedisAsyncConnectionImpl<K, V> getConnection(Intent intent, int slot) {
        if (debugEnabled) {
            logger.debug("getConnection(" + intent + ", " + slot + ")");
        }

        if (intent == Intent.READ && readFrom != null) {
            return getReadConnection(slot);
        }
        return getWriteConnection(slot);
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
    @SuppressWarnings({ "unchecked", "hiding", "rawtypes" })  getConnection(Intent intent, int slot) 
=======
    @SuppressWarnings({ "unchecked", "hiding", "rawtypes" }) StatefulRedisConnection<K, V> getConnection(Intent intent, int slot) {
        if (debugEnabled) {
            logger.debug("getConnection(" + intent + ", " + slot + ")");
        }

        try {
            if (intent == Intent.READ && readFrom != null) {
                return getReadConnection(slot);
            }
            return getWriteConnection(slot);
        } catch (RedisException e) {
            throw e;
        } catch (UncheckedExecutionException | ExecutionException e) {
            throw new RedisException(e.getCause());
        } catch (RuntimeException e) {
            throw new RedisException(e);
        }
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    protected RedisAsyncConnectionImpl<K, V> getWriteConnection(int slot) {
        RedisAsyncConnectionImpl<K, V> writer;// avoid races when reconfiguring partitions.
        synchronized (stateLock) {
            writer = writers[slot];
        }

        if (writer == null) {
            RedisClusterNode partition = partitions.getPartitionBySlot(slot);
            if (partition == null) {
                throw new RedisException("Cannot determine a partition for slot " + slot + " (Partitions: " + partitions + ")");
            }

            try {
                // Use always host and port for slot-oriented operations. We don't want to get reconnected on a different
                // host because the nodeId can be handled by a different host.
                RedisURI uri = partition.getUri();
                ConnectionKey key = new ConnectionKey(Intent.WRITE, uri.getHost(), uri.getPort());
                return writers[slot] = connections.get(key);
            } catch (UncheckedExecutionException e) {
                throw new RedisException(e.getCause());
            } catch (Exception e) {
                throw new RedisException(e);
            }
        }
        return writer;
    }

    protected RedisAsyncConnectionImpl<K, V> getReadConnection(int slot) {
        RedisAsyncConnectionImpl<K, V> readerCandidates[];// avoid races when reconfiguring partitions.
        synchronized (stateLock) {
            readerCandidates = readers[slot];
        }

        if (readerCandidates == null) {
            RedisClusterNode master = partitions.getPartitionBySlot(slot);
            if (master == null) {
                throw new RedisException("Cannot determine a partition to read for slot " + slot + " (Partitions: "
                        + partitions + ")");
            }

            final List<RedisNodeDescription> candidates = getReadCandidates(master);
            List<RedisNodeDescription> selection = readFrom.select(new ReadFrom.Nodes() {
                @Override
                public List<RedisNodeDescription> getNodes() {
                    return candidates;
                }

                @Override
                public Iterator<RedisNodeDescription> iterator() {
                    return candidates.iterator();
                }
            });

            if (selection.isEmpty()) {
                throw new RedisException("Cannot determine a partition to read for slot " + slot + " (Partitions: "
                        + partitions + ") with setting " + readFrom);
            }

            readerCandidates = getReadFromConnections(selection);
            readers[slot] = readerCandidates;
        }

        // try working connections at first
        for (RedisAsyncConnectionImpl<K, V> readerCandidate : readerCandidates) {
            if (!readerCandidate.isOpen()) {
                continue;
            }
            return readerCandidate;
        }

        // fall-back to the first connection for same behavior as writing
        return readerCandidates[0];
    }

    private RedisAsyncConnectionImpl<K, V>[] getReadFromConnections(List<RedisNodeDescription> selection) {
        RedisAsyncConnectionImpl<K, V>[] readerCandidates;
        try {
            // Use always host and port for slot-oriented operations. We don't want to get reconnected on a different
            // host because the nodeId can be handled by a different host.

            readerCandidates = new RedisAsyncConnectionImpl[selection.size()];

            for (int i = 0; i < selection.size(); i++) {
                RedisNodeDescription redisClusterNode = selection.get(i);

                RedisURI uri = redisClusterNode.getUri();
                ConnectionKey key = new ConnectionKey(redisClusterNode.getRole() == RedisInstance.Role.MASTER ? Intent.WRITE
                        : Intent.READ, uri.getHost(), uri.getPort());

                readerCandidates[i] = connections.get(key);
            }

        } catch (UncheckedExecutionException e) {
            throw new RedisException(e.getCause());
        } catch (Exception e) {
            throw new RedisException(e);
        }
        return readerCandidates;
    }
            @Override
            public SocketAddress get() {

                if (connectionKey.nodeId != null) {
                    return getSocketAddress(connectionKey.nodeId);
                }
                return new InetSocketAddress(connectionKey.host, connectionKey.port);
            }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    protected StatefulRedisConnection<K, V> getWriteConnection(int slot) throws ExecutionException {
        StatefulRedisConnection<K, V> writer;// avoid races when reconfiguring partitions.
        synchronized (stateLock) {
            writer = writers[slot];
        }

        if (writer == null) {
            RedisClusterNode partition = partitions.getPartitionBySlot(slot);
            if (partition == null) {
                throw new RedisException("Cannot determine a partition for slot " + slot + " (Partitions: " + partitions + ")");
            }

            // Use always host and port for slot-oriented operations. We don't want to get reconnected on a different
            // host because the nodeId can be handled by a different host.
            RedisURI uri = partition.getUri();
            ConnectionKey key = new ConnectionKey(Intent.WRITE, uri.getHost(), uri.getPort());
            return writers[slot] = connections.get(key);

        }
        return writer;
    }

    protected StatefulRedisConnection<K, V> getReadConnection(int slot) throws ExecutionException {
        StatefulRedisConnection<K, V> readerCandidates[];// avoid races when reconfiguring partitions.
        synchronized (stateLock) {
            readerCandidates = readers[slot];
        }

        if (readerCandidates == null) {
            RedisClusterNode master = partitions.getPartitionBySlot(slot);
            if (master == null) {
                throw new RedisException("Cannot determine a partition to read for slot " + slot + " (Partitions: "
                        + partitions + ")");
            }

            List<RedisNodeDescription> candidates = getReadCandidates(master);
            List<RedisNodeDescription> selection = readFrom.select(new ReadFrom.Nodes() {
                @Override
                public List<RedisNodeDescription> getNodes() {
                    return candidates;
                }

                @Override
                public Iterator<RedisNodeDescription> iterator() {
                    return candidates.iterator();
                }
            });

            if (selection.isEmpty()) {
                throw new RedisException("Cannot determine a partition to read for slot " + slot + " (Partitions: "
                        + partitions + ") with setting " + readFrom);
            }

            readerCandidates = getReadFromConnections(selection);
            readers[slot] = readerCandidates;
        }

        // try working connections at first
        for (StatefulRedisConnection<K, V> readerCandidate : readerCandidates) {
            if (!readerCandidate.isOpen()) {
                continue;
            }
            return readerCandidate;
        }

        // fall-back to the first connection for same behavior as writing
        return readerCandidates[0];
    }

    private StatefulRedisConnection<K, V>[] getReadFromConnections(List<RedisNodeDescription> selection)
            throws ExecutionException {
        StatefulRedisConnection<K, V>[] readerCandidates;
        // Use always host and port for slot-oriented operations. We don't want to get reconnected on a different
        // host because the nodeId can be handled by a different host.

        readerCandidates = new StatefulRedisConnection[selection.size()];

        for (int i = 0; i < selection.size(); i++) {
            RedisNodeDescription redisClusterNode = selection.get(i);

            RedisURI uri = redisClusterNode.getUri();
            ConnectionKey key = new ConnectionKey(redisClusterNode.getRole() == RedisInstance.Role.MASTER ? Intent.WRITE
                    : Intent.READ, uri.getHost(), uri.getPort());

            readerCandidates[i] = connections.get(key);
        }

        return readerCandidates;
    }

    private Supplier<SocketAddress> getSocketAddressSupplier(final ConnectionKey connectionKey) {
        return () -> {
            if (connectionKey.nodeId != null) {
                return getSocketAddress(connectionKey.nodeId);
            }
            return new InetSocketAddress(connectionKey.host, connectionKey.port);
        };
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    private List<RedisNodeDescription> getReadCandidates(RedisClusterNode master) {
        List<RedisNodeDescription> candidates = Lists.newArrayList();

        for (RedisClusterNode partition : partitions) {
            if (master.getNodeId().equals(partition.getNodeId()) || master.getNodeId().equals(partition.getSlaveOf())) {
                candidates.add(partition);
            }
        }
        return candidates;
    }
    @Override
    public
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override RedisAsyncConnectionImpl<K, V> getConnection(Intent intent, String nodeId) {
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
    @Override  getConnection(Intent intent, String nodeId) {
=======
    @Override StatefulRedisConnection<K, V> getConnection(Intent intent, String nodeId) {
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
        if (debugEnabled) {
            logger.debug("getConnection(" + intent + ", " + nodeId + ")");
        }

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
        try {
            ConnectionKey key = new ConnectionKey(intent, nodeId);
            return connections.get(key);
        } catch (Exception e) {
            throw new RedisException(e);
        }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
        try {
            ConnectionKey key = new ConnectionKey(intent, nodeId);
            return connections.get(key);
        } catch (UncheckedExecutionException | ExecutionException e) {
            throw new RedisException(e.getCause());
        }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override
    @SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
    public RedisAsyncConnectionImpl<K, V> getConnection(Intent intent, String host, int port) {
        try {
            if (debugEnabled) {
                logger.debug("getConnection(" + intent + ", " + host + ", " + port + ")");
            }

            if (validateClusterNodeMembership()) {
                RedisClusterNode redisClusterNode = getPartition(host, port);

                if (redisClusterNode == null) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(host, port);
                    throw invalidConnectionPoint(hostAndPort.toString());
                }
            }

            ConnectionKey key = new ConnectionKey(intent, host, port);
            return connections.get(key);
        } catch (UncheckedExecutionException e) {
            throw new RedisException(e.getCause());
        } catch (Exception e) {
            throw new RedisException(e);
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    @Override
    @SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
    public StatefulRedisConnection<K, V> getConnection(Intent intent, String host, int port) {
        try {
            if (debugEnabled) {
                logger.debug("getConnection(" + intent + ", " + host + ", " + port + ")");
            }

            if (validateClusterNodeMembership()) {
                RedisClusterNode redisClusterNode = getPartition(host, port);

                if (redisClusterNode == null) {
                    HostAndPort hostAndPort = HostAndPort.fromParts(host, port);
                    throw invalidConnectionPoint(hostAndPort.toString());
                }
            }

            ConnectionKey key = new ConnectionKey(intent, host, port);
            return connections.get(key);
        } catch (RedisException e) {
            throw e;
        } catch (UncheckedExecutionException | ExecutionException e) {
            throw new RedisException(e.getCause());
        } catch (RuntimeException e) {
            throw new RedisException(e);
        }
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    private RedisClusterNode getPartition(String host, int port) {
        for (RedisClusterNode partition : partitions) {
            RedisURI uri = partition.getUri();
            if (port == uri.getPort() && host.equals(uri.getHost())) {
                return partition;
            }
        }
        return null;
    }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override
    public void close() {
        ImmutableMap<ConnectionKey, RedisAsyncConnectionImpl<K, V>> copy = ImmutableMap.copyOf(this.connections.asMap());
        this.connections.invalidateAll();
        resetFastConnectionCache();
        for (RedisAsyncConnectionImpl<K, V> kvRedisAsyncConnection : copy.values()) {
            if (kvRedisAsyncConnection.isOpen()) {
                kvRedisAsyncConnection.close();
            }
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    @Override
    public void close() {
        ImmutableMap<ConnectionKey, StatefulRedisConnection<K, V>> copy = ImmutableMap.copyOf(this.connections.asMap());
        this.connections.invalidateAll();
        resetFastConnectionCache();
        for (StatefulRedisConnection<K, V> kvRedisAsyncConnection : copy.values()) {
            if (kvRedisAsyncConnection.isOpen()) {
                kvRedisAsyncConnection.close();
            }
        }
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override
    public void reset() {
        ImmutableMap<ConnectionKey, RedisAsyncConnectionImpl<K, V>> copy = ImmutableMap.copyOf(this.connections.asMap());
        for (RedisAsyncConnectionImpl<K, V> kvRedisAsyncConnection : copy.values()) {
            kvRedisAsyncConnection.reset();
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    @Override
    public void reset() {
        allConnections().forEach(StatefulRedisConnection::reset);
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    /**
     * Synchronize on {@code stateLock} to initiate a happens-before relation and clear the thread caches of other threads.
     * 
     * @param partitions the new partitions.
     */
    @Override
    public void setPartitions(Partitions partitions) {
        synchronized (stateLock) {
            this.partitions = partitions;
            reconfigurePartitions();
        }
    }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private void reconfigurePartitions() {
        Set<ConnectionKey> staleConnections = getStaleConnectionKeys();

        for (ConnectionKey key : staleConnections) {
            RedisAsyncConnectionImpl<K, V> connection = connections.getIfPresent(key);
            if (connection.getChannelWriter() instanceof ClusterNodeCommandHandler) {
                ClusterNodeCommandHandler<?, ?> clusterNodeCommandHandler = (ClusterNodeCommandHandler<?, ?>) connection
                        .getChannelWriter();
                clusterNodeCommandHandler.prepareClose();
            }
        }

        resetFastConnectionCache();

        if (redisClusterClient.expireStaleConnections()) {
            closeStaleConnections();
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    private void reconfigurePartitions() {
        Set<ConnectionKey> staleConnections = getStaleConnectionKeys();

        for (ConnectionKey key : staleConnections) {
            StatefulRedisConnection<K, V> connection = connections.getIfPresent(key);

            RedisChannelHandler<K, V> redisChannelHandler = (RedisChannelHandler<K, V>) connection;

            if (redisChannelHandler.getChannelWriter() instanceof ClusterNodeCommandHandler) {
                ClusterNodeCommandHandler<?, ?> clusterNodeCommandHandler = (ClusterNodeCommandHandler<?, ?>) redisChannelHandler
                        .getChannelWriter();
                clusterNodeCommandHandler.prepareClose();
            }
        }

        resetFastConnectionCache();

        if (redisClusterClient.expireStaleConnections()) {
            closeStaleConnections();
        }
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    /**
     * Close stale connections.
     */
    @Override
    public
    @Override void closeStaleConnections() {
        logger.debug("closeStaleConnections() count before expiring: {}", getConnectionCount());

        Set<ConnectionKey> stale = getStaleConnectionKeys();

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
        for (ConnectionKey connectionKey : stale) {
            RedisAsyncConnectionImpl<K, V> connection = connections.getIfPresent(connectionKey);
            if (connection != null) {
                connections.invalidate(connectionKey);
                connection.close();
            }
        }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
        for (ConnectionKey connectionKey : stale) {
            StatefulRedisConnection<K, V> connection = connections.getIfPresent(connectionKey);
            if (connection != null) {
                connections.invalidate(connectionKey);
                connection.close();
            }
        }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java

        logger.debug("closeStaleConnections() count after expiring: {}", getConnectionCount());
    }
    /**
     * Retrieve a set of PoolKey's for all pooled connections that are within the pool but not within the {@link Partitions}.
     * 
     * @return Set of {@link ConnectionKey}s
     */
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private Set<ConnectionKey> getStaleConnectionKeys() {
        Map<ConnectionKey, RedisAsyncConnectionImpl<K, V>> map = Maps.newHashMap(connections.asMap());
        Set<ConnectionKey> stale = Sets.newHashSet();

        for (ConnectionKey connectionKey : map.keySet()) {

            if (connectionKey.nodeId != null && partitions.getPartitionByNodeId(connectionKey.nodeId) != null) {
                continue;
            }

            if (connectionKey.host != null && getPartition(connectionKey.host, connectionKey.port) != null) {
                continue;
            }
            stale.add(connectionKey);
        }
        return stale;
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
    private Set<ConnectionKey> getStaleConnectionKeys() 
=======
    private Set<ConnectionKey> getStaleConnectionKeys() {
        Map<ConnectionKey, StatefulRedisConnection<K, V>> map = Maps.newHashMap(connections.asMap());
        Set<ConnectionKey> stale = Sets.newHashSet();

        for (ConnectionKey connectionKey : map.keySet()) {

            if (connectionKey.nodeId != null && partitions.getPartitionByNodeId(connectionKey.nodeId) != null) {
                continue;
            }

            if (connectionKey.host != null && getPartition(connectionKey.host, connectionKey.port) != null) {
                continue;
            }
            stale.add(connectionKey);
        }
        return stale;
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    /**
     * Set auto-flush on all commands. Synchronize on {@code stateLock} to initiate a happens-before relation and clear the
     * thread caches of other threads.
     * 
     * @param autoFlush state of autoFlush.
     */
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override
    public void setAutoFlushCommands(boolean autoFlush) {
        synchronized (stateLock) {
            this.autoFlushCommands = autoFlush;
        }
        for (RedisAsyncConnectionImpl<K, V> connection : connections.asMap().values()) {
            connection.getChannelWriter().setAutoFlushCommands(autoFlush);
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    @Override
    public void setAutoFlushCommands(boolean autoFlush) {
        synchronized (stateLock) {
            this.autoFlushCommands = autoFlush;
        }

        allConnections().forEach(connection -> connection.setAutoFlushCommands(autoFlush));
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    @Override
    public void flushCommands() {
        for (RedisAsyncConnectionImpl<K, V> connection : connections.asMap().values()) {
            connection.getChannelWriter().flushCommands();
        }
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
    @Override
    public void flushCommands() {
        allConnections().forEach(StatefulConnection::flushCommands);
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    @Override
    public void setReadFrom(ReadFrom readFrom) {
        synchronized (stateLock) {
            this.readFrom = readFrom;
            Arrays.fill(readers, null);
        }
    }
    @Override
    public ReadFrom getReadFrom() {
        return this.readFrom;
    }
    /**
     *
     * @return number of connections.
     */
    protected long getConnectionCount() {
        return connections.size();
    }
    /**
     * Reset the internal connection cache. This is necessary because the {@link Partitions} have no reference to the connection
     * cache.
     *
     * Synchronize on {@code stateLock} to initiate a happens-before relation and clear the thread caches of other threads.
     */
    protected void resetFastConnectionCache() {
        synchronized (stateLock) {
            Arrays.fill(writers, null);
            Arrays.fill(readers, null);

        }
    }
    private RuntimeException invalidConnectionPoint(String message) {
        return new IllegalArgumentException("Connection to " + message
                + " not allowed. This connection point is not known in the cluster view");
    }
    private Supplier<SocketAddress> getSocketAddressSupplier(final ConnectionKey connectionKey) {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
        return new Supplier<SocketAddress>() {
            @Override
            public SocketAddress get() {

                if (connectionKey.nodeId != null) {
                    return getSocketAddress(connectionKey.nodeId);
                }
                return new InetSocketAddress(connectionKey.host, connectionKey.port);
            }

        };
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
=======
        return () -> {
            if (connectionKey.nodeId != null) {
                return getSocketAddress(connectionKey.nodeId);
            }
            return new InetSocketAddress(connectionKey.host, connectionKey.port);
        };
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java
    }
    protected SocketAddress getSocketAddress(String nodeId) {
        for (RedisClusterNode partition : partitions) {
            if (partition.getNodeId().equals(nodeId)) {
                return partition.getUri().getResolvedAddress();
            }
        }
        return null;
    }
    /**
     * Connection to identify a connection either by nodeId or host/port.
     */
    private static class ConnectionKey {
        private final ClusterConnectionProvider.Intent intent;
        private final String nodeId;
        private final String host;
        private final int port;

        public ConnectionKey(Intent intent, String nodeId) {
            this.intent = intent;
            this.nodeId = nodeId;
            this.host = null;
            this.port = 0;
        }

        public ConnectionKey(Intent intent, String host, int port) {
            this.intent = intent;
            this.host = host;
            this.port = port;
            this.nodeId = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ConnectionKey))
                return false;

            ConnectionKey key = (ConnectionKey) o;

            if (port != key.port)
                return false;
            if (intent != key.intent)
                return false;
            if (nodeId != null ? !nodeId.equals(key.nodeId) : key.nodeId != null)
                return false;
            return !(host != null ? !host.equals(key.host) : key.host != null);
        }

        @Override
        public int hashCode() {
            int result = intent != null ? intent.name().hashCode() : 0;
            result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
            result = 31 * result + (host != null ? host.hashCode() : 0);
            result = 31 * result + port;
            return result;
        }
    }
    private boolean validateClusterNodeMembership() {
        return redisClusterClient.getClusterClientOptions() == null
                || redisClusterClient.getClusterClientOptions().isValidateClusterNodeMembership();
    }
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
    private class ConnectionFactory<K, V> extends CacheLoader<ConnectionKey, RedisAsyncConnectionImpl<K, V>> {
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
    private class ConnectionFactory<K, V>  {
=======
    private class ConnectionFactory<K, V> extends CacheLoader<ConnectionKey, StatefulRedisConnection<K, V>> {
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java

        private final RedisClusterClient redisClusterClient;

        private final RedisCodec<K, V> redisCodec;

        private final RedisChannelWriter<K, V> clusterWriter;

        public ConnectionFactory(RedisClusterClient redisClusterClient, RedisCodec<K, V> redisCodec,
                RedisChannelWriter<K, V> clusterWriter) {
            this.redisClusterClient = redisClusterClient;
            this.redisCodec = redisCodec;
            this.clusterWriter = clusterWriter;
        }

        @Override
        public
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/left.java
        @Override RedisAsyncConnectionImpl<K, V> load(ConnectionKey key) throws Exception {

            RedisAsyncConnectionImpl<K, V> connection = null;
            if (key.nodeId != null) {
                if (partitions.getPartitionByNodeId(key.nodeId) == null) {
                    throw invalidConnectionPoint("node id " + key.nodeId);
                }

                // NodeId connections do not provide command recovery due to cluster reconfiguration
                connection = redisClusterClient.connectNode(redisCodec, key.nodeId, null, getSocketAddressSupplier(key));
            }

            if (key.host != null) {

                if (validateClusterNodeMembership()) {
                    if (getPartition(key.host, key.port) == null) {
                        throw invalidConnectionPoint(key.host + ":" + key.port);
                    }
                }

                // Host and port connections do not provide command recovery due to cluster reconfiguration
                connection = redisClusterClient.connectNode(redisCodec, key.host + ":" + key.port, clusterWriter,
                        getSocketAddressSupplier(key));
            }

            if (key.intent == Intent.READ) {
                connection.readOnly();
            }

            synchronized (stateLock) {
                connection.getChannelWriter().setAutoFlushCommands(autoFlushCommands);
            }
            return connection;
        }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/base.java
        @Override  load(ConnectionKey key) throws Exception 
=======
        @Override StatefulRedisConnection<K, V> load(ConnectionKey key) throws Exception {

            StatefulRedisConnection<K, V> connection = null;
            if (key.nodeId != null) {
                if (partitions.getPartitionByNodeId(key.nodeId) == null) {
                    throw invalidConnectionPoint("node id " + key.nodeId);
                }

                // NodeId connections do not provide command recovery due to cluster reconfiguration
                connection = redisClusterClient.connectToNode(redisCodec, key.nodeId, null, getSocketAddressSupplier(key));
            }

            if (key.host != null) {

                if (validateClusterNodeMembership()) {
                    if (getPartition(key.host, key.port) == null) {
                        throw invalidConnectionPoint(key.host + ":" + key.port);
                    }
                }

                // Host and port connections do provide command recovery due to cluster reconfiguration
                connection = redisClusterClient.connectToNode(redisCodec, key.host + ":" + key.port, clusterWriter,
                        getSocketAddressSupplier(key));
            }

            if (key.intent == Intent.READ) {
                connection.sync().readOnly();
            }

            synchronized (stateLock) {
                connection.setAutoFlushCommands(autoFlushCommands);
            }

            return connection;
        }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/PooledClusterConnectionProvider.java/right.java

    }
    // Contains NodeId-identified and HostAndPort-identified connections.
    /**
     * Synchronize on {@code stateLock} to initiate a happens-before relation and clear the thread caches of other threads.
     * 
     * @param partitions the new partitions.
     */
    /**
     * Close stale connections.
     */
    /**
     * Retrieve a set of PoolKey's for all pooled connections that are within the pool but not within the {@link Partitions}.
     * 
     * @return Set of {@link ConnectionKey}s
     */
    /**
     * Set auto-flush on all commands. Synchronize on {@code stateLock} to initiate a happens-before relation and clear the
     * thread caches of other threads.
     * 
     * @param autoFlush state of autoFlush.
     */
    protected Collection<StatefulRedisConnection<K, V>> allConnections() {
        return ImmutableList.copyOf(connections.asMap().values());
    }
    /**
     *
     * @return number of connections.
     */
    /**
     * Reset the internal connection cache. This is necessary because the {@link Partitions} have no reference to the connection
     * cache.
     *
     * Synchronize on {@code stateLock} to initiate a happens-before relation and clear the thread caches of other threads.
     */
    /**
     * Connection to identify a connection either by nodeId or host/port.
     */

}
