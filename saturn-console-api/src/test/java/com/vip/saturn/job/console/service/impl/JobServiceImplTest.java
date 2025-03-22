package com.vip.saturn.job.console.service.impl;
import com.vip.saturn.job.console.domain.*;
import com.vip.saturn.job.console.domain.ExecutionInfo.ExecutionStatus;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.mybatis.entity.JobConfig4DB;
import com.vip.saturn.job.console.mybatis.service.CurrentJobConfigService;
import com.vip.saturn.job.console.repository.zookeeper.CuratorRepository.CuratorFrameworkOp;
import com.vip.saturn.job.console.service.RegistryCenterService;
import com.vip.saturn.job.console.service.SystemConfigService;
import com.vip.saturn.job.console.service.helper.SystemConfigProperties;
import com.vip.saturn.job.console.utils.JobNodePath;
import com.vip.saturn.job.console.utils.SaturnConstants;
import com.vip.saturn.job.sharding.node.SaturnExecutorsNode;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import static com.vip.saturn.job.console.service.impl.JobServiceImpl.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(value = MockitoJUnitRunner.class) public class JobServiceImplTest {
  @Mock private CuratorFrameworkOp curatorFrameworkOp;

  @Mock private CurrentJobConfigService currentJobConfigService;

  @Mock private RegistryCenterService registryCenterService;

  @Mock private SystemConfigService systemConfigService;

  @InjectMocks private JobServiceImpl jobService;

  private String namespace = "saturn-job-test.vip.com";

  private String jobName = "testJob";

  private String userName = "weicong01.li";

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test public void testGetGroup() {
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(new JobConfig4DB()));
    assertEquals(jobService.getGroups(namespace).size(), 1);
  }

  @Test public void testGetDependingJobs() throws SaturnJobConsoleException {
    String dependedJob = "dependedJob";
    String dependingJob = "dependingJob";
    JobConfig4DB dependedJobConfig = new JobConfig4DB();
    dependedJobConfig.setJobName(dependedJob);
    dependedJobConfig.setDependencies(dependingJob);
    JobConfig4DB dependingJobConfig = new JobConfig4DB();
    dependingJobConfig.setJobName(dependingJob);
    dependingJobConfig.setEnabled(Boolean.TRUE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, dependedJob)).thenReturn(dependedJobConfig);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(dependedJobConfig, dependingJobConfig));
    List<DependencyJob> dependingJobs = jobService.getDependingJobs(namespace, dependedJob);
    assertEquals(dependingJobs.size(), 1);
    assertEquals(dependingJobs.get(0).getJobName(), dependingJob);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, dependedJob)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08%s\uff09\u4f9d\u8d56\u7684\u6240\u6709\u4f5c\u4e1a\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", dependedJob));
    jobService.getDependingJobs(namespace, dependedJob);
  }

  @Test public void testGetDependedJobs() throws SaturnJobConsoleException {
    String dependedJob = "dependedJob";
    String dependingJob = "dependingJob";
    JobConfig4DB dependedJobConfig = new JobConfig4DB();
    dependedJobConfig.setJobName(dependedJob);
    dependedJobConfig.setEnabled(Boolean.TRUE);
    dependedJobConfig.setDependencies(dependingJob);
    JobConfig4DB dependingJobConfig = new JobConfig4DB();
    dependingJobConfig.setJobName(dependingJob);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, dependingJob)).thenReturn(dependingJobConfig);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(dependedJobConfig, dependingJobConfig));
    List<DependencyJob> dependingJobs = jobService.getDependedJobs(namespace, dependingJob);
    assertEquals(dependingJobs.size(), 1);
    assertEquals(dependingJobs.get(0).getJobName(), dependedJob);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, dependingJob)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u83b7\u53d6\u4f9d\u8d56\u8be5\u4f5c\u4e1a\uff08%s\uff09\u7684\u6240\u6709\u4f5c\u4e1a\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", dependingJob));
    jobService.getDependedJobs(namespace, dependingJob);
  }

  @Test public void testEnableJobFailByJobNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u542f\u7528\u8be5\u4f5c\u4e1a\uff08%s\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.enableJob(namespace, jobName, userName);
  }

  @Test public void testEnableJobFailByJobHasEnabled() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.TRUE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u5df2\u7ecf\u5904\u4e8e\u542f\u7528\u72b6\u6001", jobName));
    jobService.enableJob(namespace, jobName, userName);
  }

  @Test public void testEnabledJobFailByJobHasFinished() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(false);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(true);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u542f\u7528\u8be5\u4f5c\u4e1a\uff08%s\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5904\u4e8eSTOPPED\u72b6\u6001", jobName));
    jobService.enableJob(namespace, jobName, userName);
  }

  @Test public void testEnabledJobSuccess() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
    jobService.enableJob(namespace, jobName, userName);
    verify(currentJobConfigService).updateByPrimaryKey(jobConfig4DB);
    verify(curatorFrameworkOp).update(eq(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_ENABLED)), eq(true));
  }

  @Test public void testDisableJobFailByJobNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u7981\u7528\u8be5\u4f5c\u4e1a\uff08%s\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.disableJob(namespace, jobName, userName);
  }

  @Test public void testDisableJobFailByJobHasDisabled() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a\uff08%s\uff09\u5df2\u7ecf\u5904\u4e8e\u7981\u7528\u72b6\u6001", jobName));
    jobService.disableJob(namespace, jobName, userName);
  }

  @Test public void testDisableJobFailByUpdateError() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.TRUE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(currentJobConfigService.updateByPrimaryKey(jobConfig4DB)).thenThrow(new SaturnJobConsoleException("update error"));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("update error");
    jobService.disableJob(namespace, jobName, userName);
  }

  @Test public void testDisableJobSuccess() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.TRUE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    jobService.disableJob(namespace, jobName, userName);
    verify(currentJobConfigService).updateByPrimaryKey(jobConfig4DB);
    verify(curatorFrameworkOp).update(eq(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_ENABLED)), eq(false));
  }

  @Test public void testRemoveJobFailByNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a\uff08%s\uff09\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.removeJob(namespace, jobName);
  }

  @Test public void testRemoveJobFailByNotStopped() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.TRUE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a(%s)\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5904\u4e8eSTOPPED\u72b6\u6001", jobName));
    jobService.removeJob(namespace, jobName);
  }

  @Test public void testRemoveJobFailByLimitTime() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
    Stat stat = new Stat();
    stat.setCtime(System.currentTimeMillis());
    when(curatorFrameworkOp.getStat(eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u5220\u9664\u8be5\u4f5c\u4e1a(%s)\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u521b\u5efa\u65f6\u95f4\u8ddd\u79bb\u73b0\u5728\u4e0d\u8d85\u8fc7%d\u5206\u949f", jobName, SaturnConstants.JOB_CAN_BE_DELETE_TIME_LIMIT / 60000));
    jobService.removeJob(namespace, jobName);
  }

  @Test public void testRemoveJobFailByDeleteDBError() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setId(1L);
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
    Stat stat = new Stat();
    stat.setCtime(System.currentTimeMillis() - (3 * 60 * 1000));
    when(curatorFrameworkOp.getStat(eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
    when(currentJobConfigService.deleteByPrimaryKey(jobConfig4DB.getId())).thenThrow(new SaturnJobConsoleException("delete error"));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("delete error");
    jobService.removeJob(namespace, jobName);
  }

  @Test public void testRemoveJobSuccess() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setId(1L);
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(Boolean.FALSE);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "completed")))).thenReturn(true);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getExecutionNodePath(jobName, "1", "running")))).thenReturn(false);
    Stat stat = new Stat();
    stat.setCtime(System.currentTimeMillis() - (3 * 60 * 1000));
    when(curatorFrameworkOp.getStat(eq(JobNodePath.getJobNodePath(jobName)))).thenReturn(stat);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getConfigNodePath(jobName, "toDelete")))).thenReturn(true);
    jobService.removeJob(namespace, jobName);
    verify(currentJobConfigService).deleteByPrimaryKey(jobConfig4DB.getId());
    verify(curatorFrameworkOp).deleteRecursive(eq(JobNodePath.getConfigNodePath(jobName, "toDelete")));
    verify(curatorFrameworkOp).create(eq(JobNodePath.getConfigNodePath(jobName, "toDelete")));
  }

  @Test public void testGetCandidateExecutorsFailByNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08%s\uff09\u53ef\u9009\u62e9\u7684\u4f18\u5148Executor\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.getCandidateExecutors(namespace, jobName);
  }

  @Test public void testGetCandidaExecutorsByExecutorPathNotExist() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(false);
    assertTrue(jobService.getCandidateExecutors(namespace, jobName).isEmpty());
  }

  @Test public void testGetCandidateExecutors() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(true);
    String executor = "executor";
    when(curatorFrameworkOp.getChildren(eq(SaturnExecutorsNode.getExecutorsNodePath()))).thenReturn(Lists.newArrayList(executor));
    assertEquals(jobService.getCandidateExecutors(namespace, jobName).size(), 1);
    when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PREFER_LIST))).thenReturn(true);
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PREFER_LIST))).thenReturn("preferExecutor2,@preferExecutor3");
    assertEquals(jobService.getCandidateExecutors(namespace, jobName).size(), 3);
  }

  @Test public void testSetPreferListFailByNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8bbe\u7f6e\u8be5\u4f5c\u4e1a\uff08%s\uff09\u4f18\u5148Executor\u5931\u8d25\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.setPreferList(namespace, jobName, "preferList", userName);
  }

  @Test public void testSetPreferListFailByLocalModeNotStop() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    jobConfig4DB.setLocalMode(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u542f\u7528\u72b6\u6001\u7684\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a(%s)\uff0c\u4e0d\u80fd\u8bbe\u7f6e\u4f18\u5148Executor\uff0c\u8bf7\u5148\u7981\u7528\u5b83", jobName));
    jobService.setPreferList(namespace, jobName, "preferList", userName);
  }

  @Test public void testSetPreferListSuccess() throws Exception {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(false);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    String preferList = "preferList";
    jobService.setPreferList(namespace, jobName, preferList, userName);
    verify(currentJobConfigService).updateNewAndSaveOld2History(any(JobConfig4DB.class), eq(jobConfig4DB), eq(userName));
    verify(curatorFrameworkOp).update(eq(SaturnExecutorsNode.getJobConfigPreferListNodePath(jobName)), eq(preferList));
    verify(curatorFrameworkOp).delete(eq(SaturnExecutorsNode.getJobConfigForceShardNodePath(jobName)));
    verify(curatorFrameworkOp).create(eq(SaturnExecutorsNode.getJobConfigForceShardNodePath(jobName)));
  }

  @Test public void testAddJobFailByWithoutJobName() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f5c\u4e1a\u540d\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByJobNameInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName("!@#aa");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f5c\u4e1a\u540d\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByDependingJobNameInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setDependencies("12!@@");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f9d\u8d56\u7684\u4f5c\u4e1a\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3001\u82f1\u6587\u9017\u53f7,");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByWithoutJobType() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f5c\u4e1a\u7c7b\u578b\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByJobTypeInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType("unknown");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f5c\u4e1a\u7c7b\u578b\u672a\u77e5");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByJavaJobWithoutClass() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.JAVA_JOB.name());
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByPassiveJavaJobWithoutClass() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.PASSIVE_JAVA_JOB.name());
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByVMSJobWithoutClass() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByShellJobWithoutCron() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.SHELL_JOB.name());
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8ecron\u4f5c\u4e1a\uff0ccron\u8868\u8fbe\u5f0f\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByShellJobCronInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.SHELL_JOB.name());
    jobConfig.setCron("xxxxx");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("cron\u8868\u8fbe\u5f0f\u8bed\u6cd5\u6709\u8bef");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByMsgJobWithoutQueue() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8e\u6d88\u606f\u4f5c\u4e1a\uff0cqueue\u5fc5\u586b");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByLocalModeJobWithoutShardingItem() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(true);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u586b\u3002");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByNoLocalModeJobWithoutShardingItem() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(false);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5206\u7247\u6570\u4e0d\u80fd\u4e3a\u7a7a\uff0c\u5e76\u4e14\u4e0d\u80fd\u5c0f\u4e8e1");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByNoLocalModeJoShardingItemInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(false);
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("001");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u5206\u7247\u53c2\u6570\'%s\'\u683c\u5f0f\u6709\u8bef", "001"));
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByNoLocalModeJoShardingItemInvalidNumber() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(false);
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("x=x");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u5206\u7247\u53c2\u6570\'%s\'\u683c\u5f0f\u6709\u8bef", "x=x"));
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByAddSystemJob() throws SaturnJobConsoleException {
    JobConfig jobConfig = createValidJob();
    jobConfig.setJobMode(JobMode.SYSTEM_PREFIX);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4f5c\u4e1a\u6a21\u5f0f\u6709\u8bef\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7cfb\u7edf\u4f5c\u4e1a");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByJobIsExist() throws SaturnJobConsoleException {
    JobConfig jobConfig = createValidJob();
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(eq(namespace), eq(jobName))).thenReturn(jobConfig4DB);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u5df2\u7ecf\u5b58\u5728", jobName));
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByLimitNum() throws SaturnJobConsoleException {
    JobConfig jobConfig = createValidJob();
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    when(systemConfigService.getIntegerValue(eq(SystemConfigProperties.MAX_JOB_NUM), eq(100))).thenReturn(1);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u603b\u4f5c\u4e1a\u6570\u8d85\u8fc7\u6700\u5927\u9650\u5236(%d)\uff0c\u4f5c\u4e1a\u540d%s\u521b\u5efa\u5931\u8d25", 1, jobName));
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobSuccess() throws Exception {
    JobConfig jobConfig = createValidJob();
    when(registryCenterService.getCuratorFrameworkOp(eq(namespace))).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getJobNodePath(jobConfig.getJobName())))).thenReturn(true);
    jobService.addJob(namespace, jobConfig, userName);
    verify(curatorFrameworkOp).deleteRecursive(eq(JobNodePath.getJobNodePath(jobConfig.getJobName())));
    verify(currentJobConfigService).create(any(JobConfig4DB.class));
  }

  @Test public void testCopyJobSuccess() throws Exception {
    JobConfig jobConfig = createValidJob();
    when(registryCenterService.getCuratorFrameworkOp(eq(namespace))).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(eq(JobNodePath.getJobNodePath(jobConfig.getJobName())))).thenReturn(true);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    when(currentJobConfigService.findConfigByNamespaceAndJobName(eq(namespace), eq("copyJob"))).thenReturn(jobConfig4DB);
    jobService.copyJob(namespace, jobConfig, "copyJob", userName);
    verify(curatorFrameworkOp).deleteRecursive(eq(JobNodePath.getJobNodePath(jobConfig.getJobName())));
    verify(currentJobConfigService).create(any(JobConfig4DB.class));
  }

  private JobConfig createValidJob() {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(false);
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=1");
    return jobConfig;
  }

  @Test public void testAddJobFailByNoLocalModeJoShardingItemLess() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(false);
    jobConfig.setShardingTotalCount(2);
    jobConfig.setShardingItemParameters("0=1");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5206\u7247\u53c2\u6570\u4e0d\u80fd\u5c0f\u4e8e\u5206\u7247\u603b\u6570");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByLocalModeJobShardingItemInvalid() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setQueueName("queue");
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(true);
    jobConfig.setShardingItemParameters("test");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u987b\u5305\u542b\u5982*=xx\u3002");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByLocalModeJobHasDownStream() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setLocalMode(true);
    jobConfig.setShardingItemParameters("*=xx");
    jobConfig.setDownStream("test");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u975e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u624d\u80fd\u914d\u7f6e\u4e0b\u6e38\u4f5c\u4e1a");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButShardingTotalCountIsNotOne() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(2);
    jobConfig.setShardingItemParameters("0=0,1=1");
    jobConfig.setDownStream("test");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u5206\u7247\u6570\u4e3a1\uff0c\u624d\u80fd\u914d\u7f6e\u4e0b\u6e38\u4f5c\u4e1a");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButIsSelf() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=0");
    jobConfig.setDownStream(jobName);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4e0b\u6e38\u4f5c\u4e1a(" + jobName + ")\u4e0d\u80fd\u662f\u8be5\u4f5c\u4e1a\u672c\u8eab");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButNotExisting() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=0");
    jobConfig.setDownStream("test");
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4e0b\u6e38\u4f5c\u4e1a(test)\u4e0d\u5b58\u5728");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButIsAncestor() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=0");
    jobConfig.setDownStream("test1");
    JobConfig4DB test1 = new JobConfig4DB();
    test1.setJobName("test1");
    test1.setDownStream(jobName);
    when(currentJobConfigService.findConfigsByNamespace(eq(namespace))).thenReturn(Arrays.asList(test1));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4e0b\u6e38\u4f5c\u4e1a(test1)\u4e0d\u80fd\u662f\u8be5\u4f5c\u4e1a\u7684\u7956\u5148");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButIsNotPassive() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=0");
    jobConfig.setDownStream("test1");
    JobConfig4DB test1 = new JobConfig4DB();
    test1.setJobName("test1");
    when(currentJobConfigService.findConfigsByNamespace(eq(namespace))).thenReturn(Arrays.asList(test1));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4e0b\u6e38\u4f5c\u4e1a(test1)\u4e0d\u662f\u88ab\u52a8\u4f5c\u4e1a");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testAddJobFailByHasDownStreamButIsAncestor2() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    jobConfig.setJobType(JobType.MSG_JOB.name());
    jobConfig.setJobClass("testCLass");
    jobConfig.setShardingTotalCount(1);
    jobConfig.setShardingItemParameters("0=0");
    jobConfig.setDownStream("test1, test2 ");
    JobConfig4DB test1 = new JobConfig4DB();
    test1.setJobName("test1");
    test1.setJobType(JobType.PASSIVE_JAVA_JOB.name());
    JobConfig4DB test2 = new JobConfig4DB();
    test2.setJobName("test2");
    test2.setJobType(JobType.PASSIVE_JAVA_JOB.name());
    test2.setDownStream(jobName);
    when(currentJobConfigService.findConfigsByNamespace(eq(namespace))).thenReturn(Arrays.asList(test1, test2));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u4e0b\u6e38\u4f5c\u4e1a(test2)\u4e0d\u80fd\u662f\u8be5\u4f5c\u4e1a\u7684\u7956\u5148");
    jobService.addJob(namespace, jobConfig, userName);
  }

  @Test public void testGetUnSystemJobsWithCondition() throws SaturnJobConsoleException {
    String namespace = "ns1";
    String jobName = "testJob";
    int count = 4;
    Map<String, Object> condition = buildCondition(null);
    when(currentJobConfigService.findConfigsByNamespaceWithCondition(eq(namespace), eq(condition), Matchers.<Pageable>anyObject())).thenReturn(buildJobConfig4DBList(namespace, jobName, count));
    assertTrue(jobService.getUnSystemJobsWithCondition(namespace, condition, 1, 25).size() == count);
  }

  @Test public void testGetMaxJobNum() {
    assertEquals(jobService.getMaxJobNum(), 100);
  }

  @Test public void testGetUnSystemJob() {
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(new JobConfig4DB()));
    assertEquals(jobService.getUnSystemJobs(namespace).size(), 1);
  }

  @Test public void testGetUnSystemJobWithConditionAndStatus() throws SaturnJobConsoleException {
    String namespace = "ns1";
    String jobName = "testJob";
    int count = 4;
    List<JobConfig4DB> jobConfig4DBList = buildJobConfig4DBList(namespace, jobName, count);
    Map<String, Object> condition = buildCondition(JobStatus.READY);
    when(currentJobConfigService.findConfigsByNamespaceWithCondition(eq(namespace), eq(condition), Matchers.<Pageable>anyObject())).thenReturn(jobConfig4DBList);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    for (int i = 0; i < count; i++) {
      JobConfig4DB jobConfig4DB = jobConfig4DBList.get(i);
      jobConfig4DB.setEnabled(i % 2 == 1);
      when(currentJobConfigService.findConfigByNamespaceAndJobName(eq(namespace), eq(jobConfig4DB.getJobName()))).thenReturn(jobConfig4DB);
      when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobConfig4DB.getJobName()))).thenReturn(null);
    }
    assertTrue(jobService.getUnSystemJobsWithCondition(namespace, condition, 1, 25).size() == (count / 2));
  }

  @Test public void testGetUnSystemJobNames() {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    assertEquals(jobService.getUnSystemJobs(namespace).size(), 1);
  }

  @Test public void testGetJobName() {
    when(currentJobConfigService.findConfigNamesByNamespace(namespace)).thenReturn(null);
    assertTrue(jobService.getJobNames(namespace).isEmpty());
    when(currentJobConfigService.findConfigNamesByNamespace(namespace)).thenReturn(Lists.newArrayList(jobName));
    assertEquals(jobService.getJobNames(namespace).size(), 1);
  }

  @Test public void testPersistJobFromDb() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    jobService.persistJobFromDB(namespace, jobConfig);
    jobService.persistJobFromDB(jobConfig, curatorFrameworkOp);
  }

  @Test public void testImportFailByWithoutJobName() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u540d\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByJobNameInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName("!@avb");
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u540d\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByWithoutJobType() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u7c7b\u578b\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByUnknownJobType() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn("xxx");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u7c7b\u578b\u672a\u77e5\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByJavaJobWithoutClass() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.JAVA_JOB.name());
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByPassiveJavaJobWithoutClass() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.PASSIVE_JAVA_JOB.name());
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByVMSJobWithoutClass() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8ejava\u4f5c\u4e1a\uff0c\u4f5c\u4e1a\u5b9e\u73b0\u7c7b\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByShellJobWithoutCron() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.SHELL_JOB.name());
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8ecron\u4f5c\u4e1a\uff0ccron\u8868\u8fbe\u5f0f\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByShellJobCronInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.SHELL_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_CRON))).thenReturn("xxxx");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("cron\u8868\u8fbe\u5f0f\u8bed\u6cd5\u6709\u8bef\uff0c"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByWithoutShardingCount() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5206\u7247\u6570\u5fc5\u586b"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByShardingCountInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("xxx");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5206\u7247\u6570\u6709\u8bef"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByShardingCountLess4One() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("0");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5206\u7247\u6570\u4e0d\u80fd\u5c0f\u4e8e1"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByTimeoutSecondsInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_SECONDS))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u8d85\u65f6\uff08Kill\u7ebf\u7a0b/\u8fdb\u7a0b\uff09\u65f6\u95f4\u6709\u8bef\uff0c"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByLocalJobWithoutShardingParam() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u586b\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByLocalJobShardingParamInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5bf9\u4e8e\u672c\u5730\u6a21\u5f0f\u4f5c\u4e1a\uff0c\u5206\u7247\u53c2\u6570\u5fc5\u987b\u5305\u542b\u5982*=xx\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByShardingParamLess4Count() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("2");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u5206\u7247\u53c2\u6570\u4e0d\u80fd\u5c0f\u4e8e\u5206\u7247\u603b\u6570\u3002"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByProcessCountIntervalSecondsInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PROCESS_COUNT_INTERVAL_SECONDS))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u7edf\u8ba1\u5904\u7406\u6570\u636e\u91cf\u7684\u95f4\u9694\u79d2\u6570\u6709\u8bef\uff0c"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByLoadLevelInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOAD_LEVEL))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u8d1f\u8377\u6709\u8bef"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByJobDegreeInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_DEGREE))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u91cd\u8981\u7b49\u7ea7\u6709\u8bef\uff0c"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByJobModeInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_MODE))).thenReturn(JobMode.SYSTEM_PREFIX);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f5c\u4e1a\u6a21\u5f0f\u6709\u8bef\uff0c\u4e0d\u80fd\u6dfb\u52a0\u7cfb\u7edf\u4f5c\u4e1a"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByDependenciesInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_DEPENDENCIES))).thenReturn("!@error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u4f9d\u8d56\u7684\u4f5c\u4e1a\u53ea\u5141\u8bb8\u5305\u542b\uff1a\u6570\u5b570-9\u3001\u5c0f\u5199\u5b57\u7b26a-z\u3001\u5927\u5199\u5b57\u7b26A-Z\u3001\u4e0b\u5212\u7ebf_\u3001\u82f1\u6587\u9017\u53f7,"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByTimeout4AlarmSecondsInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_4_ALARM_SECONDS))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u8d85\u65f6\uff08\u544a\u8b66\uff09\u65f6\u95f4\u6709\u8bef\uff0c"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByTimeZoneInvalid() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIME_ZONE))).thenReturn("error");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u65f6\u533a\u6709\u8bef"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByLocalJobNotSupportFailover() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("*=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("true");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_FAILOVER))).thenReturn("true");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u672c\u5730\u6a21\u5f0f\u4e0d\u652f\u6301failover"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByVMSJobNotSupportFailover() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("*=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_LOCAL_MODE))).thenReturn("false");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_FAILOVER))).thenReturn("true");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u6d88\u606f\u4f5c\u4e1a\u4e0d\u652f\u6301failover"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailByVMSJobNotSupportRerun() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_RERUN))).thenReturn("true");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(StringContains.containsString("\u6d88\u606f\u4f5c\u4e1a\u4e0d\u652f\u6301rerun"));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportFailTotalCountLimit() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    when(systemConfigService.getIntegerValue(SystemConfigProperties.MAX_JOB_NUM, 100)).thenReturn(1);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u603b\u4f5c\u4e1a\u6570\u8d85\u8fc7\u6700\u5927\u9650\u5236(%d)\uff0c\u5bfc\u5165\u5931\u8d25", 1));
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testImportSuccess() throws SaturnJobConsoleException, IOException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.MSG_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_CLASS))).thenReturn("vip");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_ITEM_PARAMETERS))).thenReturn("0=1");
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    when(systemConfigService.getIntegerValue(SystemConfigProperties.MAX_JOB_NUM, 100)).thenReturn(100);
    jobService.importJobs(namespace, data, userName);
  }

  @Test public void testExport() throws SaturnJobConsoleException, IOException, BiffException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigsByNamespace(namespace)).thenReturn(Lists.newArrayList(jobConfig4DB));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    File file = jobService.exportJobs(namespace);
    MultipartFile data = new MockMultipartFile("test.xls", new FileInputStream(file));
    Workbook workbook = Workbook.getWorkbook(data.getInputStream());
    assertNotNull(workbook);
    Sheet[] sheets = workbook.getSheets();
    assertEquals(sheets.length, 1);
  }

  @Test public void testIsJobShardingAllocatedExecutor() throws SaturnJobConsoleException {
    String namespace = "ns1";
    String jobName = "testJob";
    String executor = "executor1";
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
    when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "sharding"))).thenReturn("true");
    assertTrue(jobService.isJobShardingAllocatedExecutor(namespace, jobName));
  }

  @Test public void testGetExecutionStatusSuccessfully() throws Exception {
    String namespace = "ns1";
    String jobName = "jobA";
    String executorName = "exec1";
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(buildJobConfig4DB(namespace, jobName));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    List<String> shardItems = Lists.newArrayList("0", "1", "2", "3", "4");
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(shardItems);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executorName));
    when(curatorFrameworkOp.getData(JobNodePath.getServerSharding(jobName, executorName))).thenReturn("0,1,2,3,4");
    when(curatorFrameworkOp.checkExists(JobNodePath.getEnabledReportNodePath(jobName))).thenReturn(true);
    when(curatorFrameworkOp.getData(JobNodePath.getEnabledReportNodePath(jobName))).thenReturn("true");
    mockExecutionStatusNode(true, false, false, false, false, executorName, jobName, "0");
    mockExecutionStatusNode(false, true, false, false, false, executorName, jobName, "1");
    mockExecutionStatusNode(false, true, false, true, false, executorName, jobName, "2");
    mockExecutionStatusNode(true, false, true, false, false, executorName, jobName, "3");
    mockExecutionStatusNode(false, true, false, false, true, executorName, jobName, "4");
    mockJobMessage(jobName, "0", "this is message");
    mockJobMessage(jobName, "1", "this is message");
    mockJobMessage(jobName, "2", "this is message");
    mockJobMessage(jobName, "3", "this is message");
    mockJobMessage(jobName, "4", "this is message");
    mockTimezone(jobName, "Asia/Shanghai");
    mockExecutionNodeData(jobName, "0", "lastBeginTime", "0");
    mockExecutionNodeData(jobName, "0", "nextFireTime", "2000");
    mockExecutionNodeData(jobName, "0", "lastCompleteTime", "1000");
    mockExecutionNodeData(jobName, "1", "lastBeginTime", "0");
    mockExecutionNodeData(jobName, "1", "nextFireTime", "2000");
    mockExecutionNodeData(jobName, "1", "lastCompleteTime", "1000");
    mockExecutionNodeData(jobName, "2", "lastBeginTime", "0");
    mockExecutionNodeData(jobName, "2", "nextFireTime", "2000");
    mockExecutionNodeData(jobName, "2", "lastCompleteTime", "1000");
    mockExecutionNodeData(jobName, "3", "lastBeginTime", "0");
    mockExecutionNodeData(jobName, "3", "nextFireTime", "2000");
    mockExecutionNodeData(jobName, "3", "lastCompleteTime", "1000");
    mockExecutionNodeData(jobName, "4", "nextFireTime", "2000");
    mockExecutionNodeData(jobName, "4", "lastCompleteTime", "1000");
    List<ExecutionInfo> result = jobService.getExecutionStatus(namespace, jobName);
    assertEquals("size should be 5", 5, result.size());
    ExecutionInfo executionInfo = result.get(0);
    assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
    assertEquals("jobName not equal", jobName, executionInfo.getJobName());
    assertEquals("status not equal", ExecutionStatus.RUNNING, executionInfo.getStatus());
    assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
    assertFalse("failover should be false", executionInfo.getFailover());
    assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
    assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
    assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
    executionInfo = result.get(1);
    assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
    assertEquals("jobName not equal", jobName, executionInfo.getJobName());
    assertEquals("status not equal", ExecutionStatus.COMPLETED, executionInfo.getStatus());
    assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
    assertFalse("failover should be false", executionInfo.getFailover());
    assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
    assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
    assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
    executionInfo = result.get(2);
    assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
    assertEquals("jobName not equal", jobName, executionInfo.getJobName());
    assertEquals("status not equal", ExecutionStatus.FAILED, executionInfo.getStatus());
    assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
    assertFalse("failover should be false", executionInfo.getFailover());
    assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
    assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
    assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
    executionInfo = result.get(3);
    assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
    assertEquals("jobName not equal", jobName, executionInfo.getJobName());
    assertEquals("status not equal", ExecutionStatus.RUNNING, executionInfo.getStatus());
    assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
    assertTrue("failover should be false", executionInfo.getFailover());
    assertEquals("lastbeginTime not equal", "1970-01-01 08:00:00", executionInfo.getLastBeginTime());
    assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
    assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
    executionInfo = result.get(4);
    assertEquals("executorName not equal", executorName, executionInfo.getExecutorName());
    assertEquals("jobName not equal", jobName, executionInfo.getJobName());
    assertEquals("status not equal", ExecutionStatus.TIMEOUT, executionInfo.getStatus());
    assertEquals("jobMsg not equal", "this is message", executionInfo.getJobMsg());
    assertFalse("failover should be false", executionInfo.getFailover());
    assertEquals("nextFireTime not equal", "1970-01-01 08:00:02", executionInfo.getNextFireTime());
    assertEquals("lastCompleteTime not equal", "1970-01-01 08:00:01", executionInfo.getLastCompleteTime());
  }

  private void mockExecutionNodeData(String jobName, String item, String nodeName, String data) {
    when(curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, item, nodeName))).thenReturn(data);
  }

  private void mockJobMessage(String jobName, String item, String msg) {
    when(curatorFrameworkOp.getData(JobNodePath.getExecutionNodePath(jobName, item, "jobMsg"))).thenReturn(msg);
  }

  private void mockTimezone(String jobName, String timezone) {
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, "timeZone"))).thenReturn(timezone);
  }

  private void mockExecutionStatusNode(boolean isRunning, boolean isCompleted, boolean isFailover, boolean isFailed, boolean isTimeout, String executorName, String jobName, String jobItem) {
    if (isRunning) {
      when(curatorFrameworkOp.getData(JobNodePath.getRunningNodePath(jobName, jobItem))).thenReturn(executorName);
    }
    if (isCompleted) {
      when(curatorFrameworkOp.getData(JobNodePath.getCompletedNodePath(jobName, jobItem))).thenReturn(executorName);
    }
    if (isFailover) {
      when(curatorFrameworkOp.getData(JobNodePath.getFailoverNodePath(jobName, jobItem))).thenReturn(executorName);
      when(curatorFrameworkOp.getMtime(JobNodePath.getFailoverNodePath(jobName, jobItem))).thenReturn(1L);
    }
    if (isFailed) {
      when(curatorFrameworkOp.checkExists(JobNodePath.getFailedNodePath(jobName, jobItem))).thenReturn(true);
    }
    if (isTimeout) {
      when(curatorFrameworkOp.checkExists(JobNodePath.getTimeoutNodePath(jobName, jobItem))).thenReturn(true);
    }
  }

  private JobConfig4DB buildJobConfig4DB(String namespace, String jobName) {
    JobConfig4DB config = new JobConfig4DB();
    config.setNamespace(namespace);
    config.setJobName(jobName);
    config.setEnabled(true);
    config.setEnabledReport(true);
    config.setJobType(JobType.JAVA_JOB.toString());
    return config;
  }

  private List<JobConfig4DB> buildJobConfig4DBList(String namespace, String jobName, int count) {
    List<JobConfig4DB> config4DBList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      JobConfig4DB config = new JobConfig4DB();
      config.setNamespace(namespace);
      config.setJobName(jobName + i);
      config.setEnabled(true);
      config.setEnabledReport(true);
      config.setJobType(JobType.JAVA_JOB.toString());
      config4DBList.add(config);
    }
    return config4DBList;
  }

  private Map<String, Object> buildCondition(JobStatus jobStatus) {
    Map<String, Object> condition = new HashMap<>();
    condition.put("jobStatus", jobStatus);
    return condition;
  }

  @Test public void testGetJobConfigFromZK() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_JOB_TYPE))).thenReturn(JobType.SHELL_JOB.name());
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_SHARDING_TOTAL_COUNT))).thenReturn("1");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_PROCESS_COUNT_INTERVAL_SECONDS))).thenReturn("100");
    when(curatorFrameworkOp.getData(JobNodePath.getConfigNodePath(jobName, CONFIG_ITEM_TIMEOUT_SECONDS))).thenReturn("100");
    assertNotNull(jobService.getJobConfigFromZK(namespace, jobName));
  }

  @Test public void testGetJobConfigFailByJobNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u4e0d\u5b58\u5728", jobName));
    jobService.getJobConfig(namespace, jobName);
  }

  @Test public void testGetJobConfigSuccess() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(new JobConfig4DB());
    assertNotNull(jobService.getJobConfig(namespace, jobName));
  }

  @Test public void testGetJobStatusFailByJobNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u4e0d\u80fd\u83b7\u53d6\u8be5\u4f5c\u4e1a\uff08%s\uff09\u7684\u72b6\u6001\uff0c\u56e0\u4e3a\u8be5\u4f5c\u4e1a\u4e0d\u5b58\u5728", jobName));
    jobService.getJobStatus(namespace, jobName);
  }

  @Test public void testGetJobStatusSuccess() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    assertEquals(jobService.getJobStatus(namespace, jobName), JobStatus.READY);
    JobConfig jobConfig = new JobConfig();
    jobConfig.setEnabled(true);
    assertEquals(jobService.getJobStatus(namespace, jobConfig), JobStatus.READY);
  }

  @Test public void testGetServerList() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(null);
    assertTrue(jobService.getJobServerList(namespace, jobName).isEmpty());
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
    assertEquals(jobService.getJobServerList(namespace, jobName).size(), 1);
  }

  @Test public void testGetJobConfigVoFailByJobNotExist() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u4e0d\u5b58\u5728", jobName));
    jobService.getJobConfigVo(namespace, jobName);
  }

  @Test public void testGetJobConfigVoSuccess() throws SaturnJobConsoleException {
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(new JobConfig4DB());
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    assertNotNull(jobService.getJobConfigVo(namespace, jobName));
  }

  @Test public void testUpdateJobConfigFailByJobNotExist() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobConfig.getJobName())).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u4e0d\u5b58\u5728", jobName));
    jobService.getJobConfigVo(namespace, jobName);
  }

  @Test public void testUpdateJobConfigSuccess() throws SaturnJobConsoleException {
    JobConfig jobConfig = new JobConfig();
    jobConfig.setJobName(jobName);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobConfig.getJobName())).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    jobService.getJobConfigVo(namespace, jobName);
  }

  @Test public void testGetAllJobNamesFromZK() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.get$JobsNodePath())).thenReturn(null);
    assertTrue(jobService.getAllJobNamesFromZK(namespace).isEmpty());
    when(curatorFrameworkOp.getChildren(JobNodePath.get$JobsNodePath())).thenReturn(Lists.newArrayList(jobName));
    when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(true);
    assertEquals(jobService.getAllJobNamesFromZK(namespace).size(), 1);
  }

  @Test public void testUpdateJobCronFailByCronInvalid() throws SaturnJobConsoleException {
    String cron = "error";
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("The cron expression is invalid: %s", cron));
    jobService.updateJobCron(namespace, jobName, cron, null, userName);
  }

  @Test public void testUpdateJobCronFailByJobNotExist() throws SaturnJobConsoleException {
    String cron = "0 */2 * * * ?";
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(false);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("The job does not exists: %s", jobName));
    jobService.updateJobCron(namespace, jobName, cron, null, userName);
  }

  @Test public void testUpdateJobCronSuccess() throws SaturnJobConsoleException {
    String cron = "0 */2 * * * ?";
    Map<String, String> customContext = Maps.newHashMap();
    customContext.put("test", "test");
    CuratorFramework curatorFramework = mock(CuratorFramework.class);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    when(curatorFrameworkOp.getCuratorFramework()).thenReturn(curatorFramework);
    when(curatorFramework.getNamespace()).thenReturn(namespace);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(JobNodePath.getConfigNodePath(jobName))).thenReturn(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    jobService.updateJobCron(namespace, jobName, cron, customContext, userName);
  }

  @Test public void testGetJobServers() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
    when(curatorFrameworkOp.getData(JobNodePath.getLeaderNodePath(jobName, "election/host"))).thenReturn("127.0.0.1");
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    assertEquals(jobService.getJobServers(namespace, jobName).size(), 1);
  }

  @Test public void testGetJobServerStatus() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
    assertEquals(jobService.getJobServersStatus(namespace, jobName).size(), 1);
  }

  @Test public void testRunAtOneFailByNotReady() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(false);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u4e0d\u5904\u4e8eREADY\u72b6\u6001\uff0c\u4e0d\u80fd\u7acb\u5373\u6267\u884c", jobName));
    jobService.runAtOnce(namespace, jobName);
  }

  @Test public void testRunAtOnceFailByNoExecutor() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(null);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u6ca1\u6709executor\u63a5\u7ba1\u8be5\u4f5c\u4e1a(%s)\uff0c\u4e0d\u80fd\u7acb\u5373\u6267\u884c", jobName));
    jobService.runAtOnce(namespace, jobName);
  }

  @Test public void testRunAtOnceFailByNoOnlineExecutor() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("executor"));
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage("\u6ca1\u6709ONLINE\u7684executor\uff0c\u4e0d\u80fd\u7acb\u5373\u6267\u884c");
    jobService.runAtOnce(namespace, jobName);
  }

  @Test public void testRunAtOnceSuccess() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    String executor = "executor";
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
    when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "status"))).thenReturn("true");
    jobService.runAtOnce(namespace, jobName);
    verify(curatorFrameworkOp).create(JobNodePath.getRunOneTimePath(jobName, executor));
  }

  @Test public void testStopAtOneFailByNotStopping() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u8be5\u4f5c\u4e1a(%s)\u4e0d\u5904\u4e8eSTOPPING\u72b6\u6001\uff0c\u4e0d\u80fd\u7acb\u5373\u7ec8\u6b62", jobName));
    jobService.stopAtOnce(namespace, jobName);
  }

  @Test public void testStopAtOnceFailByNoExecutor() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(false);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, "1", "running"))).thenReturn(true);
    expectedException.expect(SaturnJobConsoleException.class);
    expectedException.expectMessage(String.format("\u6ca1\u6709executor\u63a5\u7ba1\u8be5\u4f5c\u4e1a(%s)\uff0c\u4e0d\u80fd\u7acb\u5373\u7ec8\u6b62", jobName));
    jobService.stopAtOnce(namespace, jobName);
  }

  @Test public void testStopAtOnceSuccess() throws SaturnJobConsoleException {
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(false);
    String executor = "executor";
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    when(curatorFrameworkOp.checkExists(JobNodePath.getExecutionNodePath(jobName, "1", "running"))).thenReturn(true);
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList(executor));
    when(curatorFrameworkOp.getData(JobNodePath.getServerNodePath(jobName, executor, "status"))).thenReturn("true");
    jobService.stopAtOnce(namespace, jobName);
    verify(curatorFrameworkOp).create(JobNodePath.getStopOneTimePath(jobName, executor));
  }

  @Test public void testGetExecutionStatusByJobHasStopped() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setJobName(jobName);
    jobConfig4DB.setEnabled(false);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    assertTrue(jobService.getExecutionStatus(namespace, jobName).isEmpty());
  }

  @Test public void testGetExecutionStatusByWithoutItem() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(null);
    assertTrue(jobService.getExecutionStatus(namespace, jobName).isEmpty());
  }

  @Test public void testGetExecutionStatus() throws SaturnJobConsoleException {
    when(registryCenterService.getCuratorFrameworkOp(namespace)).thenReturn(curatorFrameworkOp);
    JobConfig4DB jobConfig4DB = new JobConfig4DB();
    jobConfig4DB.setEnabled(true);
    when(currentJobConfigService.findConfigByNamespaceAndJobName(namespace, jobName)).thenReturn(jobConfig4DB);
    when(curatorFrameworkOp.getChildren(JobNodePath.getExecutionNodePath(jobName))).thenReturn(Lists.newArrayList("1"));
    when(curatorFrameworkOp.getChildren(JobNodePath.getServerNodePath(jobName))).thenReturn(Lists.newArrayList("server"));
    when(curatorFrameworkOp.getData(JobNodePath.getServerSharding(jobName, "server"))).thenReturn("0");
  }
}