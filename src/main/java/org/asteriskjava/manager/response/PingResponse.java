package org.asteriskjava.manager.response;

/**
 * Corresponds to a PingAction and contains an additional (yet useless) ping property.
 *
 * @author srt
 * @see org.asteriskjava.manager.action.PingAction
 */
public class PingResponse extends ManagerResponse {
  private static final long serialVersionUID = 0L;

  private String ping;

  private String timestamp;

  /**
     * Returns always "Pong".
     *
     * @return always "Pong".
     */
  public String getPing() {
    return ping;
  }

  public void setPing(String ping) {
    this.ping = ping;
  }

  /**
     * Timestamp for the response.
     * @return Timestamp as a String, e.g 1353747825.795863
     * @since 1.0.0
     */
  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}