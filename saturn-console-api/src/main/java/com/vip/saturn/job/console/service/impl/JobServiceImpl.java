package com.vip.saturn.job.console.service.impl;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vip.saturn.job.console.domain.*;
import com.vip.saturn.job.console.domain.ExecutionInfo.ExecutionStatus;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.exception.SaturnJobConsoleHttpException;
import com.vip.saturn.job.console.mybatis.entity.JobConfig4DB;
import com.vip.saturn.job.console.mybatis.entity.SaturnStatistics;
import com.vip.saturn.job.console.mybatis.service.CurrentJobConfigService;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository.CuratorFrameworkOp;
import com.vip.saturn.job.console.service.DashboardService;
import com.vip.saturn.job.console.service.JobService;
import com.vip.saturn.job.console.service.RegistryCenterService;
import com.vip.saturn.job.console.service.SystemConfigService;
import com.vip.saturn.job.console.service.helper.SystemConfigProperties;
import com.vip.saturn.job.console.utils.*;
import com.vip.saturn.job.sharding.node.SaturnExecutorsNode;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.lang.Boolean;
import java.text.ParseException;
import java.util.*;

/**
 * @author hebelala
 */
@Service public class JobServiceImpl implements JobService {
  private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

  private static final int DEFAULT_MAX_JOB_NUM = 100;

  private static final int DEFAULT_INTERVAL_TIME_OF_ENABLED_REPORT = 5;

  @Resource private RegistryCenterService registryCenterService;

  @Resource private CurrentJobConfigService currentJobConfigService;

  @Resource private SystemConfigService systemConfigService;

  @Resource private DashboardService dashboardService;

  private MapperFacade mapper;

  private Random random = new Random();

