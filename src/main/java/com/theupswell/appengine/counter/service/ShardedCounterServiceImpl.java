package com.theupswell.appengine.counter.service;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import com.google.appengine.api.capabilities.CapabilitiesService;
import com.google.appengine.api.capabilities.CapabilitiesServiceFactory;
import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.IdentifiableValue;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.theupswell.appengine.counter.Counter;
import com.theupswell.appengine.counter.CounterBuilder;
import com.theupswell.appengine.counter.data.CounterData;
import com.theupswell.appengine.counter.data.CounterData.CounterStatus;
import com.theupswell.appengine.counter.data.CounterShardData;

/**
 * A durable implementation of a {@link ShardedCounterService} that provides counter increment, decrement, and delete
 * functionality. This implementation is is backed by one or more Datastore "shard" entities which each hold a discrete
 * count in order to provide for high throughput. When aggregated, the sum total of all CounterShard entity counts is
 * the value of the counter. See the google link below for more details on Sharded Counters in Appengine. Note that
 * CounterShards may go negative, depending on the configuration of the counter. Also, note that there is no difference
 * between a counter being "zero" and a counter not existing. As such, there is no concept of "creating" a counter, and
 * deleting a counter will actually just remove all shards for a given counter, thereby resetting the counter to 0. <br/>
 * <br/>
 * Note that this implementation is capable of incrementing/decrementing various counter shard counts but does not
 * automatically increase or reduce the <b>number</b> of shards for a given counter in response to load.<br/>
 * <br/>
 * All datastore operations are performed using Objectify.<br/>
 * <br/>
 * <br/>
 * <b>Incrementing a Counter</b><br/>
 * When incrementing, a random shard is selected to prevent a single shard from being written to too frequently.<br/>
 * <br/>
 * <b>Decrementing a Counter</b><br/>
 * This implementation does not support negative counts, so CounterShard counts can not go below zero. Thus, when
 * decrementing, a random shard is selected to prevent a single shard from being written to too frequently. However, if
 * a particular shard cannot be decremented then other shards are tried until all shards have been tried. If no shard
 * can be decremented, then the decrement function is considered complete, even though nothing was decremented. Because
 * of this, it is possible that a request to reduce a counter by more than its available count will succeed with a
 * lesser count having been reduced. <b>Getting the Count</b><br/>
 * Aggregate ounter lookups are first attempted using Memcache. If the counter value is not in the cache, then
 * the shards are read from the datastore and accumulated to reconstruct the current count. This operation has a cost of
 * O(numShards), or O(N). Increase the number of shards to improve counter increment throughput, but beware that this
 * has a cost - it makes counter lookups from the Datastore more expensive.<br/>
 * <br/>
 * <b>Throughput</b><br/>
 * As an upper-bound calculation of throughput and shard-count, the Psy "Gangham Style" youtube video (arguably one of
 * the most viral videos of all time) reached 750m views in approximately 60 days. If that video's 'hit-counter' was
 * using appengine-counter as its underlying implementation, then the counter would have needed to sustain an increment
 * rate of 145 updates per second for 60 days. Since each CounterShard could have provided up to 5 updates per second
 * (this seems to be the average indicated by the appengine team and in various documentation), then the counter would
 * have required at least 29 CounterShard entities, which in the grand scheme of the Appengine Datastore seems pretty
 * small. In reality, a counter with this much traffic would not need to be highly consistent, but it could have been
 * using appengine-counter.<br/>
 * <br/>
 * <b>Future Improvements</b><br/>
 * <ul>
 * <li><b>CounterShard Expansion</b>: A shard-expansion mechanism can be envisioned to increase the number of
 * CounterShard entities for a particular Counter when load increases to a specified amount for a given Counter.</li>
 * <li><b>CounterShard Contraction</b>: A shard-reduction mechanism can be envisioned to aggregate multiple shards (and
 * their counts) into fewer shards to improve datastore counter lookup performance when Counter load falls below some
 * threshold.</li>
 * <li><b>Counter Reset</b>: Reset a counter to zero by resetting all counter shards 'counts' to zero. This would need
 * to be, by nature of this implementation, async.</li>
 * </ul>
 * 
 * @see "https://developers.google.com/appengine/articles/sharding_counters"
 * @author David Fuelling
 */
