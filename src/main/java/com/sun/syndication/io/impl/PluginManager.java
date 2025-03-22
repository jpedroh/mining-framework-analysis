package com.sun.syndication.io.impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.sun.syndication.io.DelegatingModuleGenerator;
import com.sun.syndication.io.DelegatingModuleParser;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;


<<<<<<< /usr/src/app/output/rometools/rome/648a1f04cf5a4c55a13bfff801389cfe93c6976e/src/main/java/com/sun/syndication/io/impl/PluginManager.java/left.java
/**
 * <p>
 * 
 * @author Alejandro Abdelnur
 * 
 */
public abstract class PluginManager<T extends java.lang.Object> {
  private final String[] propertyValues;

  private Map<String, T> pluginsMap;

  private List<T> pluginsList;

  private final List<String> keys;

  private final WireFeedParser parentParser;

  private final WireFeedGenerator parentGenerator;

  /**
     * Creates a PluginManager
     * <p>
     * 
     * @param propertyKey property key defining the plugins classes
     * 
     */
  protected PluginManager(final String propertyKey) {
    this(propertyKey, null, null);
  }

  protected PluginManager(final String propertyKey, final WireFeedParser parentParser, final WireFeedGenerator parentGenerator) {
    this.parentParser = parentParser;
    this.parentGenerator = parentGenerator;
    propertyValues = PropertiesLoader.getPropertiesLoader().getTokenizedProperty(propertyKey, ", ");
    loadPlugins();
    pluginsMap = Collections.unmodifiableMap(pluginsMap);
    pluginsList = Collections.unmodifiableList(pluginsList);
    keys = Collections.unmodifiableList(new ArrayList<String>(pluginsMap.keySet()));
  }

  protected abstract String getKey(T obj);

  protected List<String> getKeys() {
    return keys;
  }

  protected List<T> getPlugins() {
    return pluginsList;
  }

  protected Map<String, T> getPluginMap() {
    return pluginsMap;
  }

  protected T getPlugin(final String key) {
    return pluginsMap.get(key);
  }

  private void loadPlugins() {
    final List<T> finalPluginsList = new ArrayList<T>();
    pluginsList = new ArrayList<T>();
    pluginsMap = new HashMap<String, T>();
    String className = null;
    try {
      final Class<T>[] classes = getClasses();
      for (final Class<T> classe : classes) {
        className = classe.getName();
        final T plugin = classe.newInstance();
        if (plugin instanceof DelegatingModuleParser) {
          ((DelegatingModuleParser) plugin).setFeedParser(parentParser);
        }
        if (plugin instanceof DelegatingModuleGenerator) {
          ((DelegatingModuleGenerator) plugin).setFeedGenerator(parentGenerator);
        }
        pluginsMap.put(getKey(plugin), plugin);
        pluginsList.add(plugin);
      }
      Iterator<T> i = pluginsMap.values().iterator();
      while (i.hasNext()) {
        finalPluginsList.add(i.next());
      }
      i = pluginsList.iterator();
      while (i.hasNext()) {
        final Object plugin = i.next();
        if (!finalPluginsList.contains(plugin)) {
          i.remove();
        }
      }
    } catch (final Exception ex) {
      throw new RuntimeException("could not instantiate plugin " + className, ex);
    } catch (final ExceptionInInitializerError er) {
      throw new RuntimeException("could not instantiate plugin " + className, er);
    }
  }

  /**
     * Loads and returns the classes defined in the properties files. If the
     * system property "rome.pluginmanager.useloadclass" is set to true then
     * classLoader.loadClass will be used to load classes (instead of
     * Class.forName). This is designed to improve OSGi compatibility. Further
     * information can be found in
     * https://rome.dev.java.net/issues/show_bug.cgi?id=118
     * <p>
     * 
     * @return array containing the classes defined in the properties files.
     * @throws java.lang.ClassNotFoundException thrown if one of the classes
     *             defined in the properties file cannot be loaded and hard
     *             failure is ON.
     * 
     */
  private Class<T>[] getClasses() throws ClassNotFoundException {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final List<Class<T>> classes = new ArrayList<Class<T>>();
    final boolean useLoadClass = Boolean.valueOf(System.getProperty("rome.pluginmanager.useloadclass", "false")).booleanValue();
    for (final String _propertyValue : propertyValues) {
      @SuppressWarnings(value = { "unchecked" }) final Class<T> mClass = (Class<T>) (useLoadClass ? classLoader.loadClass(_propertyValue) : Class.forName(_propertyValue, true, classLoader));
      classes.add(mClass);
    }
    @SuppressWarnings(value = { "unchecked" }) final Class<T>[] array = (Class<T>[]) new Class[classes.size()];
    classes.toArray(array);
    return array;
  }
}
=======
/**
 * <p>
 * 
 * @author Alejandro Abdelnur
 * 
 */
