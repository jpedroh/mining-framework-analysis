package org.asteriskjava.manager.event;

/**
 * A HangupEvent is triggered when a channel is hung up.<p>
 * It is implemented in <code>channel.c</code>
 *
 * @author srt
 * @version $Id$
 */
public class HangupEvent extends AbstractChannelEvent {
  /**
     * Serializable version identifier.
     */
  static final long serialVersionUID = 0L;

  private Integer cause;

  private String causeTxt;

  private String 
<<<<<<< /usr/src/app/output/srt/asterisk-java/2131fda1860f47da7d05dd3d494922e7d90b2ab3/src/main/java/org/asteriskjava/manager/event/HangupEvent.java/left.java
  connectedlinename
=======
  accountCode
>>>>>>> /usr/src/app/output/srt/asterisk-java/2131fda1860f47da7d05dd3d494922e7d90b2ab3/src/main/java/org/asteriskjava/manager/event/HangupEvent.java/right.java
  ;

  private String 
<<<<<<< /usr/src/app/output/srt/asterisk-java/2131fda1860f47da7d05dd3d494922e7d90b2ab3/src/main/java/org/asteriskjava/manager/event/HangupEvent.java/left.java
  connectedlinenum
=======
  connectedLineNum
>>>>>>> /usr/src/app/output/srt/asterisk-java/2131fda1860f47da7d05dd3d494922e7d90b2ab3/src/main/java/org/asteriskjava/manager/event/HangupEvent.java/right.java
  ;

  private String connectedLineName;

  public HangupEvent(Object source) {
    super(source);
  }

  /**
     * Returns the cause of the hangup.
     *
     * @return the hangup cause.
     * @see org.asteriskjava.live.HangupCause
     */
  public Integer getCause() {
    return cause;
  }

  /**
     * Sets the cause of the hangup.
     *
     * @param cause the hangup cause.
     */
  public void setCause(Integer cause) {
    this.cause = cause;
  }

  /**
     * Returns the textual representation of the hangup cause.
     *
     * @return the textual representation of the hangup cause.
     * @since 0.2
     */
  public String getCauseTxt() {
    return causeTxt;
  }

  /**
     * Sets the textual representation of the hangup cause.
     *
     * @param causeTxt the textual representation of the hangup cause.
     * @since 0.2
     */
  public void setCauseTxt(String causeTxt) {
    this.causeTxt = causeTxt;
  }

  /**
     * Returns the Caller*ID name of the channel connected if set.
     * If the channel has no caller id set "unknown" is returned.
     *
     * @since 1.0.0
     */
  public String getConnectedlinename() {
    return connectedlinename;
  }

  public String getAccountCode() {
    return accountCode;
  }

  public void setConnectedlinename(String connectedlinename) {
    this.connectedlinename = connectedlinename;
  }

  public void setAccountCode(String accountCode) {
    this.accountCode = accountCode;
  }

  /**
     * Returns the Caller*ID number of the channel connected if set.
     * If the channel has no caller id set "unknown" is returned.
     *
     * @since 1.0.0
     */
  public String getConnectedlinenum() {
    return connectedlinenum;
  }

  public String getConnectedLineNum() {
    return connectedLineNum;
  }

  public void setConnectedlinenum(String connectedlinenum) {
    this.connectedlinenum = connectedlinenum;
  }

  public void setConnectedLineNum(String connectedLineNum) {
    this.connectedLineNum = connectedLineNum;
  }

  public String getConnectedLineName() {
    return connectedLineName;
  }

  public void setConnectedLineName(String connectedLineName) {
    this.connectedLineName = connectedLineName;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("HangupEvent [cause=");
    builder.append(cause);
    builder.append(", causeTxt=");
    builder.append(causeTxt);
    builder.append(", accountCode=");
    builder.append(accountCode);
    builder.append(", connectedLineNum=");
    builder.append(connectedLineNum);
    builder.append(", connectedLineName=");
    builder.append(connectedLineName);
    builder.append("]");
    return builder.toString();
  }
}