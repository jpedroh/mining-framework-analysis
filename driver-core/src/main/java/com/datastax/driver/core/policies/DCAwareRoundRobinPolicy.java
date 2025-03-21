package com.datastax.driver.core.policies;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.base.Joiner;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.AbstractIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Statement;

/**
 * A data-center aware Round-robin load balancing policy.
 * <p>
 * This policy provides round-robin queries over the node of the local
 * data center. It also includes in the query plans returned a configurable
 * number of hosts in the remote data centers, but those are always tried
 * after the local nodes. In other words, this policy guarantees that no
 * host in a remote data center will be queried unless no host in the local
 * data center can be reached.
 * <p>
 * If used with a single data center, this policy is equivalent to the
 * {@code LoadBalancingPolicy.RoundRobin} policy, but its DC awareness
 * incurs a slight overhead so the {@code LoadBalancingPolicy.RoundRobin}
 * policy could be preferred to this policy in that case.
 */
public class DCAwareRoundRobinPolicy implements LoadBalancingPolicy, CloseableLoadBalancingPolicy {
  private static final Logger logger = LoggerFactory.getLogger(DCAwareRoundRobinPolicy.class);

  private final String UNSET = "";

  private final ConcurrentMap<String, CopyOnWriteArrayList<Host>> perDcLiveHosts = new ConcurrentHashMap<String, CopyOnWriteArrayList<Host>>();

  private final AtomicInteger index = new AtomicInteger();

  @VisibleForTesting volatile String localDc;

  private final ConcurrentMap<String, CopyOnWriteArrayList<Host>> perDcSuspectedHosts = new ConcurrentHashMap<String, CopyOnWriteArrayList<Host>>();

  private final int usedHostsPerRemoteDc;

  private final boolean dontHopForLocalCL;

  private volatile Configuration configuration;

  /**
     * Creates a new datacenter aware round robin policy that auto-discover
     * the local data-center.
     * <p>
     * If this constructor is used, the data-center used as local will the
     * data-center of the first Cassandra node the driver connects to. This
     * will always be ok if all the contact points use at {@code Cluster}
     * creation are in the local data-center. If it's not the case, you should
     * provide the local data-center name yourself by using one of the other
     * constructor of this class.
     * <p>
     * This constructor is a shortcut for {@code new DCAwareRoundRobinPolicy(null)},
     * and as such will ignore all hosts in remote data-centers.
     */
  public DCAwareRoundRobinPolicy() {
    this(null, 0, false, true);
  }

  /**
     * Creates a new datacenter aware round robin policy given the name of
     * the local datacenter.
     * <p>
     * The name of the local datacenter provided must be the local
     * datacenter name as known by Cassandra.
     * <p>
     * The policy created will ignore all remote hosts. In other words,
     * this is equivalent to {@code new DCAwareRoundRobinPolicy(localDc, 0)}.
     *
     * @param localDc the name of the local datacenter (as known by
     * Cassandra). If this is {@code null}, the policy will default to the
     * data-center of the first node connected to.
     */
  public DCAwareRoundRobinPolicy(String localDc) {
    this(localDc, 0, false, false);
  }

  /**
     * Creates a new DCAwareRoundRobin policy given the name of the local
     * datacenter and that uses the provided number of host per remote
     * datacenter as failover for the local hosts.
     * <p>
     * The name of the local datacenter provided must be the local
     * datacenter name as known by Cassandra.
     * <p>
     * If {@code usedHostsPerRemoteDc > 0}, then if for a query no host
     * in the local datacenter can be reached and if the consistency
     * level of the query is not {@code LOCAL_ONE} or {@code LOCAL_QUORUM},
     * then up to {@code usedHostsPerRemoteDc} host per remote data-center
     * will be tried by the policy as a fallback. Please note that no
     * remote host will be used for {@code LOCAL_ONE} and {@code LOCAL_QUORUM}
     * since this would change the meaning of the consistency level (and
     * thus somewhat break the consistency contract).
     *
     * @param localDc the name of the local datacenter (as known by
     * Cassandra). If this is {@code null}, the policy will default to the
     * data-center of the first node connected to.
     * @param usedHostsPerRemoteDc the number of host per remote
     * datacenter that policies created by the returned factory should
     * consider. Created policies {@code distance} method will return a
     * {@code HostDistance.REMOTE} distance for only {@code
     * usedHostsPerRemoteDc} hosts per remote datacenter. Other hosts
     * of the remote datacenters will be ignored (and thus no
     * connections to them will be maintained).
     */
  public DCAwareRoundRobinPolicy(String localDc, int usedHostsPerRemoteDc) {
    this(localDc, usedHostsPerRemoteDc, false, false);
  }

