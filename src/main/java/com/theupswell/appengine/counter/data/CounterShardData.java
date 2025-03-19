package com.theupswell.appengine.counter.data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindex;
import com.theupswell.appengine.counter.data.base.AbstractEntity;

/**
 * Represents a discrete shard belonging to a named counter.<br/>
 * <br/>
 * An individual shard is written to infrequently to allow the counter in aggregate to be incremented rapidly.
 * 
 * @author David Fuelling
 */
@Entity @Cache @Getter @Setter @Unindex @ToString(callSuper = true) public class CounterShardData extends AbstractEntity {
  static final String COUNTER_SHARD_KEY_SEPARATOR = "-";

  private long count;

  /**
	 * Default Constructor for Objectify
	 * 
	 * @deprecated Use the param-based constructors instead.
	 */
  @Deprecated public CounterShardData() {
  }

  /**
	 * Param-based Constructor
	 * 
	 * @param counterName
	 * @param shardNumber
	 */
  public CounterShardData(final String counterName, final int shardNumber) {
    setId(constructCounterShardIdentifier(counterName, shardNumber));
  }

  /**
	 * Param-based Constructor
	 *
	 * @param counterShardDataKey
	 */
  public CounterShardData(final Key<CounterShardData> counterShardDataKey) {
    Preconditions.checkNotNull(counterShardDataKey);
    setId(counterShardDataKey.getName());
  }

  /**
	 * @return The last dateTime that an increment occurred.
	 */
  public DateTime getLastIncrement() {
    return getUpdatedDateTime();
  }

  /**
	 * Set the amount of this shard with a new {@code count}.
	 * 
	 * @param count
	 */
  public void setCount(long count) {
    this.count = count;
    this.setUpdatedDateTime(DateTime.now(DateTimeZone.UTC));
  }

  /**
	 * Helper method to set the internal identifier for this entity.
	 * 
	 * @param counterName
	 * @param shardNumber A unique identifier to distinguish shards for the same {@code counterName} from each other.
	 */
  @VisibleForTesting static String constructCounterShardIdentifier(final String counterName, final int shardNumber) {
    Preconditions.checkNotNull(counterName);
    Preconditions.checkArgument(!StringUtils.isBlank(counterName), "CounterData Names may not be null, blank, or empty!");
    Preconditions.checkArgument(shardNumber >= 0, "shardNumber must be greater than or equal to 0!");
    return counterName + COUNTER_SHARD_KEY_SEPARATOR + shardNumber;
  }

  /**
	 * Create a {@link Key Key<CounterShardData>}. Keys for this entity are not "parented" so that they can be added
	 * under high volume load in a given application. Note that CounterData will be in a namespace specific.
	 * 
	 * @param counterName
	 * @param shardNumber
	 * @return
	 */
  public static Key<CounterShardData> key(final String counterName, final int shardNumber) {
    return Key.create(CounterShardData.class, constructCounterShardIdentifier(counterName, shardNumber));
  }
}