  private MapType customContextType = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, String.class);

  @Autowired public void setMapperFactory(MapperFactory mapperFactory) {
    this.mapper = mapperFactory.getMapperFacade();
  }

  @Override public JobOverviewVo getJobOverviewVo(String namespace) throws SaturnJobConsoleException {
    JobOverviewVo jobOverviewVo = new JobOverviewVo();
    try {
      List<JobInfo> jobInfoList = new ArrayList<>();
      int enabledNumber = 0;
      List<JobConfig> unSystemJobs = getUnSystemJobs(namespace);
      if (unSystemJobs != null) {
        CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
        for (JobConfig jobConfig : unSystemJobs) {
          try {
            JobInfo jobInfo = mapper.map(jobConfig, JobInfo.class);
            jobInfo.setDefaultValues();
            JobType jobType = JobType.getJobType(jobConfig.getJobType());
            if (JobType.UNKOWN_JOB.equals(jobType)) {
              if (jobInfo.getJobClass() != null && jobInfo.getJobClass().indexOf("SaturnScriptJob") != -1) {
                jobInfo.setJobType(JobType.SHELL_JOB.name());
              } else {
                jobInfo.setJobType(JobType.JAVA_JOB.name());
              }
            }
            jobInfo.setStatus(getJobStatus(jobConfig.getJobName(), curatorFrameworkOp, jobConfig.getEnabled()));
            if (StringUtils.isNotBlank(jobConfig.getPreferList())) {
              String containerTaskIdsNodePath = ContainerNodePath.getDcosTasksNodePath();
              List<String> containerTaskIds = curatorFrameworkOp.getChildren(containerTaskIdsNodePath);
              jobInfo.setMigrateEnabled(isMigrateEnabled(jobConfig.getPreferList(), containerTaskIds));
            } else {
              jobInfo.setMigrateEnabled(false);
            }
            updateJobInfoShardingList(curatorFrameworkOp, jobConfig.getJobName(), jobInfo);
            if (jobInfo.getEnabled()) {
              enabledNumber++;
            }
            jobInfoList.add(jobInfo);
          } catch (Exception e) {
            log.error("list job " + jobConfig.getJobName() + " error", e);
          }
        }
      }
      jobOverviewVo.setJobs(jobInfoList);
      jobOverviewVo.setEnabledNumber(enabledNumber);
      jobOverviewVo.setTotalNumber(jobInfoList.size());
      try {
        RegistryCenterConfiguration conf = registryCenterService.findConfigByNamespace(namespace);
        if (conf != null) {
          SaturnStatistics saturnStatistics = dashboardService.allUnnormalJob(conf.getZkAddressList());
          if (saturnStatistics != null) {
            String result = saturnStatistics.getResult();
            if (result != null) {
              List<AbnormalJob> abnormalJobs = JSON.parseArray(result, AbnormalJob.class);
              if (abnormalJobs != null) {
                jobOverviewVo.setAbnormalNumber(abnormalJobs.size());
              }
            }
          }
        } else {
          throw new SaturnJobConsoleException(String.format("\u6ca1\u6709\u627e\u5230\u8be5\u57df\uff08%s\uff09\u7684\u6ce8\u518c\u4fe1\u606f", namespace));
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    } catch (SaturnJobConsoleException e) {
      throw e;
    } catch (Exception e) {
      throw new SaturnJobConsoleException(e);
    }
    return jobOverviewVo;
  }

  private JobStatus getJobStatus(final String jobName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp, boolean enabled) {
    boolean isAllShardsFinished = isAllShardsFinished(jobName, curatorFrameworkOp);
    if (enabled) {
      if (isAllShardsFinished) {
        return JobStatus.READY;
      }
      return JobStatus.RUNNING;
    } else {
      if (isAllShardsFinished) {
        return JobStatus.STOPPED;
      }
      return JobStatus.STOPPING;
    }
  }

  private boolean isAllShardsFinished(final String jobName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    List<String> executionItems = curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName));
    boolean isAllShardsFinished = true;
    if (executionItems != null && !executionItems.isEmpty()) {
      for (String itemStr : executionItems) {
        boolean isItemCompleted = curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, itemStr, "completed"));
        boolean isItemRunning = curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, itemStr, "running"));
        if (!isItemCompleted && isItemRunning) {
          isAllShardsFinished = false;
          break;
        }
      }
    }
    return isAllShardsFinished;
  }

  private boolean isMigrateEnabled(String preferList, List<String> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return false;
    }
    List<String> preferTasks = new ArrayList<>();
    String[] split = preferList.split(",");
    for (int i = 0; i < split.length; i++) {
      String prefer = split[i].trim();
      if (prefer.startsWith("@")) {
        preferTasks.add(prefer.substring(1));
      }
    }
    if (!preferTasks.isEmpty()) {
      for (String task : tasks) {
        if (!preferTasks.contains(task)) {
          return true;
        }
      }
    }
    return false;
  }

  private void updateJobInfoShardingList(CuratorRepository.CuratorFrameworkOp curatorFrameworkOp, String jobName, JobInfo jobInfo) {
    if (JobStatus.STOPPED.equals(jobInfo.getStatus())) {
      return;
    }
    String executorsPath = JobNodePath.getServerNodePath(jobName);
    List<String> executors = curatorFrameworkOp.getChildren(executorsPath);
    if (CollectionUtils.isEmpty(executors)) {
      return;
    }
    StringBuilder shardingListSb = new StringBuilder();
    for (String executor : executors) {
      String sharding = curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "sharding"));
      if (!Strings.isNullOrEmpty(sharding)) {
        shardingListSb.append(executor).append(",");
      }
    }
    if (shardingListSb != null && shardingListSb.length() > 0) {
      jobInfo.setShardingList(shardingListSb.substring(0, shardingListSb.length() - 1));
    }
  }

  @Override public List<String> getGroups(String namespace) throws SaturnJobConsoleException {
    List<String> groups = new ArrayList<>();
    List<JobConfig> unSystemJobs = getUnSystemJobs(namespace);
    if (unSystemJobs != null) {
      for (JobConfig jobConfig : unSystemJobs) {
        String jobGroups = jobConfig.getGroups();
        if (jobGroups != null && !groups.contains(jobGroups)) {
          groups.add(jobGroups);
        }
      }
    }
    return groups;
  }

  @Override public List<DependencyJob> getDependingJobs(String namespace, String jobName) throws SaturnJobConsoleException {
    List<DependencyJob> dependencyJobs = new ArrayList<>();
    JobConfig4DB currentJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (currentJobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u4f9d\u8d56\u7684\u6240\u6709\u4f5c\u4e1a\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    List<JobConfig> unSystemJobs = getUnSystemJobs(namespace);
    if (unSystemJobs != null) {
      String dependencies = currentJobConfig.getDependencies();
      List<String> dependencyList = new ArrayList<>();
      if (StringUtils.isNotBlank(dependencies)) {
        String[] split = dependencies.split(",");
        for (String tmp : split) {
          if (StringUtils.isNotBlank(tmp)) {
            dependencyList.add(tmp.trim());
          }
        }
      }
      if (!dependencyList.isEmpty()) {
        for (JobConfig jobConfig : unSystemJobs) {
          if (jobConfig.getJobName().equals(jobName)) {
            continue;
          }
          if (dependencyList.contains(jobConfig.getJobName())) {
            DependencyJob dependencyJob = new DependencyJob();
            dependencyJob.setJobName(jobConfig.getJobName());
            dependencyJob.setEnabled(jobConfig.getEnabled());
            dependencyJobs.add(dependencyJob);
          }
        }
      }
    }
    return dependencyJobs;
  }

  @Override public List<DependencyJob> getDependedJobs(String namespace, String jobName) throws SaturnJobConsoleException {
    List<DependencyJob> dependencyJobs = new ArrayList<>();
    JobConfig4DB currentJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (currentJobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u83b7\u53d6\u4f9d\u8d56\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u7684\u6240\u6709\u4f5c\u4e1a\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    List<JobConfig> unSystemJobs = getUnSystemJobs(namespace);
    if (unSystemJobs != null) {
      for (JobConfig jobConfig : unSystemJobs) {
        if (jobConfig.getJobName().equals(jobName)) {
          continue;
        }
        String dependencies = jobConfig.getDependencies();
        if (StringUtils.isNotBlank(dependencies)) {
          String[] split = dependencies.split(",");
          for (String tmp : split) {
            if (jobName.equals(tmp.trim())) {
              DependencyJob dependencyJob = new DependencyJob();
              dependencyJob.setJobName(jobConfig.getJobName());
              dependencyJob.setEnabled(jobConfig.getEnabled());
              dependencyJobs.add(dependencyJob);
            }
          }
        }
      }
    }
    return dependencyJobs;
  }

  @Transactional @Override public void enableJob(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    if (jobConfig.getEnabled()) {
      throw new SaturnJobConsoleException("\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u5df2\u7ecf\u5904\u4e8e\u542f\u7528\u72b6\u6001");
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    boolean allShardsFinished = isAllShardsFinished(jobName, curatorFrameworkOp);
    if (!allShardsFinished) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u542f\u7528\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5904\u4e8eSTOPPED\u72b6\u6001");
    }
    jobConfig.setEnabled(true);
    jobConfig.setLastUpdateTime(new Date());
    try {
      currentJobConfigService.updateByPrimaryKey(jobConfig);
    } catch (Exception e) {
      throw new SaturnJobConsoleException(e);
    }
    curatorFrameworkOp.update(JobNodePath.getConfigNodePath(jobName, "enabled"), true);
  }

  @Transactional @Override public void disableJob(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u7981\u7528\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    if (!jobConfig.getEnabled()) {
      throw new SaturnJobConsoleException("\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u5df2\u7ecf\u5904\u4e8e\u7981\u7528\u72b6\u6001");
    }
    jobConfig.setEnabled(false);
    jobConfig.setLastUpdateTime(new Date());
    try {
      currentJobConfigService.updateByPrimaryKey(jobConfig);
    } catch (Exception e) {
      throw new SaturnJobConsoleException(e);
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    curatorFrameworkOp.update(JobNodePath.getConfigNodePath(jobName, "enabled"), false);
  }

  @Override public void removeJob(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    JobStatus jobStatus = getJobStatus(jobName, curatorFrameworkOp, jobConfig.getEnabled());
    if (JobStatus.STOPPED.equals(jobStatus)) {
      Stat stat = curatorFrameworkOp.getStat(JobNodePath.getJobNodePath(jobName));
      if (stat != null) {
        long createTimeDiff = System.currentTimeMillis() - stat.getCtime();
        if (createTimeDiff < SaturnConstants.JOB_CAN_BE_DELETE_TIME_LIMIT) {
          throw new SaturnJobConsoleException("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u521b\u5efa\u65f6\u95f4\u8ddd\u79bb\u73b0\u5728\u4e0d\u8d85\u8fc7" + (SaturnConstants.JOB_CAN_BE_DELETE_TIME_LIMIT / 60000) + "\u5206\u949f");
        }
      }
      try {
        currentJobConfigService.deleteByPrimaryKey(jobConfig.getId());
      } catch (Exception e) {
        throw new SaturnJobConsoleException(e);
      }
      String toDeleteNodePath = JobNodePath.getConfigNodePath(jobName, "toDelete");
      if (curatorFrameworkOp.checkExists(toDeleteNodePath)) {
        curatorFrameworkOp.deleteRecursive(toDeleteNodePath);
      }
      curatorFrameworkOp.create(toDeleteNodePath);
      for (int i = 0; i < 20; i++) {
        String jobServerPath = JobNodePath.getServerNodePath(jobName);
        if (!curatorFrameworkOp.checkExists(jobServerPath)) {
          curatorFrameworkOp.deleteRecursive(JobNodePath.getJobNodePath(jobName));
          return;
        }
        List<String> executors = curatorFrameworkOp.getChildren(jobServerPath);
        if (CollectionUtils.isEmpty(executors)) {
          curatorFrameworkOp.deleteRecursive(JobNodePath.getJobNodePath(jobName));
          return;
        }
        boolean hasOnlineExecutor = false;
        for (String executor : executors) {
          if (curatorFrameworkOp.checkExists(ExecutorNodePath.getExecutorNodePath(executor, "ip")) && curatorFrameworkOp.checkExists(JobNodePath.getServerStatus(jobName, executor))) {
            hasOnlineExecutor = true;
          } else {
            curatorFrameworkOp.deleteRecursive(JobNodePath.getServerNodePath(jobName, executor));
          }
        }
        if (!hasOnlineExecutor) {
          curatorFrameworkOp.deleteRecursive(JobNodePath.getJobNodePath(jobName));
          return;
        }
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          throw new SaturnJobConsoleException(e);
        }
      }
    } else {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5904\u4e8eSTOPPED\u72b6\u6001");
    }
  }

  @Override public List<ExecutorProvided> getCandidateExecutors(String namespace, String jobName) throws SaturnJobConsoleException {
    List<ExecutorProvided> executorProvidedList = new ArrayList<>();
    JobConfig4DB currentJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (currentJobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u53ef\u9009\u62e9\u7684\u4f18\u5148Executor\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    String executorsNodePath = SaturnExecutorsNode.getExecutorsNodePath();
    if (!curatorFrameworkOp.checkExists(executorsNodePath)) {
      return executorProvidedList;
    }
    List<String> executors = curatorFrameworkOp.getChildren(executorsNodePath);
    if (executors != null && executors.size() > 0) {
      for (String executor : executors) {
        if (curatorFrameworkOp.checkExists(SaturnExecutorsNode.getExecutorTaskNodePath(executor))) {
          continue;
        }
        ExecutorProvided executorProvided = new ExecutorProvided();
        executorProvided.setExecutorName(executor);
        executorProvided.setNoTraffic(curatorFrameworkOp.checkExists(SaturnExecutorsNode.getExecutorNoTrafficNodePath(executor)));
        String ip = curatorFrameworkOp.getData(SaturnExecutorsNode.getExecutorIpNodePath(executor));
        if (StringUtils.isNotBlank(ip)) {
          executorProvided.setType(ExecutorProvidedType.ONLINE);
        } else {
          executorProvided.setType(ExecutorProvidedType.OFFLINE);
        }
        executorProvidedList.add(executorProvided);
      }
    }
    executorProvidedList.addAll(getContainerTaskIds(curatorFrameworkOp));
    if (StringUtils.isNotBlank(jobName)) {
      String preferListNodePath = JobNodePath.getConfigNodePath(jobName, "preferList");
      if (curatorFrameworkOp.checkExists(preferListNodePath)) {
        String preferList = curatorFrameworkOp.getData(preferListNodePath);
        if (!Strings.isNullOrEmpty(preferList)) {
          String[] preferExecutorList = preferList.split(",");
          for (String preferExecutor : preferExecutorList) {
            if (executors != null && !executors.contains(preferExecutor) && !preferExecutor.startsWith("@")) {
              ExecutorProvided executorProvided = new ExecutorProvided();
              executorProvided.setExecutorName(preferExecutor);
              executorProvided.setType(ExecutorProvidedType.DELETED);
              executorProvided.setNoTraffic(curatorFrameworkOp.checkExists(SaturnExecutorsNode.getExecutorNoTrafficNodePath(preferExecutor)));
              executorProvidedList.add(executorProvided);
            }
          }
        }
      }
    }
    return executorProvidedList;
  }

  /**
	 * 先获取DCOS节点下的taskID节点；如果没有此节点，则尝试从executor节点下获取; <p> 不存在既有DCOS容器，又有K8S容器的模式。
	 */
  private List<ExecutorProvided> getContainerTaskIds(CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    List<ExecutorProvided> executorProvidedList = new ArrayList<>();
    List<String> containerTaskIds = getDCOSContainerTaskIds(curatorFrameworkOp);
    if (CollectionUtils.isEmpty(containerTaskIds)) {
      containerTaskIds = getK8SContainerTaskIds(curatorFrameworkOp);
    }
    if (!CollectionUtils.isEmpty(containerTaskIds)) {
      for (String task : containerTaskIds) {
        ExecutorProvided executorProvided = new ExecutorProvided();
        executorProvided.setExecutorName(task);
        executorProvided.setType(ExecutorProvidedType.DOCKER);
        executorProvidedList.add(executorProvided);
      }
    }
    return executorProvidedList;
  }

  private List<String> getDCOSContainerTaskIds(CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    List<String> containerTaskIds = Lists.newArrayList();
    String containerNodePath = ContainerNodePath.getDcosTasksNodePath();
    if (curatorFrameworkOp.checkExists(containerNodePath)) {
      containerTaskIds = curatorFrameworkOp.getChildren(containerNodePath);
    }
    return containerTaskIds;
  }

  private List<String> getK8SContainerTaskIds(CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    List<String> taskIds = new ArrayList<>();
    String executorsNodePath = SaturnExecutorsNode.getExecutorsNodePath();
    List<String> executors = curatorFrameworkOp.getChildren(executorsNodePath);
    if (executors != null && executors.size() > 0) {
      for (String executor : executors) {
        String executorTaskNodePath = SaturnExecutorsNode.getExecutorTaskNodePath(executor);
        if (curatorFrameworkOp.checkExists(executorTaskNodePath)) {
          String taskId = curatorFrameworkOp.getData(executorTaskNodePath);
          if (taskId != null && !taskIds.contains(taskId)) {
            taskIds.add(taskId);
          }
        }
      }
    }
    return taskIds;
  }

  @Override public void setPreferList(String namespace, String jobName, String preferList) throws SaturnJobConsoleException {
    JobConfig4DB oldJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (oldJobConfig == null) {
      throw new SaturnJobConsoleException("\u8bbe\u7f6e\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u4f18\u5148Executor\u5931\u8d25\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    JobConfig4DB newJobConfig = mapper.map(oldJobConfig, JobConfig4DB.class);
    newJobConfig.setPreferList(preferList);
    try {
      currentJobConfigService.updateNewAndSaveOld2History(newJobConfig, oldJobConfig, null);
    } catch (Exception e) {
      log.error("exception is thrown during change preferList in db", e);
      throw new SaturnJobConsoleException(e);
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    String jobConfigPreferListNodePath = SaturnExecutorsNode.getJobConfigPreferListNodePath(jobName);
    curatorFrameworkOp.update(jobConfigPreferListNodePath, preferList);
    String jobConfigForceShardNodePath = SaturnExecutorsNode.getJobConfigForceShardNodePath(jobName);
    curatorFrameworkOp.delete(jobConfigForceShardNodePath);
    curatorFrameworkOp.create(jobConfigForceShardNodePath);
  }

  private void validateJobConfig(JobConfig jobConfig) throws SaturnJobConsoleException {
    if (jobConfig.getJobName() == null || jobConfig.getJobName().trim().isEmpty()) {
      throw new SaturnJobConsoleException("\u4f5c\u4e1a\u540d\u5fc5\u586b");
    }
    if (!jobConfig.getJobName().matches("[0-9a-zA-Z_]*")) {
      throw new SaturnJobConsoleException("\u4f5c\u4e1a\u540d\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_");
    }
    if (jobConfig.getDependencies() != null && !jobConfig.getDependencies().matches("[0-9a-zA-Z_,]*")) {
      throw new SaturnJobConsoleException("\u4f9d\u8d56\u7684\u4f5c\u4e1a\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3001\u82f1\u6587\u9017\u53f7,");
    }
    if (jobConfig.getJobType() == null || jobConfig.getJobType().trim().isEmpty()) {
      throw new SaturnJobConsoleException("\u4f5c\u4e1a\u7c7b\u578b\u5fc5\u586b");
    }
    if (JobType.getJobType(jobConfig.getJobType()).equals(JobType.UNKOWN_JOB)) {
      throw new SaturnJobConsoleException("\u4f5c\u4e1a\u7c7b\u578b\u672a\u77e5");
    }
    if (jobConfig.getJobType().equals(JobType.JAVA_JOB.name())) {
      if (jobConfig.getJobClass() == null || jobConfig.getJobClass().trim().isEmpty()) {
        throw new SaturnJobConsoleException("\u5bf9\u4e8eJAVA\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b");
      }
    }
    if (jobConfig.getJobType().equals(JobType.JAVA_JOB.name()) || jobConfig.getJobType().equals(JobType.SHELL_JOB.name())) {
      if (jobConfig.getCron() == null || jobConfig.getCron().trim().isEmpty()) {
        throw new SaturnJobConsoleException("\u5bf9\u4e8eJAVA/SHELL\u4f5c\u4e1a\uff0ccron\u8868\u8fbe\u5f0f\u5fc5\u586b");
      }
      try {
        CronExpression.validateExpression(jobConfig.getCron());
      } catch (ParseException e) {
        throw new SaturnJobConsoleException("cron\u8868\u8fbe\u5f0f\u8bed\u6cd5\u6709\u8bef\uff0c" + e.toString());
      }
    } else {
      jobConfig.setCron("");
    }
    if (jobConfig.getLocalMode() != null && jobConfig.getLocalMode()) {
      if (jobConfig.getShardingItemParameters() == null) {
        throw new SaturnJobConsoleException("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u586b\u3002");
      } else {
        String[] split = jobConfig.getShardingItemParameters().split(",");
        boolean includeXing = false;
        for (String tmp : split) {
          String[] split2 = tmp.split("=");
          if ("*".equalsIgnoreCase(split2[0].trim())) {
            includeXing = true;
            break;
          }
        }
        if (!includeXing) {
          throw new SaturnJobConsoleException("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u987b\u5305\u542b\u5982*=xx\u3002");
        }
      }
    } else {
      if (jobConfig.getShardingTotalCount() == null || jobConfig.getShardingTotalCount() < 1) {
        throw new SaturnJobConsoleException("\u5206\u7247\u6570\u4e0d\u80fd\u4e3a\u7a7a\uff0c\u5e76\u4e14\u4e0d\u80fd\u5c0f\u4e8e1");
      }
      if (jobConfig.getShardingTotalCount() > 0) {
        if (jobConfig.getShardingItemParameters() == null || jobConfig.getShardingItemParameters().trim().isEmpty() || jobConfig.getShardingItemParameters().split(",").length < jobConfig.getShardingTotalCount()) {
          throw new SaturnJobConsoleException("\u5206\u7247\u53c2\u6570\u4e0d\u80fd\u5c0f\u4e8e\u5206\u7247\u603b\u6570");
        }
      }
    }
    if (jobConfig.getJobMode() != null && jobConfig.getJobMode().startsWith(JobMode.SYSTEM_PREFIX)) {
      throw new SaturnJobConsoleException("\u4f5c\u4e1a\u6a21\u5f0f\u6709\u8bef\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7cfb\u7edf\u4f5c\u4e1a");
    }
  }

  @Transactional @Override public void addJob(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException {
    addOrCopyJob(namespace, jobConfig, null);
  }

  @Transactional @Override public void copyJob(String namespace, JobConfig jobConfig, String jobNameCopied) throws SaturnJobConsoleException {
    addOrCopyJob(namespace, jobConfig, jobNameCopied);
  }

  private void addOrCopyJob(String namespace, JobConfig jobConfig, String jobNameCopied) throws SaturnJobConsoleException {
    validateJobConfig(jobConfig);
    String jobName = jobConfig.getJobName();
    JobConfig4DB oldJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (oldJobConfig != null) {
      throw new SaturnJobConsoleException(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u5df2\u7ecf\u5b58\u5728", jobName));
    }
    int maxJobNum = getMaxJobNum();
    if (jobIncExceeds(namespace, maxJobNum, 1)) {
      throw new SaturnJobConsoleException(String.format("\u603b\u4f5c\u4e1a\u6570\u8d85\u8fc7\u6700\u5927\u9650\u5236(%d)\uff0c\u4f5c\u4e1a\u540d%s\u521b\u5efa\u5931\u8d25", maxJobNum, jobName));
    } else {
      JobConfig jobConfig2 = jobConfig;
      if (jobNameCopied != null) {
        JobConfig4DB jobConfig4DBCopied = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobNameCopied);
        jobConfig2 = mapper.map(jobConfig4DBCopied, JobConfig.class);
        mapper.map(jobConfig, jobConfig2);
      }
      persistJob(namespace, jobConfig2);
    }
  }

  private void persistJob(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException {
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    if (curatorFrameworkOp.checkExists(JobNodePath.getJobNodePath(jobConfig.getJobName()))) {
      curatorFrameworkOp.deleteRecursive(JobNodePath.getJobNodePath(jobConfig.getJobName()));
    }
    correctConfigValueIfNeeded(jobConfig);
    saveJobConfigToDb(namespace, jobConfig);
    saveJobConfigToZk(jobConfig, curatorFrameworkOp);
  }

  @Override public int getMaxJobNum() {
    int result = systemConfigService.getIntegerValue(SystemConfigProperties.MAX_JOB_NUM, DEFAULT_MAX_JOB_NUM);
    return result <= 0 ? DEFAULT_MAX_JOB_NUM : result;
  }

  @Override public boolean jobIncExceeds(String namespace, int maxJobNum, int inc) throws SaturnJobConsoleException {
    if (maxJobNum <= 0) {
      return false;
    }
    int curJobSize = getUnSystemJobs(namespace).size();
    return (curJobSize + inc) > maxJobNum;
  }

  @Override public List<JobConfig> getUnSystemJobs(String namespace) throws SaturnJobConsoleException {
    List<JobConfig> unSystemJobs = new ArrayList<>();
    List<JobConfig4DB> jobConfig4DBList = currentJobConfigService.findConfigsByNamespace(namespace);
    if (jobConfig4DBList != null) {
      for (JobConfig4DB jobConfig4DB : jobConfig4DBList) {
        if (!(StringUtils.isNotBlank(jobConfig4DB.getJobMode()) && jobConfig4DB.getJobMode().startsWith(JobMode.SYSTEM_PREFIX))) {
          unSystemJobs.add(mapper.map(jobConfig4DB, JobConfig.class));
        }
      }
    }
    return unSystemJobs;
  }

  @Override public List<String> getUnSystemJobNames(String namespace) throws SaturnJobConsoleException {
    List<String> unSystemJobs = new ArrayList<>();
    List<JobConfig4DB> jobConfig4DBList = currentJobConfigService.findConfigsByNamespace(namespace);
    if (jobConfig4DBList != null) {
      for (JobConfig4DB jobConfig4DB : jobConfig4DBList) {
        if (!(StringUtils.isNotBlank(jobConfig4DB.getJobMode()) && jobConfig4DB.getJobMode().startsWith(JobMode.SYSTEM_PREFIX))) {
          unSystemJobs.add(jobConfig4DB.getJobName());
        }
      }
    }
    return unSystemJobs;
  }

  @Override public void persistJobFromDB(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException {
    jobConfig.setDefaultValues();
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    saveJobConfigToZk(jobConfig, curatorFrameworkOp);
  }

  /**
	 * 对作业配置的一些属性进行矫正
	 */
  private void correctConfigValueIfNeeded(JobConfig jobConfig) {
    jobConfig.setDefaultValues();
    jobConfig.setEnabled(false);
    jobConfig.setFailover(jobConfig.getLocalMode() == false);
    if (JobType.SHELL_JOB.name().equals(jobConfig.getJobType())) {
      jobConfig.setJobClass("");
    }
    jobConfig.setEnabledReport(getEnabledReport(jobConfig.getJobType(), jobConfig.getCron(), jobConfig.getTimeZone()));
  }

  /**
	 * 对于定时作业，根据cron和INTERVAL_TIME_OF_ENABLED_REPORT来计算是否需要上报状态 see #286
	 */
  private boolean getEnabledReport(String jobType, String cron, String timeZone) {
    boolean enabledReport = true;
    if (jobType.equals(JobType.JAVA_JOB.name()) || jobType.equals(JobType.SHELL_JOB.name())) {
      try {
        Integer intervalTimeConfigured = systemConfigService.getIntegerValue(SystemConfigProperties.INTERVAL_TIME_OF_ENABLED_REPORT, DEFAULT_INTERVAL_TIME_OF_ENABLED_REPORT);
        if (intervalTimeConfigured == null) {
          log.warn("unexpected error, get INTERVAL_TIME_OF_ENABLED_REPORT null");
          intervalTimeConfigured = DEFAULT_INTERVAL_TIME_OF_ENABLED_REPORT;
        }
        CronExpression cronExpression = new CronExpression(cron);
        cronExpression.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date lastNextTime = cronExpression.getNextValidTimeAfter(new Date());
        if (lastNextTime != null) {
          for (int i = 0; i < 5; i++) {
            Date nextTime = cronExpression.getNextValidTimeAfter(lastNextTime);
            if (nextTime == null) {
              break;
            }
            long interval = nextTime.getTime() - lastNextTime.getTime();
            if (interval < intervalTimeConfigured * 1000) {
              enabledReport = false;
              break;
            }
            lastNextTime = nextTime;
          }
        }
      } catch (ParseException e) {
        log.warn(e.getMessage(), e);
      }
    } else {
      enabledReport = false;
    }
    return enabledReport;
  }

  private void saveJobConfigToDb(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException {
    String jobName = jobConfig.getJobName();
    JobConfig4DB oldJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (oldJobConfig != null) {
      log.warn("when create a new job, a jobConfig with the same name from db exists, will delete it first. namespace:{} and jobName:{}", namespace, jobName);
      try {
        currentJobConfigService.deleteByPrimaryKey(oldJobConfig.getId());
      } catch (Exception e) {
        log.error("exception is thrown during delete job config in db", e);
        throw new SaturnJobConsoleException("\u521b\u5efa\u4f5c\u4e1a\u65f6\uff0c\u6570\u636e\u5e93\u5b58\u5728\u5df2\u7ecf\u5b58\u5728\u8be5\u4f5c\u4e1a\u7684\u76f8\u5173\u914d\u7f6e\uff01\u5e76\u4e14\u6e05\u7406\u8be5\u914d\u7f6e\u7684\u65f6\u5019\u5931\u8d25", e);
      }
    }
    JobConfig4DB currentJobConfig = new JobConfig4DB();
    mapper.map(jobConfig, currentJobConfig);
    currentJobConfig.setCreateTime(new Date());
    currentJobConfig.setLastUpdateTime(new Date());
    currentJobConfig.setNamespace(namespace);
    try {
      currentJobConfigService.create(currentJobConfig);
    } catch (Exception e) {
      log.error("exception is thrown during creating job config in db", e);
      throw new SaturnJobConsoleException(e);
    }
  }

  private void saveJobConfigToZk(JobConfig jobConfig, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    String jobName = jobConfig.getJobName();
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "enabled"), jobConfig.getEnabled());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "description"), jobConfig.getDescription());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "customContext"), jobConfig.getCustomContext());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "jobType"), jobConfig.getJobType());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "jobMode"), jobConfig.getJobMode());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "shardingItemParameters"), jobConfig.getShardingItemParameters());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "jobParameter"), jobConfig.getJobParameter());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "queueName"), jobConfig.getQueueName());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "channelName"), jobConfig.getChannelName());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "failover"), jobConfig.getFailover());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "monitorExecution"), "true");
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "timeout4AlarmSeconds"), jobConfig.getTimeout4AlarmSeconds());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "timeoutSeconds"), jobConfig.getTimeoutSeconds());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "timeZone"), jobConfig.getTimeZone());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "cron"), jobConfig.getCron());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "pausePeriodDate"), jobConfig.getPausePeriodDate());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "pausePeriodTime"), jobConfig.getPausePeriodTime());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "processCountIntervalSeconds"), jobConfig.getProcessCountIntervalSeconds());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "shardingTotalCount"), jobConfig.getShardingTotalCount());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "showNormalLog"), jobConfig.getShowNormalLog());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "loadLevel"), jobConfig.getLoadLevel());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "jobDegree"), jobConfig.getJobDegree());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "enabledReport"), jobConfig.getEnabledReport());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "preferList"), jobConfig.getPreferList());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "useDispreferList"), jobConfig.getUseDispreferList());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "localMode"), jobConfig.getLocalMode());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "useSerial"), jobConfig.getUseSerial());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "dependencies"), jobConfig.getDependencies());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "groups"), jobConfig.getGroups());
    curatorFrameworkOp.fillJobNodeIfNotExist(JobNodePath.getConfigNodePath(jobName, "jobClass"), jobConfig.getJobClass());
  }

  @Override public void importJobs(String namespace, MultipartFile file) throws SaturnJobConsoleException {
    try {
      Workbook workbook = Workbook.getWorkbook(file.getInputStream());
      Sheet[] sheets = workbook.getSheets();
      List<JobConfig> jobConfigList = new ArrayList<>();
      for (int i = 0; i < sheets.length; i++) {
        Sheet sheet = sheets[i];
        int rows = sheet.getRows();
        for (int row = 1; row < rows; row++) {
          Cell[] rowCells = sheet.getRow(row);
          if (!isBlankRow(rowCells)) {
            jobConfigList.add(convertJobConfig(i + 1, row + 1, rowCells));
          }
        }
      }
      int maxJobNum = getMaxJobNum();
      if (jobIncExceeds(namespace, maxJobNum, jobConfigList.size())) {
        throw new SaturnJobConsoleException(String.format("\u603b\u4f5c\u4e1a\u6570\u8d85\u8fc7\u6700\u5927\u9650\u5236(%d)\uff0c\u5bfc\u5165\u5931\u8d25", maxJobNum));
      }
      for (JobConfig jobConfig : jobConfigList) {
        addJob(namespace, jobConfig);
      }
    } catch (SaturnJobConsoleException e) {
      throw e;
    } catch (Exception e) {
      throw new SaturnJobConsoleException(e);
    }
  }

  private boolean isBlankRow(Cell[] rowCells) {
    for (int i = 0; i < rowCells.length; i++) {
      if (!CellType.EMPTY.equals(rowCells[i].getType())) {
        return false;
      }
    }
    return true;
  }

  private JobConfig convertJobConfig(int sheetNumber, int rowNumber, Cell[] rowCells) throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    String jobName = getContents(rowCells, 0);
    if (jobName == null || jobName.trim().isEmpty()) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 1, "\u4f5c\u4e1a\u540d\u5fc5\u586b\u3002"));
    }
    if (!jobName.matches("[0-9a-zA-Z_]*")) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 1, "\u4f5c\u4e1a\u540d\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3002"));
    }
    jobConfig.setJobName(jobName);
    String jobType = getContents(rowCells, 1);
    if (jobType == null || jobType.trim().isEmpty()) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 2, "\u4f5c\u4e1a\u7c7b\u578b\u5fc5\u586b\u3002"));
    }
    if (JobType.getJobType(jobType).equals(JobType.UNKOWN_JOB)) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 2, "\u4f5c\u4e1a\u7c7b\u578b\u672a\u77e5\u3002"));
    }
    jobConfig.setJobType(jobType);
    String jobClass = getContents(rowCells, 2);
    if (jobType.equals(JobType.JAVA_JOB.name())) {
      if (jobClass == null || jobClass.trim().isEmpty()) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 3, "\u5bf9\u4e8eJAVA\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b\u3002"));
      }
    }
    jobConfig.setJobClass(jobClass);
    String cron = getContents(rowCells, 3);
    if (jobType.equals(JobType.JAVA_JOB.name()) || jobType.equals(JobType.SHELL_JOB.name())) {
      if (cron == null || cron.trim().isEmpty()) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 4, "\u5bf9\u4e8eJAVA/SHELL\u4f5c\u4e1a\uff0ccron\u8868\u8fbe\u5f0f\u5fc5\u586b\u3002"));
      }
      cron = cron.trim();
      try {
        CronExpression.validateExpression(cron);
      } catch (ParseException e) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 4, "cron\u8868\u8fbe\u5f0f\u8bed\u6cd5\u6709\u8bef\uff0c" + e.toString()));
      }
    } else {
      cron = "";
    }
    jobConfig.setCron(cron);
    jobConfig.setDescription(getContents(rowCells, 4));
    jobConfig.setLocalMode(Boolean.valueOf(getContents(rowCells, 5)));
    int shardingTotalCount = 1;
    if (jobConfig.getLocalMode()) {
      jobConfig.setShardingTotalCount(shardingTotalCount);
    } else {
      String tmp = getContents(rowCells, 6);
      if (tmp != null) {
        try {
          shardingTotalCount = Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
          throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 7, "\u5206\u7247\u6570\u6709\u8bef\uff0c" + e.toString()));
        }
      } else {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 7, "\u5206\u7247\u6570\u5fc5\u586b"));
      }
      if (shardingTotalCount < 1) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 7, "\u5206\u7247\u6570\u4e0d\u80fd\u5c0f\u4e8e1"));
      }
      jobConfig.setShardingTotalCount(shardingTotalCount);
    }
    int timeoutSeconds = 0;
    try {
      String tmp = getContents(rowCells, 7);
      if (tmp != null && !tmp.trim().isEmpty()) {
        timeoutSeconds = Integer.parseInt(tmp.trim());
      }
    } catch (NumberFormatException e) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 8, "\u8d85\u65f6\uff08Kill\u7ebf\u7a0b/\u8fdb\u7a0b\uff09\u65f6\u95f4\u6709\u8bef\uff0c" + e.toString()));
    }
    jobConfig.setTimeoutSeconds(timeoutSeconds);
    jobConfig.setJobParameter(getContents(rowCells, 8));
    String shardingItemParameters = getContents(rowCells, 9);
    if (jobConfig.getLocalMode()) {
      if (shardingItemParameters == null) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 10, "\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u586b\u3002"));
      } else {
        String[] split = shardingItemParameters.split(",");
        boolean includeXing = false;
        for (String tmp : split) {
          String[] split2 = tmp.split("=");
          if ("*".equalsIgnoreCase(split2[0].trim())) {
            includeXing = true;
            break;
          }
        }
        if (!includeXing) {
          throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 10, "\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u987b\u5305\u542b\u5982*=xx\u3002"));
        }
      }
    } else {
      if (shardingTotalCount > 0) {
        if (shardingItemParameters == null || shardingItemParameters.trim().isEmpty() || shardingItemParameters.split(",").length < shardingTotalCount) {
          throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 10, "\u5206\u7247\u53c2\u6570\u4e0d\u80fd\u5c0f\u4e8e\u5206\u7247\u603b\u6570\u3002"));
        }
      }
    }
    jobConfig.setShardingItemParameters(shardingItemParameters);
    jobConfig.setQueueName(getContents(rowCells, 10));
    jobConfig.setChannelName(getContents(rowCells, 11));
    jobConfig.setPreferList(getContents(rowCells, 12));
    jobConfig.setUseDispreferList(!Boolean.valueOf(getContents(rowCells, 13)));
    int processCountIntervalSeconds = 300;
    try {
      String tmp = getContents(rowCells, 14);
      if (tmp != null && !tmp.trim().isEmpty()) {
        processCountIntervalSeconds = Integer.parseInt(tmp.trim());
      }
    } catch (NumberFormatException e) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 15, "\u7edf\u8ba1\u5904\u7406\u6570\u636e\u91cf\u7684\u95f4\u9694\u79d2\u6570\u6709\u8bef\uff0c" + e.toString()));
    }
    jobConfig.setProcessCountIntervalSeconds(processCountIntervalSeconds);
    int loadLevel = 1;
    try {
      String tmp = getContents(rowCells, 15);
      if (tmp != null && !tmp.trim().isEmpty()) {
        loadLevel = Integer.parseInt(tmp.trim());
      }
    } catch (NumberFormatException e) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 16, "\u8d1f\u8377\u6709\u8bef\uff0c" + e.toString()));
    }
    jobConfig.setLoadLevel(loadLevel);
    jobConfig.setShowNormalLog(Boolean.valueOf(getContents(rowCells, 16)));
    jobConfig.setPausePeriodDate(getContents(rowCells, 17));
    jobConfig.setPausePeriodTime(getContents(rowCells, 18));
    jobConfig.setUseSerial(Boolean.valueOf(getContents(rowCells, 19)));
    int jobDegree = 0;
    try {
      String tmp = getContents(rowCells, 20);
      if (tmp != null && !tmp.trim().isEmpty()) {
        jobDegree = Integer.parseInt(tmp.trim());
      }
    } catch (NumberFormatException e) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 21, "\u4f5c\u4e1a\u91cd\u8981\u7b49\u7ea7\u6709\u8bef\uff0c" + e.toString()));
    }
    jobConfig.setJobDegree(jobDegree);
    String jobMode = getContents(rowCells, 22);
    if (jobMode != null && jobMode.startsWith(com.vip.saturn.job.console.domain.JobMode.SYSTEM_PREFIX)) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 23, "\u4f5c\u4e1a\u6a21\u5f0f\u6709\u8bef\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7cfb\u7edf\u4f5c\u4e1a"));
    }
    jobConfig.setJobMode(jobMode);
    String dependencies = getContents(rowCells, 23);
    ;
    if (dependencies != null && !dependencies.matches("[0-9a-zA-Z_,]*")) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 24, "\u4f9d\u8d56\u7684\u4f5c\u4e1a\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3001\u82f1\u6587\u9017\u53f7,"));
    }
    jobConfig.setDependencies(dependencies);
    jobConfig.setGroups(getContents(rowCells, 24));
    int timeout4AlarmSeconds = 0;
    try {
      String tmp = getContents(rowCells, 25);
      if (tmp != null && !tmp.trim().isEmpty()) {
        timeout4AlarmSeconds = Integer.parseInt(tmp.trim());
      }
    } catch (NumberFormatException e) {
      throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 26, "\u8d85\u65f6\uff08\u544a\u8b66\uff09\u65f6\u95f4\u6709\u8bef\uff0c" + e.toString()));
    }
    jobConfig.setTimeout4AlarmSeconds(timeout4AlarmSeconds);
    String timeZone = getContents(rowCells, 26);
    if (timeZone == null || timeZone.trim().length() == 0) {
      timeZone = SaturnConstants.TIME_ZONE_ID_DEFAULT;
    } else {
      timeZone = timeZone.trim();
      if (!SaturnConstants.TIME_ZONE_IDS.contains(timeZone)) {
        throw new SaturnJobConsoleException(createExceptionMessage(sheetNumber, rowNumber, 27, "\u65f6\u533a\u6709\u8bef"));
      }
    }
    jobConfig.setTimeZone(timeZone);
    return jobConfig;
  }

  private String getContents(Cell[] rowCell, int column) {
    if (rowCell.length > column) {
      return rowCell[column].getContents();
    }
    return null;
  }

  private String createExceptionMessage(int sheetNumber, int rowNumber, int columnNumber, String message) {
    return "\u5185\u5bb9\u683c\u5f0f\u6709\u8bef\uff0c\u9519\u8bef\u53d1\u751f\u5728\u8868\u683c\u9875:" + sheetNumber + "\uff0c\u884c\u53f7:" + rowNumber + "\uff0c\u5217\u53f7:" + columnNumber + "\uff0c\u9519\u8bef\u4fe1\u606f\uff1a" + message;
  }

  @Override public File exportJobs(String namespace) throws SaturnJobConsoleException {
    try {
      File tmp = new File(SaturnConstants.CACHES_FILE_PATH, "tmp_exportFile_" + System.currentTimeMillis() + "_" + random.nextInt(1000) + ".xls");
      if (!tmp.exists()) {
        FileUtils.forceMkdir(tmp.getParentFile());
        tmp.createNewFile();
      }
      WritableWorkbook writableWorkbook = Workbook.createWorkbook(tmp);
      WritableSheet sheet1 = writableWorkbook.createSheet("Sheet1", 0);
      sheet1.addCell(new Label(0, 0, "\u4f5c\u4e1a\u540d\u79f0"));
      sheet1.addCell(new Label(1, 0, "\u4f5c\u4e1a\u7c7b\u578b"));
      sheet1.addCell(new Label(2, 0, "\u4f5c\u4e1a\u5b9e\u73b0\u7c7b"));
      sheet1.addCell(new Label(3, 0, "cron\u8868\u8fbe\u5f0f"));
      sheet1.addCell(new Label(4, 0, "\u4f5c\u4e1a\u63cf\u8ff0"));
      Label localModeLabel = new Label(5, 0, "\u672c\u5730\u6a21\u5f0f");
      setCellComment(localModeLabel, "\u5bf9\u4e8e\u975e\u672c\u5730\u6a21\u5f0f\uff0c\u9ed8\u8ba4\u4e3afalse\uff1b\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\uff0c\u8be5\u914d\u7f6e\u65e0\u6548\uff0c\u56fa\u5b9a\u4e3atrue");
      sheet1.addCell(localModeLabel);
      Label shardingTotalCountLabel = new Label(6, 0, "\u5206\u7247\u6570");
      setCellComment(shardingTotalCountLabel, "\u5bf9\u672c\u5730\u4f5c\u4e1a\u65e0\u6548");
      sheet1.addCell(shardingTotalCountLabel);
      Label timeoutSecondsLabel = new Label(7, 0, "\u8d85\u65f6\uff08Kill\u7ebf\u7a0b/\u8fdb\u7a0b\uff09\u65f6\u95f4");
      setCellComment(timeoutSecondsLabel, "0\u8868\u793a\u65e0\u8d85\u65f6");
      sheet1.addCell(timeoutSecondsLabel);
      sheet1.addCell(new Label(8, 0, "\u81ea\u5b9a\u4e49\u53c2\u6570"));
      sheet1.addCell(new Label(9, 0, "\u5206\u7247\u5e8f\u5217\u53f7/\u53c2\u6570\u5bf9\u7167\u8868"));
      sheet1.addCell(new Label(10, 0, "Queue\u540d"));
      sheet1.addCell(new Label(11, 0, "\u6267\u884c\u7ed3\u679c\u53d1\u9001\u7684Channel"));
      Label preferListLabel = new Label(12, 0, "\u4f18\u5148Executor");
      setCellComment(preferListLabel, "\u53ef\u586bexecutorName\uff0c\u591a\u4e2a\u5143\u7d20\u4f7f\u7528\u82f1\u6587\u9017\u53f7\u9694\u5f00");
      sheet1.addCell(preferListLabel);
      Label usePreferListOnlyLabel = new Label(13, 0, "\u53ea\u4f7f\u7528\u4f18\u5148Executor");
      setCellComment(usePreferListOnlyLabel, "\u9ed8\u8ba4\u4e3afalse");
      sheet1.addCell(usePreferListOnlyLabel);
      sheet1.addCell(new Label(14, 0, "\u7edf\u8ba1\u5904\u7406\u6570\u636e\u91cf\u7684\u95f4\u9694\u79d2\u6570"));
      sheet1.addCell(new Label(15, 0, "\u8d1f\u8377"));
      sheet1.addCell(new Label(16, 0, "\u663e\u793a\u63a7\u5236\u53f0\u8f93\u51fa\u65e5\u5fd7"));
      sheet1.addCell(new Label(17, 0, "\u6682\u505c\u65e5\u671f\u6bb5"));
      sheet1.addCell(new Label(18, 0, "\u6682\u505c\u65f6\u95f4\u6bb5"));
      Label useSerialLabel = new Label(19, 0, "\u4e32\u884c\u6d88\u8d39");
      setCellComment(useSerialLabel, "\u9ed8\u8ba4\u4e3afalse");
      sheet1.addCell(useSerialLabel);
      Label jobDegreeLabel = new Label(20, 0, "\u4f5c\u4e1a\u91cd\u8981\u7b49\u7ea7");
      setCellComment(jobDegreeLabel, "0:\u6ca1\u6709\u5b9a\u4e49,1:\u975e\u7ebf\u4e0a\u4e1a\u52a1,2:\u7b80\u5355\u4e1a\u52a1,3:\u4e00\u822c\u4e1a\u52a1,4:\u91cd\u8981\u4e1a\u52a1,5:\u6838\u5fc3\u4e1a\u52a1");
      sheet1.addCell(jobDegreeLabel);
      Label enabledReportLabel = new Label(21, 0, "\u4e0a\u62a5\u8fd0\u884c\u72b6\u6001");
      setCellComment(enabledReportLabel, "\u5bf9\u4e8e\u5b9a\u65f6\u4f5c\u4e1a\uff0c\u9ed8\u8ba4\u4e3atrue\uff1b\u5bf9\u4e8e\u6d88\u606f\u4f5c\u4e1a\uff0c\u9ed8\u8ba4\u4e3afalse");
      sheet1.addCell(enabledReportLabel);
      Label jobModeLabel = new Label(22, 0, "\u4f5c\u4e1a\u6a21\u5f0f");
      setCellComment(jobModeLabel, "\u7528\u6237\u4e0d\u80fd\u6dfb\u52a0\u7cfb\u7edf\u4f5c\u4e1a");
      sheet1.addCell(jobModeLabel);
      Label dependenciesLabel = new Label(23, 0, "\u4f9d\u8d56\u7684\u4f5c\u4e1a");
      setCellComment(dependenciesLabel, "\u4f5c\u4e1a\u7684\u542f\u7528\u3001\u7981\u7528\u4f1a\u68c0\u67e5\u4f9d\u8d56\u5173\u7cfb\u7684\u4f5c\u4e1a\u7684\u72b6\u6001\u3002\u4f9d\u8d56\u591a\u4e2a\u4f5c\u4e1a\uff0c\u4f7f\u7528\u82f1\u6587\u9017\u53f7\u7ed9\u5f00\u3002");
      sheet1.addCell(dependenciesLabel);
      Label groupsLabel = new Label(24, 0, "\u6240\u5c5e\u5206\u7ec4");
      setCellComment(groupsLabel, "\u4f5c\u4e1a\u6240\u5c5e\u5206\u7ec4\uff0c\u4e00\u4e2a\u4f5c\u4e1a\u53ea\u80fd\u5c5e\u4e8e\u4e00\u4e2a\u5206\u7ec4\uff0c\u4e00\u4e2a\u5206\u7ec4\u53ef\u4ee5\u5305\u542b\u591a\u4e2a\u4f5c\u4e1a");
      sheet1.addCell(groupsLabel);
      Label timeout4AlarmSecondsLabel = new Label(25, 0, "\u8d85\u65f6\uff08\u544a\u8b66\uff09\u65f6\u95f4");
      setCellComment(timeout4AlarmSecondsLabel, "0\u8868\u793a\u65e0\u8d85\u65f6");
      sheet1.addCell(timeout4AlarmSecondsLabel);
      Label timeZoneLabel = new Label(26, 0, "\u65f6\u533a");
      setCellComment(timeZoneLabel, "\u4f5c\u4e1a\u8fd0\u884c\u65f6\u533a");
      sheet1.addCell(timeZoneLabel);
      List<JobConfig> unSystemJobs = getUnSystemJobs(namespace);
      if (unSystemJobs != null && !unSystemJobs.isEmpty()) {
        CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
        for (int i = 0; i < unSystemJobs.size(); i++) {
          String jobName = unSystemJobs.get(i).getJobName();
          sheet1.addCell(new Label(0, i + 1, jobName));
          sheet1.addCell(new Label(1, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobType"))));
          sheet1.addCell(new Label(2, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobClass"))));
          sheet1.addCell(new Label(3, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "cron"))));
          sheet1.addCell(new Label(4, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "description"))));
          sheet1.addCell(new Label(5, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "localMode"))));
          sheet1.addCell(new Label(6, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "shardingTotalCount"))));
          sheet1.addCell(new Label(7, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeoutSeconds"))));
          sheet1.addCell(new Label(8, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobParameter"))));
          sheet1.addCell(new Label(9, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "shardingItemParameters"))));
          sheet1.addCell(new Label(10, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "queueName"))));
          sheet1.addCell(new Label(11, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "channelName"))));
          sheet1.addCell(new Label(12, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "preferList"))));
          String useDispreferList = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "useDispreferList"));
          if (useDispreferList != null) {
            useDispreferList = String.valueOf(!Boolean.valueOf(useDispreferList));
          }
          sheet1.addCell(new Label(13, i + 1, useDispreferList));
          sheet1.addCell(new Label(14, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "processCountIntervalSeconds"))));
          sheet1.addCell(new Label(15, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "loadLevel"))));
          sheet1.addCell(new Label(16, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "showNormalLog"))));
          sheet1.addCell(new Label(17, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "pausePeriodDate"))));
          sheet1.addCell(new Label(18, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "pausePeriodTime"))));
          sheet1.addCell(new Label(19, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "useSerial"))));
          sheet1.addCell(new Label(20, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobDegree"))));
          sheet1.addCell(new Label(21, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "enabledReport"))));
          sheet1.addCell(new Label(22, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobMode"))));
          sheet1.addCell(new Label(23, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "dependencies"))));
          sheet1.addCell(new Label(24, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "groups"))));
          sheet1.addCell(new Label(25, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeout4AlarmSeconds"))));
          sheet1.addCell(new Label(26, i + 1, curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeZone"))));
        }
      }
      writableWorkbook.write();
      writableWorkbook.close();
      return tmp;
    } catch (Exception e) {
      throw new SaturnJobConsoleException(e);
    }
  }

  private void setCellComment(WritableCell cell, String comment) {
    WritableCellFeatures cellFeatures = new WritableCellFeatures();
    cellFeatures.setComment(comment);
    cell.setCellFeatures(cellFeatures);
  }

  @Override public JobConfig getJobConfigFromZK(String namespace, String jobName) throws SaturnJobConsoleException {
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    JobConfig result = new JobConfig();
    result.setJobName(jobName);
    result.setJobType(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobType")));
    result.setJobClass(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobClass")));
    if (StringUtils.isBlank(result.getJobType())) {
      if (result.getJobClass().indexOf("script") > 0) {
        result.setJobType(JobType.SHELL_JOB.name());
      } else {
        result.setJobType(JobType.JAVA_JOB.name());
      }
    }
    result.setShardingTotalCount(Integer.parseInt(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "shardingTotalCount"))));
    String timeZone = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeZone"));
    if (Strings.isNullOrEmpty(timeZone)) {
      result.setTimeZone(SaturnConstants.TIME_ZONE_ID_DEFAULT);
    } else {
      result.setTimeZone(timeZone);
    }
    result.setCron(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "cron")));
    result.setPausePeriodDate(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "pausePeriodDate")));
    result.setPausePeriodTime(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "pausePeriodTime")));
    result.setShardingItemParameters(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "shardingItemParameters")));
    result.setJobParameter(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobParameter")));
    result.setProcessCountIntervalSeconds(Integer.parseInt(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "processCountIntervalSeconds"))));
    String timeout4AlarmSecondsStr = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeout4AlarmSeconds"));
    if (Strings.isNullOrEmpty(timeout4AlarmSecondsStr)) {
      result.setTimeout4AlarmSeconds(0);
    } else {
      result.setTimeout4AlarmSeconds(Integer.parseInt(timeout4AlarmSecondsStr));
    }
    result.setTimeoutSeconds(Integer.parseInt(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeoutSeconds"))));
    String lv = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "loadLevel"));
    if (Strings.isNullOrEmpty(lv)) {
      result.setLoadLevel(1);
    } else {
      result.setLoadLevel(Integer.parseInt(lv));
    }
    String jobDegree = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobDegree"));
    if (Strings.isNullOrEmpty(jobDegree)) {
      result.setJobDegree(0);
    } else {
      result.setJobDegree(Integer.parseInt(jobDegree));
    }
    result.setEnabled(Boolean.valueOf(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "enabled"))));
    result.setPreferList(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "preferList")));
    String useDispreferList = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "useDispreferList"));
    if (Strings.isNullOrEmpty(useDispreferList)) {
      result.setUseDispreferList(null);
    } else {
      result.setUseDispreferList(Boolean.valueOf(useDispreferList));
    }
    result.setLocalMode(Boolean.valueOf(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "localMode"))));
    result.setDependencies(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "dependencies")));
    result.setGroups(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "groups")));
    result.setDescription(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "description")));
    result.setJobMode(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "jobMode")));
    result.setUseSerial(Boolean.valueOf(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "useSerial"))));
    result.setQueueName(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "queueName")));
    result.setChannelName(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "channelName")));
    if (curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName, "showNormalLog")) == false) {
      curatorFrameworkOp.create(JobNodePath.getConfigNodePath(jobName, "showNormalLog"));
    }
    String enabledReport = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "enabledReport"));
    Boolean enabledReportValue = Boolean.valueOf(enabledReport);
    if (Strings.isNullOrEmpty(enabledReport)) {
      enabledReportValue = true;
    }
    result.setEnabledReport(enabledReportValue);
    result.setShowNormalLog(Boolean.valueOf(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "showNormalLog"))));
    return result;
  }

  @Override public JobConfig getJobConfig(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u4e0d\u5b58\u5728", jobName));
    }
    return mapper.map(jobConfig, JobConfig.class);
  }

  @Override public JobStatus getJobStatus(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08" + jobName + "\uff09\u7684\u72b6\u6001\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728");
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    return getJobStatus(jobName, curatorFrameworkOp, jobConfig.getEnabled());
  }

  @Override public JobInfo getJobInfo(String namespace, String jobName) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (jobConfig == null) {
      throw new SaturnJobConsoleException(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u4e0d\u5b58\u5728", jobName));
    }
    JobInfo jobInfo = mapper.map(jobConfig, JobInfo.class);
    jobInfo.setTimeZonesProvided(Arrays.asList(TimeZone.getAvailableIDs()));
    jobInfo.setPreferListProvided(getCandidateExecutors(namespace, jobName));
    List<String> unSystemJobNames = getUnSystemJobNames(namespace);
    if (unSystemJobNames != null) {
      unSystemJobNames.remove(jobName);
      jobInfo.setDependenciesProvided(unSystemJobNames);
    }
    return jobInfo;
  }

  @Transactional @Override public void updateJobConfig(String namespace, JobConfig jobConfig) throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobConfig.getJobName());
    if (jobConfig4DB == null) {
      throw new SaturnJobConsoleException(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u4e0d\u5b58\u5728", jobConfig.getJobName()));
    }
    jobConfig.setDefaultValues();
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    BooleanWrapper bw = new BooleanWrapper(false);
    CuratorRepository.CuratorFrameworkOp.CuratorTransactionOp curatorTransactionOp = null;
    try {
      curatorTransactionOp = curatorFrameworkOp.inTransaction().replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "jobMode"), jobConfig.getJobMode(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "shardingTotalCount"), jobConfig.getShardingTotalCount(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "loadLevel"), jobConfig.getLoadLevel(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "jobDegree"), jobConfig.getJobDegree(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "enabledReport"), jobConfig.getEnabledReport(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "timeZone"), StringUtils.trim(jobConfig.getTimeZone()), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "cron"), StringUtils.trim(jobConfig.getCron()), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "pausePeriodDate"), jobConfig.getPausePeriodDate(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "pausePeriodTime"), jobConfig.getPausePeriodTime(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "shardingItemParameters"), jobConfig.getShardingItemParameters(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "jobParameter"), jobConfig.getJobParameter(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "processCountIntervalSeconds"), jobConfig.getProcessCountIntervalSeconds(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "timeout4AlarmSeconds"), jobConfig.getTimeout4AlarmSeconds(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "timeoutSeconds"), jobConfig.getTimeoutSeconds(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "dependencies"), jobConfig.getDependencies(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "groups"), jobConfig.getGroups(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "description"), jobConfig.getDescription(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "channelName"), StringUtils.trim(jobConfig.getChannelName()), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "queueName"), StringUtils.trim(jobConfig.getQueueName()), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "showNormalLog"), jobConfig.getShowNormalLog(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "preferList"), jobConfig.getPreferList(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "useDispreferList"), jobConfig.getUseDispreferList(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "failover"), jobConfig.getFailover(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "localMode"), jobConfig.getLocalMode(), bw).replaceIfchanged(JobNodePath.getConfigNodePath(jobConfig.getJobName(), "useSerial"), jobConfig.getUseSerial(), bw);
      if (jobConfig.getEnabledReport() != null && !jobConfig.getEnabledReport()) {
        log.info("the switch of enabledReport set to false, now deleteJob the execution zk node");
        String executionNodePath = JobNodePath.getExecutionNodePath(jobConfig.getJobName());
        if (curatorFrameworkOp.checkExists(executionNodePath)) {
          curatorFrameworkOp.deleteRecursive(executionNodePath);
        }
      }
    } catch (Exception e) {
      log.error("update settings to zk failed: {}", e);
      throw new SaturnJobConsoleException(e);
    }
    try {
      if (bw.isValue()) {
        JobConfig4DB newJobConfig4DB = mapper.map(jobConfig4DB, JobConfig4DB.class);
        mapper.map(jobConfig, newJobConfig4DB);
        currentJobConfigService.updateNewAndSaveOld2History(newJobConfig4DB, jobConfig4DB, null);
      }
      if (curatorTransactionOp != null) {
        curatorTransactionOp.commit();
      }
    } catch (Exception e) {
      log.error("update settings to db failed: {}", e);
      throw new SaturnJobConsoleException(e);
    }
  }

  @Override public List<String> getAllJobNamesFromZK(String namespace) throws SaturnJobConsoleException {
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    List<String> allJobs = new ArrayList<>();
    String jobsNodePath = JobNodePath.get$JobsNodePath();
    if (curatorFrameworkOp.checkExists(jobsNodePath)) {
      List<String> jobs = curatorFrameworkOp.getChildren(jobsNodePath);
      if (jobs != null && jobs.size() > 0) {
        for (String job : jobs) {
          if (curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(job))) {
            allJobs.add(job);
          }
        }
      }
    }
    Collections.sort(allJobs);
    return allJobs;
  }

  @Transactional @Override public void updateJobCron(String namespace, String jobName, String cron, Map<String, String> customContext) throws SaturnJobConsoleException {
    String cron0 = cron;
    if (cron0 != null && !cron0.trim().isEmpty()) {
      try {
        cron0 = cron0.trim();
        CronExpression.validateExpression(cron0);
      } catch (ParseException e) {
        throw new SaturnJobConsoleException("The cron expression is valid: " + cron);
      }
    } else {
      cron0 = "";
    }
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    if (curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))) {
      String newCustomContextStr = null;
      String newCron = null;
      String oldCustomContextStr = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "customContext"));
      Map<String, String> oldCustomContextMap = toCustomContext(oldCustomContextStr);
      if (customContext != null && !customContext.isEmpty()) {
        oldCustomContextMap.putAll(customContext);
        newCustomContextStr = toCustomContext(oldCustomContextMap);
        if (newCustomContextStr.getBytes().length > 1024 * 1024) {
          throw new SaturnJobConsoleException("The all customContext is out of zk limit memory(1M)");
        }
      }
      String oldCron = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "cron"));
      if (cron0 != null && oldCron != null && !cron0.equals(oldCron.trim())) {
        newCron = cron0;
      }
      if (newCustomContextStr != null || newCron != null) {
        saveCronToDb(jobName, curatorFrameworkOp, newCustomContextStr, newCron);
      }
      if (newCustomContextStr != null) {
        curatorFrameworkOp.update(JobNodePath.getConfigNodePath(jobName, "customContext"), newCustomContextStr);
      }
      if (newCron != null) {
        curatorFrameworkOp.update(JobNodePath.getConfigNodePath(jobName, "cron"), newCron);
      }
    } else {
      throw new SaturnJobConsoleException("The job is not found: " + jobName);
    }
  }

  private void saveCronToDb(String jobName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp, String newCustomContextStr, String newCron) throws SaturnJobConsoleException {
    String namespace = curatorFrameworkOp.getCuratorFramework().getNamespace();
    JobConfig4DB oldCurrentJobConfig = currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName);
    if (oldCurrentJobConfig == null) {
      String errorMsg = "\u5728DB\u627e\u4e0d\u5230\u8be5\u4f5c\u4e1a\u7684\u914d\u7f6e, namespace\uff1a" + namespace + " jobname:" + jobName;
      log.error(errorMsg);
      throw new SaturnJobConsoleHttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMsg);
    }
    JobConfig4DB newCurrentJobConfig = mapper.map(oldCurrentJobConfig, JobConfig4DB.class);
    if (newCustomContextStr != null) {
      newCurrentJobConfig.setCustomContext(newCustomContextStr);
    }
    if (newCron != null) {
      newCurrentJobConfig.setCron(newCron);
    }
    try {
      currentJobConfigService.updateNewAndSaveOld2History(newCurrentJobConfig, oldCurrentJobConfig, null);
    } catch (Exception e) {
      log.error("exception is thrown during change job state in db", e);
      throw new SaturnJobConsoleHttpException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e);
    }
  }

  /**
	 * 将str转为map
	 *
	 * @param customContextStr str字符串
	 * @return 自定义上下文map
	 */
  private Map<String, String> toCustomContext(String customContextStr) {
    Map<String, String> customContext = null;
    if (customContextStr != null) {
      customContext = JsonUtils.fromJSON(customContextStr, customContextType);
    }
    if (customContext == null) {
      customContext = new HashMap<>();
    }
    return customContext;
  }

  /**
	 * 将map转为str字符串
	 *
	 * @param customContextMap 自定义上下文map
	 * @return 自定义上下文str
	 */
  private String toCustomContext(Map<String, String> customContextMap) {
    String result = JsonUtils.toJSON(customContextMap);
    if (result == null) {
      result = "";
    }
    return result.trim();
  }

  @Override public List<JobServer> getJobServers(String namespace, String jobName) throws SaturnJobConsoleException {
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    String serverNodePath = JobNodePath.getServerNodePath(jobName);
    List<String> executors = new ArrayList<>();
    if (curatorFrameworkOp.checkExists(serverNodePath)) {
      executors = curatorFrameworkOp.getChildren(serverNodePath);
    }
    String leaderIp = curatorFrameworkOp.getData(JobNodePath.getLeaderNodePath(jobName, "election/host"));
    List<JobServer> result = new ArrayList<>();
    if (executors != null) {
      for (String each : executors) {
        result.add(getJobServer(namespace, jobName, leaderIp, each, curatorFrameworkOp));
      }
    }
    return result;
  }

  private JobServer getJobServer(String namespace, String jobName, String leaderIp, String executorName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) throws SaturnJobConsoleException {
    JobServer result = new JobServer();
    result.setExecutorName(executorName);
    result.setIp(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "ip")));
    result.setVersion(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "version")));
    String processSuccessCount = curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "processSuccessCount"));
    result.setProcessSuccessCount(null == processSuccessCount ? 0 : Integer.parseInt(processSuccessCount));
    String processFailureCount = curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "processFailureCount"));
    result.setProcessFailureCount(null == processFailureCount ? 0 : Integer.parseInt(processFailureCount));
    result.setSharding(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "sharding")));
    result.setStatus(getServerStatus(executorName, curatorFrameworkOp));
    result.setLeader(executorName.equals(leaderIp));
    result.setJobStatus(getJobStatus(namespace, jobName));
    result.setJobVersion(getJobVersion(jobName, executorName, curatorFrameworkOp));
    return result;
  }

  private ServerStatus getServerStatus(String executorName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    String ip = curatorFrameworkOp.getData(ExecutorNodePath.getExecutorNodePath(executorName, "ip"));
    return ServerStatus.getServerStatus(ip);
  }

  private String getJobVersion(String jobName, String executorName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    String jobVersion = curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executorName, "jobVersion"));
    return jobVersion == null ? "" : jobVersion;
  }

  @Override public void runAtOnce(String namespace, String jobName, String executorName) throws SaturnJobConsoleException {
    CuratorRepository.CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    String path = JobNodePath.getRunOneTimePath(jobName, executorName);
    if (curatorFrameworkOp.checkExists(path)) {
      curatorFrameworkOp.delete(path);
    }
    curatorFrameworkOp.create(path);
  }

  @Override public void stopAtOnce(String namespace, String jobName, String executorName) throws SaturnJobConsoleException {
    CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    String path = JobNodePath.getStopOneTimePath(jobName, executorName);
    if (curatorFrameworkOp.checkExists(path)) {
      curatorFrameworkOp.delete(path);
    }
    curatorFrameworkOp.create(path);
  }

  @Override public List<ExecutionInfo> getExecutionStatus(String namespace, String jobName) throws SaturnJobConsoleException {
    if (JobStatus.STOPPED.equals(getJobStatus(namespace, jobName))) {
      return Lists.newArrayList();
    }
    CuratorFrameworkOp curatorFrameworkOp = registryCenterService.getCuratorFrameworkOp(namespace);
    updateReportNodeAndWait(jobName, curatorFrameworkOp, 500L);
    if (!curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName))) {
      return Lists.newArrayList();
    }
    List<ExecutionInfo> result = Lists.newArrayList();
    Map<String, String> itemExecutorMap = getItem2ExecutorMap(jobName, curatorFrameworkOp);
    for (String shardItem : itemExecutorMap.keySet()) {
      result.add(buildExecutionInfo(jobName, shardItem, itemExecutorMap.get(shardItem), curatorFrameworkOp));
    }
    Collections.sort(result);
    return result;
  }

  private void updateReportNodeAndWait(String jobName, CuratorFrameworkOp curatorFrameworkOp, long sleepInMill) {
    curatorFrameworkOp.update(JobNodePath.getReportPath(jobName), System.currentTimeMillis());
    try {
      Thread.sleep(sleepInMill);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }

  private ExecutionInfo buildExecutionInfo(String jobName, String shardItem, String executorName, CuratorFrameworkOp curatorFrameworkOp) {
    ExecutionInfo executionInfo = new ExecutionInfo();
    executionInfo.setJobName(jobName);
    executionInfo.setItem(Integer.parseInt(shardItem));
    setExecutorNameAndStatus(jobName, shardItem, executorName, curatorFrameworkOp, executionInfo);
    String jobMsg = curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, shardItem, "jobMsg"));
    executionInfo.setJobMsg(jobMsg);
    String timeZoneStr = curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeZone"));
    if (StringUtils.isBlank(timeZoneStr)) {
      timeZoneStr = SaturnConstants.TIME_ZONE_ID_DEFAULT;
    }
    executionInfo.setTimeZone(timeZoneStr);
    TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
    String lastBeginTime = curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, shardItem, "lastBeginTime"));
    executionInfo.setLastBeginTime(SaturnConsoleUtils.parseMillisecond2DisplayTime(lastBeginTime, timeZone));
    String nextFireTime = curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, shardItem, "nextFireTime"));
    executionInfo.setNextFireTime(SaturnConsoleUtils.parseMillisecond2DisplayTime(nextFireTime, timeZone));
    String lastCompleteTime = curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, shardItem, "lastCompleteTime"));
    if (lastCompleteTime != null) {
      long lastCompleteTimeLong = Long.parseLong(lastCompleteTime);
      if (lastBeginTime == null) {
        executionInfo.setLastCompleteTime(SaturnConsoleUtils.parseMillisecond2DisplayTime(lastCompleteTime, timeZone));
      } else {
        long lastBeginTimeLong = Long.parseLong(lastBeginTime);
        if (lastCompleteTimeLong >= lastBeginTimeLong) {
          executionInfo.setLastCompleteTime(SaturnConsoleUtils.parseMillisecond2DisplayTime(lastCompleteTime, timeZone));
        }
      }
    }
    return executionInfo;
  }

  private void setExecutorNameAndStatus(String jobName, String shardItem, String executorName, CuratorFrameworkOp curatorFrameworkOp, ExecutionInfo executionInfo) {
    boolean isEnabledReport = SaturnConsoleUtils.checkIfJobIsEnabledReport(jobName, curatorFrameworkOp);
    if (!isEnabledReport) {
      executionInfo.setExecutorName(executorName);
      executionInfo.setStatus(ExecutionStatus.BLANK);
      return;
    }
    String completedNodePath = JobNodePath.getCompletedNodePath(jobName, shardItem);
    String completedData = curatorFrameworkOp.getData(completedNodePath);
    if (completedData != null) {
      executionInfo.setExecutorName(StringUtils.isBlank(completedData) ? executorName : completedData);
      executionInfo.setStatus(ExecutionStatus.getExecutionStatus(false, true, false, false, isEnabledReport));
      return;
    }
    String runningNodePath = JobNodePath.getRunningNodePath(jobName, shardItem);
    String runningData = curatorFrameworkOp.getData(runningNodePath);
    if (runningData != null) {
      executionInfo.setExecutorName(StringUtils.isBlank(runningData) ? executorName : runningData);
      long mtime = curatorFrameworkOp.getMtime(runningNodePath);
      executionInfo.setTimeConsumed((new Date().getTime() - mtime) / 1000);
      executionInfo.setStatus(ExecutionStatus.getExecutionStatus(true, false, false, false, isEnabledReport));
      return;
    }
    String failedNodePath = JobNodePath.getFailedNodePath(jobName, shardItem);
    if (curatorFrameworkOp.checkExists(failedNodePath)) {
      executionInfo.setExecutorName(executorName);
      executionInfo.setStatus(ExecutionStatus.getExecutionStatus(false, false, true, false, isEnabledReport));
      return;
    }
    String failoverNodePath = JobNodePath.getFailoverNodePath(jobName, shardItem);
    String failoverData = curatorFrameworkOp.getData(failoverNodePath);
    if (failoverData != null) {
      executionInfo.setExecutorName(failoverData);
      executionInfo.setFailover(true);
      executionInfo.setStatus(ExecutionStatus.getExecutionStatus(false, false, false, false, isEnabledReport));
      return;
    }
    String timeoutNodePath = JobNodePath.getTimeoutNodePath(jobName, shardItem);
    if (curatorFrameworkOp.checkExists(timeoutNodePath)) {
      executionInfo.setExecutorName(executorName);
      executionInfo.setStatus(ExecutionStatus.getExecutionStatus(false, false, false, true, isEnabledReport));
      return;
    }
  }

  private Map<String, String> getItem2ExecutorMap(String jobName, CuratorRepository.CuratorFrameworkOp curatorFrameworkOp) {
    String serverNodePath = JobNodePath.getServerNodePath(jobName);
    List<String> servers = curatorFrameworkOp.getChildren(serverNodePath);
    if (servers == null || servers.size() == 0) {
      return Maps.newHashMap();
    }
    Map<String, String> resultMap = new HashMap<>();
    for (String server : servers) {
      String shardingData = curatorFrameworkOp.getData(JobNodePath.getServerSharding(jobName, server));
      if (StringUtils.isBlank(shardingData)) {
        continue;
      }
      String[] shardingValues = shardingData.split(",");
      for (String value : shardingValues) {
        if (StringUtils.isBlank(value)) {
          continue;
        }
        resultMap.put(value.trim(), server);
      }
    }
    return resultMap;
  }
}