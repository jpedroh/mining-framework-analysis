package com.grunka.random.fortuna.entropy;
import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UptimeEntropySource implements EntropySource {
  private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();


<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/UptimeEntropySource.java/left.java
  @Override public void schedule(EventScheduler scheduler) {
    scheduler.schedule(1, TimeUnit.SECONDS);
  }
=======
  @Override public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
    return scheduler.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);
  }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/UptimeEntropySource.java/right.java


  @Override public void event(EventAdder adder) {
    long uptime = runtimeMXBean.getUptime();
    adder.add(Util.twoLeastSignificantBytes(uptime));
  }
}