package io.cloudslang.worker.management.services;
import io.cloudslang.engine.node.entities.WorkerKeepAliveInfo;
import io.cloudslang.engine.node.services.WorkerNodeService;
import io.cloudslang.orchestrator.services.EngineVersionService;
import io.cloudslang.worker.management.WorkerConfigurationService;
import io.cloudslang.worker.management.monitor.WorkerStateUpdateService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static ch.lambdaj.Lambda.max;
import static ch.lambdaj.Lambda.on;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.valueOf;
import static java.lang.System.getProperty;

public class WorkerManager implements ApplicationListener, EndExecutionCallback, WorkerRecoveryListener {
  private static final Logger logger = Logger.getLogger(WorkerManager.class);

  private static final int KEEP_ALIVE_FAIL_LIMIT = 5;

  private static final String DOTNET_PATH = System.getenv("WINDIR") + "/Microsoft.NET/Framework";

  @Resource private String workerUuid;

  @Autowired protected WorkerNodeService workerNodeService;

  @Autowired private EngineVersionService engineVersionService;

  @Autowired protected WorkerConfigurationService workerConfigurationService;

  @Autowired protected WorkerRecoveryManager recoveryManager;

  @Autowired protected WorkerVersionService workerVersionService;

  private BlockingQueue<Runnable> inBuffer;

  @Autowired @Qualifier(value = "numberOfExecutionThreads") private Integer numberOfThreads;

  @Autowired(required = false) @Qualifier(value = "initStartUpSleep") private Long initStartUpSleep = 15 * 1000L;

  @Autowired(required = false) @Qualifier(value = "maxStartUpSleep") private Long maxStartUpSleep = 10 * 60 * 1000L;

  @Autowired private WorkerConfigurationUtils workerConfigurationUtils;

  @Autowired @Qualifier(value = "inBufferCapacity") private Integer capacity;

  @Autowired private WorkerStateUpdateService workerStateUpdateService;

  private int keepAliveFailCount = 0;

  private ExecutorService executorService;

  private ConcurrentMap<Long, Queue<Future>> mapOfRunningTasks;

  private volatile boolean endOfInit = false;

  private volatile boolean initStarted = false;

  private boolean up = false;

  private int threadPoolVersion = 0;

  private boolean newCancelBehaviour;

