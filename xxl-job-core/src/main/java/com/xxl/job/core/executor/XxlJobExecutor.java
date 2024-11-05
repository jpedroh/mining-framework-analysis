package com.xxl.job.core.executor;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.impl.netty_http.server.NettyHttpServer;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.serialize.Serializer;
import com.xxl.rpc.serialize.impl.HessianSerializer;
import com.xxl.rpc.util.IpUtil;
import com.xxl.rpc.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor {
  private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

  private String adminAddresses;

  private String appName;

  private String accessToken;

  private String address;

  private String ip;

  private int port;

  private String logPath;

  private int logRetentionDays;

  public void setAdminAddresses(String adminAddresses) {
    this.adminAddresses = adminAddresses;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setLogPath(String logPath) {
    this.logPath = logPath;
  }

  public void setLogRetentionDays(int logRetentionDays) {
    this.logRetentionDays = logRetentionDays;
  }

  public void start() throws Exception {
    XxlJobFileAppender.initLogPath(logPath);
    initAdminBizList(adminAddresses, accessToken);
    JobLogFileCleanThread.getInstance().start(logRetentionDays);
    TriggerCallbackThread.getInstance().start();
    port = port > 0 ? port : NetUtil.findAvailablePort(9999);
    ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();
    initRpcProvider(address, ip, port, appName, accessToken);
  }

  public void destroy() {
    stopRpcProvider();
    if (jobThreadRepository.size() > 0) {
      for (Map.Entry<Long, JobThread> item : jobThreadRepository.entrySet()) {
        JobThread oldJobThread = removeJobThread(item.getKey(), "web container destroy and kill the job.");
        if (oldJobThread != null) {
          try {
            oldJobThread.join();
          } catch (InterruptedException e) {
            logger.error(">>>>>>>>>>> xxl-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
          }
        }
      }
      jobThreadRepository.clear();
    }
    jobHandlerRepository.clear();
    JobLogFileCleanThread.getInstance().toStop();
    TriggerCallbackThread.getInstance().toStop();
  }

  private static List<AdminBiz> adminBizList;

  private static Serializer serializer = new HessianSerializer();

  private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
    if (adminAddresses != null && adminAddresses.trim().length() > 0) {
      for (String address : adminAddresses.trim().split(",")) {
        if (address != null && address.trim().length() > 0) {
          AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);
          if (adminBizList == null) {
            adminBizList = new ArrayList<AdminBiz>();
          }
          adminBizList.add(adminBiz);
        }
      }
    }
  }

  public static List<AdminBiz> getAdminBizList() {
    return adminBizList;
  }

  public static Serializer getSerializer() {
    return serializer;
  }

  private XxlRpcProviderFactory xxlRpcProviderFactory = null;

  private void initRpcProvider(String address, String ip, int port, String appName, String accessToken) throws Exception {
    if (address == null || address.trim().length() == 0) {
      address = IpUtil.getIpPort(ip, port);
    }
    Map<String, String> serviceRegistryParam = new HashMap<String, String>();
    serviceRegistryParam.put("appName", appName);
    serviceRegistryParam.put("address", address);
    xxlRpcProviderFactory = new XxlRpcProviderFactory();
    xxlRpcProviderFactory.setServer(NettyHttpServer.class);
    xxlRpcProviderFactory.setSerializer(HessianSerializer.class);
    xxlRpcProviderFactory.setCorePoolSize(20);
    xxlRpcProviderFactory.setMaxPoolSize(200);
    xxlRpcProviderFactory.setIp(ip);
    xxlRpcProviderFactory.setPort(port);
    xxlRpcProviderFactory.setAccessToken(accessToken);
    xxlRpcProviderFactory.setServiceRegistry(ExecutorServiceRegistry.class);
    xxlRpcProviderFactory.setServiceRegistryParam(serviceRegistryParam);
    xxlRpcProviderFactory.addService(ExecutorBiz.class.getName(), null, new ExecutorBizImpl());
    xxlRpcProviderFactory.start();
  }

  public static class ExecutorServiceRegistry extends ServiceRegistry {
    @Override public void start(Map<String, String> param) {
      ExecutorRegistryThread.getInstance().start(param.get("appName"), param.get("address"));
    }

    @Override public void stop() {
      ExecutorRegistryThread.getInstance().toStop();
    }

    @Override public boolean registry(Set<String> keys, String value) {
      return false;
    }

    @Override public boolean remove(Set<String> keys, String value) {
      return false;
    }

    @Override public Map<String, TreeSet<String>> discovery(Set<String> keys) {
      return null;
    }

    @Override public TreeSet<String> discovery(String key) {
      return null;
    }
  }

  private void stopRpcProvider() {
    try {
      xxlRpcProviderFactory.stop();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();

  public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
    logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
    return jobHandlerRepository.put(name, jobHandler);
  }

  public static IJobHandler loadJobHandler(String name) {
    return jobHandlerRepository.get(name);
  }

  private static ConcurrentMap<Long, JobThread> jobThreadRepository = new ConcurrentHashMap<Long, JobThread>();

  public static JobThread registJobThread(long jobId, IJobHandler handler, String removeOldReason) {
    JobThread newJobThread = new JobThread(jobId, handler);
    newJobThread.start();
    logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[] { jobId, handler });
    JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);
    if (oldJobThread != null) {
      oldJobThread.toStop(removeOldReason);
      oldJobThread.interrupt();
    }
    return newJobThread;
  }

  public static JobThread removeJobThread(long jobId, String removeOldReason) {
    JobThread oldJobThread = jobThreadRepository.remove(jobId);
    if (oldJobThread != null) {
      oldJobThread.toStop(removeOldReason);
      oldJobThread.interrupt();
      return oldJobThread;
    }
    return null;
  }

  public static JobThread loadJobThread(long jobId) {
    JobThread jobThread = jobThreadRepository.get(jobId);
    return jobThread;
  }
}