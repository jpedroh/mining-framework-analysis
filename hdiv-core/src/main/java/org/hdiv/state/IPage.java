package org.hdiv.state;
import java.util.Collection;

public interface IPage {
  /**
	 * Adds a new state to the page <code>this</code>.
	 * 
	 * @param state
	 *            State that represents all the data that composes a possible request.
	 */
  public void addState(IState state);

  /**
	 * Checks if exists a state with the given identifier <code>key</code>.
	 * 
	 * @param id
	 *            State identifier
	 * @return true if exist
	 */
  public boolean existState(int id);

  /**
	 * Returns the state with the given identifier <code>key</code> from the map of states
	 * 
	 * @param id
	 *            State identifier
	 * @return IState State with the identifier <code>key</code>.
	 */
  public IState getState(int id);

  /**
	 * @return Returns the page name.
	 */
  public String getName();

  /**
	 * @return Returns the page id.
	 */
  public int getId();

  /**
	 * @param id
	 *            The page id to set.
	 */
  public void setId(int id);

  /**
	 * @return Returns the page states.
	 */
  public Collection<? extends Object> getStates();

  /**
	 * @return number of states.
	 */
  public int getStatesCount();

  /**
	 * Obtain next valid state id.
	 * 
	 * @return State Id to use.
	 */
  public int getNextStateId();

  /**
	 * Mark this page as reused in more than one request. Most common case is in Ajax requests.
	 */
  public void markAsReused();

  /**
	 * Is this request reused in more than one request?
	 * 
	 * @return isReused
	 */
  public boolean isReused();

  /**
	 * Returns the unique id of flow.
	 * 
	 * @return the flow id
	 */
  public String getFlowId();

  /**
	 * @param flowId
	 *            the flowId to set
	 */
  public void setFlowId(String flowId);

  /**
	 * Returns the corresponding token for the given HTTP method.
	 * 
	 * @param method
	 *            HTTP method
	 * @return the randomToken
	 * @since HDIV 2.1.7
	 */
  public String getRandomToken(String method);

  /**
	 * @param randomToken
	 *            the randomToken to set
	 * @param method
	 *            HTTP method
	 * @since HDIV 2.1.7
	 */
  public void setRandomToken(String randomToken, String method);

  /**
	 * @param parentStateId
	 * 			  the parentStateId to set
	 * 
	 * @since HDIV 2.1.13
	 */
  public void setParentStateId(String parentStateId);

  /**
	 * Returns the state id of the parent page
	 * 	  
	 * @return the parent state id
	 * 
	 * @since HDIV 2.1.13
	 */
  public String getParentStateId();
}