package com.datastax.driver.core;
import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.SetMultimap;
import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.DriverInternalError;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.policies.*;

/**
 * information and known state of a Cassandra cluster.
 * <p>
 * This is the main entry point of the driver. A simple example of access to a
 * Cassandra cluster would be:
 * <pre>
 *   Cluster cluster = Cluster.builder().addContactPoint("192.168.0.1").build();
 *   Session session = cluster.connect("db1");
 *
 *   for (Row row : session.execute("SELECT * FROM table1"))
 *       // do something ...
 * </pre>
 * <p>
 * A cluster object maintains a permanent connection to one of the cluster nodes
 * which it uses solely to maintain information on the state and current
 * topology of the cluster. Using the connection, the driver will discover all
 * the nodes currently in the cluster as well as new nodes joining the cluster
 * subsequently.
 */
public class Cluster implements Closeable {
  private static final Logger logger = LoggerFactory.getLogger(Cluster.class);

  private static final AtomicInteger CLUSTER_ID = new AtomicInteger(0);

  private static final int DEFAULT_THREAD_KEEP_ALIVE = 30;

  final Manager manager;

  /**
     * Constructs a new Cluster instance.
     * <p>
     * This constructor is mainly exposed so Cluster can be sub-classed as a means to make testing/mocking
     * easier or to "intercept" its method call. Most users shouldn't extend this class however and
     * should prefer either using the {@link #builder} or calling {@link #buildFrom} with a custom
     * Initializer.
     *
     * @param name the name to use for the cluster (this is not the Cassandra cluster name, see {@link #getClusterName}).
     * @param contactPoints the list of contact points to use for the new cluster.
     * @param configuration the configuration for the new cluster.
     */
  protected Cluster(String name, List<InetSocketAddress> contactPoints, Configuration configuration) {
    this(name, contactPoints, configuration, Collections.<Host.StateListener>emptySet());
  }

  /**
     * Constructs a new Cluster instance.
     * <p>
     * This constructor is mainly exposed so Cluster can be sub-classed as a means to make testing/mocking
     * easier or to "intercept" its method call. Most users shouldn't extend this class however and
     * should prefer using the {@link #builder}.
     *
     * @param initializer the initializer to use.
     * @see #buildFrom
     */
  protected Cluster(Initializer initializer) {
    this(initializer.getClusterName(), checkNotEmpty(initializer.getContactPoints()), initializer.getConfiguration(), initializer.getInitialListeners());
  }

  private static List<InetSocketAddress> checkNotEmpty(List<InetSocketAddress> contactPoints) {
    if (contactPoints.isEmpty()) {
      throw new IllegalArgumentException("Cannot build a cluster without contact points");
    }
    return contactPoints;
  }

  private Cluster(String name, List<InetSocketAddress> contactPoints, Configuration configuration, Collection<Host.StateListener> listeners) {
    this.manager = new Manager(name, contactPoints, configuration, listeners);
  }

  /**
     * Initialize this Cluster instance.
     *
     * This method creates an initial connection to one of the contact points
     * used to construct the {@code Cluster} instance. That connection is then
     * used to populate the cluster {@link Metadata}.
     * <p>
     * Calling this method is optional in the sense that any call to one of the
     * {@code connect} methods of this object will automatically trigger a call
     * to this method beforehand. It is thus only useful to call this method if
     * for some reason you want to populate the metadata (or test that at least
     * one contact point can be reached) without creating a first {@code
     * Session}.
     * <p>
     * Please note that this method only create one connection for metadata
     * gathering reasons. In particular, it doesn't create any connection pool.
     * Those are created when a new {@code Session} is created through
     * {@code connect}.
     * <p>
     * This method has no effect if the cluster is already initialized.
     *
     * @return this {@code Cluster} object.
     *
     * @throws NoHostAvailableException if no host amongst the contact points
     * can be reached.
     * @throws AuthenticationException if an authentication error occurs
     * while contacting the initial contact points.
     */
  public Cluster init() {
    this.manager.init();
    return this;
  }

  /**
     * Build a new cluster based on the provided initializer.
     * <p>
     * Note that for building a cluster pragmatically, Cluster.Builder
     * provides a slightly less verbose shortcut with {@link Builder#build}.
     * <p>
     * Also note that that all the contact points provided by {@code
     * initializer} must share the same port.
     *
     * @param initializer the Cluster.Initializer to use
     * @return the newly created Cluster instance
     *
     * @throws IllegalArgumentException if the list of contact points provided
     * by {@code initializer} is empty or if not all those contact points have the same port.
     */
  public static Cluster buildFrom(Initializer initializer) {
    return new Cluster(initializer);
  }

  /**
     * Creates a new {@link Cluster.Builder} instance.
     * <p>
     * This is a convenience method for {@code new Cluster.Builder()}.
     *
     * @return the new cluster builder.
     */
  public static Cluster.Builder builder() {
    return new Cluster.Builder();
  }

  /**
     * Creates a new session on this cluster but does not initialize it.
     * <p>
     * Because this method does not perform any initialization, it cannot fail.
     * The initialization of the session (the connection of the Session to the
     * Cassandra nodes) will occur if either the {@link Session#init} method is
     * called explicitly, or the time the
     * returned session object is called.
     * <p>
     * Once a session returned by this method gets initialized (see above), it
     * will be set to no keyspace. If you want to set such session to a
     * keyspace, you will have to explicitly execute a 'USE mykeyspace' query.
     * <p>
     * Note that if you do not particularly need to defer initialization, it is
     * simpler to use one of the {@code connect()} method of this class.
     *
     * @return a new, non-initialized session on this cluster.
     */
  public Session newSession() {
    return manager.newSession();
  }

  /**
     * Creates a new session on this cluster and initialize it.
     * <p>
     * Note that this method will initialize the newly created session, trying
     * to connect to the Cassandra nodes before returning. If you only want
     * to create a Session object without initializing it right away, see
     * {@link #newSession}.
     *
     * @return a new session on this cluster sets to no keyspace.
     *
     * @throws NoHostAvailableException if the Cluster has not been initialized yet
     * ({@link #init} has not be called and this is the first connect call) and
     * no host amongst the contact points can be reached.
     * @throws AuthenticationException if an authentication error occurs
     * while contacting the initial contact points.
     */
  public Session connect() {
    init();
    Session session = manager.newSession();
    session.init();
    return session;
  }

