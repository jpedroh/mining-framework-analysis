package com.datastax.driver.core;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.policies.ReconnectionPolicy;

/**
 * A Cassandra node.
 *
 * This class keeps the information the driver maintain on a given Cassandra node.
 */
public class Host {
  private static final Logger logger = LoggerFactory.getLogger(Host.class);

  private final InetSocketAddress address;

  enum State {
    ADDED,
    DOWN,
    SUSPECT,
    UP
  }

  volatile State state;

  private final ConvictionPolicy policy;

  final AtomicReference<ListenableFuture<?>> initialReconnectionAttempt = new AtomicReference<ListenableFuture<?>>(Futures.immediateFuture(null));

  final AtomicReference<ScheduledFuture<?>> reconnectionAttempt = new AtomicReference<ScheduledFuture<?>>();

  final ExecutionInfo defaultExecutionInfo;

  private volatile String datacenter;

  private volatile String rack;

  private volatile VersionNumber cassandraVersion;

  volatile InetAddress listenAddress;

  Host(InetSocketAddress address, ConvictionPolicy.Factory policy) {
    if (address == null || policy == null) {
      throw new NullPointerException();
    }
    this.address = address;
    this.policy = policy.create(this);
    this.defaultExecutionInfo = new ExecutionInfo(ImmutableList.of(this));
    this.state = State.ADDED;
  }

  void setLocationInfo(String datacenter, String rack) {
    this.datacenter = datacenter;
    this.rack = rack;
  }

  void setVersionAndListenAdress(String cassandraVersion, InetAddress listenAddress) {
    if (listenAddress != null) {
      this.listenAddress = listenAddress;
    }
    if (cassandraVersion == null) {
      return;
    }
    try {
      this.cassandraVersion = VersionNumber.parse(cassandraVersion);
    } catch (IllegalArgumentException e) {
      logger.warn("Error parsing Cassandra version {}. This shouldn\'t have happened", cassandraVersion);
    }
  }

  /**
     * Returns the node address.
     * <p>
     * This is a shortcut for {@code getSocketAddress().getAddress()}.
     *
     * @return the node {@link InetAddress}.
     */
  public InetAddress getAddress() {
    return address.getAddress();
  }

  /**
     * Returns the node socket address.
     *
     * @return the node {@link InetSocketAddress}.
     */
  public InetSocketAddress getSocketAddress() {
    return address;
  }

  /**
     * Returns the name of the datacenter this host is part of.
     * <p>
     * The returned datacenter name is the one as known by Cassandra.
     * It is also possible for this information to be unavailable. In that
     * case this method returns {@code null}, and the caller should always be aware
     * of this possibility.
     *
     * @return the Cassandra datacenter name or null if datacenter is unavailable.
     */
  public String getDatacenter() {
    return datacenter;
  }

  /**
     * Returns the name of the rack this host is part of.
     * <p>
     * The returned rack name is the one as known by Cassandra.
     * It is also possible for this information to be unavailable. In that case
     * this method returns {@code null}, and the caller should always aware of this
     * possibility.
     *
     * @return the Cassandra rack name or null if the rack is unavailable
     */
  public String getRack() {
    return rack;
  }

  /**
     * The Cassandra version the host is running.
     * <p>
     * As for other host information fetch from Cassandra above, the returned
     * version can theoretically be null if the information is unavailable.
     *
     * @return the Cassandra version the host is running.
     */
  public VersionNumber getCassandraVersion() {
    return cassandraVersion;
  }

  /**
     * Returns whether the host is considered up by the driver.
     * <p>
     * Please note that this is only the view of the driver and may not reflect
     * reality. In particular a node can be down but the driver hasn't detected
     * it yet, or it can have been restarted and the driver hasn't detected it
     * yet (in particular, for hosts to which the driver does not connect (because
     * the {@code LoadBalancingPolicy.distance} method says so), this information
     * may be durably inaccurate). This information should thus only be
     * considered as best effort and should not be relied upon too strongly.
     *
     * @return whether the node is considered up.
     */
  public boolean isUp() {
    return state == State.UP || state == State.SUSPECT;
  }

  public ListenableFuture<?> getInitialReconnectionAttemptFuture() {
    return initialReconnectionAttempt.get();
  }

  @Override public int hashCode() {
    return address.hashCode();
  }

  boolean wasJustAdded() {
    return state == State.ADDED;
  }

  @Override public String toString() {
    return address.toString();
  }

  void setDown() {
    state = State.DOWN;
  }

  void setUp() {
    policy.reset();
    state = State.UP;
  }

  boolean setSuspected() {
    if (state != State.UP) {
      return false;
    }
    state = State.SUSPECT;
    return true;
  }

  boolean signalConnectionFailure(ConnectionException exception) {
    return policy.addFailure(exception);
  }

  public interface StateListener {
    /**
         * Called when a new node is added to the cluster.
         * <p>
         * The newly added node should be considered up.
         *
         * @param host the host that has been newly added.
         */
    public void onAdd(Host host);

    /**
         * Called when a node is determined to be up.
         *
         * @param host the host that has been detected up.
         */
    public void onUp(Host host);

    /**
         * Called when a node is suspected to be dead.
         * <p>
         * A node is suspected to be dead when an error occurs on one of it's
         * opened connection. As soon as an host is suspected, a connection attempt
         * to that host is immediately tried. If this succeed, then it means that
         * the connection was disfunctional but that the node was not really down.
         * If this fails however, this means the node is truly dead, onDown() is
         * called and further reconnection attempts are scheduled according to the
         * {@link com.datastax.driver.core.policies.ReconnectionPolicy} in place.
         * <p>
         * When this event is triggered, it is possible to call the host
         * {@link #getInitialReconnectionAttemptFuture} method to wait until the
         * initial and immediate reconnection attempt succeed or fail.
         * <p>
         * Note that some StateListener may ignore that event. If a node that
         * that is suspected down turns out to be truly down (that is, the driver
         * cannot successfully connect to it right away), then {@link #onDown} will
         * be called.
         */
    public void onSuspected(Host host);

    /**
         * Called when a node is determined to be down.
         *
         * @param host the host that has been detected down.
         */
    public void onDown(Host host);

    /**
         * Called when a node is removed from the cluster.
         *
         * @param host the removed host.
         */
    public void onRemove(Host host);
  }
}