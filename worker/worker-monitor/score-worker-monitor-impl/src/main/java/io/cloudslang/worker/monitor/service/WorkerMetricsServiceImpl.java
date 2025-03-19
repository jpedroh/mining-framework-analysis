package io.cloudslang.worker.monitor.service;
import io.cloudslang.score.events.EventBus;
import io.cloudslang.score.events.EventConstants;
import io.cloudslang.score.events.ScoreEvent;
import io.cloudslang.worker.management.monitor.WorkerStateUpdateService;
import io.cloudslang.worker.monitor.PerfMetricCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerMetricsServiceImpl implements WorkerMetricsService {
  protected static final Logger logger = LogManager.getLogger(WorkerMetricsServiceImpl.class);


<<<<<<< Unknown file: This is a bug in JDime.
=======
  static int capacity = Integer.getInteger("metrics.collection.sampleCount", Integer.MAX_VALUE);
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/right.java


  boolean disabled = Boolean.getBoolean("worker.monitoring.disable");

  @Autowired PerfMetricCollector perfMetricCollector;

  @Autowired private WorkerStateUpdateService workerStateUpdateService;

  private LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> collectMetricQueue = new LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>>(Integer.MAX_VALUE);

  @Autowired private EventBus eventBus;

  @Override public void collectPerformanceMetrics() {
    try {
      if (!isMonitoringDisabled()) {
        Map<WorkerPerformanceMetric, Serializable> metricInfo = perfMetricCollector.collectMetrics();
        collectMetricQueue.put(metricInfo);
        if (logger.isDebugEnabled()) {
          logger.debug("Collected worker metric " + metricInfo.size());
        }
      }
    } catch (Exception e) {
      logger.error("Failed to load metric into queue", e);
    }
  }

  private boolean isMonitoringDisabled() {
    return disabled || workerStateUpdateService.isMonitoringDisabled();
  }

  @Override public void dispatchPerformanceMetrics() {
    try {
      List<Map<WorkerPerformanceMetric, Serializable>> metricData = getCurrentBatch(collectMetricQueue);
      if (!isMonitoringDisabled() && metricData != null && metricData.size() > 0) {
        ScoreEvent scoreEvent = new ScoreEvent(EventConstants.WORKER_PERFORMANCE_MONITOR, (Serializable) metricData);
        eventBus.dispatch(scoreEvent);
        if (logger.isDebugEnabled()) {
          logger.debug("Dispatched worker metric " + metricData.size());
        }
      }
    } catch (Exception e) {
      logger.error("Failed to dispatch metric info event", e);
    }
  }

  private List<Map<WorkerPerformanceMetric, Serializable>> getCurrentBatch(LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> metricQueue) {
    List<Map<WorkerPerformanceMetric, Serializable>> metricList = new ArrayList<>();
    metricQueue.drainTo(metricList);
    return metricList;
  }
}