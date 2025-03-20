package com.grunka.random.fortuna.entropy;
import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GarbageCollectorEntropySource implements EntropySource {
  private final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();


<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/GarbageCollectorEntropySource.java/left.java
  @Override public void schedule(EventScheduler scheduler) {
    scheduler.schedule(10, TimeUnit.SECONDS);
  }
=======
  @Override public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
    return scheduler.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.SECONDS);
  }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/GarbageCollectorEntropySource.java/right.java


  @Override public void event(EventAdder adder) {
    long sum = 0;
    for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
      sum += garbageCollectorMXBean.getCollectionCount() + garbageCollectorMXBean.getCollectionTime();
    }
    adder.add(Util.twoLeastSignificantBytes(sum));
  }
}