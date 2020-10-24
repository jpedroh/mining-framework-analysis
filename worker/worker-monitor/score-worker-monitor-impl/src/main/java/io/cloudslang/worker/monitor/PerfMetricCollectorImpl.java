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
package io.cloudslang.worker.monitor;

import io.cloudslang.worker.management.services.WorkerManager;
import io.cloudslang.worker.monitor.service.MetricKeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PerfMetricCollectorImpl implements PerfMetricCollector {

    @Autowired
    @Qualifier("numberOfExecutionThreads")
    private Integer numberOfThreads;

    @Autowired
    private WorkerManager workerManager;

    List<WorkerPerfMetric> workerPerfMetrics;

    public PerfMetricCollectorImpl() { createMetrics(); }

    private void createMetrics() {
        workerPerfMetrics = new ArrayList<>();
        workerPerfMetrics.add(new CpuPerProcess());
        workerPerfMetrics.add(new DiskUsagePerProcess());
        workerPerfMetrics.add(new MemoryPerProcess());
        workerPerfMetrics.add(new HeapSize());
//        workerPerfMetrics.add(new ThreadCountUtilization());
    }

    @Override
    public Map<MetricKeyValue, Serializable> collectMetric() {
        Map<MetricKeyValue, Serializable> currentValues = new HashMap<>();
        for (WorkerPerfMetric metric :
                workerPerfMetrics) {
            currentValues.putAll(metric.measure());
        }
        currentValues.put(MetricKeyValue.WORKER_ID,workerManager.getWorkerUuid());
        currentValues.put(MetricKeyValue.WORKER_MEASURED_TIME,System.currentTimeMillis());
        currentValues.put(MetricKeyValue.THREAD_UTILIZATION,((workerManager.getRunningTasksCount()*100)/numberOfThreads));
        return currentValues;
    }
}
