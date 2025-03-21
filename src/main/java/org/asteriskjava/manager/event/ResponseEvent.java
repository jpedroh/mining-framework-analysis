package org.asteriskjava.manager.event;

/**
 * Abstract base class for events triggered in response to a ManagerAction.<p>
 * All ResponseEvents contain an additional action id property that links the
 * event to the action that caused it.
 *
 * @author srt
 * @version $Id$
 * @see org.asteriskjava.manager.action.ManagerAction
 */
public abstract class ResponseEvent extends ManagerEvent {
  private static final long serialVersionUID = 1L;

  private String actionId;

  private String internalActionId;

  public ResponseEvent(Object source) {
    super(source);
  }

  /**
     * Returns the user provided action id of the ManagerAction that caused
     * this event. If the application did not set an action id this method
     * returns <code>null</code>.
     *
     * @return the action id of the ManagerAction that caused this event or
     *         <code>null</code> if none was set.
     * @see org.asteriskjava.manager.action.ManagerAction#setActionId(String)
     */
  public final String getActionId() {
    return actionId;
  }

  /**
     * Sets the action id of the ManagerAction that caused this event.
     *
     * @param actionId the action id of the ManagerAction that caused this
     *                 event.
     */
  public final void setActionId(String actionId) {
    this.actionId = actionId;
  }

  /**
     * Returns the internal action id of the ManagerAction that caused this
     * event.<p>
     * Warning: This method is internal to Asterisk-Java and should never be
     * used in application code.
     *
     * @return the internal action id of the ManagerAction that caused this
     *         event.
     * @since 0.2
     */
  public final String getInternalActionId() {
    return internalActionId;
  }

  /**
     * Sets the internal action id of the ManagerAction that caused this event.
     *
     * @param internalActionId the internal action id of the ManagerAction that
     *                         caused this event.
     * @since 0.2
     */
  public final void setInternalActionId(String internalActionId) {
    this.internalActionId = internalActionId;
  }
}