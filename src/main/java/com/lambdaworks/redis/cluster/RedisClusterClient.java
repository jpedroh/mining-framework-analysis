package com.lambdaworks.redis.cluster;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.lambdaworks.redis.cluster.ClusterTopologyRefresh.RedisUriComparator.INSTANCE;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.lambdaworks.redis.*;
import com.lambdaworks.redis.cluster.event.ClusterTopologyChangedEvent;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import com.lambdaworks.redis.protocol.CommandHandler;
import com.lambdaworks.redis.protocol.RedisCommand;
import com.lambdaworks.redis.resource.ClientResources;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.cluster.api.NodeSelectionSupport;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import com.lambdaworks.redis.cluster.api.sync.RedisAdvancedClusterCommands;
import com.lambdaworks.redis.output.ValueStreamingChannel;

/**
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
 * A scalable thread-safe <a href="http://redis.io/">Redis</a> cluster client. Multiple threads may share one connection. The
 * cluster client handles command routing based on the first key of the command and maintains a view on the cluster that is
 * available when calling the {@link #getPartitions()} method.
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
 * A scalable thread-safe <a href="http://redis.io/">Redis</a> cluster client. Multiple threads may share one connection
 * provided they avoid blocking and transactional operations such as BLPOP and MULTI/EXEC.
=======
 * A scalable thread-safe <a href="http://redis.io/">Redis</a> cluster client. Multiple threads may share one connection. The
 * cluster client handles command routing based on the first key of the command and maintains a view on the cluster that is
 * available when calling the {@link #getPartitions()} method.
 *
 * <p>
 * Connections to the cluster members are opened on the first access to the cluster node and managed by the
 * {@link StatefulRedisClusterConnection}. You should not use transactional commands on cluster connections since {@code MULTI},
 * {@code EXEC} and {@code DISCARD} have no key and cannot be assigned to a particular node.
 * </p>
 * <p>
 * The Redis cluster client provides a {@link RedisAdvancedClusterCommands sync}, {@link RedisAdvancedClusterAsyncCommands
 * async} and {@link com.lambdaworks.redis.cluster.api.rx.RedisAdvancedClusterReactiveCommands reactive} API.
 * </p>
 *
 * <p>
 * Connections to particular nodes can be obtained by {@link StatefulRedisClusterConnection#getConnection(String)} providing the
 * node id or {@link StatefulRedisClusterConnection#getConnection(String, int)} by host and port.
 * </p>
 *
 * <p>
 * <a href="http://redis.io/topics/cluster-spec#multiple-keys-operations">Multiple keys operations</a> have to operate on a key
 * that hashes to the same slot. Following commands do not need to follow that rule since they are pipelined according its hash
 * value to multiple nodes in parallel:
 * </p>
 * <ul>
 * <li>{@link RedisAdvancedClusterAsyncCommands#del(Object[]) DEL}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mget(Object[]) MGET}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mget(ValueStreamingChannel, Object[]) MGET with streaming}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#mset(Map) MSET}</li>
 * <li>{@link RedisAdvancedClusterAsyncCommands#msetnx(Map) MSETNX}</li>
 * </ul>
 *
 * <p>
 * Cluster commands can be issued to multiple hosts in parallel by using the {@link NodeSelectionSupport} API. A set of nodes is
 * selected using a {@link java.util.function.Predicate} and commands can be issued to the node selection
 * 
 * <code><pre>
   AsyncExecutions<String> ping = commands.masters().commands().ping();
   Collection<RedisClusterNode> nodes = ping.nodes();
   nodes.stream().forEach(redisClusterNode -&gt; ping.get(redisClusterNode));
 * </pre></code>
 * </p>
 *
 * {@link RedisClusterClient} is an expensive resource. Reuse this instance or the {@link ClientResources} as much as possible.
 *
 *
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java
 * 
 * <p>
 * Connections to particular nodes can be obtained by {@link RedisAdvancedClusterConnection#getConnection(String)} providing the
 * node id or {@link RedisAdvancedClusterConnection#getConnection(String, int)} by host and port.
 * </p>
 *
 * {@link RedisClusterClient} is an expensive resource. Reuse this instance or the {@link ClientResources} as much as possible.
 *
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 */
public class RedisClusterClient extends AbstractRedisClient {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(RedisClusterClient.class);