  /**
     * Creates a new session on this cluster, initialize it and sets the keyspace
     * to the provided one.
     * <p>
     * Note that this method will initialize the newly created session, trying
     * to connect to the Cassandra nodes before returning. If you only want
     * to create a Session object without initializing it right away, see
     * {@link #newSession}.
     *
     * @param keyspace The name of the keyspace to use for the created
     * {@code Session}.
     * @return a new session on this cluster sets to keyspace
     * {@code keyspaceName}.
     *
     * @throws NoHostAvailableException if the Cluster has not been initialized yet
     * ({@link #init} has not be called and this is the first connect call) and
     * no host amongst the contact points can be reached, or if no host can be
     * contacted to set the {@code keyspace}.
     * @throws AuthenticationException if an authentication error occurs
     * while contacting the initial contact points.
     */
  public Session connect(String keyspace) {
    long timeout = getConfiguration().getSocketOptions().getConnectTimeoutMillis();
    Session session = connect();
    try {
      ResultSetFuture future = session.executeAsync("USE " + keyspace);
      Uninterruptibles.getUninterruptibly(future, timeout, TimeUnit.MILLISECONDS);
      return session;
    } catch (TimeoutException e) {
      throw new DriverInternalError(String.format("No responses after %d milliseconds while setting current keyspace. This should not happen, unless you have setup a very low connection timeout.", timeout));
    } catch (ExecutionException e) {
      throw DefaultResultSetFuture.extractCauseFromExecutionException(e);
    } catch (RuntimeException e) {
      session.close();
      throw e;
    }
  }

  /**
     * The name of this cluster object.
     * <p>
     * Note that this is not the Cassandra cluster name, but rather a name
     * assigned to this Cluster object. Currently, that name is only used
     * for one purpose: to distinguish exposed JMX metrics when multiple
     * Cluster instances live in the same JVM (which should be rare in the first
     * place). That name can be set at Cluster building time (through
     * {@link Builder#withClusterName} for instance) but will default to a
     * name like {@code cluster1} where each Cluster instance in the same JVM
     * will have a different number.
     *
     * @return the name for this cluster instance.
     */
  public String getClusterName() {
    return manager.clusterName;
  }

  /**
     * Returns read-only metadata on the connected cluster.
     * <p>
     * This includes the known nodes with their status as seen by the driver,
     * as well as the schema definitions. Since this return metadata on the
     * connected cluster, this method may trigger the creation of a connection
     * if none has been established yet (neither {@code init()} nor {@code connect()}
     * has been called yet).
     *
     * @return the cluster metadata.
     *
     * @throws NoHostAvailableException if the Cluster has not been initialized yet
     * and no host amongst the contact points can be reached.
     * @throws AuthenticationException if an authentication error occurs
     * while contacting the initial contact points.
     */
  public Metadata getMetadata() {
    manager.init();
    return manager.metadata;
  }

  /**
     * The cluster configuration.
     *
     * @return the cluster configuration.
     */
  public Configuration getConfiguration() {
    return manager.configuration;
  }

  /**
     * The cluster metrics.
     *
     * @return the cluster metrics, or {@code null} if metrics collection has
     * been disabled (that is if {@link Configuration#getMetricsOptions}
     * returns {@code null}).
     */
  public Metrics getMetrics() {
    return manager.metrics;
  }

  /**
     * Registers the provided listener to be notified on hosts
     * up/down/added/removed events.
     * <p>
     * Registering the same listener multiple times is a no-op.
     * <p>
     * Note that while {@link LoadBalancingPolicy} implements
     * {@code Host.StateListener}, the configured load balancing does not
     * need to (and should not) be registered through this method to
     * received host related events.
     *
     * @param listener the new {@link Host.StateListener} to register.
     * @return this {@code Cluster} object;
     */
  public Cluster register(Host.StateListener listener) {
    manager.listeners.add(listener);
    return this;
  }

  /**
     * Unregisters the provided listener from being notified on hosts events.
     * <p>
     * This method is a no-op if {@code listener} hadn't previously be
     * registered against this Cluster.
     *
     * @param listener the {@link Host.StateListener} to unregister.
     * @return this {@code Cluster} object;
     */
  public Cluster unregister(Host.StateListener listener) {
    manager.listeners.remove(listener);
    return this;
  }

  /**
     * Registers the provided tracker to be updated with hosts read
     * latencies.
     * <p>
     * Registering the same listener multiple times is a no-op.
     * <p>
     * Be wary that the registered tracker {@code update} method will be call
     * very frequently (at the end of every query to a Cassandra host) and
     * should thus not be costly.
     * <p>
     * The main use case for a {@code LatencyTracker} is so
     * {@link LoadBalancingPolicy} can implement latency awareness
     * Typically, {@link LatencyAwarePolicy} registers  it's own internal
     * {@code LatencyTracker} (automatically, you don't have to call this
     * method directly).
     *
     * @param tracker the new {@link LatencyTracker} to register.
     * @return this {@code Cluster} object;
     */
  public Cluster register(LatencyTracker tracker) {
    manager.trackers.add(tracker);
    return this;
  }

  /**
     * Unregisters the provided latency tracking from being updated
     * with host read latencies.
     * <p>
     * This method is a no-op if {@code tracker} hadn't previously be
     * registered against this Cluster.
     *
     * @param tracker the {@link LatencyTracker} to unregister.
     * @return this {@code Cluster} object;
     */
  public Cluster unregister(LatencyTracker tracker) {
    manager.trackers.remove(tracker);
    return this;
  }

  /**
     * Initiates a shutdown of this cluster instance.
     * <p>
     * This method is asynchronous and return a future on the completion
     * of the shutdown process. As soon a the cluster is shutdown, no
     * new request will be accepted, but already submitted queries are
     * allowed to complete. This method closes all connections from all
     * sessions and reclaims all resources used by this Cluster
     * instance.
     * <p>
     * If for some reason you wish to expedite this process, the
     * {@link CloseFuture#force} can be called on the result future.
     * <p>
     * This method has no particular effect if the cluster was already closed
     * (in which case the returned future will return immediately).
     *
     * @return a future on the completion of the shutdown process.
     */
  public CloseFuture closeAsync() {
    return manager.close();
  }

