package io.cloudslang.orchestrator.services;
import io.cloudslang.score.facade.execution.ExecutionSummary;
import io.cloudslang.score.facade.execution.PauseReason;
import io.cloudslang.score.facade.entities.Execution;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public interface PauseResumeService {
  Long pauseExecution(Long executionId, String branchId, PauseReason reason);

  void injectInterrupts(Long executionId, Map<String, ArrayList<String>> interrupts);

  void deleteInterrupts(Long executionId, Map<String, ArrayList<String>> interrupts);

  void resumeExecution(Long executionId, String branchId, Map<String, Serializable> map);

  PauseReason writeExecutionObject(Long executionId, String branchId, Execution execution);

  Set<String> readAllPausedExecutionBranchIds();

  ExecutionSummary readPausedExecution(Long executionId, String branchId);

  List<Long> readPauseIds(Long executionId);

  void createNoRobotGroup(Execution execution, Long pauseId, String branchId);

  void deletePauseData(String executionId, String branchId);

  void injectInterrupts(Long executionId, Map<String, Set<String>> interrupts);

  void deleteInterrupts(Long executionId, Map<String, Set<String>> interrupts);
}