  /**
     * Creates a new DCAwareRoundRobin policy given the name of the local
     * datacenter and that uses the provided number of host per remote
     * datacenter as failover for the local hosts.
     * <p>
     * This constructor is equivalent to {@link #DCAwareRoundRobinPolicy(String, int)}
     * but allows to override the policy of never using remote data-center
     * nodes for {@code LOCAL_ONE} and {@code LOCAL_QUORUM} queries. It is
     * however inadvisable to do so in almost all cases, as this would
     * potentially break consistency guarantees and if you are fine with that,
     * it's probably better to use a weaker consitency like {@code ONE}, {@code
     * TWO} or {@code THREE}. As such, this constructor should generally
     * be avoided in favor of {@link #DCAwareRoundRobinPolicy(String, int)}.
     * Use it only if you know and understand what you do.
     *
     * @param localDc the name of the local datacenter (as known by
     * Cassandra). If this is {@code null}, the policy will default to the
     * data-center of the first node connected to.
     * @param usedHostsPerRemoteDc the number of host per remote
     * datacenter that policies created by the returned factory should
     * consider. Created policies {@code distance} method will return a
     * {@code HostDistance.REMOTE} distance for only {@code
     * usedHostsPerRemoteDc} hosts per remote datacenter. Other hosts
     * of the remote datacenters will be ignored (and thus no
     * connections to them will be maintained).
     * @param allowRemoteDCsForLocalConsistencyLevel whether or not the
     * policy may return remote host when building query plan for query
     * having consitency {@code LOCAL_ONE} and {@code LOCAL_QUORUM}.
     */
  public DCAwareRoundRobinPolicy(String localDc, int usedHostsPerRemoteDc, boolean allowRemoteDCsForLocalConsistencyLevel) {
    this(localDc, usedHostsPerRemoteDc, allowRemoteDCsForLocalConsistencyLevel, false);
  }

  private DCAwareRoundRobinPolicy(String localDc, int usedHostsPerRemoteDc, boolean allowRemoteDCsForLocalConsistencyLevel, boolean allowEmptyLocalDc) {
    if (!allowEmptyLocalDc && Strings.isNullOrEmpty(localDc)) {
      throw new IllegalArgumentException("Null or empty data center specified for DC-aware policy");
    }
    this.localDc = localDc == null ? UNSET : localDc;
    this.usedHostsPerRemoteDc = usedHostsPerRemoteDc;
    this.dontHopForLocalCL = !allowRemoteDCsForLocalConsistencyLevel;
  }

  @Override public void init(Cluster cluster, Collection<Host> hosts) {
    if (localDc != UNSET) {
      logger.info("Using provided data-center name \'{}\' for DCAwareRoundRobinPolicy", localDc);
    }
    this.configuration = cluster.getConfiguration();
    ArrayList<String> notInLocalDC = new ArrayList<String>();
    for (Host host : hosts) {
      String dc = dc(host);
      if (localDc == UNSET && dc != UNSET) {
        logger.info("Using data-center name \'{}\' for DCAwareRoundRobinPolicy (if this is incorrect, please provide the correct datacenter name with DCAwareRoundRobinPolicy constructor)", dc);
        localDc = dc;
      } else {
        if (!dc.equals(localDc)) {
          notInLocalDC.add(String.format("%s (%s)", host.toString(), dc));
        }
      }
      if (!dc.equals(localDc)) {
        notInLocalDC.add(String.format("%s (%s)", host.toString(), host.getDatacenter()));
      }
      CopyOnWriteArrayList<Host> prev = perDcLiveHosts.get(dc);
      if (prev == null) {
        perDcLiveHosts.put(dc, new CopyOnWriteArrayList<Host>(Collections.singletonList(host)));
      } else {
        prev.addIfAbsent(host);
      }
    }
    if (notInLocalDC.size() > 0) {
      String nonLocalHosts = Joiner.on(",").join(notInLocalDC);
      logger.warn(
<<<<<<< /usr/src/app/output/datastax/java-driver/5a6a084a6ed84a2742a7c2211c1071193df4c2e7/driver-core/src/main/java/com/datastax/driver/core/policies/DCAwareRoundRobinPolicy.java/left.java
      "Some contact points don\'t match specified local data center. Local DC = {}. Non-conforming contact points: {}"
=======
      "Some contact points don\'t match local data center. Local DC = {}. Non-conforming contact points: {}"
>>>>>>> /usr/src/app/output/datastax/java-driver/5a6a084a6ed84a2742a7c2211c1071193df4c2e7/driver-core/src/main/java/com/datastax/driver/core/policies/DCAwareRoundRobinPolicy.java/right.java
      , localDc, nonLocalHosts);
    }
  }

