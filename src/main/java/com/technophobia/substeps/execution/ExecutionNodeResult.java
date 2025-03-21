package com.technophobia.substeps.execution;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author ian
 * 
 */
public class ExecutionNodeResult implements Serializable {

<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/execution/ExecutionNodeResult.java/left.java
  private static final long serialVersionUID = -1444083371334604179L;
=======
  private Long completedAt;
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/execution/ExecutionNodeResult.java/right.java


  private ExecutionResult result = ExecutionResult.NOT_RUN;

  private Throwable thrown = null;


<<<<<<< /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/execution/ExecutionNodeResult.java/left.java
  private final long executionNodeId;
=======
  private Long startedAt;
>>>>>>> /usr/src/app/output/technophobia/substeps-core/07b94728d642a789e11b45c284432544e6a013a9/src/main/java/com/technophobia/substeps/execution/ExecutionNodeResult.java/right.java


  public ExecutionNodeResult(final long id) {
    this.executionNodeId = id;
  }

  public String getStackTrace() {
    if (thrown != null) {
      final StringWriter sw = new StringWriter();
      final PrintWriter pw = new PrintWriter(sw);
      thrown.printStackTrace(pw);
      pw.close();
      return sw.toString();
    } else {
      return "";
    }
  }

  /**
     * @return the result
     */
  public ExecutionResult getResult() {
    return result;
  }

  /**
     * @param result
     *            the result to set
     */
  public void setResult(final ExecutionResult result) {
    this.result = result;
  }

  /**
     * @return the failureStackTrace
     */
  public Throwable getThrown() {
    return thrown;
  }

  /**
     * @param failureStackTrace
     *            the failureStackTrace to set
     */
  public void setThrown(final Throwable failureStackTrace) {
    thrown = failureStackTrace;
  }

  /**
     * @param theException
     */
  public void setFailed(final Throwable theException) {
    result = ExecutionResult.FAILED;
    thrown = theException;
    recordComplete();
  }

  /**
	 * 
	 */
  public void setFinished() {
    result = ExecutionResult.PASSED;
    recordComplete();
  }

  /**
	 * 
	 */
  public void setStarted() {
    result = ExecutionResult.RUNNING;
    startedAt = System.currentTimeMillis();
  }

  /**
     * @param t
     */
  public void setFailedToParse(final Throwable t) {
    result = ExecutionResult.PARSE_FAILURE;
    thrown = t;
  }

  /**
     * @param theException
     */
  public void setSetupTearFailure(final Throwable t) {
    result = ExecutionResult.SETUP_TEARDOWN_FAILURE;
    thrown = t;
  }

  /**
     * @return the executionNodeId
     */
  public long getExecutionNodeId() {
    return executionNodeId;
  }

  public Long getRunningDuration() {
    return startedAt != null && completedAt != null ? completedAt - startedAt : null;
  }

  private void recordComplete() {
    completedAt = System.currentTimeMillis();
  }
}