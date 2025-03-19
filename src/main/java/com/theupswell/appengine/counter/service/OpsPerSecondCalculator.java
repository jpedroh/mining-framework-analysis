<<<<<<< /usr/src/app/output/theupswell/appengine-counter/303ad503d31a1b375af2a85f46a0c512ace1dfd1/src/main/java/com/theupswell/appengine/counter/service/OpsPerSecondCalculator.java/left.java
package com.theupswell.appengine.counter.service;

/**
 * A helper class that translates between an "Operations Per-Second" number (aka "opsPerSecond") and the number of
 * counter shards that can sustain the indicated operations per-second.
 */
public class OpsPerSecondCalculator
{
	private static final int ENTITY_GROUP_UPDATES_PER_SECOND = 5;

	/**
	 * Computes the number of "operations per second" ("opsPerSecond) that the indicated number of shards can support.
	 *
	 * @param numShards
	 * @return
	 */
	public static int getOpsPerSecond(final int numShards)
	{
		// A counter can sustain ~5 updates per second per shard.
		return numShards * ENTITY_GROUP_UPDATES_PER_SECOND;
	}

	/**
	 * Computes the number of shards required to support the indicated "operations per second" reflected by
	 * {@code opsPerSecond}.
	 * 
	 * @param opsPerSecond
	 * @return
	 */
	public static int getNumShards(final int opsPerSecond)
	{
		// A counter can sustain ~5 updates per second per shard, and a counter needs at least 1 shard. 2 shards can
		// support up to ~10 ops per second.

		return (int) Math.ceil(opsPerSecond / ENTITY_GROUP_UPDATES_PER_SECOND);
	}
}
||||||| /usr/src/app/output/theupswell/appengine-counter/303ad503d31a1b375af2a85f46a0c512ace1dfd1/src/main/java/com/theupswell/appengine/counter/service/OpsPerSecondCalculator.java/base.java
package com.theupswell.appengine.counter.service;

/**
 * A helper class that translates between an "Operations Per-Second" number (aka "opsPerSecond") and the number of
 * counter shards that can sustain the indicated operations per-second.
 */
public class OpsPerSecondCalculator
{
	private static final int ENTITY_GROUP_UPDATES_PER_SECOND = 5;

	/**
	 * Computes the number of "operations per second" ("opsPerSecond) that the indicated number of shards can support.
	 *
	 * @param numShards
	 * @return
	 */
	public static int getOpsPerSecond(final int numShards)
	{
		// A counter can sustain ~5 updates per second per shard.
		return numShards * ENTITY_GROUP_UPDATES_PER_SECOND;
	}

	/**
	 * Computes the number of shards required to support the indicated "operations per second" reflected by
	 * {@code opsPerSecond}.
	 * 
	 * @param opsPerSecond
	 * @return
	 */
	public static int getNumShards(final int opsPerSecond)
	{
		// A counter can sustain ~5 updates per second per shard, and a counter needs at least 1 shard. 2 shards can
		// support up to ~10 ops per second.

		return (int) Math.ceil(opsPerSecond / ENTITY_GROUP_UPDATES_PER_SECOND);

	}
}
=======
fatal: path 'src/main/java/com/theupswell/appengine/counter/service/OpsPerSecondCalculator.java' does not exist in '2eaf5b5c27c22c411eb066f546cb5051b1a73a9d'
>>>>>>> /usr/src/app/output/theupswell/appengine-counter/303ad503d31a1b375af2a85f46a0c512ace1dfd1/src/main/java/com/theupswell/appengine/counter/service/OpsPerSecondCalculator.java/right.java