public class ShardedCounterServiceImpl implements ShardedCounterService {
  private static final Logger logger = Logger.getLogger(ShardedCounterServiceImpl.class.getName());

  public static final String COUNTER_NAME = "counterName";

  /**
	 * A random number generating, for distributing writes across shards.
	 */
  protected final Random generator = new Random();

  protected final MemcacheService memcacheService;

  protected final CapabilitiesService capabilitiesService;

  protected final ShardedCounterServiceConfiguration config;

  /**
	 * Default Constructor for Dependency-Injection that uses {@link MemcacheServiceFactory} to construct the
	 * {@link MemcacheService} and {@link CapabilitiesServiceFactory} to construct the {@link CapabilitiesService}.
	 * dependency for this service.
	 */
  public ShardedCounterServiceImpl() {
    this(MemcacheServiceFactory.getMemcacheService(), CapabilitiesServiceFactory.getCapabilitiesService());
  }

  /**
	 * Default Constructor for Dependency-Injection that uses a default number of counter shards (set to 1) and a
	 * default configuration per {@link ShardedCounterServiceConfiguration#defaultConfiguration}.
	 * 
	 * @param memcacheService
	 * @param capabilitiesService
	 */
  public ShardedCounterServiceImpl(final MemcacheService memcacheService, final CapabilitiesService capabilitiesService) {
    this(memcacheService, capabilitiesService, ShardedCounterServiceConfiguration.defaultConfiguration());
  }

  /**
	 * Default Constructor for Dependency-Injection.
	 * 
	 * @param memcacheService
	 * @param capabilitiesService
	 * @param config The configuration for this service
	 */
  public ShardedCounterServiceImpl(final MemcacheService memcacheService, final CapabilitiesService capabilitiesService, final ShardedCounterServiceConfiguration config) {
    Preconditions.checkNotNull(memcacheService, "Invalid memcacheService!");
    Preconditions.checkNotNull(capabilitiesService, "Invalid capabilitiesService!");
    Preconditions.checkNotNull(config);
    this.memcacheService = memcacheService;
    this.capabilitiesService = capabilitiesService;
    this.config = config;
    Preconditions.checkArgument(config.getNumInitialShards() > 0, "Number of Shards for a new CounterData must be greater than 0!");
    if (config.getRelativeUrlPathForDeleteTaskQueue() != null) {
      Preconditions.checkArgument(!StringUtils.isBlank(config.getRelativeUrlPathForDeleteTaskQueue()), "Must be null (for the Default Queue) or a non-blank String!");
    }
  }