  private String dc(Host host) {
    String dc = host.getDatacenter();
    return dc == null ? localDc : dc;
  }

  @SuppressWarnings(value = { "unchecked" }) private static CopyOnWriteArrayList<Host> cloneList(CopyOnWriteArrayList<Host> list) {
    return (CopyOnWriteArrayList<Host>) list.clone();
  }

  /**
     * Return the HostDistance for the provided host.
     * <p>
     * This policy consider nodes in the local datacenter as {@code LOCAL}.
     * For each remote datacenter, it considers a configurable number of
     * hosts as {@code REMOTE} and the rest is {@code IGNORED}.
     * <p>
     * To configure how many host in each remote datacenter is considered
     * {@code REMOTE}, see {@link #DCAwareRoundRobinPolicy(String, int)}.
     *
     * @param host the host of which to return the distance of.
     * @return the HostDistance to {@code host}.
     */
  @Override public HostDistance distance(Host host) {
    String dc = dc(host);
    if (dc == UNSET || dc.equals(localDc)) {
      return HostDistance.LOCAL;
    }
    CopyOnWriteArrayList<Host> dcHosts = perDcLiveHosts.get(dc);
    if (dcHosts == null || usedHostsPerRemoteDc == 0) {
      return HostDistance.IGNORED;
    }
    dcHosts = cloneList(dcHosts);
    return dcHosts.subList(0, Math.min(dcHosts.size(), usedHostsPerRemoteDc)).contains(host) ? HostDistance.REMOTE : HostDistance.IGNORED;
  }

