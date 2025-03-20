package com.grunka.random.fortuna.accumulator;

import com.grunka.random.fortuna.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Accumulator {
    private final AtomicInteger sourceCount = new AtomicInteger(0);
<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/left.java
    private final List<ScheduledFuture<?>> entropyFutures = new ArrayList<>();
||||||| /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/base.java
    private final List<ScheduledFuture<?>> entropyFutures = new ConcurrentHashMap<>();
=======
    private final List<ScheduledFuture<?>> entropyFutures = new HashSet<>();
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/right.java
    private final Pool[] pools;
    private final ScheduledExecutorService scheduler;
    public Accumulator(Pool[] pools, ScheduledExecutorService scheduler) {
        this.pools = pools;
        this.scheduler = scheduler;
    }
    public Pool[] getPools() {
        return pools;
    }
<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/left.java
    public void addSource(EntropySource entropySource) {
        int sourceId = sourceCount.getAndIncrement();
        EventAdder eventAdder = new EventAdderImpl(sourceId, pools);
        AtomicBoolean scheduled = new AtomicBoolean();
        entropySource.schedule(((delay, timeUnit) -> {
            entropyFutures.add(scheduler.scheduleWithFixedDelay(() -> entropySource.event(eventAdder), 0, delay, timeUnit));
            scheduled.set(true);
        }));
        if (!scheduled.get()) {
            throw new IllegalStateException("Entropy source " + entropySource.getClass().getName() + " was not scheduled to run");
        }
    }
||||||| /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/base.java
=======
    public void addSource(EntropySource entropySource) {
        int sourceId = sourceCount.getAndIncrement();
        EventAdder eventAdder = new EventAdderImpl(sourceId, pools, entropySource.getClass());
        futures.add(
            entropySource.schedule(() -> entropySource.event(eventAdder), scheduler)
        );
    }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/right.java
    public void shutdownSources() {
        entropyFutures.forEach(f -> f.cancel(false));
        entropyFutures.clear();
    }
<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/left.java
    private final Set<Future<?>> futures = new ArrayList<>();
||||||| /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/base.java
    private final Set<Future<?>> futures = new ConcurrentHashMap<>();
=======
    private final Set<Future<?>> futures = new HashSet<>();
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/Accumulator.java/right.java
}
