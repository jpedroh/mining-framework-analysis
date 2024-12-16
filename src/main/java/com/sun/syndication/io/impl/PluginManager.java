/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.io.DelegatingModuleGenerator;
import com.sun.syndication.io.DelegatingModuleParser;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 
 * @author Alejandro Abdelnur
 * 
 */
public abstract class PluginManager<T> {
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
     * @param propertyKey
     * 		property key defining the plugins classes
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

    // PRIVATE - LOADER PART
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
                    ((DelegatingModuleParser) (plugin)).setFeedParser(parentParser);
                }
                if (plugin instanceof DelegatingModuleGenerator) {
                    ((DelegatingModuleGenerator) (plugin)).setFeedGenerator(parentGenerator);
                }
                pluginsMap.put(getKey(plugin), plugin);
                // to preserve the order of
                pluginsList.add(plugin);
                                        // definition
                // in the rome.properties files
            }
            Iterator<T> i = pluginsMap.values().iterator();
            while (i.hasNext()) {
                // to remove overridden plugin
                finalPluginsList.add(i.next());
                                                // impls
            } 
            i = pluginsList.iterator();
            while (i.hasNext()) {
                final Object plugin = i.next();
                if (!finalPluginsList.contains(plugin)) {
                    i.remove();
                }
            } 
        } catch (final java.lang.Exception ex) {
            throw new RuntimeException("could not instantiate plugin " + className, ex);
        } catch (final java.lang.ExceptionInInitializerError er) {
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
        for (final String propertyValue : propertyValues) {
            final Class mClass;
            if (useLoadClass) {
                mClass = classLoader.loadClass(propertyValue);
            } else {
                mClass = Class.forName(propertyValue, true, classLoader);
            }
            classes.add(mClass);
        }
        @SuppressWarnings("unchecked")
        final Class<T>[] array = ((Class<T>[]) (new Class[classes.size()]));
        classes.toArray(array);
        return array;
    }
}