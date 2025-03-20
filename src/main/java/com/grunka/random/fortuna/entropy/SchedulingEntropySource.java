package com.grunka.random.fortuna.entropy;

import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulingEntropySource implements EntropySource {

    private Instant lastTime = Instant.now();

    @Override
    public void schedule(EventScheduler scheduler) {
        scheduler.schedule(10, TimeUnit.MILLISECONDS);
    }

<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/SchedulingEntropySource.java/left.java
    @Override
    public void event(EventAdder adder) {
        long now = System.nanoTime();
        long elapsed = now - lastTime;
        lastTime = now;
        adder.add(Util.twoLeastSignificantBytes(elapsed));
    }
||||||| /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/SchedulingEntropySource.java/base.java
    @Override
    public void event(EventScheduler scheduler, EventAdder adder) {
        long now = System.nanoTime();
        long elapsed = now - lastTime;
        lastTime = now;
        adder.add(Util.twoLeastSignificantBytes(elapsed));
        scheduler.schedule(10, TimeUnit.MILLISECONDS);
    }
=======
    @Override
    public void event(EventAdder adder) {
        Instant now = Instant.now();
        long elapsed = now.isAfter(lastTime)
            ? Duration.between(lastTime, now).toNanos()
            : Duration.between(now, lastTime).toNanos();
        lastTime = now;
        adder.add(Util.twoLeastSignificantBytes(elapsed));
    }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/SchedulingEntropySource.java/right.java

    @Override
    public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
        return scheduler.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.MILLISECONDS);
    }
}
