package com.xxl.job.core.thread;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.FileUtil;
import com.xxl.job.core.util.JdkSerializeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerCallbackThread {
  private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

  private final static int RETRY_COUNT = 20;

  private static TriggerCallbackThread instance = new TriggerCallbackThread();

  public static TriggerCallbackThread getInstance() {
    return instance;
  }

  /**
     * job results callback queue
     */
  private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<HandleCallbackParam>();

  public static void pushCallBack(HandleCallbackParam callback) {
    getInstance().callBackQueue.add(callback);
    logger.debug(">>>>>>>>>>> xxl-job, push callback request, logId:{}", callback.getLogId());
  }

  /**
     * callback thread
     */
  private Thread triggerCallbackThread;

  private Thread triggerRetryCallbackThread;

  private volatile boolean toStop = false;

  private Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

  public void start() {
    if (XxlJobExecutor.getAdminBizList() == null) {
      logger.warn(">>>>>>>>>>> xxl-job, executor callback config fail, adminAddresses is null.");
      return;
    }
    triggerCallbackThread = new Thread(new Runnable() {
      @Override public void run() {
        while (!toStop) {
          try {
            HandleCallbackParam callback = getInstance().callBackQueue.take();
            if (callback != null) {
              List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
              int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
              callbackParamList.add(callback);
              if (callbackParamList != null && callbackParamList.size() > 0) {
                doCallback(callbackParamList, null);
              }
            }
          } catch (Exception e) {
            if (!toStop) {
              logger.error(e.getMessage(), e);
            }
          }
        }
        try {
          List<HandleCallbackParam> callbackParamList = new ArrayList<HandleCallbackParam>();
          int drainToNum = getInstance().callBackQueue.drainTo(callbackParamList);
          if (callbackParamList != null && callbackParamList.size() > 0) {
            doCallback(callbackParamList, null);
          }
        } catch (Exception e) {
          if (!toStop) {
            logger.error(e.getMessage(), e);
          }
        }
        logger.info(">>>>>>>>>>> xxl-job, executor callback thread destory.");
      }
    });
    triggerCallbackThread.setDaemon(true);
    triggerCallbackThread.setName("xxl-job, executor TriggerCallbackThread");
    triggerCallbackThread.start();
    triggerRetryCallbackThread = new Thread(new Runnable() {
      @Override public void run() {
        while (!toStop) {
          try {
            retryFailCallbackFile();
          } catch (Exception e) {
            if (!toStop) {
              logger.error(e.getMessage(), e);
            }
          }
          try {
            TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
          } catch (InterruptedException e) {
            if (!toStop) {
              logger.error(e.getMessage(), e);
            }
          }
        }
        logger.info(">>>>>>>>>>> xxl-job, executor retry callback thread destory.");
      }
    });
    triggerRetryCallbackThread.setDaemon(true);
    triggerRetryCallbackThread.start();
  }

  public void toStop() {
    toStop = true;
    if (triggerCallbackThread != null) {
      triggerCallbackThread.interrupt();
      try {
        triggerCallbackThread.join();
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }
    }
    if (triggerRetryCallbackThread != null) {
      triggerRetryCallbackThread.interrupt();
      try {
        triggerRetryCallbackThread.join();
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  /**
     * do callback, will retry if error
     * @param callbackParamList
     */
  private void doCallback(List<HandleCallbackParam> callbackParamList, File callbaclLogFile) {
    boolean callbackRet = false;
    for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        ReturnT<String> callbackResult = adminBiz.callback(callbackParamList);
        if (callbackResult != null && ReturnT.SUCCESS_CODE == callbackResult.getCode()) {
          callbackLog(callbackParamList, "<br>----------- xxl-job job callback finish.");
          callbackRet = true;
          if (callbaclLogFile != null) {
            callbaclLogFile.delete();
          }
          break;
        } else {
          callbackLog(callbackParamList, "<br>----------- xxl-job job callback fail, callbackResult:" + callbackResult);
        }
      } catch (Exception e) {
        callbackLog(callbackParamList, "<br>----------- xxl-job job callback error, errorMsg:" + e.getMessage());
      }
    }
    if (!callbackRet) {
      appendFailCallbackFile(callbackParamList);
    }
  }

  /**
     * callback log
     */
  private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
    for (HandleCallbackParam callbackParam : callbackParamList) {
      String logFileName = XxlJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
      XxlJobContext.setXxlJobContext(new XxlJobContext(-1, logFileName, -1, -1));
      XxlJobLogger.log(logContent);
    }
  }

  private static String failCallbackFilePath = XxlJobFileAppender.getLogPath().concat(File.separator).concat("callbacklog").concat(File.separator);

  private static String failCallbackFileName = failCallbackFilePath.concat("xxl-job-callback-{x}").concat(".log");

  private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
    if (callbackParamList == null || callbackParamList.size() == 0) {
      return;
    }
    byte[] callbackParamList_bytes = JdkSerializeTool.serialize(callbackParamList);
    File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
    if (callbackLogFile.exists()) {
      for (int i = 0; i < 100; i++) {
        callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i))));
        if (!callbackLogFile.exists()) {
          break;
        }
      }
    }
    FileUtil.writeFileContent(callbackLogFile, callbackParamList_bytes);
  }

  private void retryFailCallbackFile() {
    File callbackLogPath = new File(failCallbackFilePath);
    if (!callbackLogPath.exists()) {
      return;
    }
    if (callbackLogPath.isFile()) {
      callbackLogPath.delete();
    }
    if (!(callbackLogPath.isDirectory() && callbackLogPath.list() != null && callbackLogPath.list().length > 0)) {
      return;
    }
    for (File callbaclLogFile : callbackLogPath.listFiles()) {
      byte[] callbackParamList_bytes = FileUtil.readFileContent(callbaclLogFile);

<<<<<<< /usr/src/app/output/xuxueli/xxl-job/2ca451972986ecac7962cb8d16aedcebb6bed1d5/xxl-job-core/src/main/java/com/xxl/job/core/thread/TriggerCallbackThread.java/left.java
      if (vaidateRetryCount(callbackParamList_bytes)) {
        continue;
      }
=======
      if (callbackParamList_bytes == null || callbackParamList_bytes.length < 1) {
        callbaclLogFile.delete();
        continue;
      }
>>>>>>> /usr/src/app/output/xuxueli/xxl-job/2ca451972986ecac7962cb8d16aedcebb6bed1d5/xxl-job-core/src/main/java/com/xxl/job/core/thread/TriggerCallbackThread.java/right.java

      List<HandleCallbackParam> callbackParamList = (List<HandleCallbackParam>) JdkSerializeTool.deserialize(callbackParamList_bytes, List.class);
      doCallback(callbackParamList, callbaclLogFile);
    }
  }

  private boolean vaidateRetryCount(byte[] callbackParamList_bytes) {
    String md5Key = DigestUtils.md5DigestAsHex(callbackParamList_bytes);
    Integer md5Val = retryCountMap.get(md5Key);
    if (md5Val == null) {
      retryCountMap.put(md5Key, 1);
      return true;
    }
    if (md5Val < RETRY_COUNT) {
      md5Val++;
      retryCountMap.put(md5Key, md5Val);
      return true;
    }
    return false;
  }
}