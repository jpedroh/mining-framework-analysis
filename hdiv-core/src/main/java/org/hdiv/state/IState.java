package org.hdiv.state;
import java.util.Collection;
import java.util.List;
import org.hdiv.util.Method;

public interface IState {
  /**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter <code>parameter</code>, it is
	 * also added to the required parameters.
	 *
	 * @param parameter The parameter
	 */
  void addParameter(IParameter parameter);

  /**
	 * Returns the parameter that matches the given identifier <code>key</code>. Null is returned if the parameter name
	 * is not found.
	 *
	 * @param key parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
  IParameter getParameter(String key);

  /**
	 * Returns all the parameters of the IState.
	 *
	 * @return List of {@link IParameter}
	 */
  Collection<IParameter> getParameters();

  /**
	 * @return Returns the action associated to state <code>this</code>.
	 */
  String getAction();

  /**
	 * @param action The action to set.
	 */
  void setAction(String action);

  /**
	 * @return Returns the id.
	 */
  int getId();

  /**
	 * Checks if exists a parameter with the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return True if exists a parameter with this identifier <code>key</code>. False otherwise.
	 */
  boolean existParameter(String key);

  /**
	 * @return Returns required parameters.
	 */
  List<String> getRequiredParams();

  /**
	 * @return IState parameters in one String.
	 */
  String getParams();

  /**
	 * @param params IState parameters in one String.
	 */
  void setParams(String params);

  /**
	 * @return HTTP method
	 */
  Method getMethod();

  /**
	 * @param method HTTP method for this request
	 */
  void setMethod(Method method);
}