package org.omnifaces.util.concurrentlinkedhashmap;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.omnifaces.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap.DrainStatus.IDLE;
import static org.omnifaces.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap.DrainStatus.PROCESSING;
import static org.omnifaces.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap.DrainStatus.REQUIRED;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A hash table supporting full concurrency of retrievals, adjustable expected
 * concurrency for updates, and a maximum capacity to bound the map by. This
 * implementation differs from {@link ConcurrentHashMap} in that it maintains a
 * page replacement algorithm that is used to evict an entry when the map has
 * exceeded its capacity. Unlike the <tt>Java Collections Framework</tt>, this
 * map does not have a publicly visible constructor and instances are created
 * through a {@link Builder}.
 * <p>
 * An entry is evicted from the map when the <tt>weighted capacity</tt> exceeds
 * its <tt>maximum weighted capacity</tt> threshold. A {@link EntryWeigher}
 * determines how many units of capacity that an entry consumes. The default
 * weigher assigns each value a weight of <tt>1</tt> to bound the map by the
 * total number of key-value pairs. A map that holds collections may choose to
 * weigh values by the number of elements in the collection and bound the map
 * by the total number of elements that it contains. A change to a value that
 * modifies its weight requires that an update operation is performed on the
 * map.
 * <p>
 * An {@link EvictionListener} may be supplied for notification when an entry
 * is evicted from the map. This listener is invoked on a caller's thread and
 * will not block other threads from operating on the map. An implementation
 * should be aware that the caller's thread will not expect long execution
 * times or failures as a side effect of the listener being notified. Execution
 * safety and a fast turn around time can be achieved by performing the
 * operation asynchronously, such as by submitting a task to an
 * {@link java.util.concurrent.ExecutorService}.
 * <p>
 * The <tt>concurrency level</tt> determines the number of threads that can
 * concurrently modify the table. Using a significantly higher or lower value
 * than needed can waste space or lead to thread contention, but an estimate
 * within an order of magnitude of the ideal value does not usually have a
 * noticeable impact. Because placement in hash tables is essentially random,
 * the actual concurrency will vary.
 * <p>
 * This class and its views and iterators implement all of the
 * <em>optional</em> methods of the {@link Map} and {@link Iterator}
 * interfaces.
 * <p>
 * Like {@link java.util.Hashtable} but unlike {@link HashMap}, this class
 * does <em>not</em> allow <tt>null</tt> to be used as a key or value. Unlike
 * {@link java.util.LinkedHashMap}, this class does <em>not</em> provide
 * predictable iteration order. A snapshot of the keys and entries may be
 * obtained in ascending and descending order of retention.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @see <a href="http://code.google.com/p/concurrentlinkedhashmap/">
 *      http://code.google.com/p/concurrentlinkedhashmap/</a>
 */
