package org.apache.accumulo.server.tablets;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.accumulo.core.util.LazySingletons.RANDOM;
import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.conf.Property;
import org.apache.accumulo.core.util.FastFormat;
import org.apache.accumulo.server.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allocates unique names for an accumulo instance. The names are unique for the lifetime of the
 * instance.
 *
 * This is useful for filenames because it makes caching easy.
 */
public class UniqueNameAllocator {
  private static Logger log = LoggerFactory.getLogger(UniqueNameAllocator.class);

  private static final int DEFAULT_BASE_ALLOCATION = Integer.parseInt(Property.GENERAL_FILENAME_BASE_ALLOCATION.getDefaultValue());

  private ServerContext context;

  private long next = 0;

  private long maxAllocated = 0;

  private String nextNamePath;

  public UniqueNameAllocator(ServerContext context) {
    this.context = context;
    nextNamePath = Constants.ZROOT + "/" + context.getInstanceID() + Constants.ZNEXT_FILE;
  }

  public synchronized String getNextName() {
    while (next >= maxAllocated) {
      final int allocate = 
<<<<<<< /usr/src/app/output/apache/accumulo/5bec1c33af6c464ce75f25ade7b4bd4dba0709ea/server/base/src/main/java/org/apache/accumulo/server/tablets/UniqueNameAllocator.java/left.java
      100 + RANDOM.get().nextInt(100)
=======
      getAllocation()
>>>>>>> /usr/src/app/output/apache/accumulo/5bec1c33af6c464ce75f25ade7b4bd4dba0709ea/server/base/src/main/java/org/apache/accumulo/server/tablets/UniqueNameAllocator.java/right.java
      ;
      try {
        byte[] max = context.getZooReaderWriter().mutateExisting(nextNamePath, (currentValue) -> {
          long l = Long.parseLong(new String(currentValue, UTF_8), Character.MAX_RADIX);
          return Long.toString(l + allocate, Character.MAX_RADIX).getBytes(UTF_8);
        });
        maxAllocated = Long.parseLong(new String(max, UTF_8), Character.MAX_RADIX);
        next = maxAllocated - allocate;
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return new String(FastFormat.toZeroPaddedString(next++, 7, Character.MAX_RADIX, new byte[0]), UTF_8);
  }

  private int getAllocation() {
    int baseAllocation = context.getConfiguration().getCount(Property.GENERAL_FILENAME_BASE_ALLOCATION);
    int jitterAllocation = context.getConfiguration().getCount(Property.GENERAL_FILENAME_JITTER_ALLOCATION);
    if (baseAllocation <= 0) {
      log.warn("{} was set to {}, must be greater than 0. Using the default {}.", Property.GENERAL_FILENAME_BASE_ALLOCATION.getKey(), baseAllocation, DEFAULT_BASE_ALLOCATION);
      baseAllocation = DEFAULT_BASE_ALLOCATION;
    }
    int totalAllocation = baseAllocation;
    if (jitterAllocation > 0) {
      totalAllocation += random.nextInt(jitterAllocation);
    }
    log.debug("Allocating {} filenames", totalAllocation);
    return totalAllocation;
  }
}