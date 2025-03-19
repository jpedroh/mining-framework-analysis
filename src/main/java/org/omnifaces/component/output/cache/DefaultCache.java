package org.omnifaces.component.output.cache;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.omnifaces.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * An in-memory cache implementation that's used if the user did not configure an explicit caching provider.
 * <p>
 * For the actual implementation, a repackaged {@link ConcurrentLinkedHashMap} is used if a maximum capacity is requested,
 * otherwise a plain {@link ConcurrentHashMap} is used.
 * <p>
 * <b>See:</b> <a href="http://code.google.com/p/concurrentlinkedhashmap">http://code.google.com/p/concurrentlinkedhashmap</a>
 *
 * @since 1.1
 * @author Arjan Tijms
 *
 */
public class DefaultCache extends TimeToLiveCache {
  private static final long serialVersionUID = 9043165102510796018L;

  public DefaultCache(Integer defaultTimeToLive, Integer maxCapacity) {
    super(defaultTimeToLive);
    setCacheStore(createCacheStore(maxCapacity));
  }

  private Map<String, CacheEntry> createCacheStore(Integer maxCapacity) {
    if (maxCapacity != null) {
      return new ConcurrentLinkedHashMap.Builder<String, CacheEntry>().maximumWeightedCapacity(maxCapacity).build();
    } else {
      return new ConcurrentHashMap<String, CacheEntry>();
    }
  }
}