package org.cts.registry;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * This class manages all supported registry. It permits to declare a custom
 * registry or remove one.
 *
 * @author Erwan Bocher
 */
public final class RegistryManager {
  static final Logger LOGGER = Logger.getLogger(RegistryManager.class);

  private final Map<String, Registry> registries = new HashMap<String, Registry>();

  private final List<RegistryManagerListener> listeners = new ArrayList<RegistryManagerListener>();

  /**
     * Create a registry manager filled with all internal registries
     */
  public RegistryManager() {
  }

  /**
     * Adds a listener.
     *
     * @param listener a listener
     */
  public void addRegistryManagerListener(RegistryManagerListener listener) {
    listeners.add(listener);
  }

  /**
     * Remove the listener if it is present in the listener list
     *
     * @param listener
     * @return true if the listener was successfully removed. False if the
     * specified parameter was not a listener
     */
  public boolean removeRegistryManagerListener(RegistryManagerListener listener) {
    return listeners.remove(listener);
  }

  /**
     * Declare a registry to the {@code RegistryManager}
     *
     * @param registryClass
     */
  public void addRegistry(Registry registryClass) {
    addRegistry(registryClass, false);
  }

  /**
     * Declare a registry to the {@code RegistryManager} An existing registry
     * can be replaced by a new one.
     *
     * @param functionName
     * @param functionClass
     * @param replace
     */
  public void addRegistry(Registry registry, boolean replace) {
    LOGGER.trace("Adding a new registry " + registry.getRegistryName());
    String registryName = registry.getRegistryName().toLowerCase();
    if (!replace && registries.containsKey(registryName)) {
      throw new IllegalArgumentException("Registry " + registryName + " already exists");
    }
    registries.put(registryName, registry);
    fireRegistryAdded(registryName);
  }

  /**
     * Listener to inform that a registry has been added.
     *
     * @param functionName
     */
  private void fireRegistryAdded(String functionName) {
    for (RegistryManagerListener listener : listeners) {
      listener.registryAdded(functionName);
    }
  }

  /**
     * Gets if the registry with the given name has been registered.
     *
     * @param name a registry name ie epsg, ignf, esri...
     * @return true if registered
     */
  public boolean contains(String name) {
    return registries.containsKey(name);
  }

  /**
     * Gets all registered registry names
     *
     * @return an array of names
     */
  public String[] getRegistryNames() {
    LOGGER.trace("Getting all function names");
    Set<String> k = registries.keySet();
    return k.toArray(new String[k.size()]);
  }

  /**
     * Return the corresponding registry
     *
     * @param string
     * @return
     */
  public Registry getRegistry(String registryName) {
    LOGGER.trace("Getting the registry " + registryName);
    Registry registry = registries.get(registryName.toLowerCase());
    if (registry == null) {
      return null;
    } else {
      return registry;
    }
  }
}