package com.xxl.job.core.thread;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.*;
import java.util.HashSet;
import java.util.Set;

/**
 * handler thread
 * @author xuxueli 2016-1-16 19:52:47
 */
public class JobThread extends Thread {
  private static Logger logger = LoggerFactory.getLogger(JobThread.class);

  private int jobId;

  private IJobHandler handler;

  private LinkedBlockingQueue<TriggerParam> triggerQueue;

  private Set<Integer> triggerLogIdSet;

  private volatile boolean toStop = false;

  private String stopReason;

  private boolean running = false;

  private int idleTimes = 0;

  public JobThread(int jobId, IJobHandler handler) {
    this.jobId = jobId;
    this.handler = handler;
    this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
    this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Integer>());
  }

  public IJobHandler getHandler() {
    return handler;
  }

  /**
     * new trigger to queue
     *
     * @param triggerParam
     * @return
     */
  public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
    if (triggerLogIdSet.contains(triggerParam.getLogId())) {
      logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
      return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
    }
    triggerLogIdSet.add(triggerParam.getLogId());
    triggerQueue.add(triggerParam);
    return ReturnT.SUCCESS;
  }

  /**
     * kill job thread
     *
     * @param stopReason
     */
  public void toStop(String stopReason) {
    this.toStop = true;
    this.stopReason = stopReason;
  }

  /**
     * is running job
     * @return
     */
  public boolean isRunningOrHasQueue() {
    return running || triggerQueue.size() > 0;
  }

  @Override public void run() {
    try {
      handler.init();
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
    }
    while (!toStop) {
      running = false;
      idleTimes++;
      TriggerParam triggerParam = null;
      ReturnT<String> executeResult = null;
      ExecutorService singleThread = Executors.newSingleThreadExecutor();
      try {
        triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
        if (triggerParam != null) {
          running = true;
          idleTimes = 0;
          triggerLogIdSet.remove(triggerParam.getLogId());
          String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());
          XxlJobFileAppender.contextHolder.set(logFileName);
          ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(triggerParam.getBroadcastIndex(), triggerParam.getBroadcastTotal()));
          XxlJobLogger.log("<br>----------- xxl-job job execute start -----------<br>----------- Param:" + triggerParam.getExecutorParams());
          int executeTimeout = triggerParam.getExecuteTimeout();
          final TriggerParam finalTriggerParam = triggerParam;
          Future<ReturnT<String>> future = singleThread.submit(new Callable<ReturnT<String>>() {
            @Override public ReturnT<String> call() throws Exception {
              return handler.execute(finalTriggerParam.getExecutorParams());
            }
          });
          try {
            if (executeTimeout > 0) {
              executeResult = future.get(executeTimeout, TimeUnit.SECONDS);
            } else {
              executeResult = future.get();
            }
          } catch (TimeoutException timeoutException) {
            executeResult = ReturnT.TIMEOUT;
          }
          if (executeResult == null) {
            executeResult = IJobHandler.FAIL;
          }
          XxlJobLogger.log("<br>----------- xxl-job job execute end(finish) -----------<br>----------- ReturnT:" + executeResult);
        } else {
          if (idleTimes > 30) {
            XxlJobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
          }
        }
      } catch (Throwable e) {
        if (toStop) {
          XxlJobLogger.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        executeResult = new ReturnT<String>(ReturnT.FAIL_CODE, errorMsg);
        XxlJobLogger.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- xxl-job job execute end(error) -----------");
      } finally {
        if (singleThread != null) {
          singleThread.shutdown();
        }
        if (triggerParam != null) {
          if (!toStop) {
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), executeResult));
          } else {
            ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [\u4e1a\u52a1\u8fd0\u884c\u4e2d\uff0c\u88ab\u5f3a\u5236\u7ec8\u6b62]");
            TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
          }
        }
      }
    }
    while (triggerQueue != null && triggerQueue.size() > 0) {
      TriggerParam triggerParam = triggerQueue.poll();
      if (triggerParam != null) {
        ReturnT<String> stopResult = new ReturnT<String>(ReturnT.FAIL_CODE, stopReason + " [\u4efb\u52a1\u5c1a\u672a\u6267\u884c\uff0c\u5728\u8c03\u5ea6\u961f\u5217\u4e2d\u88ab\u7ec8\u6b62]");
        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTim(), stopResult));
      }
    }
    try {
      handler.destroy();
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
    }
    logger.info(">>>>>>>>>>> xxl-job JobThread stoped, hashCode:{}", Thread.currentThread());
  }
}