@ThreadSafe public final class ConcurrentLinkedHashMap<K extends java.lang.Object, V extends java.lang.Object> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
  /** The maximum weighted capacity of the map. */
  static final long MAXIMUM_CAPACITY = Long.MAX_VALUE - Integer.MAX_VALUE;

  /** The maximum number of pending operations per buffer. */
  static final int MAXIMUM_BUFFER_SIZE = 1048576;

  /** The number of pending operations per buffer before attempting to drain. */
  static final int BUFFER_THRESHOLD = 16;

  /** The default initial capacity. */
  static final int DEFAULT_INITIAL_CAPACITY = 16;

  /** The default load factor. */
  static final float DEFAULT_LOAD_FACTOR = 0.75f;

  /** The number of buffers to use. */
  static final int NUMBER_OF_BUFFERS;

  /** Mask value for indexing into the buffers. */
  static final int BUFFER_MASK;

  /** The maximum number of operations to perform per amortized drain. */
  static final int AMORTIZED_DRAIN_THRESHOLD;

  /** A queue that discards all entries. */
  static final Queue<?> DISCARDING_QUEUE = new DiscardingQueue();

  static {
    int buffers = ceilingNextPowerOfTwo(Runtime.getRuntime().availableProcessors());
    AMORTIZED_DRAIN_THRESHOLD = (1 + buffers) * BUFFER_THRESHOLD;
    NUMBER_OF_BUFFERS = buffers;
    BUFFER_MASK = buffers - 1;
  }

  static int ceilingNextPowerOfTwo(int x) {
    return 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(x - 1));
  }

  enum DrainStatus {
    IDLE,
    REQUIRED,
    PROCESSING
  }

  private final ConcurrentMap<K, Node> data;

  private final int concurrencyLevel;

  @GuardedBy(value = "evictionLock") private final LinkedDeque<Node> evictionDeque;

  @GuardedBy(value = "evictionLock") private volatile long weightedSize;

  @GuardedBy(value = "evictionLock") private volatile long capacity;

  private volatile int nextOrder;

  @GuardedBy(value = "evictionLock") private int drainedOrder;

  private final Lock evictionLock;

  private final Queue<Task>[] buffers;

  private final AtomicIntegerArray bufferLengths;

  private final AtomicReference<DrainStatus> drainStatus;

  private final EntryWeigher<? super K, ? super V> weigher;

  private final Queue<Node> pendingNotifications;

  private final EvictionListener<K, V> listener;

  private transient Set<K> keySet;

  private transient Collection<V> values;

  private transient Set<Entry<K, V>> entrySet;

  /**
   * Creates an instance based on the builder's configuration.
   */
  @SuppressWarnings(value = { "unchecked" }) private ConcurrentLinkedHashMap(Builder<K, V> builder) {
    concurrencyLevel = builder.concurrencyLevel;
    capacity = Math.min(builder.capacity, MAXIMUM_CAPACITY);
    data = new ConcurrentHashMap<K, ConcurrentLinkedHashMap<K, V>.Node>(builder.initialCapacity, DEFAULT_LOAD_FACTOR, concurrencyLevel);
    weigher = builder.weigher;
    nextOrder = Integer.MIN_VALUE;
    drainedOrder = Integer.MIN_VALUE;
    evictionLock = new ReentrantLock();
    evictionDeque = new LinkedDeque<ConcurrentLinkedHashMap<K, V>.Node>();
    drainStatus = new AtomicReference<DrainStatus>(IDLE);
    buffers = new Queue[NUMBER_OF_BUFFERS];
    bufferLengths = new AtomicIntegerArray(NUMBER_OF_BUFFERS);
    for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
      buffers[i] = new ConcurrentLinkedQueue<Task>();
    }
    listener = builder.listener;
    pendingNotifications = (listener == DiscardingListener.INSTANCE) ? (Queue<Node>) DISCARDING_QUEUE : new ConcurrentLinkedQueue<Node>();
  }

  /** Ensures that the object is not null. */
  static void checkNotNull(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }
  }

  /** Ensures that the argument expression is true. */
  static void checkArgument(boolean expression) {
    if (!expression) {
      throw new IllegalArgumentException();
    }
  }

  /** Ensures that the state expression is true. */
  static void checkState(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

  /**
   * Retrieves the maximum weighted capacity of the map.
   *
   * @return the maximum weighted capacity
   */
  public long capacity() {
    return capacity;
  }

  /**
   * Sets the maximum weighted capacity of the map and eagerly evicts entries
   * until it shrinks to the appropriate size.
   *
   * @param capacity the maximum weighted capacity of the map
   * @throws IllegalArgumentException if the capacity is negative
   */
  public void setCapacity(long capacity) {
    checkArgument(capacity >= 0);
    evictionLock.lock();
    try {
      this.capacity = Math.min(capacity, MAXIMUM_CAPACITY);
      drainBuffers(AMORTIZED_DRAIN_THRESHOLD);
      evict();
    }  finally {
      evictionLock.unlock();
    }
    notifyListener();
  }

  /** Determines whether the map has exceeded its capacity. */
  boolean hasOverflowed() {
    return weightedSize > capacity;
  }

  /**
   * Evicts entries from the map while it exceeds the capacity and appends
   * evicted entries to the notification queue for processing.
   */
  @GuardedBy(value = "evictionLock") void evict() {
    while (hasOverflowed()) {
      Node node = evictionDeque.poll();
      if (node == null) {
        return;
      }
      if (data.remove(node.key, node)) {
        pendingNotifications.add(node);
      }
      node.makeDead();
    }
  }

  /**
   * Performs the post-processing work required after the map operation.
   *
   * @param task the pending operation to be applied
   */
  void afterCompletion(Task task) {
    boolean delayable = schedule(task);
    if (shouldDrainBuffers(delayable)) {
      tryToDrainBuffers(AMORTIZED_DRAIN_THRESHOLD);
    }
    notifyListener();
  }

  /**
   * Schedules the task to be applied to the page replacement policy.
   *
   * @param task the pending operation
   * @return if the draining of the buffers can be delayed
   */
  boolean schedule(Task task) {
    int index = bufferIndex();
    int buffered = bufferLengths.incrementAndGet(index);
    if (task.isWrite()) {
      buffers[index].add(task);
      drainStatus.set(REQUIRED);
      return false;
    }
    if (buffered <= MAXIMUM_BUFFER_SIZE) {
      buffers[index].add(task);
      return (buffered <= BUFFER_THRESHOLD);
    } else {
      bufferLengths.decrementAndGet(index);
      return false;
    }
  }

  /** Returns the index to the buffer that the task should be scheduled on. */
  static int bufferIndex() {
    return (int) Thread.currentThread().getId() & BUFFER_MASK;
  }

  /** Returns the ordering value to assign to a task. */
  int nextOrdering() {
    return nextOrder++;
  }

  /**
   * Determines whether the buffers should be drained.
   *
   * @param delayable if a drain should be delayed until required
   * @return if a drain should be attempted
   */
  boolean shouldDrainBuffers(boolean delayable) {
    DrainStatus status = drainStatus.get();
    return (status != PROCESSING) & (!delayable | (status == REQUIRED));
  }

  /**
   * Attempts to acquire the eviction lock and apply the pending operations to
   * the page replacement policy.
   *
   * @param maxToDrain the maximum number of operations to drain
   */
  void tryToDrainBuffers(int maxToDrain) {
    if (evictionLock.tryLock()) {
      try {
        drainStatus.set(PROCESSING);
        drainBuffers(maxToDrain);
      }  finally {
        drainStatus.compareAndSet(PROCESSING, IDLE);
        evictionLock.unlock();
      }
    }
  }

  /**
   * Drains the buffers and applies the pending operations.
   *
   * @param maxToDrain the maximum number of operations to drain
   */
  @GuardedBy(value = "evictionLock") void drainBuffers(int maxToDrain) {
    Task[] tasks = new Task[maxToDrain];
    int maxTaskIndex = moveTasksFromBuffers(tasks);
    runTasks(tasks, maxTaskIndex);
    updateDrainedOrder(tasks, maxTaskIndex);
  }

  /**
   * Moves the tasks from the buffers into the output array.
   *
   * @param tasks the ordered array of the pending operations
   * @return the highest index location of a task that was added to the array
   */
  @GuardedBy(value = "evictionLock") int moveTasksFromBuffers(Task[] tasks) {
    int maxTaskIndex = -1;
    for (int i = 0; i < buffers.length; i++) {
      int maxIndex = moveTasksFromBuffer(tasks, i);
      maxTaskIndex = Math.max(maxIndex, maxTaskIndex);
    }
    return maxTaskIndex;
  }

  /**
   * Moves the tasks from the specified buffer into the output array.
   *
   * @param tasks the ordered array of the pending operations
   * @param bufferIndex the buffer to drain into the tasks array
   * @return the highest index location of a task that was added to the array
   */
  @GuardedBy(value = "evictionLock") int moveTasksFromBuffer(Task[] tasks, int bufferIndex) {
    Queue<Task> buffer = buffers[bufferIndex];
    int removedFromBuffer = 0;
    Task task;
    int maxIndex = -1;
    while ((task = buffer.poll()) != null) {
      removedFromBuffer++;
      int index = task.getOrder() - drainedOrder;
      if (index < 0) {
        task.run();
      } else {
        if (index >= tasks.length) {
          maxIndex = tasks.length - 1;
          addTaskToChain(tasks, task, maxIndex);
          break;
        } else {
          maxIndex = Math.max(index, maxIndex);
          addTaskToChain(tasks, task, index);
        }
      }
    }
    bufferLengths.addAndGet(bufferIndex, -removedFromBuffer);
    return maxIndex;
  }

  /**
   * Adds the task as the head of the chain at the index location.
   *
   * @param tasks the ordered array of the pending operations
   * @param task the pending operation to add
   * @param index the array location
   */
  @GuardedBy(value = "evictionLock") void addTaskToChain(Task[] tasks, Task task, int index) {
    task.setNext(tasks[index]);
    tasks[index] = task;
  }

  /**
   * Runs the pending page replacement policy operations.
   *
   * @param tasks the ordered array of the pending operations
   * @param maxTaskIndex the maximum index of the array
   */
  @GuardedBy(value = "evictionLock") void runTasks(Task[] tasks, int maxTaskIndex) {
    for (int i = 0; i <= maxTaskIndex; i++) {
      runTasksInChain(tasks[i]);
    }
  }

  /**
   * Runs the pending operations on the linked chain.
   *
   * @param task the first task in the chain of operations
   */
  @GuardedBy(value = "evictionLock") void runTasksInChain(Task task) {
    Task nextTask = task;
    while (nextTask != null) {
      Task currentTask = nextTask;
      nextTask = nextTask.getNext();
      currentTask.setNext(null);
      currentTask.run();
    }
  }

  /**
   * Updates the order to start the next drain from.
   *
   * @param tasks the ordered array of operations
   * @param maxTaskIndex the maximum index of the array
   */
  @GuardedBy(value = "evictionLock") void updateDrainedOrder(Task[] tasks, int maxTaskIndex) {
    if (maxTaskIndex >= 0) {
      Task task = tasks[maxTaskIndex];
      drainedOrder = task.getOrder() + 1;
    }
  }

  /** Notifies the listener of entries that were evicted. */
  void notifyListener() {
    Node node;
    while ((node = pendingNotifications.poll()) != null) {
      listener.onEviction(node.key, node.getValue());
    }
  }

  private class ReadTask extends AbstractTask {
    private final Node node;

    ReadTask(Node node) {
      this.node = node;
    }

    @Override @GuardedBy(value = "evictionLock") public void run() {
      if (evictionDeque.contains(node)) {
        evictionDeque.moveToBack(node);
      }
    }

    @Override public boolean isWrite() {
      return false;
    }
  }

  private final class AddTask extends AbstractTask {
    private final Node node;

    private final int weight;

    AddTask(Node node, int weight) {
      this.weight = weight;
      this.node = node;
    }

    @Override @GuardedBy(value = "evictionLock") public void run() {
      weightedSize += weight;
      if (node.get().isAlive()) {
        evictionDeque.add(node);
        evict();
      }
    }

    @Override public boolean isWrite() {
      return true;
    }
  }

  private final class RemovalTask extends AbstractTask {
    private final Node node;

    RemovalTask(Node node) {
      this.node = node;
    }

    @Override @GuardedBy(value = "evictionLock") public void run() {
      evictionDeque.remove(node);
      node.makeDead();
    }

    @Override public boolean isWrite() {
      return true;
    }
  }

  private final class UpdateTask extends ReadTask {
    private final int weightDifference;

    public UpdateTask(Node node, int weightDifference) {
      super(node);
      this.weightDifference = weightDifference;
    }

    @Override @GuardedBy(value = "evictionLock") public void run() {
      super.run();
      weightedSize += weightDifference;
      evict();
    }

    @Override public boolean isWrite() {
      return true;
    }
  }

  @Override public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override public int size() {
    return data.size();
  }

  /**
   * Returns the weighted size of this map.
   *
   * @return the combined weight of the values in this map
   */
  public long weightedSize() {
    return Math.max(0, weightedSize);
  }

  @Override public void clear() {
    evictionLock.lock();
    try {
      Node node;
      while ((node = evictionDeque.poll()) != null) {
        data.remove(node.key, node);
        node.makeDead();
      }
      for (int i = 0; i < buffers.length; i++) {
        Queue<Task> buffer = buffers[i];
        int removed = 0;
        Task task;
        while ((task = buffer.poll()) != null) {
          if (task.isWrite()) {
            task.run();
          }
          removed++;
        }
        bufferLengths.addAndGet(i, -removed);
      }
    }  finally {
      evictionLock.unlock();
    }
  }

  @Override public boolean containsKey(Object key) {
    return data.containsKey(key);
  }

  @Override public boolean containsValue(Object value) {
    checkNotNull(value);
    for (Node node : data.values()) {
      if (node.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }

  @Override public V get(Object key) {
    final Node node = data.get(key);
    if (node == null) {
      return null;
    }
    afterCompletion(new ReadTask(node));
    return node.getValue();
  }

  @Override public V put(K key, V value) {
    return put(key, value, false);
  }

  @Override public V putIfAbsent(K key, V value) {
    return put(key, value, true);
  }

  /**
   * Adds a node to the list and the data store. If an existing node is found,
   * then its value is updated if allowed.
   *
   * @param key key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @param onlyIfAbsent a write is performed only if the key is not already
   *     associated with a value
   * @return the prior value in the data store or null if no mapping was found
   */
  V put(K key, V value, boolean onlyIfAbsent) {
    checkNotNull(value);
    final int weight = weigher.weightOf(key, value);
    final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
    final Node node = new Node(key, weightedValue);
    for ( ; ; ) {
      final Node prior = data.putIfAbsent(node.key, node);
      if (prior == null) {
        afterCompletion(new AddTask(node, weight));
        return null;
      } else {
        if (onlyIfAbsent) {
          afterCompletion(new ReadTask(prior));
          return prior.getValue();
        }
      }
      for ( ; ; ) {
        final WeightedValue<V> oldWeightedValue = prior.get();
        if (!oldWeightedValue.isAlive()) {
          break;
        }
        if (prior.compareAndSet(oldWeightedValue, weightedValue)) {
          final int weightedDifference = weight - oldWeightedValue.weight;
          final Task task = (weightedDifference == 0) ? new ReadTask(prior) : new UpdateTask(prior, weightedDifference);
          afterCompletion(task);
          return oldWeightedValue.value;
        }
      }
    }
  }

  @Override public V remove(Object key) {
    final Node node = data.remove(key);
    if (node == null) {
      return null;
    }
    node.makeRetired();
    afterCompletion(new RemovalTask(node));
    return node.getValue();
  }

  @Override public boolean remove(Object key, Object value) {
    Node node = data.get(key);
    if ((node == null) || (value == null)) {
      return false;
    }
    WeightedValue<V> weightedValue = node.get();
    for ( ; ; ) {
      if (weightedValue.hasValue(value)) {
        if (node.tryToRetire(weightedValue)) {
          if (data.remove(key, node)) {
            afterCompletion(new RemovalTask(node));
            return true;
          }
        } else {
          weightedValue = node.get();
          if (weightedValue.isAlive()) {
            continue;
          }
        }
      }
      return false;
    }
  }

  @Override public V replace(K key, V value) {
    checkNotNull(value);
    final int weight = weigher.weightOf(key, value);
    final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
    final Node node = data.get(key);
    if (node == null) {
      return null;
    }
    for ( ; ; ) {
      WeightedValue<V> oldWeightedValue = node.get();
      if (!oldWeightedValue.isAlive()) {
        return null;
      }
      if (node.compareAndSet(oldWeightedValue, weightedValue)) {
        int weightedDifference = weight - oldWeightedValue.weight;
        final Task task = (weightedDifference == 0) ? new ReadTask(node) : new UpdateTask(node, weightedDifference);
        afterCompletion(task);
        return oldWeightedValue.value;
      }
    }
  }

  @Override public boolean replace(K key, V oldValue, V newValue) {
    checkNotNull(oldValue);
    checkNotNull(newValue);
    final int weight = weigher.weightOf(key, newValue);
    final WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, weight);
    final Node node = data.get(key);
    if (node == null) {
      return false;
    }
    for ( ; ; ) {
      final WeightedValue<V> weightedValue = node.get();
      if (!weightedValue.isAlive() || !weightedValue.hasValue(oldValue)) {
        return false;
      }
      if (node.compareAndSet(weightedValue, newWeightedValue)) {
        int weightedDifference = weight - weightedValue.weight;
        final Task task = (weightedDifference == 0) ? new ReadTask(node) : new UpdateTask(node, weightedDifference);
        afterCompletion(task);
        return true;
      }
    }
  }

  @Override public Set<K> keySet() {
    if (keySet == null) {
      keySet = new KeySet();
    }
    return keySet;
  }

  /**
   * Returns a unmodifiable snapshot {@link Set} view of the keys contained in
   * this map. The set's iterator returns the keys whose order of iteration is
   * the ascending order in which its entries are considered eligible for
   * retention, from the least-likely to be retained to the most-likely.
   * <p>
   * Beware that, unlike in {@link #keySet()}, obtaining the set is <em>NOT</em>
   * a constant-time operation. Because of the asynchronous nature of the page
   * replacement policy, determining the retention ordering requires a traversal
   * of the keys.
   *
   * @return an ascending snapshot view of the keys in this map
   */
  public Set<K> ascendingKeySet() {
    return orderedKeySet(true, Integer.MAX_VALUE);
  }

  /**
   * Returns an unmodifiable snapshot {@link Set} view of the keys contained in
   * this map. The set's iterator returns the keys whose order of iteration is
   * the ascending order in which its entries are considered eligible for
   * retention, from the least-likely to be retained to the most-likely.
   * <p>
   * Beware that, unlike in {@link #keySet()}, obtaining the set is <em>NOT</em>
   * a constant-time operation. Because of the asynchronous nature of the page
   * replacement policy, determining the retention ordering requires a traversal
   * of the keys.
   *
   * @param limit the maximum size of the returned set
   * @return a ascending snapshot view of the keys in this map
   * @throws IllegalArgumentException if the limit is negative
   */
  public Set<K> ascendingKeySetWithLimit(int limit) {
    return orderedKeySet(true, limit);
  }

  /**
   * Returns an unmodifiable snapshot {@link Set} view of the keys contained in
   * this map. The set's iterator returns the keys whose order of iteration is
   * the descending order in which its entries are considered eligible for
   * retention, from the most-likely to be retained to the least-likely.
   * <p>
   * Beware that, unlike in {@link #keySet()}, obtaining the set is <em>NOT</em>
   * a constant-time operation. Because of the asynchronous nature of the page
   * replacement policy, determining the retention ordering requires a traversal
   * of the keys.
   *
   * @return a descending snapshot view of the keys in this map
   */
  public Set<K> descendingKeySet() {
    return orderedKeySet(false, Integer.MAX_VALUE);
  }

  /**
   * Returns an unmodifiable snapshot {@link Set} view of the keys contained in
   * this map. The set's iterator returns the keys whose order of iteration is
   * the descending order in which its entries are considered eligible for
   * retention, from the most-likely to be retained to the least-likely.
   * <p>
   * Beware that, unlike in {@link #keySet()}, obtaining the set is <em>NOT</em>
   * a constant-time operation. Because of the asynchronous nature of the page
   * replacement policy, determining the retention ordering requires a traversal
   * of the keys.
   *
   * @param limit the maximum size of the returned set
   * @return a descending snapshot view of the keys in this map
   * @throws IllegalArgumentException if the limit is negative
   */
  public Set<K> descendingKeySetWithLimit(int limit) {
    return orderedKeySet(false, limit);
  }

  Set<K> orderedKeySet(boolean ascending, int limit) {
    checkArgument(limit >= 0);
    evictionLock.lock();
    try {
      drainBuffers(AMORTIZED_DRAIN_THRESHOLD);
      int initialCapacity = (weigher == Weighers.entrySingleton()) ? Math.min(limit, (int) weightedSize()) : DEFAULT_INITIAL_CAPACITY;
      Set<K> keys = new LinkedHashSet<K>(initialCapacity);
      Iterator<Node> iterator = ascending ? evictionDeque.iterator() : evictionDeque.descendingIterator();
      while (iterator.hasNext() && (limit > keys.size())) {
        keys.add(iterator.next().key);
      }
      return unmodifiableSet(keys);
    }  finally {
      evictionLock.unlock();
    }
  }

  @Override public Collection<V> values() {
    if (values == null) {
      values = new Values();
    }
    return values;
  }

  @Override public Set<Entry<K, V>> entrySet() {
    if (entrySet == null) {
      entrySet = new EntrySet();
    }
    return entrySet;
  }

  /**
   * Returns an unmodifiable snapshot {@link Map} view of the mappings contained
   * in this map. The map's collections return the mappings whose order of
   * iteration is the ascending order in which its entries are considered
   * eligible for retention, from the least-likely to be retained to the
   * most-likely.
   * <p>
   * Beware that obtaining the mappings is <em>NOT</em> a constant-time
   * operation. Because of the asynchronous nature of the page replacement
   * policy, determining the retention ordering requires a traversal of the
   * entries.
   *
   * @return a ascending snapshot view of this map
   */
  public Map<K, V> ascendingMap() {
    return orderedMap(true, Integer.MAX_VALUE);
  }

  /**
   * Returns an unmodifiable snapshot {@link Map} view of the mappings contained
   * in this map. The map's collections return the mappings whose order of
   * iteration is the ascending order in which its entries are considered
   * eligible for retention, from the least-likely to be retained to the
   * most-likely.
   * <p>
   * Beware that obtaining the mappings is <em>NOT</em> a constant-time
   * operation. Because of the asynchronous nature of the page replacement
   * policy, determining the retention ordering requires a traversal of the
   * entries.
   *
   * @param limit the maximum size of the returned map
   * @return a ascending snapshot view of this map
   * @throws IllegalArgumentException if the limit is negative
   */
  public Map<K, V> ascendingMapWithLimit(int limit) {
    return orderedMap(true, limit);
  }

  /**
   * Returns an unmodifiable snapshot {@link Map} view of the mappings contained
   * in this map. The map's collections return the mappings whose order of
   * iteration is the descending order in which its entries are considered
   * eligible for retention, from the most-likely to be retained to the
   * least-likely.
   * <p>
   * Beware that obtaining the mappings is <em>NOT</em> a constant-time
   * operation. Because of the asynchronous nature of the page replacement
   * policy, determining the retention ordering requires a traversal of the
   * entries.
   *
   * @return a descending snapshot view of this map
   */
  public Map<K, V> descendingMap() {
    return orderedMap(false, Integer.MAX_VALUE);
  }

  /**
   * Returns an unmodifiable snapshot {@link Map} view of the mappings contained
   * in this map. The map's collections return the mappings whose order of
   * iteration is the descending order in which its entries are considered
   * eligible for retention, from the most-likely to be retained to the
   * least-likely.
   * <p>
   * Beware that obtaining the mappings is <em>NOT</em> a constant-time
   * operation. Because of the asynchronous nature of the page replacement
   * policy, determining the retention ordering requires a traversal of the
   * entries.
   *
   * @param limit the maximum size of the returned map
   * @return a descending snapshot view of this map
   * @throws IllegalArgumentException if the limit is negative
   */
  public Map<K, V> descendingMapWithLimit(int limit) {
    return orderedMap(false, limit);
  }

  Map<K, V> orderedMap(boolean ascending, int limit) {
    checkArgument(limit >= 0);
    evictionLock.lock();
    try {
      drainBuffers(AMORTIZED_DRAIN_THRESHOLD);
      int initialCapacity = (weigher == Weighers.entrySingleton()) ? Math.min(limit, (int) weightedSize()) : DEFAULT_INITIAL_CAPACITY;
      Map<K, V> map = new LinkedHashMap<K, V>(initialCapacity);
      Iterator<Node> iterator = ascending ? evictionDeque.iterator() : evictionDeque.descendingIterator();
      while (iterator.hasNext() && (limit > map.size())) {
        Node node = iterator.next();
        map.put(node.key, node.getValue());
      }
      return unmodifiableMap(map);
    }  finally {
      evictionLock.unlock();
    }
  }

  @Immutable private static final class WeightedValue<V extends java.lang.Object> {
    private final int weight;

    private final V value;

    WeightedValue(V value, int weight) {
      this.weight = weight;
      this.value = value;
    }

    boolean hasValue(Object o) {
      return (o == value) || value.equals(o);
    }

    /**
	 * If the entry is available in the hash-table and page replacement policy.
	 */
    boolean isAlive() {
      return weight > 0;
    }
  }

  @SuppressWarnings(value = { "serial" }) private final class Node extends AtomicReference<WeightedValue<V>> implements Linked<Node> {
    private final K key;

    @GuardedBy(value = "evictionLock") private Node prev;

    @GuardedBy(value = "evictionLock") private Node next;

    /** Creates a new, unlinked node. */
    Node(K key, WeightedValue<V> weightedValue) {
      super(weightedValue);
      this.key = key;
    }

    @Override @GuardedBy(value = "evictionLock") public Node getPrevious() {
      return prev;
    }

    @Override @GuardedBy(value = "evictionLock") public void setPrevious(Node prev) {
      this.prev = prev;
    }

    @Override @GuardedBy(value = "evictionLock") public Node getNext() {
      return next;
    }

    @Override @GuardedBy(value = "evictionLock") public void setNext(Node next) {
      this.next = next;
    }

    /** Retrieves the value held by the current <tt>WeightedValue</tt>. */
    V getValue() {
      return get().value;
    }

    /**
	 * Attempts to transition the node from the <tt>alive</tt> state to the
	 * <tt>retired</tt> state.
	 *
	 * @param expect the expected weighted value
	 * @return if successful
	 */
    boolean tryToRetire(WeightedValue<V> expect) {
      if (expect.isAlive()) {
        WeightedValue<V> retired = new WeightedValue<V>(expect.value, -expect.weight);
        return compareAndSet(expect, retired);
      }
      return false;
    }

    /**
	 * Atomically transitions the node from the <tt>alive</tt> state to the
	 * <tt>retired</tt> state, if a valid transition.
	 */
    void makeRetired() {
      for ( ; ; ) {
        WeightedValue<V> current = get();
        if (!current.isAlive()) {
          return;
        }
        WeightedValue<V> retired = new WeightedValue<V>(current.value, -current.weight);
        if (compareAndSet(current, retired)) {
          return;
        }
      }
    }

    /**
	 * Atomically transitions the node to the <tt>dead</tt> state and decrements
	 * the <tt>weightedSize</tt>.
	 */
    @GuardedBy(value = "evictionLock") void makeDead() {
      for ( ; ; ) {
        WeightedValue<V> current = get();
        WeightedValue<V> dead = new WeightedValue<V>(current.value, 0);
        if (compareAndSet(current, dead)) {
          weightedSize -= Math.abs(current.weight);
          return;
        }
      }
    }
  }

  private final class KeySet extends AbstractSet<K> {
    private final ConcurrentLinkedHashMap<K, V> map = ConcurrentLinkedHashMap.this;

    @Override public int size() {
      return map.size();
    }

    @Override public void clear() {
      map.clear();
    }

    @Override public Iterator<K> iterator() {
      return new KeyIterator();
    }

    @Override public boolean contains(Object obj) {
      return containsKey(obj);
    }

    @Override public boolean remove(Object obj) {
      return (map.remove(obj) != null);
    }

    @Override public Object[] toArray() {
      return map.data.keySet().toArray();
    }

    @Override public <T extends java.lang.Object> T[] toArray(T[] array) {
      return map.data.keySet().toArray(array);
    }
  }

  private final class KeyIterator implements Iterator<K> {
    private final Iterator<K> iterator = data.keySet().iterator();

    private K current;

    @Override public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override public K next() {
      current = iterator.next();
      return current;
    }

    @Override public void remove() {
      checkState(current != null);
      ConcurrentLinkedHashMap.this.remove(current);
      current = null;
    }
  }

  private final class Values extends AbstractCollection<V> {
    @Override public int size() {
      return ConcurrentLinkedHashMap.this.size();
    }

    @Override public void clear() {
      ConcurrentLinkedHashMap.this.clear();
    }

    @Override public Iterator<V> iterator() {
      return new ValueIterator();
    }

    @Override public boolean contains(Object o) {
      return containsValue(o);
    }
  }

  private final class ValueIterator implements Iterator<V> {
    private final Iterator<Node> iterator = data.values().iterator();

    private Node current;

    @Override public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override public V next() {
      current = iterator.next();
      return current.getValue();
    }

    @Override public void remove() {
      checkState(current != null);
      ConcurrentLinkedHashMap.this.remove(current.key);
      current = null;
    }
  }

  private final class EntrySet extends AbstractSet<Entry<K, V>> {
    private final ConcurrentLinkedHashMap<K, V> map = ConcurrentLinkedHashMap.this;

    @Override public int size() {
      return map.size();
    }

    @Override public void clear() {
      map.clear();
    }

    @Override public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator();
    }

    @Override public boolean contains(Object obj) {
      if (!(obj instanceof Entry<?, ?>)) {
        return false;
      }
      Entry<?, ?> entry = (Entry<?, ?>) obj;
      Node node = map.data.get(entry.getKey());
      return (node != null) && (node.getValue().equals(entry.getValue()));
    }

    @Override public boolean add(Entry<K, V> entry) {
      return (map.putIfAbsent(entry.getKey(), entry.getValue()) == null);
    }

    @Override public boolean remove(Object obj) {
      if (!(obj instanceof Entry<?, ?>)) {
        return false;
      }
      Entry<?, ?> entry = (Entry<?, ?>) obj;
      return map.remove(entry.getKey(), entry.getValue());
    }
  }

  private final class EntryIterator implements Iterator<Entry<K, V>> {
    private final Iterator<Node> iterator = data.values().iterator();

    private Node current;

    @Override public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override public Entry<K, V> next() {
      current = iterator.next();
      return new WriteThroughEntry(current);
    }

    @Override public void remove() {
      checkState(current != null);
      ConcurrentLinkedHashMap.this.remove(current.key);
      current = null;
    }
  }

  private final class WriteThroughEntry extends SimpleEntry<K, V> {
    static final long serialVersionUID = 1;

    WriteThroughEntry(Node node) {
      super(node.key, node.getValue());
    }

    @Override public V setValue(V value) {
      put(getKey(), value);
      return super.setValue(value);
    }

    Object writeReplace() {
      return new SimpleEntry<K, V>(this);
    }
  }

  private static final class BoundedEntryWeigher<K extends java.lang.Object, V extends java.lang.Object> implements EntryWeigher<K, V>, Serializable {
    static final long serialVersionUID = 1;

    private final EntryWeigher<? super K, ? super V> weigher;

    BoundedEntryWeigher(EntryWeigher<? super K, ? super V> weigher) {
      checkNotNull(weigher);
      this.weigher = weigher;
    }

    @Override public int weightOf(K key, V value) {
      int weight = weigher.weightOf(key, value);
      checkArgument(weight >= 1);
      return weight;
    }

    Object writeReplace() {
      return weigher;
    }
  }

  private static final class DiscardingQueue extends AbstractQueue<Object> {
    @Override public boolean add(Object e) {
      return true;
    }

    @Override public boolean offer(Object e) {
      return true;
    }

    @Override public Object poll() {
      return null;
    }

    @Override public Object peek() {
      return null;
    }

    @Override public int size() {
      return 0;
    }

    @Override public Iterator<Object> iterator() {
      return emptyList().iterator();
    }
  }

  private enum DiscardingListener implements EvictionListener<Object, Object> {
    INSTANCE
    ;

    @Override public void onEviction(Object key, Object value) {
    }
  }

  private interface Task extends Runnable {
    /** The priority order. */
    int getOrder();

    /** If the task represents an add, modify, or remove operation. */
    boolean isWrite();

    /** Returns the next task on the link chain. */
    Task getNext();

    /** Sets the next task on the link chain. */
    void setNext(Task task);
  }

  private abstract class AbstractTask implements Task {
    private final int order;

    private Task task;

    AbstractTask() {
      order = nextOrdering();
    }

    @Override public int getOrder() {
      return order;
    }

    @Override public Task getNext() {
      return task;
    }

    @Override public void setNext(Task task) {
      this.task = task;
    }
  }

  static final long serialVersionUID = 1;

  Object writeReplace() {
    return new SerializationProxy<K, V>(this);
  }

  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Proxy required");
  }

  private static final class SerializationProxy<K extends java.lang.Object, V extends java.lang.Object> implements Serializable {
    private final EntryWeigher<? super K, ? super V> weigher;

    private final EvictionListener<K, V> listener;

    private final int concurrencyLevel;

    private final Map<K, V> data;

    private final long capacity;

    SerializationProxy(ConcurrentLinkedHashMap<K, V> map) {
      concurrencyLevel = map.concurrencyLevel;
      data = new HashMap<K, V>(map);
      capacity = map.capacity;
      listener = map.listener;
      weigher = map.weigher;
    }

    Object readResolve() {
      ConcurrentLinkedHashMap<K, V> map = new Builder<K, V>().concurrencyLevel(concurrencyLevel).maximumWeightedCapacity(capacity).listener(listener).weigher(weigher).build();
      map.putAll(data);
      return map;
    }

    static final long serialVersionUID = 1;
  }

  public static final class Builder<K extends java.lang.Object, V extends java.lang.Object> {
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    static final int DEFAULT_INITIAL_CAPACITY = 16;

    private EvictionListener<K, V> listener;

    private EntryWeigher<? super K, ? super V> weigher;

    private int concurrencyLevel;

    private int initialCapacity;

    private long capacity;

    @SuppressWarnings(value = { "unchecked" }) public Builder() {
      capacity = -1;
      weigher = Weighers.entrySingleton();
      initialCapacity = DEFAULT_INITIAL_CAPACITY;
      concurrencyLevel = DEFAULT_CONCURRENCY_LEVEL;
      listener = (EvictionListener<K, V>) DiscardingListener.INSTANCE;
    }

    /**
	 * Specifies the initial capacity of the hash table (default <tt>16</tt>).
	 * This is the number of key-value pairs that the hash table can hold
	 * before a resize operation is required.
	 *
	 * @param initialCapacity the initial capacity used to size the hash table
	 *     to accommodate this many entries.
	 * @return This builder.
	 * @throws IllegalArgumentException if the initialCapacity is negative
	 */
    public Builder<K, V> initialCapacity(int initialCapacity) {
      checkArgument(initialCapacity >= 0);
      this.initialCapacity = initialCapacity;
      return this;
    }

    /**
	 * Specifies the maximum weighted capacity to coerce the map to and may
	 * exceed it temporarily.
	 *
	 * @param capacity the weighted threshold to bound the map by
	 * @return This builder.
	 * @throws IllegalArgumentException if the maximumWeightedCapacity is
	 *     negative
	 */
    public Builder<K, V> maximumWeightedCapacity(long capacity) {
      checkArgument(capacity >= 0);
      this.capacity = capacity;
      return this;
    }

    /**
	 * Specifies the estimated number of concurrently updating threads. The
	 * implementation performs internal sizing to try to accommodate this many
	 * threads (default <tt>16</tt>).
	 *
	 * @param concurrencyLevel the estimated number of concurrently updating
	 *     threads
	 * @return This builder.
	 * @throws IllegalArgumentException if the concurrencyLevel is less than or
	 *     equal to zero
	 */
    public Builder<K, V> concurrencyLevel(int concurrencyLevel) {
      checkArgument(concurrencyLevel > 0);
      this.concurrencyLevel = concurrencyLevel;
      return this;
    }

    /**
	 * Specifies an optional listener that is registered for notification when
	 * an entry is evicted.
	 *
	 * @param listener the object to forward evicted entries to
	 * @return This builder.
	 * @throws NullPointerException if the listener is null
	 */
    public Builder<K, V> listener(EvictionListener<K, V> listener) {
      checkNotNull(listener);
      this.listener = listener;
      return this;
    }

    /**
	 * Specifies an algorithm to determine how many the units of capacity an
	 * entry consumes. The default algorithm bounds the map by the number of
	 * key-value pairs by giving each entry a weight of <tt>1</tt>.
	 *
	 * @param weigher the algorithm to determine a entry's weight
	 * @return This builder.
	 * @throws NullPointerException if the weigher is null
	 */
    public Builder<K, V> weigher(Weigher<? super V> weigher) {
      this.weigher = (weigher == Weighers.singleton()) ? Weighers.<K, V>entrySingleton() : new BoundedEntryWeigher<K, V>(Weighers.asEntryWeigher(weigher));
      return this;
    }

    /**
	 * Specifies an algorithm to determine how many the units of capacity a
	 * value consumes. The default algorithm bounds the map by the number of
	 * key-value pairs by giving each entry a weight of <tt>1</tt>.
	 *
	 * @param weigher the algorithm to determine a value's weight
	 * @return This builder.
	 * @throws NullPointerException if the weigher is null
	 */
    public Builder<K, V> weigher(EntryWeigher<? super K, ? super V> weigher) {
      this.weigher = (weigher == Weighers.entrySingleton()) ? Weighers.<K, V>entrySingleton() : new BoundedEntryWeigher<K, V>(weigher);
      return this;
    }

    /**
	 * Creates a new {@link ConcurrentLinkedHashMap} instance.
	 *
	 * @return The built {@link ConcurrentLinkedHashMap} instance.
	 * @throws IllegalStateException if the maximum weighted capacity was
	 *     not set
	 */
    public ConcurrentLinkedHashMap<K, V> build() {
      checkState(capacity >= 0);
      return new ConcurrentLinkedHashMap<K, V>(this);
    }
  }
}