package io.cloudslang.worker.monitor.metrics;
import io.cloudslang.worker.monitor.service.WorkerPerformanceMetric;
import org.apache.commons.lang3.tuple.Pair;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import javax.annotation.PostConstruct;
import java.io.Serializable;

public class MemoryUtilizationService extends WorkerPerformanceMetricBase {
  private long usedRamProcess;

  private OSProcess process;

  private long totalRam;

  @PostConstruct public void init() {
    SystemInfo si = new SystemInfo();
    process = getProcess();
    GlobalMemory globalMemory = si.getHardware().getMemory();
    this.totalRam = globalMemory.getTotal();
  }

  @Override public Pair<WorkerPerformanceMetric, Serializable> measure() {
    Pair<WorkerPerformanceMetric, Serializable> memUsage = Pair.of(WorkerPerformanceMetric.MEMORY_USAGE, getCurrentValue());
    return memUsage;
  }

  public double getCurrentValue() {
    this.usedRamProcess = process.getResidentSetSize();
    double ramUsed = 
<<<<<<< /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/metrics/MemoryUtilizationService.java/left.java
    ((double) usedRamProcess * 100 / totalRam)
=======
    (((double) usedRamProcess) / totalRam) * 100
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/metrics/MemoryUtilizationService.java/right.java
    ;
    return formatTo2Decimal(ramUsed);
  }
}