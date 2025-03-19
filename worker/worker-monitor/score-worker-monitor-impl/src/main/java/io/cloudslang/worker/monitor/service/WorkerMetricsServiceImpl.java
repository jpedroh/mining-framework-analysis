/*
 * Copyright © 2014-2017 EntIT Software LLC, a Micro Focus company (L.P.)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
<<<<<<< /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/left.java
    protected static final Logger logger = LogManager.getLogger(WorkerMetricsServiceImpl.class);
    boolean disabled = Boolean.getBoolean("worker.monitoring.disable");
    @Autowired
    PerfMetricCollector perfMetricCollector;
||||||| /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/base.java
    protected static final Logger logger = LogManager.getLogger(WorkerMetricsServiceImpl.class);
    static int capacity = Integer.getInteger("metrics.collection.sampleCount", 10);
    boolean disabled = Boolean.getBoolean("worker.monitoring.disable");
    @Autowired
    PerfMetricCollector perfMetricCollector;
=======
	protected static final Logger logger = LogManager.getLogger(WorkerMetricsServiceImpl.class);
	static int capacity = Integer.getInteger("metrics.collection.sampleCount", Integer.MAX_VALUE);
	boolean disabled = Boolean.getBoolean("worker.monitoring.disable");
	@Autowired
	PerfMetricCollector perfMetricCollector;
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/right.java

	@Autowired
	private WorkerStateUpdateService workerStateUpdateService;

<<<<<<< /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/left.java
    private LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> collectMetricQueue = new LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>>(Integer.MAX_VALUE);
    @Autowired
    private EventBus eventBus;
||||||| /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/base.java
    private LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> collectMetricQueue = new LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>>(capacity);
    @Autowired
    private EventBus eventBus;
=======
	private LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> collectMetricQueue = new LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>>(capacity);
	@Autowired
	private EventBus eventBus;
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/right.java

<<<<<<< /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/left.java
    @Override
    public void collectPerformanceMetrics() {
        try {
            if(!isMonitoringDisabled()) {
                Map<WorkerPerformanceMetric, Serializable> metricInfo = perfMetricCollector.collectMetrics();
                collectMetricQueue.put(metricInfo);
                if (logger.isDebugEnabled()) {
                    logger.debug("Collected worker metric "+ metricInfo.size());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load metric into queue", e);
        }
    }
||||||| /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/base.java
    @Override
    public void collectPerformanceMetrics() {
        try {
            if(!isMonitoringDisabled()) {
                Map<WorkerPerformanceMetric, Serializable> metricInfo = perfMetricCollector.collectMetrics();
                collectMetricQueue.put(metricInfo);
                if (logger.isDebugEnabled()) {
                    logger.debug("Collected worker metric "+ metricInfo.size());
                }
            }
            else{
                Thread.sleep(100);
            }
        } catch (Exception e) {
            logger.error("Failed to load metric into queue", e);
        }
    }
=======
	@Override
	public void collectPerformanceMetrics() {
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
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/right.java

	private boolean isMonitoringDisabled() {
		return disabled || workerStateUpdateService.isMonitoringDisabled();
	}

<<<<<<< /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/left.java
    @Override
    public void dispatchPerformanceMetrics() {
        try {
            List<Map<WorkerPerformanceMetric, Serializable>> metricData = getCurrentBatch(collectMetricQueue);
            if(!isMonitoringDisabled() && metricData!=null && metricData.size() > 0) {
                ScoreEvent scoreEvent = new ScoreEvent(EventConstants.WORKER_PERFORMANCE_MONITOR, (Serializable) metricData);
                eventBus.dispatch(scoreEvent);
                if (logger.isDebugEnabled()) {
                    logger.debug("Dispatched worker metric "+ metricData.size());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to dispatch metric info event", e);
        }
    }
||||||| /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/base.java
    @Override
    public void dispatchPerformanceMetrics() {
        try {
            if(!isMonitoringDisabled()) {
                List<Map<WorkerPerformanceMetric, Serializable>> metricData = getCurrentBatch(collectMetricQueue);
                ScoreEvent scoreEvent = new ScoreEvent(EventConstants.WORKER_PERFORMANCE_MONITOR, (Serializable) metricData);
                eventBus.dispatch(scoreEvent);
                if (logger.isDebugEnabled()) {
                    logger.debug("Dispatched worker metric "+ metricData.size());
                }
            }
            else{
                Thread.sleep(100);
            }
        } catch (Exception e) {
            logger.error("Failed to dispatch metric info event", e);
        }
    }
=======
	@Override
	public void dispatchPerformanceMetrics() {
		try {
			if (!isMonitoringDisabled()) {
				List<Map<WorkerPerformanceMetric, Serializable>> metricData = getCurrentBatch(collectMetricQueue);
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
>>>>>>> /usr/src/app/output/cloudslang/score/bfe7c0dad895f1f0fdfbdab5611568a717cab295/worker/worker-monitor/score-worker-monitor-impl/src/main/java/io/cloudslang/worker/monitor/service/WorkerMetricsServiceImpl.java/right.java

	private List<Map<WorkerPerformanceMetric, Serializable>> getCurrentBatch(LinkedBlockingQueue<Map<WorkerPerformanceMetric, Serializable>> metricQueue) {
		List<Map<WorkerPerformanceMetric, Serializable>> metricList = new ArrayList<>();
		metricQueue.drainTo(metricList);

		return metricList;
	}
}