  /**
	 * The cache will expire after {@code defeaultExpiration} seconds, so the counter will be accurate after a minute
	 * because it performs a load from the datastore.
	 * 
	 * @param counterName
	 * @return
	 */
  @Override public Counter getCounter(final String counterName) {
    Preconditions.checkArgument(!StringUtils.isBlank(counterName), "CounterData Names may not be null, blank, or empty!");
    final CounterData counterData = this.getOrCreateCounterData(counterName);
    if (CounterData.CounterStatus.DELETING == counterData.getCounterStatus()) {
      return new CounterBuilder(counterData).withCount(0L).build();
    }
    final String memCacheKey = this.assembleCounterKeyforMemcache(counterName);
    final Long cachedCounterCount = this.memcacheSafeGet(memCacheKey);
    if (cachedCounterCount != null) {
      if (getLogger().isLoggable(Level.FINE)) {
        getLogger().log(Level.FINE, "Cache Hit for Counter Named \"" + counterName + "\": value=" + cachedCounterCount);
      }
      return new CounterBuilder(counterData).withCount(cachedCounterCount).build();
    } else {
      if (getLogger().isLoggable(Level.FINE)) {
        getLogger().log(Level.FINE, "Cache Miss for CounterData Named \"" + counterName + "\": value=" + cachedCounterCount + ".  Checking Datastore instead!");
        getLogger().log(Level.FINE, "Aggregating counts from " + counterData.getNumShards() + " CounterDataShards for CounterData named \'" + counterData.getCounterName() + "\'!");
      }
      final List<Key<CounterShardData>> keysToLoad = Lists.newArrayList();
      for (int i = 0; i < counterData.getNumShards(); i++) {
        final Key<CounterShardData> counterShardKey = CounterShardData.key(counterData.getCounterName(), i);
        keysToLoad.add(counterShardKey);
      }
      long sum = 0;
      final Map<Key<CounterShardData>, CounterShardData> counterShardDatasMap = ObjectifyService.ofy().transactionless().load().keys(keysToLoad);
      final Collection<CounterShardData> counterShardDatas = counterShardDatasMap.values();
      for (CounterShardData counterShardData : counterShardDatas) {
        if (counterShardData != null) {
          sum += counterShardData.getCount();
        }
      }
      if (getLogger().isLoggable(Level.FINE)) {
        getLogger().log(Level.FINE, "The Datastore is reporting a count of " + sum + " for CounterData \"" + counterData.getCounterName() + "\" count.  Resetting memcache count to " + sum + " for this counter name");
      }
      try {
        memcacheService.put(memCacheKey, new Long(sum), config.getDefaultExpiration(), SetPolicy.SET_ALWAYS);
      } catch (MemcacheServiceException mse) {
      }
      return new CounterBuilder(counterData).withCount(sum).build();
    }
  }

  /**
	 * NOTE: We don't allow the counter's "count" to be updated by this method. Instead, {@link #increment} and
	 * {@link #decrement} should be used.
	 * 
	 * @param incomingCounter
	 */
  @Override public void updateCounterDetails(final Counter incomingCounter) {
    Preconditions.checkNotNull(incomingCounter);
    ObjectifyService.ofy().transact(new Work<Void>() {
      @Override public Void run() {
        final CounterData counterDataInDatastore = getOrCreateCounterData(incomingCounter.getCounterName());
        assertCounterDetailsMutatable(counterDataInDatastore.getCounterName(), counterDataInDatastore.getCounterStatus());
        counterDataInDatastore.setCounterDescription(incomingCounter.getCounterDescription());
        if (incomingCounter.getNumShards() < counterDataInDatastore.getNumShards()) {
          throw new RuntimeException("Reducing the number of counter shards is not currently allowed!  See https://github.com/theupswell/appengine-counter/issues/4 for more details.");
        }
        counterDataInDatastore.setNumShards(incomingCounter.getNumShards());
        counterDataInDatastore.setCounterStatus(incomingCounter.getCounterStatus());
        ObjectifyService.ofy().save().entity(counterDataInDatastore).now();
        return null;
      }
    });
  }

  @Override public Counter increment(final String counterName) {
    return this.increment(counterName, 1L);
  }

  @Override public Counter increment(final String counterName, final long amount, boolean isolatedTransactionContext) {
    Preconditions.checkArgument(!StringUtils.isBlank(counterName));
    Preconditions.checkArgument(amount > 0, "CounterData increments must be positive numbers!");
    if (isolatedTransactionContext) {
      return this.increment(counterName, amount);
    } else {
      final Work<Long> atomicIncrementShardWork = new IncrementShardWork(counterName, amount);
      final Long amountIncrementedInTx = ObjectifyService.ofy().transact(atomicIncrementShardWork);
      this.incrementMemcacheAtomic2(counterName, amountIncrementedInTx.longValue());
      return getCounter(counterName);
    }
  }