  @PostConstruct private void init() {
    logger.info("Initialize worker with UUID: " + workerUuid);
    System.setProperty("worker.uuid", workerUuid);
    inBuffer = workerConfigurationUtils.getBlockingQueue(numberOfThreads, capacity);
    executorService = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, inBuffer, new WorkerThreadFactory(valueOf(incrementAndGetTreadPoolVersion()) + "_WorkerExecutionThread"));
    mapOfRunningTasks = new ConcurrentHashMap<>(numberOfThreads);
    newCancelBehaviour = parseBoolean(getProperty("enable.new.cancel.execution", FALSE.toString()));
  }

  /**
     * Returns the amount of free memory in the worker's jvm.
     * @return amount of memory currently available, measured in bytes.
     */
  public long getFreeMemory() {
    return Runtime.getRuntime().freeMemory();
  }

  /**
     * Returns the amount of memory that the worker's jvm will attempt to use.
     * @return  amount of memory that the worker's jvm will attempt to use, in bytes.
     */
  public long getMaxMemory() {
    return Runtime.getRuntime().maxMemory();
  }

  public void addExecution(long executionId, Runnable runnable) {
    Future future = executorService.submit(runnable);
    mapOfRunningTasks.merge(executionId, newQueue(future), this::addLists);
  }

  private Queue<Future> newQueue(Future future) {
    Queue<Future> queue = new LinkedList<>();
    queue.offer(future);
    return queue;
  }

  private Queue<Future> addLists(Queue<Future> oldValue, Queue<Future> newValue) {
    oldValue.offer(newValue.poll());
    return oldValue;
  }

  @Override public void endExecution(long executionId) {
    mapOfRunningTasks.merge(executionId, new LinkedList<>(), (queue, newValue) -> {
      queue.poll();
      return !queue.isEmpty() ? queue : null;
    });
  }

  public int getInBufferSize() {
    return inBuffer.size();
  }

  @SuppressWarnings(value = { "unused" }) public void interruptCanceledExecutions() {
    try {
      if (!newCancelBehaviour) {
        for (Long executionId : mapOfRunningTasks.keySet()) {
          if (workerConfigurationService.isExecutionCancelled(executionId)) {
            Collection<Future> futures = mapOfRunningTasks.get(executionId);
            for (Future future : futures) {
              future.cancel(true);
            }
          }
        }
      }
    } catch (Exception exc) {
      logger.error("Could not stop cancelled executions: ", exc);
    }
  }

  @SuppressWarnings(value = { "unused" }) public void workerKeepAlive() {
    if (!recoveryManager.isInRecovery()) {
      if (endOfInit) {
        try {
          WorkerKeepAliveInfo workerKeepAliveInfo = workerNodeService.newKeepAlive(workerUuid);
          String newWrv = workerKeepAliveInfo.getWorkerRecoveryVersion();
          workerStateUpdateService.setEnableState(workerKeepAliveInfo.isActive());
          String currentWrv = recoveryManager.getWRV();
          if (!currentWrv.equals(newWrv)) {
            logger.warn("Got new WRV from Orchestrator during keepAlive(). Going to reload...");
            recoveryManager.doRecovery();
          }
          keepAliveFailCount = 0;
        } catch (Exception e) {
          keepAliveFailCount++;
          logger.error("Could not send keep alive to Central, keepAliveFailCount = " + keepAliveFailCount, e);
          if (keepAliveFailCount >= KEEP_ALIVE_FAIL_LIMIT) {
            logger.error("Failed sending keepAlive for " + KEEP_ALIVE_FAIL_LIMIT + " times. Invoking worker internal recovery...");
            recoveryManager.doRecovery();
          }
        }
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("worker waits for recovery");
      }
    }
  }

  @SuppressWarnings(value = { "unused" }) public void logStatistics() {
    if (logger.isDebugEnabled()) {
      logger.debug("InBuffer size: " + getInBufferSize());
      logger.debug("Running task size: " + mapOfRunningTasks.size());
    }
  }

  public String getWorkerUuid() {
    return workerUuid;
  }

  public int getRunningTasksCount() {
    return mapOfRunningTasks.size();
  }

  public int getExecutionThreadsCount() {
    return numberOfThreads;
  }

  @Override public void onApplicationEvent(final ApplicationEvent applicationEvent) {
    if (applicationEvent instanceof ContextRefreshedEvent && !initStarted) {
      doStartup();
    } else {
      if (applicationEvent instanceof ContextClosedEvent) {
        doShutdown();
      }
    }
  }

  private void doStartup() {
    new Thread(new Runnable() {
      @Override public void run() {
        initStarted = true;
        long sleep = initStartUpSleep;
        boolean shouldRetry = true;
        while (shouldRetry) {
          try {
            String newWrv = workerNodeService.up(workerUuid, workerVersionService.getWorkerVersion(), workerVersionService.getWorkerVersionId());
            recoveryManager.setWRV(newWrv);
            shouldRetry = false;
            logger.info("Worker is up");
          } catch (Exception ex) {
            logger.error("Worker failed on start up, will retry in a " + sleep / 1000 + " seconds", ex);
            try {
              Thread.sleep(sleep);
            } catch (InterruptedException iex) {
            }
            sleep = Math.min(maxStartUpSleep, sleep * 2);
          }
        }
        endOfInit = true;
        String engineVersionId = engineVersionService.getEngineVersionId();
        if (workerVersionService.getWorkerVersionId().equals(engineVersionId)) {
          up = true;
          workerConfigurationService.setEnabled(true);
          workerNodeService.updateEnvironmentParams(workerUuid, getProperty("os.name"), getProperty("java.version"), resolveDotNetVersion());
        } else {
          logger.warn("Worker\'s version is not equal to engine version. Won\'t be able to start processing flows!");
        }
      }
    }).start();
  }

  private void doShutdown() {
    endOfInit = false;
    initStarted = false;
    workerConfigurationService.setEnabled(false);
    up = false;
    logger.info("The worker is down");
  }

  protected static String resolveDotNetVersion() {
    File dotNetHome = new File(DOTNET_PATH);
    if (dotNetHome.isDirectory()) {
      File[] versionFolders = dotNetHome.listFiles(new FileFilter() {
        @Override public boolean accept(File file) {
          return file.isDirectory() && file.getName().startsWith("v");
        }
      });
      if (!ArrayUtils.isEmpty(versionFolders)) {
        String maxVersion = max(versionFolders, on(File.class).getName()).substring(1);
        return maxVersion.substring(0, 1) + ".x";
      }
    }
    return "N/A";
  }

  public boolean isUp() {
    return up;
  }

  public synchronized boolean isFromCurrentThreadPool(String threadName) {
    return threadName.startsWith(valueOf(threadPoolVersion));
  }

  public void doRecovery() {
    try {
      synchronized (this) {
        executorService.shutdownNow();
        threadPoolVersion++;
        logger.warn("Worker is in doRecovery(). Cleaning state and cancelling running tasks. It may take up to 30 seconds...");
      }
      boolean finished = executorService.awaitTermination(30, TimeUnit.SECONDS);
      if (finished) {
        logger.warn("Worker succeeded to cancel running tasks during doRecovery().");
      } else {
        logger.warn("Not all running tasks responded to cancel.");
      }
    } catch (InterruptedException ex) {
    }
    mapOfRunningTasks.clear();
    executorService = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, inBuffer, new WorkerThreadFactory(valueOf(getTreadPoolVersion()) + "_WorkerExecutionThread"));
  }

  private synchronized int getTreadPoolVersion() {
    return threadPoolVersion;
  }

  private synchronized int incrementAndGetTreadPoolVersion() {
    return (++threadPoolVersion);
  }
}