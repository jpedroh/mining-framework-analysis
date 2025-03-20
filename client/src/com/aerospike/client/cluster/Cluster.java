package com.aerospike.client.cluster;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Host;
import com.aerospike.client.Log;
import com.aerospike.client.ResultCode;
import com.aerospike.client.admin.AdminCommand;
import com.aerospike.client.async.EventLoop;
import com.aerospike.client.async.EventLoopStats;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventState;
import com.aerospike.client.async.Monitor;
import com.aerospike.client.async.NettyTlsContext;
import com.aerospike.client.async.NioEventLoops;
import com.aerospike.client.cluster.Node.AsyncPool;
import com.aerospike.client.command.Buffer;
import com.aerospike.client.listener.ClusterStatsListener;
import com.aerospike.client.policy.AuthMode;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.TCPKeepAlive;
import com.aerospike.client.policy.TlsPolicy;
import com.aerospike.client.util.ThreadLocalData;
import com.aerospike.client.util.Util;

public class Cluster implements Runnable, Closeable {
  protected final String clusterName;

  private volatile Host[] seeds;

  protected final HashMap<Host, Node> aliases;

  protected final HashMap<String, Node> nodesMap;

  private volatile Node[] nodes;

  public volatile HashMap<String, Partitions> partitionMap;

  protected final Map<String, String> ipMap;

  public final TlsPolicy tlsPolicy;

  public final NettyTlsContext nettyTlsContext;

  public final AuthMode authMode;

  protected final byte[] user;

  private byte[] password;

  private byte[] passwordHash;

  private final AtomicInteger nodeIndex;

  final AtomicInteger replicaIndex;

  private final AtomicInteger recoverCount;

  private final ConcurrentLinkedDeque<ConnectionRecover> recoverQueue;

  private final ExecutorService threadPool;

  public final TCPKeepAlive keepAlive;

  public final EventLoops eventLoops;

  public final EventState[] eventState;

  private final long maxSocketIdleNanosTran;

  private final long maxSocketIdleNanosTrim;

  protected final int minConnsPerNode;

  protected final int maxConnsPerNode;

  protected final int asyncMinConnsPerNode;

  protected final int asyncMaxConnsPerNode;

  protected final int connPoolsPerNode;

  int maxErrorRate;

  int errorRateWindow;

  public final int connectTimeout;

  public final int loginTimeout;

  public final int closeTimeout;

  public final int[] rackIds;

  private volatile int invalidNodeCount;

  private final int tendInterval;

  private int tendCount;

  private Thread tendThread;

  protected volatile boolean tendValid;

  private final boolean sharedThreadPool;

  protected final boolean useServicesAlternate;

  final boolean rackAware;

  public final boolean authEnabled;

  public boolean hasPartitionQuery;

  private boolean asyncComplete;