  @Override public Counter increment(final String counterName, final long amount) {
    Preconditions.checkArgument(!StringUtils.isBlank(counterName));
    Preconditions.checkArgument(amount > 0, "CounterData increments must be positive numbers!");
    final Work<Long> atomicIncrementShardWork = new IncrementShardWork(counterName, amount);
    Long amountIncrementedInTx = ObjectifyService.ofy().transactNew(atomicIncrementShardWork);
    this.incrementMemcacheAtomic2(counterName, amountIncrementedInTx.longValue());
    return getCounter(counterName);
  }

  @Override public void incrementInExistingTX(String counterName, long amount) {
    this.increment(counterName, amount);
  }

  @VisibleForTesting final class IncrementShardWork implements Work<Long> {
    private final String counterName;

    private final long amount;

    /**
		 * Required-Args Constructor.
		 *
		 * @param counterName
		 * @param amount
		 */
    IncrementShardWork(final String counterName, final long amount) {
      Preconditions.checkNotNull(counterName);
      Preconditions.checkArgument(!StringUtils.isBlank(counterName));
      this.counterName = counterName;
      Preconditions.checkArgument(amount > 0);
      this.amount = amount;
    }

    /**
		 * NOTE: In order for this to work properly, the CounterShardData must be gotten, created, and updated all in
		 * the same transaction in order to remain consistent (in other words, it must be atomic).
		 *
		 * @return
		 */
    @Override public Long run() {
      final CounterData counterData = getOrCreateCounterData(counterName);
      assertCounterAmountMutatable(counterData.getCounterName(), counterData.getCounterStatus());
      final String counterName = counterData.getCounterName();
      final int currentNumShards = counterData.getNumShards();
      final int shardNumber = generator.nextInt(currentNumShards);
      final Key<CounterShardData> counterShardDataKey = CounterShardData.key(counterName, shardNumber);
      CounterShardData counterShardData = ObjectifyService.ofy().load().key(counterShardDataKey).now();
      if (counterShardData == null) {
        counterShardData = new CounterShardData(counterName, shardNumber);
      }
      counterShardData.setCount(counterShardData.getCount() + amount);
      if (getLogger().isLoggable(Level.FINE)) {
        getLogger().log(Level.FINE, "Saving CounterShardData" + shardNumber + " for CounterData \"" + counterName + "\" with count " + counterShardData.getCount());
      }
      ObjectifyService.ofy().save().entity(counterShardData).now();
      return new Long(amount);
    }
  }

  @Override public Counter decrement(String counterName) {
    return this.decrement(counterName, 1L);
  }

  @Override public Counter decrement(final String counterName, final long amount) {
    Preconditions.checkNotNull(counterName, "CounterName may not be null!");
    Preconditions.checkArgument(!StringUtils.isBlank(counterName));
    final CounterData counterData = getOrCreateCounterData(counterName);
    assertCounterAmountMutatable(counterData.getCounterName(), counterData.getCounterStatus());
    final int currentNumShards = counterData.getNumShards();
    long totalAmountDecremented;
    final int randomShardNum = generator.nextInt(currentNumShards);
    final Key<CounterShardData> randomCounterShardDataKey = CounterShardData.key(counterName, randomShardNum);
    DecrementShardWork decrementShardTask = new DecrementShardWork(counterName, randomCounterShardDataKey, amount);
    Long lAmountDecrementedInTx = ObjectifyService.ofy().transactNew(decrementShardTask);
    long amountDecrementedInTx = lAmountDecrementedInTx == null ? 0L : lAmountDecrementedInTx.longValue();
    totalAmountDecremented = amountDecrementedInTx;
    long amountLeftToDecrement = amount - amountDecrementedInTx;
    if (amountLeftToDecrement > 0) {
      for (int i = 0; i < counterData.getNumShards(); i++) {
        final Key<CounterShardData> sequentialCounterShardDataKey = CounterShardData.key(counterName, i);
        if (sequentialCounterShardDataKey.equals(randomCounterShardDataKey)) {
          continue;
        }
        if (amountLeftToDecrement > 0) {
          decrementShardTask = new DecrementShardWork(counterName, sequentialCounterShardDataKey, amountLeftToDecrement);
          lAmountDecrementedInTx = ObjectifyService.ofy().transactNew(decrementShardTask);
          amountDecrementedInTx = lAmountDecrementedInTx == null ? 0L : lAmountDecrementedInTx.longValue();
          totalAmountDecremented += amountDecrementedInTx;
          amountLeftToDecrement -= amountDecrementedInTx;
        } else {
          break;
        }
      }
    }
    incrementMemcacheAtomic(counterName, (totalAmountDecremented * -1L));
    return getCounter(counterName);
  }

