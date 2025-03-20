package com.grunka.random.fortuna.entropy;
import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FreeMemoryEntropySource implements EntropySource {

<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/FreeMemoryEntropySource.java/left.java
  @Override public void schedule(EventScheduler scheduler) {
    scheduler.schedule(100, TimeUnit.MILLISECONDS);
  }
=======
  @Override public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
    return scheduler.scheduleWithFixedDelay(runnable, 0, 100, TimeUnit.MILLISECONDS);
  }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/FreeMemoryEntropySource.java/right.java


  @Override public void event(EventAdder adder) {
    long freeMemory = Runtime.getRuntime().freeMemory();
    adder.add(Util.twoLeastSignificantBytes(freeMemory));
  }
}