  public Cluster(ClientPolicy policy, Host[] hosts) {
    this.clusterName = policy.clusterName;
    this.tlsPolicy = policy.tlsPolicy;
    this.authMode = policy.authMode;
    if (tlsPolicy != null) {
      boolean useClusterName = clusterName != null && clusterName.length() > 0;
      for (int i = 0; i < hosts.length; i++) {
        Host host = hosts[i];
        if (host.tlsName == null) {
          String tlsName = useClusterName ? clusterName : host.name;
          hosts[i] = new Host(host.name, tlsName, host.port);
        }
      }
    } else {
      if (authMode == AuthMode.EXTERNAL || authMode == AuthMode.PKI) {
        throw new AerospikeException("TLS is required for authentication mode: " + authMode);
      }
    }
    this.seeds = hosts;
    if (policy.authMode == AuthMode.PKI) {
      this.authEnabled = true;
      this.user = null;
    } else {
      if (policy.user != null && policy.user.length() > 0) {
        this.authEnabled = true;
        this.user = Buffer.stringToUtf8(policy.user);
        if (authMode != AuthMode.INTERNAL) {
          this.password = Buffer.stringToUtf8(policy.password);
        }
        String pass = policy.password;
        if (pass == null) {
          pass = "";
        }
        pass = AdminCommand.hashPassword(pass);
        this.passwordHash = Buffer.stringToUtf8(pass);
      } else {
        this.authEnabled = false;
        this.user = null;
      }
    }
    if (policy.maxSocketIdle < 0) {
      throw new AerospikeException("Invalid maxSocketIdle: " + policy.maxSocketIdle);
    }
    if (policy.maxSocketIdle == 0) {
      maxSocketIdleNanosTran = 0;
      maxSocketIdleNanosTrim = TimeUnit.SECONDS.toNanos(55);
    } else {
      maxSocketIdleNanosTran = TimeUnit.SECONDS.toNanos(policy.maxSocketIdle);
      maxSocketIdleNanosTrim = maxSocketIdleNanosTran;
    }
    minConnsPerNode = policy.minConnsPerNode;
    maxConnsPerNode = policy.maxConnsPerNode;
    if (minConnsPerNode > maxConnsPerNode) {
      throw new AerospikeException("Invalid connection range: " + minConnsPerNode + " - " + maxConnsPerNode);
    }
    asyncMinConnsPerNode = policy.asyncMinConnsPerNode;
    asyncMaxConnsPerNode = (policy.asyncMaxConnsPerNode >= 0) ? policy.asyncMaxConnsPerNode : policy.maxConnsPerNode;
    if (asyncMinConnsPerNode > asyncMaxConnsPerNode) {
      throw new AerospikeException("Invalid async connection range: " + asyncMinConnsPerNode + " - " + asyncMaxConnsPerNode);
    }
    connPoolsPerNode = policy.connPoolsPerNode;
    maxErrorRate = policy.maxErrorRate;
    errorRateWindow = policy.errorRateWindow;
    connectTimeout = policy.timeout;
    loginTimeout = policy.loginTimeout;
    closeTimeout = policy.closeTimeout;
    tendInterval = policy.tendInterval;
    ipMap = policy.ipMap;
    keepAlive = policy.keepAlive;
    if (policy.threadPool == null) {
      threadPool = Executors.newCachedThreadPool(new ThreadDaemonFactory());
    } else {
      threadPool = policy.threadPool;
    }
    sharedThreadPool = policy.sharedThreadPool;
    useServicesAlternate = policy.useServicesAlternate;
    rackAware = policy.rackAware;
    if (policy.rackIds != null && policy.rackIds.size() > 0) {
      List<Integer> list = policy.rackIds;
      int max = list.size();
      rackIds = new int[max];
      for (int i = 0; i < max; i++) {
        rackIds[i] = list.get(i);
      }
    } else {
      rackIds = new int[] { policy.rackId };
    }
    aliases = new HashMap<Host, Node>();
    nodesMap = new HashMap<String, Node>();
    nodes = new Node[0];
    partitionMap = new HashMap<String, Partitions>();
    nodeIndex = new AtomicInteger();
    replicaIndex = new AtomicInteger();
    recoverCount = new AtomicInteger();
    recoverQueue = new ConcurrentLinkedDeque<ConnectionRecover>();
    eventLoops = policy.eventLoops;
    if (eventLoops != null) {
      EventLoop[] loops = eventLoops.getArray();
      if (asyncMaxConnsPerNode < loops.length) {
        throw new AerospikeException("asyncMaxConnsPerNode " + asyncMaxConnsPerNode + " must be >= event loop count " + loops.length);
      }
      eventState = new EventState[loops.length];
      for (int i = 0; i < loops.length; i++) {
        eventState[i] = loops[i].createState();
      }
      if (policy.tlsPolicy != null) {
        if (eventLoops instanceof NioEventLoops) {
          throw new AerospikeException("TLS not supported in direct NIO event loops");
        }
        if (policy.tlsPolicy.nettyContext != null) {
          nettyTlsContext = policy.tlsPolicy.nettyContext;
        } else {
          nettyTlsContext = new NettyTlsContext(policy.tlsPolicy);
        }
      } else {
        nettyTlsContext = null;
      }
    } else {
      eventState = null;
      nettyTlsContext = null;
    }
    if (policy.forceSingleNode) {
      try {
        forceSingleNode();
      } catch (RuntimeException e) {
        close();
        throw e;
      }
    } else {
      initTendThread(policy.failIfNotConnected);
    }
  }