  @VisibleForTesting final class DecrementShardWork implements Work<Long> {
    private final String counterName;

    private final Key<CounterShardData> counterShardKey;

    private final long requestedDecrementAmount;

    /**
		 * Required args Constructor
		 * 
		 * @param counterName
		 * @param counterShardKey
		 * @param requestedDecrementAmount
		 */
    @VisibleForTesting DecrementShardWork(final String counterName, final Key<CounterShardData> counterShardKey, final long requestedDecrementAmount) {
      Preconditions.checkNotNull(counterName, "CounterName may not be null!");
      Preconditions.checkArgument(!StringUtils.isBlank(counterName), "CounterName may not be blank or empty!");
      Preconditions.checkArgument(requestedDecrementAmount >= 0, "Cannot decrement with a negative number!");
      this.counterName = counterName;
      Preconditions.checkNotNull(counterShardKey, "CounterShardKey may not be null!");
      this.counterShardKey = counterShardKey;
      Preconditions.checkArgument(requestedDecrementAmount > 0, "Amount must be greater than zero!");
      this.requestedDecrementAmount = requestedDecrementAmount;
    }

    /**
		 * Attempt to decrement a particular CounterShardData by the {@code decrementAmount}, or something less if the
		 * shard does not have enough count to fulfill the entire decrement request. Note that CounterShardData counts
		 * are not permitted to go negative!
		 */
    @Override public Long run() {
      CounterData counterData = getOrCreateCounterData(counterName);
      assertCounterAmountMutatable(counterData.getCounterName(), counterData.getCounterStatus());
      CounterShardData counterShardData = ObjectifyService.ofy().load().key(counterShardKey).now();
      if (counterShardData == null) {
        return new Long(0L);
      }
      long decrementAmount = computeLargestDecrementAmountForShard(counterShardData.getCount(), requestedDecrementAmount);
      if (decrementAmount <= 0) {
        if (getLogger().isLoggable(Level.FINE)) {
          getLogger().fine("Unable to Decrement CounterShardData (" + counterShardKey + ") with count " + counterShardData.getCount() + " and requestedDecrementAmount of " + requestedDecrementAmount);
        }
        return new Long(0);
      } else {
        counterShardData.setCount(counterShardData.getCount() - decrementAmount);
        if (getLogger().isLoggable(Level.FINE)) {
          getLogger().fine("Saving Decremented CounterShardData (" + counterShardKey + ") with count " + counterShardData.getCount() + " after requestedDecrementAmount of " + requestedDecrementAmount + " and actual decrementAmount of " + decrementAmount);
        }
        ObjectifyService.ofy().save().entity(counterShardData).now();
        return new Long(decrementAmount);
      }
    }

    /**
		 * Returns a decrement amount that is either zero, or a positive long amount that a particular CounterShard can
		 * be reduced by.
		 * 
		 * @param counterShardCount
		 * @param decrementAmount
		 * @return
		 */
    @VisibleForTesting protected long computeLargestDecrementAmountForShard(long counterShardCount, long decrementAmount) {
      if (counterShardCount - decrementAmount < 0) {
        long delta = counterShardCount - decrementAmount;
        if (delta < 0L) {
          decrementAmount -= Math.abs(delta);
          if (decrementAmount < 0L) {
            decrementAmount = 0L;
          }
        }
      }
      return decrementAmount;
    }
  }