public abstract class PluginManager {
  private final String[] propertyValues;

  private Map pluginsMap;

  private List pluginsList;

  private final List keys;

  private final WireFeedParser parentParser;

  private final WireFeedGenerator parentGenerator;

  /**
     * Creates a PluginManager
     * <p>
     * 
     * @param propertyKey property key defining the plugins classes
     * 
     */
  protected PluginManager(final String propertyKey) {
    this(propertyKey, null, null);
  }

  protected PluginManager(final String propertyKey, final WireFeedParser parentParser, final WireFeedGenerator parentGenerator) {
    this.parentParser = parentParser;
    this.parentGenerator = parentGenerator;
    propertyValues = PropertiesLoader.getPropertiesLoader().getTokenizedProperty(propertyKey, ", ");
    loadPlugins();
    pluginsMap = Collections.unmodifiableMap(pluginsMap);
    pluginsList = Collections.unmodifiableList(pluginsList);
    keys = Collections.unmodifiableList(new ArrayList(pluginsMap.keySet()));
  }

  protected abstract String getKey(Object obj);

  protected List getKeys() {
    return keys;
  }

  protected List getPlugins() {
    return pluginsList;
  }

  protected Map getPluginMap() {
    return pluginsMap;
  }

  protected Object getPlugin(final String key) {
    return pluginsMap.get(key);
  }

  private void loadPlugins() {
    final List finalPluginsList = new ArrayList();
    pluginsList = new ArrayList();
    pluginsMap = new HashMap();
    String className = null;
    try {
      final Class[] classes = getClasses();
      for (final Class classe : classes) {
        className = classe.getName();
        final Object plugin = classe.newInstance();
        if (plugin instanceof DelegatingModuleParser) {
          ((DelegatingModuleParser) plugin).setFeedParser(parentParser);
        }
        if (plugin instanceof DelegatingModuleGenerator) {
          ((DelegatingModuleGenerator) plugin).setFeedGenerator(parentGenerator);
        }
        pluginsMap.put(getKey(plugin), plugin);
        pluginsList.add(plugin);
      }
      Iterator i = pluginsMap.values().iterator();
      while (i.hasNext()) {
        finalPluginsList.add(i.next());
      }
      i = pluginsList.iterator();
      while (i.hasNext()) {
        final Object plugin = i.next();
        if (!finalPluginsList.contains(plugin)) {
          i.remove();
        }
      }
    } catch (final Exception ex) {
      throw new RuntimeException("could not instantiate plugin " + className, ex);
    } catch (final ExceptionInInitializerError er) {
      throw new RuntimeException("could not instantiate plugin " + className, er);
    }
  }

  /**
     * Loads and returns the classes defined in the properties files. If the
     * system property "rome.pluginmanager.useloadclass" is set to true then
     * classLoader.loadClass will be used to load classes (instead of
     * Class.forName). This is designed to improve OSGi compatibility. Further
     * information can be found in
     * https://rome.dev.java.net/issues/show_bug.cgi?id=118
     * <p>
     * 
     * @return array containing the classes defined in the properties files.
     * @throws java.lang.ClassNotFoundException thrown if one of the classes
     *             defined in the properties file cannot be loaded and hard
     *             failure is ON.
     * 
     */
  private Class[] getClasses() throws ClassNotFoundException {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final List classes = new ArrayList();
    final boolean useLoadClass = Boolean.valueOf(System.getProperty("rome.pluginmanager.useloadclass", "false")).booleanValue();
    for (final String propertyValue : propertyValues) {
      final Class mClass;
      if (useLoadClass) {
        mClass = classLoader.loadClass(propertyValue);
      } else {
        mClass = Class.forName(propertyValue, true, classLoader);
      }
      classes.add(mClass);
    }
    final Class[] array = new Class[classes.size()];
    classes.toArray(array);
    return array;
  }
}
>>>>>>> /usr/src/app/output/rometools/rome/648a1f04cf5a4c55a13bfff801389cfe93c6976e/src/main/java/com/sun/syndication/io/impl/PluginManager.java/right.java