  public void forceSingleNode() {
    tendValid = true;
    tendThread = new Thread(this);
    Host seed = seeds[0];
    NodeValidator nv = new NodeValidator();
    Node node = null;
    try {
      node = nv.seedNode(this, seed, null);
    } catch (Exception e) {
      throw new AerospikeException("Seed " + seed + " failed: " + e.getMessage(), e);
    }
    node.createMinConnections();
    HashMap<String, Node> nodesToAdd = new HashMap<String, Node>(1);
    nodesToAdd.put(node.getName(), node);
    addNodes(nodesToAdd);
    Peers peers = new Peers(nodes.length + 16);
    node.refreshPartitions(peers);
    for (Partitions partitions : partitionMap.values()) {
      for (AtomicReferenceArray<Node> nodeArray : partitions.replicas) {
        int max = nodeArray.length();
        for (int i = 0; i < max; i++) {
          nodeArray.set(i, node);
        }
      }
    }
  }

  public void initTendThread(boolean failIfNotConnected) {
    waitTillStabilized(failIfNotConnected);
    if (Log.debugEnabled()) {
      for (Host host : seeds) {
        Log.debug("Add seed " + host);
      }
    }
    ArrayList<Host> seedsToAdd = new ArrayList<Host>(nodes.length);
    for (Node node : nodes) {
      Host host = node.getHost();
      if (!findSeed(host)) {
        seedsToAdd.add(host);
      }
    }
    if (seedsToAdd.size() > 0) {
      addSeeds(seedsToAdd.toArray(new Host[seedsToAdd.size()]));
    }
    tendValid = true;
    tendThread = new Thread(this);
    tendThread.setName("tend");
    tendThread.setDaemon(true);
    tendThread.start();
  }

  public final void addSeeds(Host[] hosts) {
    Host[] seedArray = new Host[seeds.length + hosts.length];
    int count = 0;
    for (Host seed : seeds) {
      seedArray[count++] = seed;
    }
    for (Host host : hosts) {
      if (Log.debugEnabled()) {
        Log.debug("Add seed " + host);
      }
      seedArray[count++] = host;
    }
    seeds = seedArray;
  }