  @Override public void delete(final String counterName) {
    Preconditions.checkNotNull(counterName);
    Preconditions.checkArgument(!StringUtils.isBlank(counterName));
    ObjectifyService.ofy().transactNew(new VoidWork() {
      @Override public void vrun() {
        Key<CounterData> counterDataKey = CounterData.key(counterName);
        final CounterData counterData = ObjectifyService.ofy().load().key(counterDataKey).now();
        if (counterData == null) {
          return;
        }
        Queue queue;
        if (config.getDeleteCounterShardQueueName() == null) {
          queue = QueueFactory.getDefaultQueue();
        } else {
          queue = QueueFactory.getQueue(config.getDeleteCounterShardQueueName());
        }
        counterData.setCounterStatus(CounterData.CounterStatus.DELETING);
        ObjectifyService.ofy().save().entity(counterData);
        TaskOptions taskOptions = TaskOptions.Builder.withParam(COUNTER_NAME, counterName);
        if (config.getRelativeUrlPathForDeleteTaskQueue() != null) {
          taskOptions = taskOptions.url(config.getRelativeUrlPathForDeleteTaskQueue());
        }
        queue.add(taskOptions);
      }
    });
  }

  @Override public void onTaskQueueCounterDeletion(final String counterName) {
    Preconditions.checkNotNull(counterName);
    final Key<CounterData> counterDataKey = CounterData.key(counterName);
    final CounterData counterData = ObjectifyService.ofy().load().key(counterDataKey).now();
    if (counterData == null) {
      getLogger().severe("While attempting to delete CounterData named \"" + counterName + "\", no CounterData was found in the Datastore!");
      this.memcacheSafeDelete(counterName);
      return;
    } else {
      if (counterData.getCounterStatus() != CounterData.CounterStatus.DELETING) {
        throw new RuntimeException("Can\'t delete counter \'" + counterName + "\' because it is currently not in the DELETING state!");
      } else {
        Collection<Key<CounterShardData>> counterShardDataKeys = Lists.newArrayList();
        for (int i = 0; i < counterData.getNumShards(); i++) {
          Key<CounterShardData> counterShardDataKey = CounterShardData.key(counterName, i);
          counterShardDataKeys.add(counterShardDataKey);
        }
        ObjectifyService.ofy().transactionless().delete().keys(counterShardDataKeys).now();
        ObjectifyService.ofy().transactionless().delete().key(counterData.getTypedKey()).now();
        this.memcacheSafeDelete(counterName);
      }
    }
  }

  /**
	 * Attempt to delete a counter from memcache but swallow any exceptions from memcache if it's down.
	 * 
	 * @param counterName
	 */
  @VisibleForTesting void memcacheSafeDelete(final String counterName) {
    Preconditions.checkNotNull(counterName);
    try {
      memcacheService.delete(counterName);
    } catch (MemcacheServiceException mse) {
    }
  }

  /**
	 * Attempt to delete a counter from memcache but swallow any exceptions from memcache if it's down.
	 * 
	 * @param memcacheKey
	 */
  @VisibleForTesting Long memcacheSafeGet(final String memcacheKey) {
    Preconditions.checkNotNull(memcacheKey);
    Long cachedCounterCount;
    try {
      cachedCounterCount = (Long) memcacheService.get(memcacheKey);
    } catch (MemcacheServiceException mse) {
      cachedCounterCount = null;
    }
    return cachedCounterCount;
  }