  /**
     * Returns the hosts to use for a new query.
     * <p>
     * The returned plan will always try each known host in the local
     * datacenter first, and then, if none of the local host is reachable,
     * will try up to a configurable number of other host per remote datacenter.
     * The order of the local node in the returned query plan will follow a
     * Round-robin algorithm.
     *
     * @param loggedKeyspace the keyspace currently logged in on for this
     * query.
     * @param statement the query for which to build the plan.
     * @return a new query plan, i.e. an iterator indicating which host to
     * try first for querying, which one to use as failover, etc...
     */
  @Override public Iterator<Host> newQueryPlan(String loggedKeyspace, final Statement statement) {
    CopyOnWriteArrayList<Host> localLiveHosts = perDcLiveHosts.get(localDc);
    final List<Host> hosts = localLiveHosts == null ? Collections.<Host>emptyList() : cloneList(localLiveHosts);
    final int startIdx = index.getAndIncrement();
    return new AbstractIterator<Host>() {
      private int idx = startIdx;

      private int remainingLocal = hosts.size();

      private Iterator<Host> localSuspected;

      private Iterator<String> remoteDcs;

      private List<Host> currentDcHosts;

      private int currentDcRemaining;

      private Iterator<Host> currentDcSuspected;

      @Override protected Host computeNext() {
        if (remainingLocal > 0) {
          remainingLocal--;
          int c = idx++ % hosts.size();
          if (c < 0) {
            c += hosts.size();
          }
          return hosts.get(c);
        }
        if (localSuspected == null) {
          List<Host> l = perDcSuspectedHosts.get(localDc);
          localSuspected = l == null ? Collections.<Host>emptySet().iterator() : l.iterator();
        }
        while (localSuspected.hasNext()) {
          Host h = localSuspected.next();
          waitOnReconnection(h);
          if (h.isUp()) {
            return h;
          }
        }
        ConsistencyLevel cl = statement.getConsistencyLevel() == null ? configuration.getQueryOptions().getConsistencyLevel() : statement.getConsistencyLevel();
        if (dontHopForLocalCL && cl.isDCLocal()) {
          return endOfData();
        }
        if (remoteDcs == null) {
          Set<String> copy = new HashSet<String>(perDcLiveHosts.keySet());
          copy.remove(localDc);
          remoteDcs = copy.iterator();
        }
        while (true) {
          if (currentDcHosts != null && currentDcRemaining > 0) {
            currentDcRemaining--;
            int c = idx++ % currentDcHosts.size();
            if (c < 0) {
              c += currentDcHosts.size();
            }
            return currentDcHosts.get(c);
          }
          if (currentDcSuspected != null) {
            while (currentDcSuspected.hasNext()) {
              Host h = currentDcSuspected.next();
              waitOnReconnection(h);
              if (h.isUp()) {
                return h;
              }
            }
          }
          if (!remoteDcs.hasNext()) {
            break;
          }
          String nextRemoteDc = remoteDcs.next();
          CopyOnWriteArrayList<Host> nextDcHosts = perDcLiveHosts.get(nextRemoteDc);
          if (nextDcHosts != null) {
            List<Host> dcHosts = cloneList(nextDcHosts);
            currentDcHosts = dcHosts.subList(0, Math.min(dcHosts.size(), usedHostsPerRemoteDc));
            currentDcRemaining = currentDcHosts.size();
          }
          List<Host> suspectedList = perDcSuspectedHosts.get(nextRemoteDc);
          currentDcSuspected = suspectedList == null ? null : suspectedList.iterator();
        }
        return endOfData();
      }
    };
  }

  private void waitOnReconnection(Host h) {
    try {
      h.getInitialReconnectionAttemptFuture().get(configuration.getSocketOptions().getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      throw new AssertionError(e);
    } catch (TimeoutException e) {
      logger.debug("Timeout while waiting only host initial reconnection future", e);
    }
  }

  @Override public void onUp(Host host) {
    String dc = dc(host);
    if (localDc == UNSET && dc != UNSET) {
      logger.info("Using data-center name \'{}\' for DCAwareRoundRobinPolicy (if this is incorrect, please provide the correct datacenter name with DCAwareRoundRobinPolicy constructor)", dc);
      localDc = dc;
    }
    CopyOnWriteArrayList<Host> dcHosts = perDcLiveHosts.get(dc);
    if (dcHosts == null) {
      CopyOnWriteArrayList<Host> newMap = new CopyOnWriteArrayList<Host>(Collections.singletonList(host));
      dcHosts = perDcLiveHosts.putIfAbsent(dc, newMap);
      if (dcHosts == null) {
        return;
      }
    }
    dcHosts.addIfAbsent(host);
    CopyOnWriteArrayList<Host> dcSuspected = perDcSuspectedHosts.get(dc(host));
    if (dcSuspected != null) {
      dcSuspected.remove(host);
    }
  }

  @Override public void onSuspected(Host host) {
    String dc = dc(host);
    CopyOnWriteArrayList<Host> dcSuspected = perDcSuspectedHosts.get(dc);
    if (dcSuspected == null) {
      CopyOnWriteArrayList<Host> newMap = new CopyOnWriteArrayList<Host>(Collections.singletonList(host));
      dcSuspected = perDcSuspectedHosts.putIfAbsent(dc, newMap);
      if (dcSuspected == null) {
        return;
      }
    }
    dcSuspected.addIfAbsent(host);
  }

  @Override public void onDown(Host host) {
    CopyOnWriteArrayList<Host> dcHosts = perDcLiveHosts.get(dc(host));
    if (dcHosts != null) {
      dcHosts.remove(host);
    }
    CopyOnWriteArrayList<Host> dcSuspected = perDcSuspectedHosts.get(dc(host));
    if (dcSuspected != null) {
      dcSuspected.remove(host);
    }
  }

  @Override public void onAdd(Host host) {
    onUp(host);
  }

  @Override public void onRemove(Host host) {
    onDown(host);
  }

  @Override public void close() {
  }
}