  /**
     * Initiates a shutdown of this cluster instance and blocks until
     * that shutdown completes.
     * <p>
     * This method is a shortcut for {@code closeAsync().get()}.
     */
  public void close() {
    try {
      closeAsync().get();
    } catch (ExecutionException e) {
      throw DefaultResultSetFuture.extractCauseFromExecutionException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
     * Whether this Cluster instance has been closed.
     * <p>
     * Note that this method returns true as soon as one of the close methods
     * ({@link #closeAsync} or {@link #close}) has been called, it does not guarantee
     * that the closing is done. If you want to guarantee that the closing is done,
     * you can call {@code close()} and wait until it returns (or call the get method
     * on {@code closeAsync()} with a very short timeout and check this doesn't timeout).
     *
     * @return {@code true} if this Cluster instance has been closed, {@code false}
     * otherwise.
     */
  public boolean isClosed() {
    return manager.closeFuture.get() != null;
  }

  public interface Initializer {
    /**
         * An optional name for the created cluster.
         * <p>
         * Such name is optional (a default name will be created otherwise) and is currently
         * only use for JMX reporting of metrics. See {@link Cluster#getClusterName} for more
         * information.
         *
         * @return the name for the created cluster or {@code null} to use an automatically
         * generated name.
         */
    public String getClusterName();

    /**
         * Returns the initial Cassandra hosts to connect to.
         *
         * @return the initial Cassandra contact points. See {@link Builder#addContactPoint}
         * for more details on contact points.
         */
    public List<InetSocketAddress> getContactPoints();

    /**
         * The configuration to use for the new cluster.
         * <p>
         * Note that some configuration can be modified after the cluster
         * initialization but some others cannot. In particular, the ones that
         * cannot be changed afterwards includes:
         * <ul>
         *   <li>the port use to connect to Cassandra nodes (see {@link ProtocolOptions}).</li>
         *   <li>the policies used (see {@link Policies}).</li>
         *   <li>the authentication info provided (see {@link Configuration}).</li>
         *   <li>whether metrics are enabled (see {@link Configuration}).</li>
         * </ul>
         *
         * @return the configuration to use for the new cluster.
         */
    public Configuration getConfiguration();

    /**
         * Optional listeners to register against the newly created cluster.
         * <p>
         * Note that contrary to listeners registered post Cluster creation,
         * the listeners returned by this method will see {@link Host.StateListener#onAdd}
         * events for the initial contact points.
         *
         * @return a possibly empty collection of {@code Host.StateListener} to register
         * against the newly created cluster.
         */
    public Collection<Host.StateListener> getInitialListeners();
  }

  public static class Builder implements Initializer {
    private String clusterName;

    private final List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

    private final List<InetAddress> rawAddresses = new ArrayList<InetAddress>();

    private int port = ProtocolOptions.DEFAULT_PORT;

    private int protocolVersion = -1;

    private AuthProvider authProvider = AuthProvider.NONE;

    private LoadBalancingPolicy loadBalancingPolicy;

    private ReconnectionPolicy reconnectionPolicy;

    private RetryPolicy retryPolicy;

    private AddressTranslater addressTranslater;

    private ProtocolOptions.Compression compression = ProtocolOptions.Compression.NONE;

    private SSLOptions sslOptions = null;

    private boolean metricsEnabled = true;

    private boolean jmxEnabled = true;

    private PoolingOptions poolingOptions;

    private SocketOptions socketOptions;

    private QueryOptions queryOptions;

    private Collection<Host.StateListener> listeners;

    @Override public String getClusterName() {
      return clusterName;
    }

    @Override public List<InetSocketAddress> getContactPoints() {
      if (rawAddresses.isEmpty()) {
        return addresses;
      }
      List<InetSocketAddress> allAddresses = new ArrayList<InetSocketAddress>(addresses);
      for (InetAddress address : rawAddresses) {
        allAddresses.add(new InetSocketAddress(address, port));
      }
      return allAddresses;
    }

    /**
         * An optional name for the create cluster.
         * <p>
         * Note: this is not related to the Cassandra cluster name (though you
         * are free to provide the same name). See {@link Cluster#getClusterName} for
         * details.
         * <p>
         * If you use this method and create more than one Cluster instance in the
         * same JVM (which should be avoided unless you need to connect to multiple
         * Cassandra clusters), you should make sure each Cluster instance get a
         * unique name or you may have a problem with JMX reporting.
         *
         * @param name the cluster name to use for the created Cluster instance.
         * @return this Builder.
         */
    public Builder withClusterName(String name) {
      this.clusterName = name;
      return this;
    }

    /**
         * The port to use to connect to the Cassandra host.
         * <p>
         * If not set through this method, the default port (9042) will be used
         * instead.
         *
         * @param port the port to set.
         * @return this Builder.
         */
    public Builder withPort(int port) {
      this.port = port;
      return this;
    }

    /**
         * The native protocol version to use.
         * <p>
         * The driver supports both version 1 and 2 of the native protocol. Version 2
         * of the protocol has more features and should be preferred, but it is only
         * supported by Cassandra 2.0 and above, so you will have to use version 1 with
         * Cassandra 1.2 nodes.
         * <p>
         * By default, the driver will "auto-detect" which protocol version it can use
         * when connecting to the first node. More precisely, it will try the version
         * 2 first and will fallback to version 1 if it is not supported by that first
         * node it connects to. Please note that once the version is "auto-detected",
         * it won't change: if the first node the driver connects to is a Cassandra 1.2
         * node and auto-detection is used (the default), then the native protocol
         * version 1 will be use for the lifetime of the Cluster instance.
         * <p>
         * This method allows to force the use of a particular protocol version. Forcing
         * version 1 is always fine since all Cassandra version (at least all those
         * supporting the native protocol in the first place) so far supports it. However,
         * please note that a number of features of the driver won't be available if that
         * version of thr protocol is in use, including result set paging,
         * {@link BatchStatement}, executing a non-prepared query with binary values
         * ({@link Session#execute(String, Object...)}), ... (those methods will throw
         * an UnsupportedFeatureException). Using the protocol version 1 should thus
         * only be considered when using Cassandra 1.2, until nodes have been upgraded
         * to Cassandra 2.0.
         * <p>
         * If version 2 of the protocol is used, then Cassandra 1.2 nodes will be ignored
         * (the driver won't connect to them).
         * <p>
         * The default behavior (auto-detection) is fine in almost all case, but you may
         * want to force a particular version if you have a Cassandra cluster with mixed
         * 1.2/2.0 nodes (i.e. during a Cassandra upgrade).
         *
         * @param version the native protocol version to use. The versions supported by
         * this driver are version 1 and 2. Negative values are also supported to trigger
         * auto-detection (see above) but this is the default (so you don't have to call
         * this method for that behavior).
         * @return this Builder.
         *
         * @throws IllegalArgumentException if {@code version} is neither 1, 2 or a
         * negative value.
         */
    public Builder withProtocolVersion(int version) {
      if (protocolVersion == 0 || protocolVersion > ProtocolOptions.NEWEST_SUPPORTED_PROTOCOL_VERSION) {
        throw new IllegalArgumentException(String.format("Unsupported protocol version %d; valid values must be between 1 and %d or negative (for auto-detect).", protocolVersion, ProtocolOptions.NEWEST_SUPPORTED_PROTOCOL_VERSION));
      }
      this.protocolVersion = version;
      return this;
    }

    /**
         * Adds a contact point.
         * <p>
         * Contact points are addresses of Cassandra nodes that the driver uses
         * to discover the cluster topology. Only one contact point is required
         * (the driver will retrieve the address of the other nodes
         * automatically), but it is usually a good idea to provide more than
         * one contact point, because if that single contact point is unavailable,
         * the driver cannot initialize itself correctly.
         * <p>
         * Note that by default (that is, unless you use the {@link #withLoadBalancingPolicy})
         * method of this builder), the first succesfully contacted host will be use
         * to define the local data-center for the client. If follows that if you are
         * running Cassandra in a  multiple data-center setting, it is a good idea to
         * only provided contact points that are in the same datacenter than the client,
         * or to provide manually the load balancing policy that suits your need.
         *
         * @param address the address of the node to connect to
         * @return this Builder.
         *
         * @throws IllegalArgumentException if no IP address for {@code address}
         * could be found
         * @throws SecurityException if a security manager is present and
         * permission to resolve the host name is denied.
         */
    public Builder addContactPoint(String address) {
      try {
        this.rawAddresses.add(InetAddress.getByName(address));
        return this;
      } catch (UnknownHostException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }

    /**
         * Adds contact points.
         * <p>
         * See {@link Builder#addContactPoint} for more details on contact
         * points.
         *
         * @param addresses addresses of the nodes to add as contact point.
         * @return this Builder.
         *
         * @throws IllegalArgumentException if no IP address for at least one
         * of {@code addresses} could be found
         * @throws SecurityException if a security manager is present and
         * permission to resolve the host name is denied.
         *
         * @see Builder#addContactPoint
         */
    public Builder addContactPoints(String... addresses) {
      for (String address : addresses) {
        addContactPoint(address);
      }
      return this;
    }

    /**
         * Adds contact points.
         * <p>
         * See {@link Builder#addContactPoint} for more details on contact
         * points.
         *
         * @param addresses addresses of the nodes to add as contact point.
         * @return this Builder.
         *
         * @see Builder#addContactPoint
         */
    public Builder addContactPoints(InetAddress... addresses) {
      Collections.addAll(this.rawAddresses, addresses);
      return this;
    }

    /**
         * Adds contact points.
         *
         * See {@link Builder#addContactPoint} for more details on contact
         * points.
         *
         * @param addresses addresses of the nodes to add as contact point
         * @return this Builder
         *
         * @see Builder#addContactPoint
         */
    public Builder addContactPoints(Collection<InetAddress> addresses) {
      this.rawAddresses.addAll(addresses);
      return this;
    }

    /**
         * Adds contact points.
         * <p>
         * See {@link Builder#addContactPoint} for more details on contact
         * points. Contrarily to other {@code addContactPoints} methods, this method
         * allow to provide a different port for each contact points. Since Cassandra
         * nodes must always all listen on the same port, this is rarelly what you
         * want and most use should prefer other {@code addContactPoints} methods to
         * this one. However, this can be useful if the Cassandra nodes are behind
         * a router and are not accessed directly. Note that if you are in this
         * situtation (Cassandra nodes are behind a router, not directly accessible),
         * you almost surely want to provide a specific {@code AddressTranslater}
         * (through {@link #withAddressTranslater}) to translate actual Cassandra node
         * addresses to the addresses the driver should use, otherwise the driver
         * will not be able to auto-detect new nodes (and will generally not function
         * optimally).
         *
         * @param addresses addresses of the nodes to add as contact point
         * @return this Builder
         *
         * @see Builder#addContactPoint
         */
    public Builder addContactPointsWithPorts(Collection<InetSocketAddress> addresses) {
      this.addresses.addAll(addresses);
      return this;
    }

    /**
         * Configures the load balancing policy to use for the new cluster.
         * <p>
         * If no load balancing policy is set through this method,
         * {@link Policies#defaultLoadBalancingPolicy} will be used instead.
         *
         * @param policy the load balancing policy to use.
         * @return this Builder.
         */
    public Builder withLoadBalancingPolicy(LoadBalancingPolicy policy) {
      this.loadBalancingPolicy = policy;
      return this;
    }

    /**
         * Configures the reconnection policy to use for the new cluster.
         * <p>
         * If no reconnection policy is set through this method,
         * {@link Policies#DEFAULT_RECONNECTION_POLICY} will be used instead.
         *
         * @param policy the reconnection policy to use.
         * @return this Builder.
         */
    public Builder withReconnectionPolicy(ReconnectionPolicy policy) {
      this.reconnectionPolicy = policy;
      return this;
    }

    /**
         * Configures the retry policy to use for the new cluster.
         * <p>
         * If no retry policy is set through this method,
         * {@link Policies#DEFAULT_RETRY_POLICY} will be used instead.
         *
         * @param policy the retry policy to use.
         * @return this Builder.
         */
    public Builder withRetryPolicy(RetryPolicy policy) {
      this.retryPolicy = policy;
      return this;
    }

    /**
         * Configures the address translater to use for the new cluster.
         * <p>
         * See {@link AddressTranslater} for more detail on address translation,
         * but the default tanslater, {@link IdentityTranslater}, should be
         * correct in most cases. If unsure, stick to the default.
         *
         * @param translater the translater to use.
         * @return this Builder.
         */
    public Builder withAddressTranslater(AddressTranslater translater) {
      this.addressTranslater = translater;
      return this;
    }

    /**
         * Uses the provided credentials when connecting to Cassandra hosts.
         * <p>
         * This should be used if the Cassandra cluster has been configured to
         * use the {@code PasswordAuthenticator}. If the the default {@code
         * AllowAllAuthenticator} is used instead, using this method has no
         * effect.
         *
         * @param username the username to use to login to Cassandra hosts.
         * @param password the password corresponding to {@code username}.
         * @return this Builder.
         */
    public Builder withCredentials(String username, String password) {
      this.authProvider = new PlainTextAuthProvider(username, password);
      return this;
    }

    /**
         * Use the specified AuthProvider when connecting to Cassandra
         * hosts.
         * <p>
         * Use this method when a custom authentication scheme is in place.
         * You shouldn't call both this method and {@code withCredentials}
         * on the same {@code Builder} instance as one will supersede the
         * other
         *
         * @param authProvider the {@link AuthProvider} to use to login to
         * Cassandra hosts.
         * @return this Builder
         */
    public Builder withAuthProvider(AuthProvider authProvider) {
      this.authProvider = authProvider;
      return this;
    }

    /**
         * Sets the compression to use for the transport.
         *
         * @param compression the compression to set.
         * @return this Builder.
         *
         * @see ProtocolOptions.Compression
         */
    public Builder withCompression(ProtocolOptions.Compression compression) {
      this.compression = compression;
      return this;
    }

    /**
         * Disables metrics collection for the created cluster (metrics are
         * enabled by default otherwise).
         *
         * @return this builder.
         */
    public Builder withoutMetrics() {
      this.metricsEnabled = false;
      return this;
    }

    /**
         * Enables the use of SSL for the created {@code Cluster}.
         * <p>
         * Calling this method will use default SSL options (see {@link SSLOptions#SSLOptions()}).
         * This is thus a shortcut for {@code withSSL(new SSLOptions())}.
         * <p>
         * Note that if SSL is enabled, the driver will not connect to any
         * Cassandra nodes that doesn't have SSL enabled and it is strongly
         * advised to enable SSL on every Cassandra node if you plan on using
         * SSL in the driver.
         *
         * @return this builder.
         */
    public Builder withSSL() {
      this.sslOptions = new SSLOptions();
      return this;
    }

    /**
         * Enable the use of SSL for the created {@code Cluster} using the provided options.
         *
         * @param sslOptions the SSL options to use.
         *
         * @return this builder.
         */
    public Builder withSSL(SSLOptions sslOptions) {
      this.sslOptions = sslOptions;
      return this;
    }

    /**
         * Register the provided listeners in the newly created cluster.
         * <p>
         * Note: repeated calls to this method will override the previous ones.
         *
         * @param listeners the listeners to register.
         * @return this builder.
         */
    public Builder withInitialListeners(Collection<Host.StateListener> listeners) {
      this.listeners = listeners;
      return this;
    }

    /**
         * Disables JMX reporting of the metrics.
         * <p>
         * JMX reporting is enabled by default (see {@link Metrics}) but can be
         * disabled using this option. If metrics are disabled, this is a
         * no-op.
         *
         * @return this builder.
         */
    public Builder withoutJMXReporting() {
      this.jmxEnabled = false;
      return this;
    }

    /**
         * Sets the PoolingOptions to use for the newly created Cluster.
         * <p>
         * If no pooling options are set through this method, default pooling
         * options will be used.
         *
         * @param options the pooling options to use.
         * @return this builder.
         */
    public Builder withPoolingOptions(PoolingOptions options) {
      this.poolingOptions = options;
      return this;
    }

    /**
         * Sets the SocketOptions to use for the newly created Cluster.
         * <p>
         * If no socket options are set through this method, default socket
         * options will be used.
         *
         * @param options the socket options to use.
         * @return this builder.
         */
    public Builder withSocketOptions(SocketOptions options) {
      this.socketOptions = options;
      return this;
    }

    /**
         * Sets the QueryOptions to use for the newly created Cluster.
         * <p>
         * If no query options are set through this method, default query
         * options will be used.
         *
         * @param options the query options to use.
         * @return this builder.
         */
    public Builder withQueryOptions(QueryOptions options) {
      this.queryOptions = options;
      return this;
    }

    /**
         * The configuration that will be used for the new cluster.
         * <p>
         * You <b>should not</b> modify this object directly because changes made
         * to the returned object may not be used by the cluster build.
         * Instead, you should use the other methods of this {@code Builder}.
         *
         * @return the configuration to use for the new cluster.
         */
    @Override public Configuration getConfiguration() {
      Policies policies = new Policies(loadBalancingPolicy == null ? Policies.defaultLoadBalancingPolicy() : loadBalancingPolicy, reconnectionPolicy == null ? Policies.defaultReconnectionPolicy() : reconnectionPolicy, retryPolicy == null ? Policies.defaultRetryPolicy() : retryPolicy, addressTranslater == null ? Policies.defaultAddressTranslater() : addressTranslater);
      return new Configuration(policies, new ProtocolOptions(port, protocolVersion, sslOptions, authProvider).setCompression(compression), poolingOptions == null ? new PoolingOptions() : poolingOptions, socketOptions == null ? new SocketOptions() : socketOptions, metricsEnabled ? new MetricsOptions(jmxEnabled) : null, queryOptions == null ? new QueryOptions() : queryOptions);
    }

    @Override public Collection<Host.StateListener> getInitialListeners() {
      return listeners == null ? Collections.<Host.StateListener>emptySet() : listeners;
    }

    /**
         * Builds the cluster with the configured set of initial contact points
         * and policies.
         * <p>
         * This is a convenience method for {@code Cluster.buildFrom(this)}.
         *
         * @return the newly built Cluster instance.
         */
    public Cluster build() {
      return Cluster.buildFrom(this);
    }
  }

  private static ThreadFactory threadFactory(String nameFormat) {
    return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
  }

  static long timeSince(long startNanos, TimeUnit destUnit) {
    return destUnit.convert(System.nanoTime() - startNanos, TimeUnit.NANOSECONDS);
  }

  private static String generateClusterName() {
    return "cluster" + CLUSTER_ID.incrementAndGet();
  }

  private static ListeningExecutorService makeExecutor(int threads, String name) {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, DEFAULT_THREAD_KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory(name));
    executor.allowCoreThreadTimeOut(true);
    return MoreExecutors.listeningDecorator(executor);
  }

  class Manager implements Connection.DefaultResponseHandler {
    final String clusterName;

    private boolean isInit;

    final List<InetSocketAddress> contactPoints;

    final Set<SessionManager> sessions = new CopyOnWriteArraySet<SessionManager>();

    final Metadata metadata;

    final Configuration configuration;

    final Metrics metrics;

    final Connection.Factory connectionFactory;

    final ControlConnection controlConnection;

    final ConvictionPolicy.Factory convictionPolicyFactory = new ConvictionPolicy.Simple.Factory();

    final ScheduledExecutorService reconnectionExecutor = Executors.newScheduledThreadPool(2, threadFactory("Reconnection-%d"));

    final ScheduledExecutorService scheduledTasksExecutor = Executors.newScheduledThreadPool(1, threadFactory("Scheduled Tasks-%d"));

    final ListeningExecutorService executor;

    final ListeningExecutorService blockingExecutor;

    final AtomicReference<CloseFuture> closeFuture = new AtomicReference<CloseFuture>();

    final ConcurrentMap<MD5Digest, PreparedStatement> preparedQueries = new MapMaker().weakValues().makeMap();

    final Set<Host.StateListener> listeners;

    final Set<LatencyTracker> trackers = new CopyOnWriteArraySet<LatencyTracker>();

    private Manager(String clusterName, List<InetSocketAddress> contactPoints, Configuration configuration, Collection<Host.StateListener> listeners) {
      logger.debug("Starting new cluster with contact points " + contactPoints);
      this.clusterName = clusterName == null ? generateClusterName() : clusterName;
      this.configuration = configuration;
      this.configuration.register(this);
      this.executor = makeExecutor(Runtime.getRuntime().availableProcessors(), "Cassandra Java Driver worker-%d");
      this.blockingExecutor = makeExecutor(2, "Cassandra Java Driver blocking tasks worker-%d");
      this.metadata = new Metadata(this);
      this.contactPoints = contactPoints;
      this.connectionFactory = new Connection.Factory(this, configuration);
      this.controlConnection = new ControlConnection(this);
      this.metrics = configuration.getMetricsOptions() == null ? null : new Metrics(this);
      this.listeners = new CopyOnWriteArraySet<Host.StateListener>(listeners);
    }

    synchronized void init() {
      if (isClosed()) {
        throw new IllegalStateException("Can\'t use this Cluster instance because it was previously closed");
      }
      if (isInit) {
        return;
      }
      isInit = true;
      for (InetSocketAddress address : contactPoints) {
        Host host = metadata.add(address);
        if (host != null) {
          host.setUp();
          for (Host.StateListener listener : listeners) {
            listener.onAdd(host);
          }
        }
      }
      loadBalancingPolicy().init(Cluster.this, metadata.allHosts());
      try {
        while (true) {
          try {
            controlConnection.connect();
            if (connectionFactory.protocolVersion < 0) {
              connectionFactory.protocolVersion = ProtocolOptions.NEWEST_SUPPORTED_PROTOCOL_VERSION;
            }
            return;
          } catch (UnsupportedProtocolVersionException e) {
            assert connectionFactory.protocolVersion < 1;
            if (e.versionUnsupported <= 1) {
              throw new DriverInternalError("Got a node that don\'t even support the protocol version 1, this makes no sense", e);
            }
            logger.debug("{}: retrying with version {}", e.getMessage(), e.versionUnsupported - 1);
            connectionFactory.protocolVersion = e.versionUnsupported - 1;
          }
        }
      } catch (NoHostAvailableException e) {
        close();
        throw e;
      }
    }

    int protocolVersion() {
      return connectionFactory.protocolVersion;
    }

    Cluster getCluster() {
      return Cluster.this;
    }

    LoadBalancingPolicy loadBalancingPolicy() {
      return configuration.getPolicies().getLoadBalancingPolicy();
    }

    ReconnectionPolicy reconnectionPolicy() {
      return configuration.getPolicies().getReconnectionPolicy();
    }

    InetSocketAddress translateAddress(InetAddress address) {
      InetSocketAddress sa = new InetSocketAddress(address, connectionFactory.getPort());
      return configuration.getPolicies().getAddressTranslater().translate(sa);
    }

    private Session newSession() {
      SessionManager session = new SessionManager(Cluster.this);
      sessions.add(session);
      return session;
    }

    void reportLatency(Host host, long latencyNanos) {
      for (LatencyTracker tracker : trackers) {
        tracker.update(host, latencyNanos);
      }
    }

    boolean isClosed() {
      return closeFuture.get() != null;
    }

    private CloseFuture close() {
      CloseFuture future = closeFuture.get();
      if (future != null) {
        return future;
      }
      logger.debug("Shutting down");
      reconnectionExecutor.shutdownNow();
      scheduledTasksExecutor.shutdownNow();
      executor.shutdown();
      if (metrics != null) {
        metrics.shutdown();
      }
      List<CloseFuture> futures = new ArrayList<CloseFuture>(sessions.size() + 1);
      futures.add(controlConnection.closeAsync());
      for (Session session : sessions) {
        futures.add(session.closeAsync());
      }
      future = new ClusterCloseFuture(futures);
      return closeFuture.compareAndSet(null, future) ? future : closeFuture.get();
    }

    void logUnsupportedVersionProtocol(Host host) {
      logger.warn("Detected added or restarted Cassandra host {} but ignoring it since it does not support the version 2 of the native " + "protocol which is currently in use. If you want to force the use of the version 1 of the native protocol, use " + "Cluster.Builder#usingProtocolVersion() when creating the Cluster instance.", host);
    }

    public ListenableFuture<?> triggerOnUp(final Host host) {
      return executor.submit(new ExceptionCatchingRunnable() {
        @Override public void runMayThrow() throws InterruptedException, ExecutionException {
          onUp(host);
        }
      });
    }

    private void onUp(final Host host) throws InterruptedException, ExecutionException {
      logger.trace("Host {} is UP", host);
      if (isClosed()) {
        return;
      }
      if (host.isUp()) {
        return;
      }
      if (connectionFactory.protocolVersion == 2 && !supportsProtocolV2(host)) {
        logUnsupportedVersionProtocol(host);
        return;
      }
      ScheduledFuture<?> scheduledAttempt = host.reconnectionAttempt.getAndSet(null);
      if (scheduledAttempt != null) {
        logger.debug("Cancelling reconnection attempt since node is UP");
        scheduledAttempt.cancel(false);
      }
      try {
        prepareAllQueries(host);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (UnsupportedProtocolVersionException e) {
        logUnsupportedVersionProtocol(host);
        return;
      }
      for (SessionManager s : sessions) {
        s.removePool(host);
      }
      loadBalancingPolicy().onUp(host);
      controlConnection.onUp(host);
      List<ListenableFuture<Boolean>> futures = new ArrayList<ListenableFuture<Boolean>>(sessions.size());
      for (SessionManager s : sessions) {
        futures.add(s.addOrRenewPool(host, false, blockingExecutor));
      }
      ListenableFuture<List<Boolean>> f = Futures.allAsList(futures);
      Futures.addCallback(f, new FutureCallback<List<Boolean>>() {
        public void onSuccess(List<Boolean> poolCreationResults) {
          if (Iterables.any(poolCreationResults, Predicates.equalTo(false))) {
            logger.debug("Connection pool cannot be created, not marking {} UP", host);
            return;
          }
          host.setUp();
          for (Host.StateListener listener : listeners) {
            listener.onUp(host);
          }
          for (SessionManager s : sessions) {
            s.updateCreatedPools(blockingExecutor);
          }
        }

        public void onFailure(Throwable t) {
          if (!(t instanceof InterruptedException)) {
            logger.error("Unexpected error while marking node UP: while this shouldn\'t happen, this shouldn\'t be critical", t);
          }
        }
      });
      f.get();
    }

    public ListenableFuture<?> triggerOnDown(final Host host) {
      return triggerOnDown(host, false);
    }

    public ListenableFuture<?> triggerOnDown(final Host host, final boolean isHostAddition) {
      return executor.submit(new ExceptionCatchingRunnable() {
        @Override public void runMayThrow() throws InterruptedException, ExecutionException {
          onDown(host, isHostAddition);
        }
      });
    }

    private void onDown(final Host host, final boolean isHostAddition) throws InterruptedException, ExecutionException {
      logger.trace("Host {} is DOWN", host);
      if (isClosed()) {
        return;
      }
      if (host.reconnectionAttempt.get() != null) {
        return;
      }
      HostDistance distance = loadBalancingPolicy().distance(host);
      boolean wasUp = host.isUp();
      host.setDown();
      loadBalancingPolicy().onDown(host);
      controlConnection.onDown(host);
      for (SessionManager s : sessions) {
        s.onDown(host);
      }
      if (wasUp) {
        for (Host.StateListener listener : listeners) {
          listener.onDown(host);
        }
      }
      if (distance == HostDistance.IGNORED) {
        return;
      }
      logger.debug("{} is down, scheduling connection retries", host);
      new AbstractReconnectionHandler(reconnectionExecutor, reconnectionPolicy().newSchedule(), host.reconnectionAttempt) {
        protected Connection tryReconnect() throws ConnectionException, InterruptedException, UnsupportedProtocolVersionException {
          return connectionFactory.open(host);
        }

        protected void onReconnection(Connection connection) {
          logger.debug("Successful reconnection to {}, setting host UP", host);
          controlConnection.refreshNodeInfo(host);
          try {
            if (isHostAddition) {
              onAdd(host);
            } else {
              onUp(host);
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          } catch (Exception e) {
            logger.error("Unexpected error while setting node up", e);
          }
        }

        protected boolean onConnectionException(ConnectionException e, long nextDelayMs) {
          if (logger.isDebugEnabled()) {
            logger.debug("Failed reconnection to {} ({}), scheduling retry in {} milliseconds", host, e.getMessage(), nextDelayMs);
          }
          return true;
        }

        protected boolean onUnknownException(Exception e, long nextDelayMs) {
          logger.error(String.format("Unknown error during control connection reconnection, scheduling retry in %d milliseconds", nextDelayMs), e);
          return true;
        }
      }.start();
    }

    public ListenableFuture<?> triggerOnAdd(final Host host) {
      return executor.submit(new ExceptionCatchingRunnable() {
        @Override public void runMayThrow() throws InterruptedException, ExecutionException {
          onAdd(host);
        }
      });
    }

    private void onAdd(final Host host) throws InterruptedException, ExecutionException {
      if (isClosed()) {
        return;
      }
      logger.info("New Cassandra host {} added", host);
      if (connectionFactory.protocolVersion == 2 && !supportsProtocolV2(host)) {
        logUnsupportedVersionProtocol(host);
        return;
      }
      loadBalancingPolicy().onAdd(host);
      if (loadBalancingPolicy().distance(host) == HostDistance.IGNORED) {
        host.setUp();
        for (Host.StateListener listener : listeners) {
          listener.onAdd(host);
        }
        return;
      }
      try {
        prepareAllQueries(host);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (UnsupportedProtocolVersionException e) {
        logUnsupportedVersionProtocol(host);
        return;
      }
      controlConnection.onAdd(host);
      List<ListenableFuture<Boolean>> futures = new ArrayList<ListenableFuture<Boolean>>(sessions.size());
      for (SessionManager s : sessions) {
        futures.add(s.addOrRenewPool(host, true, blockingExecutor));
      }
      ListenableFuture<List<Boolean>> f = Futures.allAsList(futures);
      Futures.addCallback(f, new FutureCallback<List<Boolean>>() {
        public void onSuccess(List<Boolean> poolCreationResults) {
          if (Iterables.any(poolCreationResults, Predicates.equalTo(false))) {
            logger.debug("Connection pool cannot be created, not marking {} UP", host);
            return;
          }
          host.setUp();
          for (Host.StateListener listener : listeners) {
            listener.onAdd(host);
          }
          for (SessionManager s : sessions) {
            s.updateCreatedPools(blockingExecutor);
          }
        }

        public void onFailure(Throwable t) {
          if (!(t instanceof InterruptedException)) {
            logger.error("Unexpected error while adding node: while this shouldn\'t happen, this shouldn\'t be critical", t);
          }
        }
      });
      f.get();
    }

    public ListenableFuture<?> triggerOnRemove(final Host host) {
      return executor.submit(new ExceptionCatchingRunnable() {
        @Override public void runMayThrow() throws InterruptedException, ExecutionException {
          onRemove(host);
        }
      });
    }

    private void onRemove(Host host) throws InterruptedException, ExecutionException {
      if (isClosed()) {
        return;
      }
      host.setDown();
      logger.debug("Removing host {}", host);
      loadBalancingPolicy().onRemove(host);
      controlConnection.onRemove(host);
      for (SessionManager s : sessions) {
        s.onRemove(host);
      }
      for (Host.StateListener listener : listeners) {
        listener.onRemove(host);
      }
    }

    public boolean signalConnectionFailure(Host host, ConnectionException exception, boolean isHostAddition) {
      boolean isDown = host.signalConnectionFailure(exception);
      if (isDown) {
        triggerOnDown(host, isHostAddition);
      }
      return isDown;
    }

    private boolean supportsProtocolV2(Host newHost) {
      return newHost.getCassandraVersion() == null || newHost.getCassandraVersion().getMajor() >= 2;
    }

    public void removeHost(Host host) {
      if (host == null) {
        return;
      }
      if (metadata.remove(host)) {
        logger.info("Cassandra host {} removed", host);
        triggerOnRemove(host);
      }
    }

    public void ensurePoolsSizing() {
      for (SessionManager session : sessions) {
        for (HostConnectionPool pool : session.pools.values()) {
          pool.ensureCoreConnections();
        }
      }
    }

    public PreparedStatement addPrepared(PreparedStatement stmt) {
      PreparedStatement previous = preparedQueries.putIfAbsent(stmt.getPreparedId().id, stmt);
      if (previous != null) {
        logger.warn("Re-preparing already prepared query {}. Please note that preparing the same query more than once is " + "generally an anti-pattern and will likely affect performance. Consider preparing the statement only once.", stmt.getQueryString());
        return previous;
      }
      return stmt;
    }

    private void prepareAllQueries(Host host) throws InterruptedException, UnsupportedProtocolVersionException {
      if (preparedQueries.isEmpty()) {
        return;
      }
      logger.debug("Preparing {} prepared queries on newly up node {}", preparedQueries.size(), host);
      try {
        Connection connection = connectionFactory.open(host);
        try {
          try {
            ControlConnection.waitForSchemaAgreement(connection, this);
          } catch (ExecutionException e) {
          }
          SetMultimap<String, String> perKeyspace = HashMultimap.create();
          for (PreparedStatement ps : preparedQueries.values()) {
            String keyspace = ps.getQueryKeyspace() == null ? "" : ps.getQueryKeyspace();
            perKeyspace.put(keyspace, ps.getQueryString());
          }
          for (String keyspace : perKeyspace.keySet()) {
            if (!keyspace.isEmpty()) {
              connection.setKeyspace(keyspace);
            }
            List<Connection.Future> futures = new ArrayList<Connection.Future>(preparedQueries.size());
            for (String query : perKeyspace.get(keyspace)) {
              futures.add(connection.write(new Requests.Prepare(query)));
            }
            for (Connection.Future future : futures) {
              try {
                future.get();
              } catch (ExecutionException e) {
                logger.debug("Unexpected error while preparing queries on new/newly up host", e);
              }
            }
          }
        }  finally {
          connection.closeAsync();
        }
      } catch (ConnectionException e) {
      } catch (AuthenticationException e) {
      } catch (BusyConnectionException e) {
      }
    }

    public void submitSchemaRefresh(final String keyspace, final String table) {
      logger.trace("Submitting schema refresh");
      executor.submit(new ExceptionCatchingRunnable() {
        @Override public void runMayThrow() throws InterruptedException, ExecutionException {
          controlConnection.refreshSchema(keyspace, table);
        }
      });
    }

    public void refreshSchemaAndSignal(final Connection connection, final DefaultResultSetFuture future, final ResultSet rs, final String keyspace, final String table) {
      if (logger.isDebugEnabled()) {
        logger.debug("Refreshing schema for {}{}", keyspace == null ? "" : keyspace, table == null ? "" : '.' + table);
      }
      executor.submit(new Runnable() {
        @Override public void run() {
          try {
            if (!ControlConnection.waitForSchemaAgreement(connection, Cluster.Manager.this)) {
              logger.warn("No schema agreement from live replicas after {} ms. The schema may not be up to date on some nodes.", ControlConnection.MAX_SCHEMA_AGREEMENT_WAIT_MS);
            }
            ControlConnection.refreshSchema(connection, keyspace, table, Cluster.Manager.this);
          } catch (Exception e) {
            logger.error("Error during schema refresh ({}). The schema from Cluster.getMetadata() might appear stale. Asynchronously submitting job to fix.", e.getMessage());
            submitSchemaRefresh(keyspace, table);
          } finally {
            future.setResult(rs);
          }
        }
      });
    }

    @Override public void handle(Message.Response response) {
      if (!(response instanceof Responses.Event)) {
        logger.error("Received an unexpected message from the server: {}", response);
        return;
      }
      final ProtocolEvent event = ((Responses.Event) response).event;
      logger.debug("Received event {}, scheduling delivery", response);
      switch (event.type) {
        case TOPOLOGY_CHANGE:
        ProtocolEvent.TopologyChange tpc = (ProtocolEvent.TopologyChange) event;
        InetSocketAddress tpAddr = translateAddress(tpc.node.getAddress());
        switch (tpc.change) {
          case NEW_NODE:
          final Host newHost = metadata.add(tpAddr);
          if (newHost != null) {
            scheduledTasksExecutor.schedule(new ExceptionCatchingRunnable() {
              @Override public void runMayThrow() throws InterruptedException, ExecutionException {
                controlConnection.refreshNodeInfo(newHost);
                onAdd(newHost);
              }
            }, 1, TimeUnit.SECONDS);
          }
          break;
          case REMOVED_NODE:
          removeHost(metadata.getHost(tpAddr));
          break;
          case MOVED_NODE:
          executor.submit(new ExceptionCatchingRunnable() {
            @Override public void runMayThrow() {
              controlConnection.refreshNodeListAndTokenMap();
            }
          });
          break;
        }
        break;
        case STATUS_CHANGE:
        ProtocolEvent.StatusChange stc = (ProtocolEvent.StatusChange) event;
        InetSocketAddress stAddr = translateAddress(stc.node.getAddress());
        switch (stc.status) {
          case UP:
          final Host hostUp = metadata.getHost(stAddr);
          if (hostUp == null) {
            final Host h = metadata.add(stAddr);
            if (h == null) {
              return;
            }
            scheduledTasksExecutor.schedule(new ExceptionCatchingRunnable() {
              @Override public void runMayThrow() throws InterruptedException, ExecutionException {
                controlConnection.refreshNodeInfo(h);
                onAdd(h);
              }
            }, 1, TimeUnit.SECONDS);
          } else {
            executor.submit(new ExceptionCatchingRunnable() {
              @Override public void runMayThrow() throws InterruptedException, ExecutionException {
                controlConnection.refreshNodeInfo(hostUp);
                onUp(hostUp);
              }
            });
          }
          break;
          case DOWN:
          Host hostDown = metadata.getHost(stAddr);
          if (hostDown != null) {
            triggerOnDown(hostDown);
          }
          break;
        }
        break;
        case SCHEMA_CHANGE:
        ProtocolEvent.SchemaChange scc = (ProtocolEvent.SchemaChange) event;
        switch (scc.change) {
          case CREATED:
          if (scc.table.isEmpty()) {
            submitSchemaRefresh(null, null);
          } else {
            submitSchemaRefresh(scc.keyspace, null);
          }
          break;
          case DROPPED:
          if (scc.table.isEmpty()) {
            submitSchemaRefresh(null, null);
          } else {
            submitSchemaRefresh(scc.keyspace, null);
          }
          break;
          case UPDATED:
          if (scc.table.isEmpty()) {
            submitSchemaRefresh(scc.keyspace, null);
          } else {
            submitSchemaRefresh(scc.keyspace, scc.table);
          }
          break;
        }
        break;
      }
    }

    void refreshConnectedHosts() {
      Host ccHost = controlConnection.connectedHost();
      if (ccHost == null || loadBalancingPolicy().distance(ccHost) != HostDistance.LOCAL) {
        controlConnection.reconnect();
      }
      for (SessionManager s : sessions) {
        s.updateCreatedPools(executor);
      }
    }

    private class ClusterCloseFuture extends CloseFuture.Forwarding {
      ClusterCloseFuture(List<CloseFuture> futures) {
        super(futures);
      }

      @Override public CloseFuture force() {
        executor.shutdownNow();
        return super.force();
      }

      @Override protected void onFuturesDone() {
        (new Thread("Shutdown-checker") {
          public void run() {
            connectionFactory.shutdown();
            try {
              reconnectionExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
              scheduledTasksExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
              executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
              set(null);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              setException(e);
            }
          }
        }).start();
      }
    }
  }
}