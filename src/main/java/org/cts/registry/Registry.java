package org.cts.registry;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Erwan Bocher
 */
public interface Registry {
  /**
     * Return the name of the registry
     */
  public String getRegistryName();

  /**
     * Return all parameters need to build a CoordinateReferenceSystem
     *
     * @return
     */
  public Map<String, String> getParameters(String code) throws RegistryException;

  /**
     * Return all supported codes for this registry
     *
     * @return
     */
  public Set<String> getSupportedCodes() throws RegistryException;
}