  private final boolean findSeed(Host search) {
    for (Host seed : seeds) {
      if (seed.equals(search)) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Tend the cluster until it has stabilized and return control.
	 * This helps avoid initial database request timeout issues when
	 * a large number of threads are initiated at client startup.
	 */
  private final void waitTillStabilized(boolean failIfNotConnected) {
    tend(failIfNotConnected, true);
    if (nodes.length == 0) {
      String message = "Cluster seed(s) failed";
      if (failIfNotConnected) {
        throw new AerospikeException(message);
      } else {
        Log.warn(message);
      }
    }
  }

  public final void run() {
    while (tendValid) {
      try {
        tend(false, false);
      } catch (Exception e) {
        if (Log.warnEnabled()) {
          Log.warn("Cluster tend failed: " + Util.getErrorMessage(e));
        }
      }
      Util.sleep(tendInterval);
    }
  }

  /**
	 * Check health of all nodes in the cluster.
	 */
  private final void tend(boolean failIfNotConnected, boolean isInit) {
    Peers peers = new Peers(nodes.length + 16);
    for (Node node : nodes) {
      node.referenceCount = 0;
      node.partitionChanged = false;
      node.rebalanceChanged = false;
    }
    if (nodes.length == 0) {
      seedNode(peers, failIfNotConnected);
      if (isInit && failIfNotConnected && nodes.length == 1 && peers.getInvalidCount() > 0) {
        peers.clusterInitError();
      }
    } else {
      for (Node node : nodes) {
        node.refresh(peers);
      }
      if (peers.genChanged) {
        peers.refreshCount = 0;
        for (Node node : nodes) {
          node.refreshPeers(peers);
        }
        ArrayList<Node> removeList = findNodesToRemove(peers.refreshCount);
        if (removeList.size() > 0) {
          removeNodes(removeList);
        }
      }
      if (peers.nodes.size() > 0) {
        addNodes(peers.nodes);
        refreshPeers(peers);
      }
    }
    invalidNodeCount = peers.getInvalidCount();
    for (Node node : nodes) {
      if (node.partitionChanged) {
        node.refreshPartitions(peers);
      }
      if (node.rebalanceChanged) {
        node.refreshRacks();
      }
    }
    tendCount++;
    if (tendCount % 30 == 0) {
      for (Node node : nodes) {
        node.balanceConnections();
      }
      if (eventState != null) {
        for (EventState es : eventState) {
          final EventLoop eventLoop = es.eventLoop;
          eventLoop.execute(new Runnable() {
            public void run() {
              try {
                final Node[] nodeArray = nodes;
                for (Node node : nodeArray) {
                  node.balanceAsyncConnections(eventLoop);
                }
              } catch (Exception e) {
                if (Log.warnEnabled()) {
                  Log.warn("balanceAsyncConnections failed: " + Util.getErrorMessage(e));
                }
              }
            }
          });
        }
      }
    }
    if (maxErrorRate > 0 && tendCount % errorRateWindow == 0) {
      for (Node node : nodes) {
        node.resetErrorCount();
      }
    }
    processRecoverQueue();
  }

  private final boolean seedNode(Peers peers, boolean failIfNotConnected) {
    Host[] seedArray = seeds;
    Exception[] exceptions = null;
    NodeValidator nv = new NodeValidator();
    for (int i = 0; i < seedArray.length; i++) {
      Host seed = seedArray[i];
      try {
        Node node = nv.seedNode(this, seed, peers);
        if (node != null) {
          addSeedAndPeers(node, peers);
          return true;
        }
      } catch (Exception e) {
        peers.fail(seed);
        if (seed.tlsName != null && tlsPolicy == null) {
          throw new AerospikeException.Connection("Seed host tlsName \'" + seed.tlsName + "\' defined but client tlsPolicy not enabled", e);
        }
        if (failIfNotConnected) {
          if (exceptions == null) {
            exceptions = new Exception[seedArray.length];
          }
          exceptions[i] = e;
        } else {
          if (Log.warnEnabled()) {
            Log.warn("Seed " + seed + " failed: " + Util.getErrorMessage(e));
          }
        }
      }
    }
    if (nv.fallback != null) {
      peers.refreshCount = 1;
      addSeedAndPeers(nv.fallback, peers);
      return true;
    }
    if (failIfNotConnected) {
      StringBuilder sb = new StringBuilder(500);
      sb.append("Failed to connect to [" + seedArray.length + "] host(s): ");
      sb.append(System.lineSeparator());
      for (int i = 0; i < seedArray.length; i++) {
        sb.append(seedArray[i]);
        sb.append(' ');
        Exception ex = exceptions == null ? null : exceptions[i];
        if (ex != null) {
          sb.append(ex.getMessage());
          sb.append(System.lineSeparator());
        }
      }
      throw new AerospikeException.Connection(sb.toString());
    }
    return false;
  }

  private void addSeedAndPeers(Node seed, Peers peers) {
    seed.createMinConnections();
    nodesMap.clear();
    addNodes(seed, peers);
    if (peers.nodes.size() > 0) {
      refreshPeers(peers);
    }
  }

  private void refreshPeers(Peers peers) {
    while (true) {
      Node[] nodeArray = new Node[peers.nodes.size()];
      int count = 0;
      for (Node node : peers.nodes.values()) {
        nodeArray[count++] = node;
      }
      peers.nodes.clear();
      for (Node node : nodeArray) {
        node.refreshPeers(peers);
      }
      if (peers.nodes.size() > 0) {
        addNodes(peers.nodes);
      } else {
        break;
      }
    }
  }

  protected Node createNode(NodeValidator nv) {
    Node node = new Node(this, nv);
    node.createMinConnections();
    return node;
  }

  private final ArrayList<Node> findNodesToRemove(int refreshCount) {
    ArrayList<Node> removeList = new ArrayList<Node>();
    for (Node node : nodes) {
      if (!node.isActive()) {
        removeList.add(node);
        continue;
      }
      if (refreshCount == 0 && node.failures >= 5) {
        removeList.add(node);
        continue;
      }
      if (nodes.length > 1 && refreshCount >= 1 && node.referenceCount == 0) {
        if (node.failures == 0) {
          if (!findNodeInPartitionMap(node)) {
            removeList.add(node);
          }
        } else {
          removeList.add(node);
        }
      }
    }
    return removeList;
  }

  private final boolean findNodeInPartitionMap(Node filter) {
    for (Partitions partitions : partitionMap.values()) {
      for (AtomicReferenceArray<Node> nodeArray : partitions.replicas) {
        int max = nodeArray.length();
        for (int i = 0; i < max; i++) {
          Node node = nodeArray.get(i);
          if (node == filter) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private void addNodes(Node seed, Peers peers) {
    Node[] nodeArray = new Node[peers.nodes.size() + 1];
    int count = 0;
    nodeArray[count++] = seed;
    addNode(seed);
    for (Node peer : peers.nodes.values()) {
      nodeArray[count++] = peer;
      addNode(peer);
    }
    hasPartitionQuery = Cluster.supportsPartitionQuery(nodeArray);
    nodes = nodeArray;
  }

  /**
	 * Add nodes using copy on write semantics.
	 */
  private final void addNodes(HashMap<String, Node> nodesToAdd) {
    Node[] nodeArray = new Node[nodes.length + nodesToAdd.size()];
    int count = 0;
    for (Node node : nodes) {
      nodeArray[count++] = node;
    }
    for (Node node : nodesToAdd.values()) {
      nodeArray[count++] = node;
      addNode(node);
    }
    hasPartitionQuery = Cluster.supportsPartitionQuery(nodeArray);
    nodes = nodeArray;
  }

  private final void addNode(Node node) {
    if (Log.infoEnabled()) {
      Log.info("Add node " + node);
    }
    nodesMap.put(node.getName(), node);
    for (Host alias : node.aliases) {
      aliases.put(alias, node);
    }
  }

  private final void removeNodes(List<Node> nodesToRemove) {
    for (Node node : nodesToRemove) {
      nodesMap.remove(node.getName());
      for (Host alias : node.aliases) {
        aliases.remove(alias);
      }
      node.close();
    }
    removeNodesCopy(nodesToRemove);
  }

  /**
	 * Remove nodes using copy on write semantics.
	 */
  private final void removeNodesCopy(List<Node> nodesToRemove) {
    Node[] nodeArray = new Node[nodes.length - nodesToRemove.size()];
    int count = 0;
    for (Node node : nodes) {
      if (findNode(node, nodesToRemove)) {
        if (Log.infoEnabled()) {
          Log.info("Remove node " + node);
        }
      } else {
        nodeArray[count++] = node;
      }
    }
    if (count < nodeArray.length) {
      if (Log.warnEnabled()) {
        Log.warn("Node remove mismatch. Expected " + nodeArray.length + " Received " + count);
      }
      Node[] nodeArray2 = new Node[count];
      System.arraycopy(nodeArray, 0, nodeArray2, 0, count);
      nodeArray = nodeArray2;
    }
    hasPartitionQuery = Cluster.supportsPartitionQuery(nodeArray);
    nodes = nodeArray;
  }

  private final static boolean findNode(Node search, List<Node> nodeList) {
    for (Node node : nodeList) {
      if (node.equals(search)) {
        return true;
      }
    }
    return false;
  }

  public final boolean isConnected() {
    Node[] nodeArray = nodes;
    if (nodeArray.length > 0 && tendValid) {
      for (Node node : nodeArray) {
        if (node.active && node.failures < 5) {
          return true;
        }
      }
    }
    return false;
  }

  public final Node getRandomNode() throws AerospikeException.InvalidNode {
    Node[] nodeArray = nodes;
    if (nodeArray.length > 0) {
      int index = Math.abs(nodeIndex.getAndIncrement() % nodeArray.length);
      for (int i = 0; i < nodeArray.length; i++) {
        Node node = nodeArray[index];
        if (node.isActive()) {
          return node;
        }
        index++;
        index %= nodeArray.length;
      }
    }
    throw new AerospikeException.InvalidNode("Cluster is empty");
  }

  public final Node[] getNodes() {
    Node[] nodeArray = nodes;
    return nodeArray;
  }

  public final Node[] validateNodes() {
    Node[] nodeArray = nodes;
    if (nodeArray.length == 0) {
      throw new AerospikeException(ResultCode.SERVER_NOT_AVAILABLE, "Cluster is empty");
    }
    return nodeArray;
  }

  public final Node getNode(String nodeName) throws AerospikeException.InvalidNode {
    Node node = findNode(nodeName);
    if (node == null) {
      throw new AerospikeException.InvalidNode("Invalid node name: " + nodeName);
    }
    return node;
  }

  protected final Node findNode(String nodeName) {
    Node[] nodeArray = nodes;
    for (Node node : nodeArray) {
      if (node.getName().equals(nodeName)) {
        return node;
      }
    }
    return null;
  }

  public final boolean isConnCurrentTran(long lastUsed) {
    return maxSocketIdleNanosTran == 0 || (System.nanoTime() - lastUsed) <= maxSocketIdleNanosTran;
  }

  public final boolean isConnCurrentTrim(long lastUsed) {
    return (System.nanoTime() - lastUsed) <= maxSocketIdleNanosTrim;
  }

  public final void recoverConnection(ConnectionRecover cs) {
    if (cs.isComplete()) {
      return;
    }
    if (recoverCount.getAndIncrement() < 10000) {
      recoverQueue.offerLast(cs);
    } else {
      recoverCount.getAndDecrement();
      cs.abort();
    }
  }

  private void processRecoverQueue() {
    byte[] buf = ThreadLocalData.getBuffer();
    ConnectionRecover last = recoverQueue.peekLast();
    ConnectionRecover cs;
    while ((cs = recoverQueue.pollFirst()) != null) {
      if (cs.drain(buf)) {
        recoverCount.getAndDecrement();
      } else {
        recoverQueue.offerLast(cs);
      }
      if (cs == last) {
        break;
      }
    }
  }

  public final ClusterStats getStats() {
    final Node[] nodeArray = nodes;
    NodeStats[] nodeStats = new NodeStats[nodeArray.length];
    int count = 0;
    for (Node node : nodeArray) {
      nodeStats[count++] = new NodeStats(node);
    }
    int threadsInUse = 0;
    if (threadPool instanceof ThreadPoolExecutor) {
      ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPool;
      threadsInUse = tpe.getActiveCount();
    }
    EventLoopStats[] eventLoopStats = null;
    if (eventLoops != null) {
      EventLoop[] eventLoopArray = eventLoops.getArray();
      for (EventLoop eventLoop : eventLoopArray) {
        if (eventLoop.inEventLoop()) {
          eventLoopStats = new EventLoopStats[eventLoopArray.length];
          for (int i = 0; i < eventLoopArray.length; i++) {
            eventLoopStats[i] = new EventLoopStats(eventLoopArray[i]);
          }
          for (int i = 0; i < nodeArray.length; i++) {
            nodeStats[i].async = nodeArray[i].getAsyncConnectionStats();
          }
          return new ClusterStats(nodeStats, eventLoopStats, threadsInUse, recoverCount.get(), invalidNodeCount);
        }
      }
      final EventLoopStats[] loopStats = new EventLoopStats[eventLoopArray.length];
      final ConnectionStats[][] connStats = new ConnectionStats[nodeArray.length][eventLoopArray.length];
      final AtomicInteger eventLoopCount = new AtomicInteger(eventLoopArray.length);
      final Monitor monitor = new Monitor();
      for (EventLoop eventLoop : eventLoopArray) {
        eventLoop.execute(new Runnable() {
          public void run() {
            int index = eventLoop.getIndex();
            loopStats[index] = new EventLoopStats(eventLoop);
            for (int i = 0; i < nodeArray.length; i++) {
              AsyncPool pool = nodeArray[i].getAsyncPool(index);
              int inPool = pool.queue.size();
              connStats[i][index] = new ConnectionStats(pool.total - inPool, inPool, pool.opened, pool.closed);
            }
            if (eventLoopCount.decrementAndGet() == 0) {
              monitor.notifyComplete();
            }
          }
        });
      }
      monitor.waitTillComplete();
      eventLoopStats = loopStats;
      for (int i = 0; i < nodeArray.length; i++) {
        int inUse = 0;
        int inPool = 0;
        int opened = 0;
        int closed = 0;
        for (EventLoop eventLoop : eventLoopArray) {
          ConnectionStats cs = connStats[i][eventLoop.getIndex()];
          inUse += cs.inUse;
          inPool += cs.inPool;
          opened += cs.opened;
          closed += cs.closed;
        }
        nodeStats[i].async = new ConnectionStats(inUse, inPool, opened, closed);
      }
    }
    return new ClusterStats(nodeStats, eventLoopStats, threadsInUse, recoverCount.get(), invalidNodeCount);
  }

  public final void getStats(ClusterStatsListener listener) {
    try {
      final Node[] nodeArray = nodes;
      NodeStats[] nodeStats = new NodeStats[nodeArray.length];
      int count = 0;
      for (Node node : nodeArray) {
        nodeStats[count++] = new NodeStats(node);
      }
      int threadsInUse = 0;
      if (threadPool instanceof ThreadPoolExecutor) {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPool;
        threadsInUse = tpe.getActiveCount();
      }
      if (eventLoops == null) {
        try {
          listener.onSuccess(new ClusterStats(nodeStats, null, threadsInUse, recoverCount.get(), invalidNodeCount));
        } catch (Throwable e) {
        }
        return;
      }
      EventLoop[] eventLoopArray = eventLoops.getArray();
      final EventLoopStats[] loopStats = new EventLoopStats[eventLoopArray.length];
      final ConnectionStats[][] connStats = new ConnectionStats[nodeArray.length][eventLoopArray.length];
      final AtomicInteger eventLoopCount = new AtomicInteger(eventLoopArray.length);
      final int threadCount = threadsInUse;
      for (EventLoop eventLoop : eventLoopArray) {
        Runnable fetch = new Runnable() {
          public void run() {
            int index = eventLoop.getIndex();
            loopStats[index] = new EventLoopStats(eventLoop);
            for (int i = 0; i < nodeArray.length; i++) {
              AsyncPool pool = nodeArray[i].getAsyncPool(index);
              int inPool = pool.queue.size();
              connStats[i][index] = new ConnectionStats(pool.total - inPool, inPool, pool.opened, pool.closed);
            }
            if (eventLoopCount.decrementAndGet() == 0) {
              for (int i = 0; i < nodeArray.length; i++) {
                int inUse = 0;
                int inPool = 0;
                int opened = 0;
                int closed = 0;
                for (EventLoop eventLoop : eventLoopArray) {
                  ConnectionStats cs = connStats[i][eventLoop.getIndex()];
                  inUse += cs.inUse;
                  inPool += cs.inPool;
                  opened += cs.opened;
                  closed += cs.closed;
                }
                nodeStats[i].async = new ConnectionStats(inUse, inPool, opened, closed);
              }
              try {
                listener.onSuccess(new ClusterStats(nodeStats, loopStats, threadCount, recoverCount.get(), invalidNodeCount));
              } catch (Throwable e) {
              }
            }
          }
        };
        if (eventLoop.inEventLoop()) {
          fetch.run();
        } else {
          eventLoop.execute(fetch);
        }
      }
    } catch (AerospikeException ae) {
      listener.onFailure(ae);
    } catch (Throwable e) {
      listener.onFailure(new AerospikeException(e));
    }
  }

  public final void interruptTendSleep() {
    tendThread.interrupt();
  }

  public final void printPartitionMap() {
    for (Entry<String, Partitions> entry : partitionMap.entrySet()) {
      String namespace = entry.getKey();
      Partitions partitions = entry.getValue();
      AtomicReferenceArray<Node>[] replicas = partitions.replicas;
      for (int i = 0; i < replicas.length; i++) {
        AtomicReferenceArray<Node> nodeArray = replicas[i];
        int max = nodeArray.length();
        for (int j = 0; j < max; j++) {
          Node node = nodeArray.get(j);
          if (node != null) {
            Log.info(namespace + ',' + i + ',' + j + ',' + node);
          }
        }
      }
    }
  }

  public void changePassword(byte[] user, byte[] password, byte[] passwordHash) {
    if (this.user != null && Arrays.equals(user, this.user)) {
      this.passwordHash = passwordHash;
      if (authMode != AuthMode.INTERNAL) {
        this.password = password;
      }
    }
  }

  /**
	 * Set max errors allowed within configurable window for all nodes.
	 * For performance reasons, maxErrorRate is not declared volatile,
	 * so we are relying on cache coherency for other threads to
	 * recognize this change.
	 */
  public final void setMaxErrorRate(int rate) {
    this.maxErrorRate = rate;
  }

  /**
	 * The number of cluster tend iterations that defines the window for maxErrorRate.
	 * For performance reasons, errorRateWindow is not declared volatile,
	 * so we are relying on cache coherency for other threads to
	 * recognize this change.
	 */
  public final void setErrorRateWindow(int window) {
    this.errorRateWindow = window;
  }

  private static boolean supportsPartitionQuery(Node[] nodes) {
    if (nodes.length == 0) {
      return false;
    }
    for (Node node : nodes) {
      if (!node.hasPartitionQuery()) {
        return false;
      }
    }
    return true;
  }

  public final ExecutorService getThreadPool() {
    return threadPool;
  }

  public final byte[] getUser() {
    return user;
  }

  public final byte[] getPassword() {
    return password;
  }

  public final byte[] getPasswordHash() {
    return passwordHash;
  }

  public final boolean isActive() {
    return tendValid;
  }

  /**
	 * Return count of add node failures in the most recent cluster tend iteration.
	 */
  public final int getInvalidNodeCount() {
    return invalidNodeCount;
  }

  public void close() {
    tendValid = false;
    tendThread.interrupt();
    if (!sharedThreadPool) {
      threadPool.shutdown();
    }
    if (eventLoops == null) {
      Node[] nodeArray = nodes;
      for (Node node : nodeArray) {
        node.closeSyncConnections();
      }
    } else {
      final long deadline = (closeTimeout > 0) ? System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(closeTimeout) : 0L;
      final AtomicInteger eventLoopCount = new AtomicInteger(eventState.length);
      final AtomicBoolean closedWithPending = new AtomicBoolean();
      boolean inEventLoop = false;
      for (final EventState state : eventState) {
        if (state.eventLoop.inEventLoop()) {
          inEventLoop = true;
        }
        state.eventLoop.execute(new Runnable() {
          public void run() {
            if (state.closed) {
              return;
            }
            if (state.pending > 0) {
              if (closeTimeout >= 0 && (closeTimeout == 0 || deadline - System.nanoTime() > 0)) {
                state.eventLoop.schedule(this, 200, TimeUnit.MILLISECONDS);
                return;
              }

<<<<<<< /usr/src/app/output/aerospike/aerospike-client-java/deae846ff30975c39b4f883823e68e10a3849425/client/src/com/aerospike/client/cluster/Cluster.java/left.java
              Log.warn("Cluster closed with pending async commands")
=======
              closedWithPending.set(true)
>>>>>>> /usr/src/app/output/aerospike/aerospike-client-java/deae846ff30975c39b4f883823e68e10a3849425/client/src/com/aerospike/client/cluster/Cluster.java/right.java
              ;
            }
            closeEventLoop(eventLoopCount, state);
          }
        });
      }
      if (!inEventLoop) {
        waitAsyncComplete();
      }
      if (closedWithPending.get()) {
        Log.warn("Cluster closed with pending async commands");
      }
    }
  }

  /**
	 * Wait until all event loops have finished processing pending cluster commands.
	 * Must be called from an event loop thread.
	 */
  private final void closeEventLoop(AtomicInteger eventLoopCount, EventState state) {
    state.closed = true;
    Node[] nodeArray = nodes;
    for (Node node : nodeArray) {
      node.closeAsyncConnections(state.index);
    }
    if (eventLoopCount.decrementAndGet() == 0) {
      for (Node node : nodeArray) {
        node.closeSyncConnections();
      }
      notifyAsyncComplete();
    }
  }

  private synchronized void waitAsyncComplete() {
    while (!asyncComplete) {
      try {
        super.wait();
      } catch (InterruptedException ie) {
      }
    }
  }

  private synchronized void notifyAsyncComplete() {
    asyncComplete = true;
    super.notify();
  }
}