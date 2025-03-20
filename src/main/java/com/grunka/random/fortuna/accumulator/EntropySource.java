package com.grunka.random.fortuna.accumulator;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public interface EntropySource {

<<<<<<< /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/EntropySource.java/left.java
  void schedule(EventScheduler scheduler);
=======
  Future<?> schedule(Runnable runnable, ScheduledExecutorService scheduler);
>>>>>>> /usr/src/app/output/grunka/fortuna/7475060adde9b911d104d64d02aceaee05fd9e4b/src/main/java/com/grunka/random/fortuna/accumulator/EntropySource.java/right.java


  void event(EventAdder adder);
}