  /**
	 * Helper method to get (or create and then get) a {@link CounterData} from the Datastore with a given name. The
	 * result of this function is guaranteed to be non-null if no exception is thrown.
	 * 
	 * @param counterName
	 * @return
	 * @throws NullPointerException in the case where no CounterData could be loaded from the Datastore.
	 */
  @VisibleForTesting protected CounterData getOrCreateCounterData(final String counterName) {
    Preconditions.checkNotNull(counterName);
    final Key<CounterData> counterKey = CounterData.key(counterName);
    return ObjectifyService.ofy().transactNew(new Work<CounterData>() {
      @Override public CounterData run() {
        CounterData counterData = ObjectifyService.ofy().load().key(counterKey).now();
        if (counterData == null) {
          counterData = new CounterData(counterName, config.getNumInitialShards());
          ObjectifyService.ofy().save().entity(counterData).now();
        }
        return counterData;
      }
    });
  }

  /**
	 * Increment the memcache version of the named-counter by {@code amount} (positive or negative) in an atomic
	 * fashion. Use memcache as a Semaphore/Mutex, and retry up to 10 times if other threads are attempting to update
	 * memcache at the same time. If nothing is in Memcache when this function is called, then do nothing because only
	 * #getCounter should "put" a value to memcache.
	 * 
	 * @param counterName
	 * @param amount
	 * @return The new count of this counter as reflected by memcache
	 */
  @VisibleForTesting protected Optional<Long> incrementMemcacheAtomic(final String counterName, final long amount) {
    final String memCacheKey = this.assembleCounterKeyforMemcache(counterName);
    int numRetries = 10;
    while (numRetries > 0) {
      try {
        IdentifiableValue identifiableCounter = memcacheService.getIdentifiable(memCacheKey);
        if (identifiableCounter == null || (identifiableCounter != null && identifiableCounter.getValue() == null)) {
          if (getLogger().isLoggable(Level.FINE)) {
            getLogger().fine("No identifiableCounter was found in Memcache.  Unable to Atomically increment for CounterName \"" + counterName + "\".  Memcache will be populated on the next called to getCounter()!");
          }
          break;
        }
        Long cachedCounterAmount = (Long) identifiableCounter.getValue();
        long newMemcacheAmount = cachedCounterAmount.longValue() + amount;
        if (newMemcacheAmount < 0) {
          newMemcacheAmount = 0;
        }
        if (getLogger().isLoggable(Level.FINE)) {
          getLogger().fine("Just before Atomic Increment of " + amount + ", Memcache has value " + identifiableCounter.getValue());
        }
        if (memcacheService.putIfUntouched(counterName, identifiableCounter, new Long(newMemcacheAmount), config.getDefaultExpiration())) {
          if (getLogger().isLoggable(Level.FINE)) {
            getLogger().fine("memcacheService.putIfUntouched SUCCESS! with value " + newMemcacheAmount);
          }
          return Optional.of(new Long(newMemcacheAmount));
        } else {
          if (getLogger().isLoggable(Level.WARNING)) {
            getLogger().log(Level.WARNING, "Unable to update memcache counter atomically.  Retrying " + numRetries + " more times...");
          }
        }
      } catch (MemcacheServiceException mse) {
        if (numRetries-- > 0) {
          if (getLogger().isLoggable(Level.WARNING)) {
            getLogger().log(Level.WARNING, "Unable to update memcache counter atomically.  Retrying " + numRetries + " more times...", mse);
          }
          continue;
        } else {
          getLogger().log(Level.SEVERE, "Unable to update memcache counter atomically, with no more allowed retries.  Evicting counter named " + counterName + " from the cache!", mse);
          memcacheService.delete(memCacheKey);
          break;
        }
      }
    }
    return Optional.absent();
  }

