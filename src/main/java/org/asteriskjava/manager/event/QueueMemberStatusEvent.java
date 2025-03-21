package org.asteriskjava.manager.event;

/**
 * A QueueMemberStatusEvent shows the status of a QueueMemberEvent
 * 
 * @author Asteria Solutions Group, Inc. http://www.asteriasgi.com/
 * @version $Id$
 */
public class QueueMemberStatusEvent extends QueueMemberEvent {
  /**
     * Serializable version identifier
     */
  private static final long serialVersionUID = -2293926744791895763L;

  private String stateInterface;

  public String getStateInterface() {
    return stateInterface;
  }

  public void setStateInterface(String stateInterface) {
    this.stateInterface = stateInterface;
  }

  /**
     * @param source
     */
  public QueueMemberStatusEvent(Object source) {
    super(source);
  }
}