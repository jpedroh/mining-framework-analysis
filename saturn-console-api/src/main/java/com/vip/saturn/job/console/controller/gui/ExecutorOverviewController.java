package com.vip.saturn.job.console.controller.gui;
import com.google.common.collect.Lists;
import com.vip.saturn.job.console.aop.annotation.Audit;
import com.vip.saturn.job.console.aop.annotation.AuditParam;
import com.vip.saturn.job.console.controller.SuccessResponseEntity;
import com.vip.saturn.job.console.domain.RequestResult;
import com.vip.saturn.job.console.domain.ServerBriefInfo;
import com.vip.saturn.job.console.domain.ServerStatus;
import com.vip.saturn.job.console.exception.SaturnJobConsoleException;
import com.vip.saturn.job.console.exception.SaturnJobConsoleGUIException;
import com.vip.saturn.job.console.service.ExecutorService;
import com.vip.saturn.job.console.utils.Permissions;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import java.util.List;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * Executor overview related operations.
 *
 * @author kfchu
 */
@RequestMapping(value = "/console/namespaces/{namespace:.+}/executors") public class ExecutorOverviewController extends AbstractGUIController {
  private static final Logger log = LoggerFactory.getLogger(ExecutorOverviewController.class);

  private static final String TRAFFIC_OPERATION_EXTRACT = "extract";

  private static final String TRAFFIC_OPERATION_RECOVER = "recover";

  @Resource private ExecutorService executorService;

