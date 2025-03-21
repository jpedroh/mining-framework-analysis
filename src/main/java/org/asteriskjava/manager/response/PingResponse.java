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

  private 
<<<<<<< /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/left.java
  String
=======
  Double
>>>>>>> /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/right.java
   timestamp;

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
  public 
<<<<<<< /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/left.java
  String
=======
  Double
>>>>>>> /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/right.java
   getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(
<<<<<<< /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/left.java
  String
=======
  Double
>>>>>>> /usr/src/app/output/srt/asterisk-java/d78b6f9d5d466bf8d509b4ff2eaf96d33eaf4fdc/src/main/java/org/asteriskjava/manager/response/PingResponse.java/right.java
   timestamp) {
    this.timestamp = timestamp;
  }
}