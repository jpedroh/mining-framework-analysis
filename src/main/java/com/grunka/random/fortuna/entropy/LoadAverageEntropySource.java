package com.grunka.random.fortuna.entropy;
import com.grunka.random.fortuna.Util;
import com.grunka.random.fortuna.accumulator.EntropySource;
import com.grunka.random.fortuna.accumulator.EventAdder;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoadAverageEntropySource implements EntropySource {
  private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();


<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/LoadAverageEntropySource.java/left.java
  @Override public void schedule(EventScheduler scheduler) {
    scheduler.schedule(1, TimeUnit.SECONDS);
  }
=======
  @Override public Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler) {
    return scheduler.scheduleWithFixedDelay(runnable, 0, 1000, TimeUnit.MILLISECONDS);
  }
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/entropy/LoadAverageEntropySource.java/right.java


  @Override public void event(EventAdder adder) {
    double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
    BigDecimal value = BigDecimal.valueOf(systemLoadAverage);
    long convertedValue = value.movePointRight(value.scale()).longValue();
    adder.add(Util.twoLeastSignificantBytes(convertedValue));
  }
}