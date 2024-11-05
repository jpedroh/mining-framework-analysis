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

public class StubPauseResumeServiceImpl implements PauseResumeService {
  @Override public Long pauseExecution(Long executionId, String branchId, PauseReason reason) {
    return null;
  }

  @Override public void resumeExecution(Long executionId, String branchId, Map<String, Serializable> map) {
  }

  @Override public void injectInterrupts(Long executionId, Map<String, ArrayList<String>> interrupts) {
  }

  @Override public void deleteInterrupts(Long executionId, Map<String, ArrayList<String>> interrupts) {
  }

  @Override public PauseReason writeExecutionObject(Long executionId, String branchId, Execution execution) {
    return null;
  }

  @Override public Set<String> readAllPausedExecutionBranchIds() {
    return null;
  }

  @Override public ExecutionSummary readPausedExecution(Long executionId, String branchId) {
    return null;
  }

  @Override public List<Long> readPauseIds(Long executionId) {
    return null;
  }

  @Override public void createNoRobotGroup(Execution execution, Long pauseId, String branchId) {
  }

  @Override public void deletePauseData(String executionId, String branchId) {
  }

  @Override public void injectInterrupts(Long executionId, Map<String, Set<String>> interrupts) {
  }

  @Override public void deleteInterrupts(Long executionId, Map<String, Set<String>> interrupts) {
  }
}