  /**
	 * <p>
	 * Increment the memcache version of the named-counter by {@code amount} (positive or negative) in an atomic fashion
	 * using {@link MemcacheService#increment(Object, long, Long)}.
	 * </p>
	 * <p>
	 * Use memcache as a Semaphore/Mutex, and retry up to 10 times if other threads are attempting to update memcache at
	 * the same time. If nothing is in Memcache when this function is called, then do nothing because only #getCounter
	 * should "put" a value to memcache.
	 * </p>
	 * 
	 * @param counterName
	 * 
	 * @param amount
	 * 
	 * @return The new count of this counter as reflected by memcache
	 */
  @VisibleForTesting protected Optional<Long> incrementMemcacheAtomic2(final String counterName, final long amount) {
    Preconditions.checkNotNull(counterName);
    Preconditions.checkArgument(amount > 0);
    final String memCacheKey = this.assembleCounterKeyforMemcache(counterName);
    int numRetries = 10;
    while (numRetries > 0) {
      try {
        final Long postIncrementValue = memcacheService.increment(memCacheKey, amount);
        if (postIncrementValue == null) {
          if (getLogger().isLoggable(Level.FINE)) {
            getLogger().fine(String.format("While trying to increment Memcache, no value was found for counter \'%s\'.  Memcache will be populated on the next called to getCounter()!", counterName));
          }
          break;
        }
        if (getLogger().isLoggable(Level.FINE)) {
          getLogger().fine(String.format("Memcache: Increment SUCCESS! Post-Increment counter is %s", postIncrementValue));
        }
        return Optional.of(postIncrementValue);
      } catch (InvalidValueException | MemcacheServiceException memcacheException) {
        if (numRetries-- > 0) {
          if (getLogger().isLoggable(Level.WARNING)) {
            getLogger().log(Level.WARNING, String.format("Memcache: Unable to atomically increment counter \'%s\'.  Retrying %s more times...", counterName, numRetries), memcacheException);
          }
          continue;
        } else {
          getLogger().log(Level.SEVERE, String.format("Memcache: Unable to atomically increment counter \'%s\', with no more retries.   Evicting counter from the cache!", counterName), memcacheException);
          memcacheService.delete(memCacheKey);
          break;
        }
      }
    }
    return Optional.absent();
  }

  /**
	 * Assembles a CounterKey for Memcache
	 * 
	 * @param counterName
	 * @return
	 */
  @VisibleForTesting protected String assembleCounterKeyforMemcache(final String counterName) {
    Preconditions.checkNotNull(counterName);
    return counterName;
  }

  /**
	 * @return
	 */
  protected Logger getLogger() {
    return logger;
  }

  /**
	 * Helper method to determine if a counter's amount can be mutated (incremented or decremented). In order for that
	 * to happen, the counter's status must be {@link CounterStatus#AVAILABLE}.
	 * 
	 * @param counterName
	 * @param counterStatus
	 * 
	 * @return
	 */
  @VisibleForTesting protected void assertCounterAmountMutatable(final String counterName, final CounterStatus counterStatus) {
    if (counterStatus != CounterStatus.AVAILABLE) {
      throw new RuntimeException(String.format("Can\'t mutate the amount of counter \'%s\' because it\'s currently in the %s state but must be in in the %s state!", counterName, counterStatus.name(), CounterStatus.AVAILABLE));
    }
  }

  /**
	 * Helper method to determine if a counter's amount can be mutated (incremented or decremented). In order for that
	 * to happen, the counter's status must be {@link CounterStatus#AVAILABLE}.
	 * 
	 * @param counterName
	 * @param counterStatus
	 * 
	 * @return
	 */
  @VisibleForTesting protected void assertCounterDetailsMutatable(final String counterName, final CounterStatus counterStatus) {
    if (counterStatus != CounterStatus.AVAILABLE && counterStatus != CounterStatus.READ_ONLY_COUNT) {
      throw new RuntimeException("Can\'t mutate the details of counter \"" + counterName + "\" because it\'s currently in the " + counterStatus + " state but must be in in the " + CounterStatus.AVAILABLE + " or " + CounterStatus.READ_ONLY_COUNT + " state!");
    }
  }
}