    protected AtomicBoolean clusterTopologyRefreshActivated = new AtomicBoolean(false);

    private ClusterTopologyRefresh refresh = new ClusterTopologyRefresh(this);

    private Partitions partitions;

    private Iterable<RedisURI> initialUris = ImmutableSet.of();

    private RedisClusterClient() {
        setOptions(ClusterClientOptions.create());
    }

    /**
     * Initialize the client with an initial cluster URI.
     *
     * @param initialUri initial cluster URI
     * @deprecated Use {@link #create(RedisURI)}
     */

    @Deprecated @Deprecated
    public RedisClusterClient(RedisURI initialUri) {
        this(ImmutableList.of(checkNotNull(initialUri, "RedisURI (initial uri) must not be null")));
    }

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     * @deprecated Use {@link #create(Iterable)}
     */

    @Deprecated
    public RedisClusterClient(List<RedisURI> redisURIs) {
        this(null, redisURIs);
    }

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param clientResources the client resources. If {@literal null}, the client will create a new dedicated instance of
     *        client resources and keep track of them.
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     */

    protected RedisClusterClient(ClientResources clientResources,Iterable<RedisURI> redisURIs) {
        super(clientResources);
        assertNotEmpty(redisURIs);

        this.initialUris = redisURIs;

        setDefaultTimeout(getFirstUri().getTimeout(), getFirstUri().getUnit());
        setOptions(new ClusterClientOptions.Builder().build());
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     * 
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(RedisURI redisURI) {
        assertNotNull(redisURI);
        return create(ImmutableList.of(redisURI));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     * 
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(Iterable<RedisURI> redisURIs) {
        assertNotEmpty(redisURIs);
        return new RedisClusterClient(null, redisURIs);
    }

    /**
     * Create a new client that connects to the supplied uri with default {@link ClientResources}. You can connect to different
     * Redis servers but you must supply a {@link RedisURI} on connecting.
     * 
     * @param uri the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(String uri) {
        checkArgument(uri != null, "uri must not be null");
        return create(RedisURI.create(uri));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     * 
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(ClientResources clientResources, RedisURI redisURI) {
        assertNotNull(clientResources);
        assertNotNull(redisURI);
        return create(clientResources, ImmutableList.of(redisURI));
    }

    /**
     * Create a new client that connects to the supplied uri with shared {@link ClientResources}.You need to shut down the
     * {@link ClientResources} upon shutting down your application. You can connect to different Redis servers but you must
     * supply a {@link RedisURI} on connecting.
     * 
     * @param clientResources the client resources, must not be {@literal null}
     * @param uri the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(ClientResources clientResources, String uri) {
        assertNotNull(clientResources);
        checkArgument(uri != null, "uri must not be null");
        return create(clientResources, RedisURI.create(uri));
    }

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     * 
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty
     * @return a new instance of {@link RedisClusterClient}
     */

    public static RedisClusterClient create(ClientResources clientResources, Iterable<RedisURI> redisURIs) {
        assertNotNull(clientResources);
        assertNotEmpty(redisURIs);
        return new RedisClusterClient(clientResources, redisURIs);
    }

    /**
     * Open a new synchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     *
     * @return A new connection.
     */

    @Deprecated
    public RedisAdvancedClusterCommands<String, String> connectCluster() {
        return connectCluster(newStringStringCodec());
    }

    /**
     * Open a new synchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys and
     * values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type.
     * @param <V> Value type.
     * @return A new connection.
     */

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
    @SuppressWarnings("unchecked")
    public <K, V> RedisAdvancedClusterConnection<K, V> connectCluster(RedisCodec<K, V> codec) {
        assertNotNull(codec);
        return (RedisAdvancedClusterConnection<K, V>) syncHandler(connectClusterAsyncImpl(codec),
                RedisAdvancedClusterConnection.class, RedisClusterConnection.class);
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
    @SuppressWarnings("unchecked")
    public <K, V> RedisAdvancedClusterConnection<K, V> connectCluster(RedisCodec<K, V> codec) {

        return (RedisAdvancedClusterConnection<K, V>) syncHandler(connectClusterAsyncImpl(codec),
                RedisAdvancedClusterConnection.class, RedisClusterConnection.class);
    }
=======
    @SuppressWarnings("unchecked")
    @Deprecated
    public <K, V> RedisAdvancedClusterCommands<K, V> connectCluster(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec).sync();
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java

    /**
     * Open a new asynchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     *
     * @return A new connection.
     */

    /**
     * Open a new asynchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys
     * and values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type.
     * @param <V> Value type.
     * @return A new connection.
     */

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
    public <K, V> RedisAdvancedClusterAsyncConnection<K, V> connectClusterAsync(RedisCodec<K, V> codec) {
        assertNotNull(codec);
        return connectClusterAsyncImpl(codec, getSocketAddressSupplier());
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
    public <K, V> RedisAdvancedClusterAsyncConnection<K, V> connectClusterAsync(RedisCodec<K, V> codec) {
        return connectClusterAsyncImpl(codec, getSocketAddressSupplier());
    }
=======
    @Deprecated
    public <K, V> RedisAdvancedClusterAsyncCommands<K, V> connectClusterAsync(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec).async();
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
    protected RedisAsyncConnectionImpl<String, String> connectAsyncImpl(final SocketAddress socketAddress) {
        return connectNode(newStringStringCodec(), socketAddress.toString(), null, new Supplier<SocketAddress>() {
            @Override
            public SocketAddress get() {
                return socketAddress;
            }
        });
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
=======
    protected StatefulRedisConnection<String, String> connectToNode(final SocketAddress socketAddress) {
        return connectToNode(newStringStringCodec(), socketAddress.toString(), null, new Supplier<SocketAddress>() {
            @Override
            public SocketAddress get() {
                return socketAddress;
            }
        });
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java

    /**
     * Create a connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values.
     * @param nodeId the nodeId
     * @param clusterWriter global cluster writer
     * @param socketAddressSupplier supplier for the socket address
     * 
     * @param <K> Key type.
     * @param <V> Value type.
     * @return a new connection
     */

    <K, V> RedisAsyncConnectionImpl<K, V> connectNode(RedisCodec<K, V> codec, String nodeId,
            RedisChannelWriter<K, V> clusterWriter, final Supplier<SocketAddress> socketAddressSupplier) {

        logger.debug("connectNode(" + nodeId + ")");
        Queue<RedisCommand<K, V, ?>> queue = new ArrayDeque<RedisCommand<K, V, ?>>();

        ClusterNodeCommandHandler<K, V> handler = new ClusterNodeCommandHandler<K, V>(clientOptions, clientResources, queue,
                clusterWriter);
        StatefulRedisConnectionImpl<K, V> connection = new StatefulRedisConnectionImpl<K, V>(handler, codec, timeout, unit);

        connectAsyncImpl(handler, connection, socketAddressSupplier);

        connection.registerCloseables(closeableResources, connection);

        return connection;
    }

    /**
     * Create a clustered connection with command distributor.
     *
     * @param codec the codec to use
     * @param socketAddressSupplier address supplier for initial connect and re-connect
     * @param <K> Key type.
     * @param <V> Value type.
     * @return a new connection
     */

    <K, V> RedisAdvancedClusterAsyncConnectionImpl<K, V> connectClusterAsyncImpl(RedisCodec<K, V> codec,
            final Supplier<SocketAddress> socketAddressSupplier) {

        if (partitions == null) {
            initializePartitions();
        }

        activateTopologyRefreshIfNeeded();

        logger.debug("connectCluster(" + socketAddressSupplier.get() + ")");
        Queue<RedisCommand<K, V, ?>> queue = new ArrayDeque<RedisCommand<K, V, ?>>();

        CommandHandler<K, V> handler = new CommandHandler<K, V>(clientOptions, clientResources, queue);

        ClusterDistributionChannelWriter<K, V> clusterWriter = new ClusterDistributionChannelWriter<K, V>(handler);
        PooledClusterConnectionProvider<K, V> pooledClusterConnectionProvider = new PooledClusterConnectionProvider<K, V>(this,
                clusterWriter, codec);

        clusterWriter.setClusterConnectionProvider(pooledClusterConnectionProvider);

        RedisAdvancedClusterAsyncConnectionImpl<K, V> connection = newRedisAdvancedClusterAsyncConnectionImpl(clusterWriter,
                codec, timeout, unit);

        connection.setReadFrom(ReadFrom.MASTER);

        connection.setPartitions(partitions);
        connectAsyncImpl(handler, connection, socketAddressSupplier);

        connection.registerCloseables(closeableResources, connection, clusterWriter, pooledClusterConnectionProvider);

        if (getFirstUri().getPassword() != null) {
            connection.auth(new String(getFirstUri().getPassword()));
        }

        return connection;
    }

    /**
     * Reload partitions and re-initialize the distribution table.
     */

    public void reloadPartitions() {
        if (partitions == null) {
            initializePartitions();
            partitions.updateCache();
        } else {
            Partitions loadedPartitions = loadPartitions();
            if (ClusterTopologyRefresh.isChanged(getPartitions(), loadedPartitions)) {
                List<RedisClusterNode> before = ImmutableList.copyOf(getPartitions());
                List<RedisClusterNode> after = ImmutableList.copyOf(loadedPartitions);

                getResources().eventBus().publish(new ClusterTopologyChangedEvent(before, after));
            }

            this.partitions.getPartitions().clear();
            this.partitions.getPartitions().addAll(loadedPartitions.getPartitions());
            this.partitions.reload(loadedPartitions.getPartitions());
        }

        updatePartitionsInConnections();
    }

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
    protected void updatePartitionsInConnections() {

        forEachClusterConnection(new Predicate<RedisAdvancedClusterAsyncConnectionImpl<?, ?>>() {
            @Override
            public boolean apply(RedisAdvancedClusterAsyncConnectionImpl<?, ?> input) {
                input.setPartitions(partitions);
                return true;
            }
        });
    }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
=======
    protected void updatePartitionsInConnections() {

        forEachClusterConnection(input -> {
            input.setPartitions(partitions);
        });
    }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java

    protected void initializePartitions() {

        Partitions loadedPartitions = loadPartitions();
        this.partitions = loadedPartitions;
    }

    /**
     * Retrieve the cluster view. Partitions are shared amongst all connections opened by this client instance.
     *
     * @return the partitions.
     */

    public Partitions getPartitions() {
        if (partitions == null) {
            initializePartitions();
        }
        return partitions;
    }

    /**
     * Retrieve partitions. Nodes within {@link Partitions} are ordered by latency. Lower latency nodes come first.
     * 
     * @return Partitions
     */

    protected Partitions loadPartitions() {

        Map<RedisURI, Partitions> partitions = refresh.loadViews(initialUris);

        if (partitions.isEmpty()) {
            throw new RedisException("Cannot retrieve initial cluster partitions from initial URIs " + initialUris);
        }

        Partitions loadedPartitions = partitions.values().iterator().next();
        RedisURI viewedBy = refresh.getViewedBy(partitions, loadedPartitions);

        for (RedisClusterNode partition : loadedPartitions) {
            if (viewedBy != null && viewedBy.getPassword() != null) {
                partition.getUri().setPassword(new String(viewedBy.getPassword()));
            }
        }

        activateTopologyRefreshIfNeeded();

        return loadedPartitions;
    }

    private void activateTopologyRefreshIfNeeded() {
        if (getOptions() instanceof ClusterClientOptions) {
            ClusterClientOptions options = (ClusterClientOptions) getOptions();
            if (options.isRefreshClusterView()) {
                synchronized (clusterTopologyRefreshActivated) {
                    if (!clusterTopologyRefreshActivated.get()) {
                        final Runnable r = new ClusterTopologyRefreshTask();
                        genericWorkerPool.scheduleAtFixedRate(r, options.getRefreshPeriod(), options.getRefreshPeriod(),
                                options.getRefreshPeriodUnit());
                        clusterTopologyRefreshActivated.set(true);
                    }
                }
            }
        }
    }

    /**
     * Check if the {@link #genericWorkerPool} is active
     *
     * @return false if the worker pool is terminating, shutdown or terminated
     */

    protected boolean isEventLoopActive() {
        if (genericWorkerPool.isShuttingDown() || genericWorkerPool.isShutdown() || genericWorkerPool.isTerminated()) {
            return false;
        }

        return true;
    }

    /**
     * Construct a new {@link RedisAsyncConnectionImpl}. Can be overridden in order to construct a subclass of
     * {@link RedisAsyncConnectionImpl}. These connections are the "inner" connections used by the
     * {@link ClusterDistributionChannelWriter}.
     *
     * @param channelWriter the channel writer
     * @param codec the codec to use
     * @param timeout Timeout value
     * @param unit Timeout unit
     * @param <K> Key type.
     * @param <V> Value type.
     * @return RedisAsyncConnectionImpl&lt;K, V&gt; instance
     */

    protected <K, V> RedisAsyncConnectionImpl<K, V> newRedisAsyncConnectionImpl(RedisChannelWriter<K, V> channelWriter,
            RedisCodec<K, V> codec, long timeout, TimeUnit unit) {
        return new RedisAsyncConnectionImpl<K, V>(channelWriter, codec, timeout, unit);
    }

    protected <K, V> RedisAdvancedClusterAsyncConnectionImpl<K, V> newRedisAdvancedClusterAsyncConnectionImpl(
            RedisChannelWriter<K, V> channelWriter, RedisCodec<K, V> codec, long timeout, TimeUnit unit) {
        return new RedisAdvancedClusterAsyncConnectionImpl<K, V>(channelWriter, codec, timeout, unit);
    }

    protected RedisURI getFirstUri() {
        assertNotEmpty(initialUris);
        Iterator<RedisURI> iterator = initialUris.iterator();
        return iterator.next();
    }

    private Supplier<SocketAddress> getSocketAddressSupplier() {
        return new Supplier<SocketAddress>() {
            @Override
            public SocketAddress get() {
                if (partitions != null) {
                    List<RedisClusterNode> ordered = getOrderedPartitions(partitions);

                    for (RedisClusterNode partition : ordered) {
                        if (partition.getUri() != null && partition.getUri().getResolvedAddress() != null) {
                            return partition.getUri().getResolvedAddress();
                        }
                    }
                }

                return getFirstUri().getResolvedAddress();
            }
        };
    }

    private List<RedisClusterNode> getOrderedPartitions(Iterable<RedisClusterNode> clusterNodes) {
        List<RedisClusterNode> ordered = Lists.newArrayList(clusterNodes);
        Collections.sort(ordered, new Comparator<RedisClusterNode>() {
            @Override
            public int compare(RedisClusterNode o1, RedisClusterNode o2) {
                return INSTANCE.compare(o1.getUri(), o2.getUri());
            }
        });
        return ordered;
    }

    protected Utf8StringCodec newStringStringCodec() {
        return new Utf8StringCodec();
    }

    /**
     * Sets the new cluster topology. The partitions are not applied to existing connections.
     * 
     * @param partitions partitions object
     */

    public void setPartitions(Partitions partitions) {
        this.partitions = partitions;
    }

    /**
     * Returns the {@link ClientResources} which are used with that client.
     * 
     * @return the {@link ClientResources} for this client
     */

    public ClientResources getResources() {
        return clientResources;
    }

    protected void forEachClusterConnection(Predicate<RedisAdvancedClusterAsyncConnectionImpl<?, ?>> function) {

        forEachCloseable(new Predicate<Closeable>() {
            @Override
            public boolean apply(Closeable input) {
                return input instanceof RedisAdvancedClusterAsyncConnectionImpl;
            }
        }, function);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Closeable> void forEachCloseable(Predicate<? super Closeable> selector, Predicate<T> function) {
        for (Closeable c : closeableResources) {
            if (selector.apply(c)) {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
                function.apply((T) c);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
                ;
=======
                function.accept((T) c);
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java
            }
        }
    }

    /**
     * Set the {@link ClusterClientOptions} for the client.
     * 
     * @param clientOptions client options for the client and connections that are created after setting the options
     */

    public void setOptions(ClusterClientOptions clientOptions) {
        super.setOptions(clientOptions);
    }

    ClusterClientOptions getClusterClientOptions() {
        if (getOptions() instanceof ClusterClientOptions) {
            return (ClusterClientOptions) getOptions();
        }
        return null;
    }

    private class ClusterTopologyRefreshTask implements Runnable {

        public ClusterTopologyRefreshTask() {
        }

        @Override
        public
        @Override void run() {
            logger.debug("ClusterTopologyRefreshTask.run()");
            if (isEventLoopActive() && getClusterClientOptions() != null) {
                if (!getClusterClientOptions().isRefreshClusterView()) {
                    logger.debug("ClusterTopologyRefreshTask is disabled");
                    return;
                }
            } else {
                logger.debug("ClusterTopologyRefreshTask is disabled");
                return;
            }

            Iterable<RedisURI> seed;
            if (partitions == null || partitions.size() == 0) {
                seed = RedisClusterClient.this.initialUris;
            } else {
                List<RedisURI> uris = Lists.newArrayList();
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
                for (RedisClusterNode partition : getOrderedPartitions(partitions)) {
                    uris.add(partition.getUri());
                }
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
=======
                for (RedisClusterNode partition : ClusterTopologyRefresh.createSortedList(partitions)) {
                    uris.add(partition.getUri());
                }
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java
                seed = uris;
            }

            logger.debug("ClusterTopologyRefreshTask requesting partitions from {}", seed);
            Map<RedisURI, Partitions> partitions = refresh.loadViews(seed);
            List<Partitions> values = Lists.newArrayList(partitions.values());
            if (!values.isEmpty() && ClusterTopologyRefresh.isChanged(getPartitions(), values.get(0))) {
                logger.debug("Using a new cluster topology");

                List<RedisClusterNode> before = ImmutableList.copyOf(getPartitions());
                List<RedisClusterNode> after = ImmutableList.copyOf(values.get(0).getPartitions());

                getResources().eventBus().publish(new ClusterTopologyChangedEvent(before, after));

                getPartitions().reload(values.get(0).getPartitions());
                updatePartitionsInConnections();

                if (isEventLoopActive() && expireStaleConnections()) {
                    genericWorkerPool.submit(new CloseStaleConnectionsTask());
                }
            }
        }
    }

    private class CloseStaleConnectionsTask implements Runnable {
        @Override
        public
        @Override void run() {
            if (isEventLoopActive() && expireStaleConnections()) {

<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
                forEachClusterConnection(new Predicate<RedisAdvancedClusterAsyncConnectionImpl<?, ?>>() {
                    @Override
                    public boolean apply(RedisAdvancedClusterAsyncConnectionImpl<?, ?> input) {

                        ClusterDistributionChannelWriter<?, ?> writer = (ClusterDistributionChannelWriter<?, ?>) input
                                .getChannelWriter();
                        writer.getClusterConnectionProvider().closeStaleConnections();
                        return true;
                    }
                });
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
                forEachClusterConnection;
=======
                forEachClusterConnection(input -> {
                    ClusterDistributionChannelWriter<?, ?> writer = (ClusterDistributionChannelWriter<?, ?>) input
                            .getChannelWriter();
                    writer.getClusterConnectionProvider().closeStaleConnections();
                });
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java
            }
        }
    }

    boolean expireStaleConnections() {
        return getClusterClientOptions() == null || getClusterClientOptions().isCloseStaleConnections();
    }

    private static <K, V> void assertNotNull(RedisCodec<K, V> codec) {
        checkArgument(codec != null, "RedisCodec must not be null");
    }

    private static void assertNotEmpty(Iterable<RedisURI> redisURIs) {
        checkArgument(redisURIs != null, "RedisURIs must not be null");
        checkArgument(redisURIs.iterator().hasNext(), "RedisURIs must not be empty");
    }

    private static void assertNotNull(RedisURI redisURI) {
        checkArgument(redisURI != null, "RedisURI must not be null");
    }

    private static void assertNotNull(ClientResources clientResources) {
        checkArgument(clientResources != null, "ClientResources must not be null");
    }

    /**
     * Initialize the client with an initial cluster URI.
     *
     * @param initialUri initial cluster URI
     * @deprecated Use {@link #create(RedisURI)}
     */

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     * @deprecated Use {@link #create(Iterable)}
     */

    /**
     * Initialize the client with a list of cluster URI's. All uris are tried in sequence for connecting initially to the
     * cluster. If any uri is successful for connection, the others are not tried anymore. The initial uri is needed to discover
     * the cluster structure for distributing the requests.
     *
     * @param clientResources the client resources. If {@literal null}, the client will create a new dedicated instance of
     *        client resources and keep track of them.
     * @param redisURIs iterable of initial {@link RedisURI cluster URIs}. Must not be {@literal null} and not empty.
     */

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with default {@link ClientResources}. You can
     * connect to different Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Create a new client that connects to the supplied uri with default {@link ClientResources}. You can connect to different
     * Redis servers but you must supply a {@link RedisURI} on connecting.
     *
     * @param uri the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURI the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Create a new client that connects to the supplied uri with shared {@link ClientResources}.You need to shut down the
     * {@link ClientResources} upon shutting down your application. You can connect to different Redis servers but you must
     * supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param uri the Redis URI, must not be {@literal null}
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Create a new client that connects to the supplied {@link RedisURI uri} with shared {@link ClientResources}. You need to
     * shut down the {@link ClientResources} upon shutting down your application.You can connect to different Redis servers but
     * you must supply a {@link RedisURI} on connecting.
     *
     * @param clientResources the client resources, must not be {@literal null}
     * @param redisURIs one or more Redis URI, must not be {@literal null} and not empty
     * @return a new instance of {@link RedisClusterClient}
     */

    /**
     * Connect to a Redis Cluster and treat keys and values as UTF-8 strings.
     * 
     * @return A new stateful Redis Cluster connection
     */

    public StatefulRedisClusterConnection<String, String> connect() {
        return connect(newStringStringCodec());
    }

    /**
     * Connect to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys and values.
     * 
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new stateful Redis Cluster connection
     */

    @SuppressWarnings("unchecked")
    public <K, V> StatefulRedisClusterConnection<K, V> connect(RedisCodec<K, V> codec) {
        return connectClusterImpl(codec);
    }

    /**
     * Open a new synchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     * 
     * @return A new connection
     * @deprecated Use {@code connect().sync()}
     */

    /**
     * Open a new synchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys and
     * values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     * @deprecated @deprecated Use {@code connect(codec).sync()}
     */

    /**
     * Open a new asynchronous connection to a Redis Cluster that treats keys and values as UTF-8 strings.
     *
     * @return A new connection
     * @deprecated Use {@code connect().async()}
     */

    @Deprecated
    public RedisAdvancedClusterAsyncCommands<String, String> connectClusterAsync() {
        return connectClusterImpl(newStringStringCodec()).async();
    }

    /**
     * Open a new asynchronous connection to a Redis Cluster. Use the supplied {@link RedisCodec codec} to encode/decode keys
     * and values.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     * @deprecated @deprecated Use {@code connect(codec).async()}
     */

    /**
     * Create a connection to a redis socket address.
     *
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param nodeId the nodeId
     * @param clusterWriter global cluster writer
     * @param socketAddressSupplier supplier for the socket address
     * 
     * @param <K> Key type
     * @param <V> Value type
     * @return A new connection
     */

    <K, V> StatefulRedisConnection<K, V> connectToNode(RedisCodec<K, V> codec, String nodeId,
            RedisChannelWriter<K, V> clusterWriter, final Supplier<SocketAddress> socketAddressSupplier) {

        assertNotNull(codec);
        assertNotEmpty(initialUris);

        checkArgument(socketAddressSupplier != null, "SocketAddressSupplier must not be null");

        logger.debug("connectNode(" + nodeId + ")");
        Queue<RedisCommand<K, V, ?>> queue = new ArrayDeque<>();

        ClusterNodeCommandHandler<K, V> handler = new ClusterNodeCommandHandler<K, V>(clientOptions, getResources(), queue,
                clusterWriter);
        StatefulRedisConnectionImpl<K, V> connection = new StatefulRedisConnectionImpl<K, V>(handler, codec, timeout, unit);

        connectAsyncImpl(handler, connection, socketAddressSupplier);

        connection.registerCloseables(closeableResources, connection);

        RedisURI redisURI = initialUris.iterator().next();
        if (redisURI.getPassword() != null && redisURI.getPassword().length != 0) {
            connection.async().auth(new String(redisURI.getPassword()));
        }

        return connection;
    }

    /**
     * Create a clustered connection with command distributor.
     * 
     * @param codec Use this codec to encode/decode keys and values, must not be {@literal null}
     * @param <K> Key type
     * @param <V> Value type
     * @return a new connection
     */

    <K, V> StatefulRedisClusterConnectionImpl<K, V> connectClusterImpl(RedisCodec<K, V> codec) {

        if (partitions == null) {
            initializePartitions();
        }

        activateTopologyRefreshIfNeeded();

        logger.debug("connectCluster(" + initialUris + ")");
        Queue<RedisCommand<K, V, ?>> queue = new ArrayDeque<RedisCommand<K, V, ?>>();

        Supplier<SocketAddress> socketAddressSupplier = getSocketAddressSupplier();

        CommandHandler<K, V> handler = new CommandHandler<K, V>(clientOptions, clientResources, queue);

        ClusterDistributionChannelWriter<K, V> clusterWriter = new ClusterDistributionChannelWriter<K, V>(handler);
        PooledClusterConnectionProvider<K, V> pooledClusterConnectionProvider = new PooledClusterConnectionProvider<K, V>(this,
                clusterWriter, codec);

        clusterWriter.setClusterConnectionProvider(pooledClusterConnectionProvider);

        StatefulRedisClusterConnectionImpl<K, V> connection = new StatefulRedisClusterConnectionImpl<>(clusterWriter,
                codec, timeout, unit);

        connection.setReadFrom(ReadFrom.MASTER);

        connection.setPartitions(partitions);

        boolean connected = false;
        RedisException causingException = null;
        int connectionAttempts = partitions.size();

        for (int i = 0; i < connectionAttempts; i++) {
            try {
                connectAsyncImpl(handler, connection, socketAddressSupplier);
                connected = true;
                break;
            } catch (RedisException e) {
                logger.warn(e.getMessage());
                causingException = e;
            }
        }

        if (!connected) {
            connection.close();
            throw causingException;
        }

        connection.registerCloseables(closeableResources, connection, clusterWriter, pooledClusterConnectionProvider);

        if (getFirstUri().getPassword() != null) {
            connection.async().auth(new String(getFirstUri().getPassword()));
        }

        return connection;
    }

    /**
     * Retrieve the cluster view. Partitions are shared amongst all connections opened by this client instance.
     *
     * @return the partitions.
     */

    /**
     * Retrieve partitions. Nodes within {@link Partitions} are ordered by latency. Lower latency nodes come first.
     * 
     * @return Partitions
     */

    /**
     * Check if the {@link #genericWorkerPool} is active
     * 
     * @return false if the worker pool is terminating, shutdown or terminated
     */

    /**
     * Sets the new cluster topology. The partitions are not applied to existing connections.
     * 
     * @param partitions partitions object
     */

    /**
     * Returns the {@link ClientResources} which are used with that client.
     *
     * @return the {@link ClientResources} for this client
     */

    protected void forEachClusterConnection(Consumer<StatefulRedisClusterConnectionImpl<?, ?>> function) {
        forEachCloseable(input -> input instanceof StatefulRedisClusterConnectionImpl, function);
    }

    protected <T extends Closeable> void forEachCloseable(Predicate<? super Closeable> selector, Consumer<T> function) {
        for (Closeable c : closeableResources) {
            if (selector.test(c)) {
<<<<<<< /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/left.java
                function.apply((T) c);
||||||| /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/base.java
                ;
=======
                function.accept((T) c);
>>>>>>> /usr/src/app/output/lettuce-io/lettuce-core/dea8f66f846bf37494c18d1ca6036edd4a2f7898/src/main/java/com/lambdaworks/redis/cluster/RedisClusterClient.java/right.java
            }
        }
    }

    /**
     * Set the {@link ClusterClientOptions} for the client.
     * 
     * @param clientOptions client options for the client and connections that are created after setting the options
     */

}