  /**
	 * 获取域下所有executor基本信息
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @GetMapping public SuccessResponseEntity getExecutors(final HttpServletRequest request, @PathVariable String namespace, @RequestParam(required = false) String status) throws SaturnJobConsoleException {
    if ("online".equalsIgnoreCase(status)) {
      return new SuccessResponseEntity(executorService.getExecutors(namespace, ServerStatus.ONLINE));
    }
    return new SuccessResponseEntity(executorService.getExecutors(namespace));
  }

  /**
	 * 获取executor被分配的作业分片信息
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @GetMapping(value = "/{executorName}/allocation") public SuccessResponseEntity getExecutorAllocation(final HttpServletRequest request, @PathVariable String namespace, @PathVariable String executorName) throws SaturnJobConsoleException {
    return new SuccessResponseEntity(executorService.getExecutorAllocation(namespace, executorName));
  }

  /**
	 * 一键重排
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorShardAllAtOnce) @PostMapping(value = "/shardAll") public SuccessResponseEntity shardAll(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace) throws SaturnJobConsoleException {
    executorService.shardAll(namespace);
    return new SuccessResponseEntity();
  }

  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorExtractOrRecoverTraffic) @PostMapping(value = "/{executorName}/traffic") public SuccessResponseEntity extractOrRecoverTraffic(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorName") @PathVariable String executorName, @AuditParam(value = "operation") @RequestParam String operation) throws SaturnJobConsoleException {
    extractOrRecoverTraffic(namespace, executorName, operation);
    return new SuccessResponseEntity();
  }

  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorBatchExtractOrRecoverTraffic) @PostMapping(value = "/traffic") public SuccessResponseEntity batchExtractOrRecoverTraffic(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorNames") @RequestParam List<String> executorNames, @AuditParam(value = "operation") @RequestParam String operation) throws SaturnJobConsoleException {
    List<String> success2ExtractOrRecoverTrafficExecutors = Lists.newArrayList();
    List<String> fail2ExtractOrRecoverTrafficExecutors = Lists.newArrayList();
    for (String executorName : executorNames) {
      try {
        extractOrRecoverTraffic(namespace, executorName, operation);
        success2ExtractOrRecoverTrafficExecutors.add(executorName);
      } catch (Exception e) {
        log.warn("exception happens during extract or recover traffic of executor:" + executorName, e);
        fail2ExtractOrRecoverTrafficExecutors.add(executorName);
      }
    }
    if (!fail2ExtractOrRecoverTrafficExecutors.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("\u64cd\u4f5c\u6210\u529f\u7684executor:").append(success2ExtractOrRecoverTrafficExecutors).append("\uff0c").append("\u64cd\u4f5c\u5931\u8d25\u7684executor:").append(fail2ExtractOrRecoverTrafficExecutors);
      throw new SaturnJobConsoleGUIException(message.toString());
    }
    return new SuccessResponseEntity();
  }

  private void extractOrRecoverTraffic(String namespace, String executorName, String operation) throws SaturnJobConsoleException {
    if (TRAFFIC_OPERATION_EXTRACT.equals(operation)) {
      executorService.extractTraffic(namespace, executorName);
    } else {
      if (TRAFFIC_OPERATION_RECOVER.equals(operation)) {
        executorService.recoverTraffic(namespace, executorName);
      } else {
        throw new SaturnJobConsoleGUIException("operation " + operation + "\u4e0d\u652f\u6301");
      }
    }
  }

  /**
	 * 移除executor
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorRemove) @DeleteMapping(value = "/{executorName}") public SuccessResponseEntity removeExecutor(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorName") @PathVariable String executorName) throws SaturnJobConsoleException {
    checkExecutorStatus(namespace, executorName, ServerStatus.OFFLINE, "Executor\u5728\u7ebf\uff0c\u4e0d\u80fd\u79fb\u9664");
    executorService.removeExecutor(namespace, executorName);
    return new SuccessResponseEntity();
  }

  /**
	 * 批量移除executor
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorBatchRemove) @DeleteMapping public SuccessResponseEntity batchRemoveExecutors(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorNames") @RequestParam List<String> executorNames) throws SaturnJobConsoleException {
    List<String> success2RemoveExecutors = Lists.newArrayList();
    List<String> fail2RemoveExecutors = Lists.newArrayList();
    for (String executorName : executorNames) {
      try {
        checkExecutorStatus(namespace, executorName, ServerStatus.OFFLINE, "Executor\u5728\u7ebf\uff0c\u4e0d\u80fd\u79fb\u9664");
        executorService.removeExecutor(namespace, executorName);
        success2RemoveExecutors.add(executorName);
      } catch (Exception e) {
        log.warn("exception happens during remove executor:" + executorName, e);
        fail2RemoveExecutors.add(executorName);
      }
    }
    if (!fail2RemoveExecutors.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("\u5220\u9664\u6210\u529f\u7684executor:").append(success2RemoveExecutors).append("\uff0c").append("\u5220\u9664\u5931\u8d25\u7684executor:").append(fail2RemoveExecutors);
      throw new SaturnJobConsoleGUIException(message.toString());
    }
    return new SuccessResponseEntity();
  }

  private void checkExecutorStatus(String namespace, String executorName, ServerStatus status, String errMsg) throws SaturnJobConsoleException {
    ServerBriefInfo executorInfo = executorService.getExecutor(namespace, executorName);
    if (executorInfo == null) {
      throw new SaturnJobConsoleGUIException("Executor\u4e0d\u5b58\u5728");
    }
    if (status != executorInfo.getStatus()) {
      throw new SaturnJobConsoleGUIException(errMsg);
    }
  }

  /**
	 * 一键Dump，包括threadump和gc.log。
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorDump) @PostMapping(value = "/{executorName}/dump") public SuccessResponseEntity dump(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorName") @PathVariable String executorName) throws SaturnJobConsoleException {
    checkExecutorStatus(namespace, executorName, ServerStatus.ONLINE, "Executor\u5fc5\u987b\u5728\u7ebf\u624d\u53ef\u4ee5dump");
    executorService.dump(namespace, executorName);
    return new SuccessResponseEntity();
  }

  /**
	 * 一键重启。
	 */
  @ApiResponses(value = { @ApiResponse(code = 200, message = "Success/Fail", response = RequestResult.class) }) @Audit @RequiresPermissions(value = Permissions.executorRestart) @PostMapping(value = "/{executorName}/restart") public SuccessResponseEntity restart(final HttpServletRequest request, @AuditParam(value = "namespace") @PathVariable String namespace, @AuditParam(value = "executorName") @PathVariable String executorName) throws SaturnJobConsoleException {
    checkExecutorStatus(namespace, executorName, ServerStatus.ONLINE, "Executor\u5fc5\u987b\u5728\u7ebf\u624d\u53ef\u4ee5\u91cd\u542f");
    executorService.restart(namespace, executorName);
    return new SuccessResponseEntity();
  }
}