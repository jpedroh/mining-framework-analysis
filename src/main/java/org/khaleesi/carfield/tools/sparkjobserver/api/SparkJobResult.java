package org.khaleesi.carfield.tools.sparkjobserver.api;
import java.util.HashMap;
import java.util.Map;

/**
 * Presents the information of spark job result, when calling 
 * <code>GET /jobs/&lt;jobId&gt;</code> to a spark job server.
 * 
 * @author bluebreezecf
 * @since 2014-09-15
 *
 */
public class SparkJobResult extends SparkJobBaseInfo {
  private String result;

  private Map<String, Object> extendAttributes = new HashMap<String, Object>();

  SparkJobResult(String contents, String jobId) {
    this.contents = contents;
    setJobId(jobId);
  }

  SparkJobResult(String contents) {
    this(contents, null);
  }

  public String getResult() {
    return result;
  }

  void setResult(String result) {
    this.result = result;
  }

  void putExtendAttribute(String key, Object value) {
    this.extendAttributes.put(key, value);
  }

  public Map<String, Object> getExtendAttributes() {
    return new HashMap<String, Object>(this.extendAttributes);
  }

  /**
	 * {@inheritDoc}
	 */
  @Override public String toString() {
    StringBuffer buff = new StringBuffer("SparkJobResult\n");
    buff.append(contents);
    return buff.toString();
  }

  /**
	 * Judges current <code>SparkJobResult</code> instance represents the 
	 * status information of a asynchronous running spark job or not.
	 * 
	 * @return true indicates it contains asynchronous running status of a
	 *         spark job, false otherwise
	 */
  public boolean containsAsynStatus() {
    return SparkJobBaseInfo.ASYNC_STATUS.contains(getStatus());
  }

  /**
	 * Judges the queried target job doesn't exist or not.
	 * 
	 * @return true indicates the related job doesn't exist, false otherwise
	 */
  public boolean jobNotExists() {
    return SparkJobBaseInfo.INFO_STATUS_ERROR.equals(getStatus()) && getResult() != null && getResult().contains("No such job ID");
  }

  /**
	 * Judges current <code>SparkJobResult</code> instance contains 
	 * error information of a failed spark job or not.
	 * 
	 * @return true indicates it contains error message, false otherwise
	 */
  public boolean containsErrorInfo() {
    return SparkJobBaseInfo.INFO_STATUS_ERROR.equals(getStatus()) && getMessage() != null;
  }

  /**
	 * Judges current <code>SparkJobResult</code> instance contains 
	 * custom-defined extend attributes of result or not
	 * 
	 * @return true indicates it contains custom-defined extend attributes, false otherwise
	 */
  public boolean containsExtendAttributes() {
    return !extendAttributes.isEmpty();
  }
}