/*-
 * ========================LICENSE_START=================================
 * restheart-core
 * %%
 * Copyright (C) 2014 - 2020 SoftInstigate
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * =========================LICENSE_END==================================
 */
package org.restheart.plugins;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.restheart.Bootstrapper;
import org.restheart.ConfigurationException;
import org.restheart.plugins.mongodb.Checker;
import org.restheart.plugins.mongodb.Hook;
import org.restheart.plugins.mongodb.Transformer;
import org.restheart.plugins.security.AuthMechanism;
import org.restheart.plugins.security.Authenticator;
import org.restheart.plugins.security.Authorizer;
import org.restheart.plugins.security.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class PluginsFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PluginsFactory.class);

    private static final String REGISTER_PLUGIN_CLASS_NAME = RegisterPlugin.class
            .getName();

    private static final Map<String, Map<String, Object>> PLUGINS_CONFS
            = consumePluginsConfiguration();

    /**
     *
     * @return the AuthenticationMechanisms
     */
    static Set<PluginRecord<AuthMechanism>> authMechanisms() {
        return createPlugins(AuthMechanism.class,
                Bootstrapper.getConfiguration().getAuthMechanisms());
    }

    /**
     *
     * @return the Authenticators
     */
    static Set<PluginRecord<Authenticator>> authenticators() {
        return createPlugins(Authenticator.class,
                Bootstrapper.getConfiguration().getAuthenticators());
    }

    /**
     *
     * @return the Authorizers
     */
    static Set<PluginRecord<Authorizer>> authorizers() {
        return createPlugins(Authorizer.class,
                Bootstrapper.getConfiguration().getAuthorizers());
    }

    /**
     *
     * @return the Token Manager
     */
    static PluginRecord<TokenManager> tokenManager() {
        Set<PluginRecord<TokenManager>> tkms = createPlugins(TokenManager.class,
                Bootstrapper.getConfiguration().getTokenManagers());

        if (tkms != null) {
            var tkm = tkms.stream().filter(t -> t.isEnabled()).findFirst();

            if (tkm != null && tkm.isPresent()) {
                return tkm.get();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * create the initializers
     */
    static Set<PluginRecord<Initializer>> initializers() {
        return createPlugins(Initializer.class, PLUGINS_CONFS);
    }

    /**
     * creates the interceptors
     */
    @SuppressWarnings("unchecked")
    static Set<PluginRecord<Interceptor>> interceptors() {
        return createPlugins(Interceptor.class, PLUGINS_CONFS);
    }

    /**
     * creates the services
     */
    @SuppressWarnings("unchecked")
    static Set<PluginRecord<Service>> services() {
        return createPlugins(Service.class, PLUGINS_CONFS);
    }

    /**
     * creates the services
     */
    @SuppressWarnings("unchecked")
    static Set<PluginRecord<Transformer>> transformers() {
        return createPlugins(Transformer.class, PLUGINS_CONFS);
    }

    /**
     * creates the services
     */
    @SuppressWarnings("unchecked")
    static Set<PluginRecord<Checker>> checkers() {
        return createPlugins(Checker.class, PLUGINS_CONFS);
    }

    /**
     * creates the services
     */
    @SuppressWarnings("unchecked")
    static Set<PluginRecord<Hook>> hooks() {
        return createPlugins(Hook.class, PLUGINS_CONFS);
    }

    /**
     * @param type the class of the plugin , e.g. Initializer.class
     */
    @SuppressWarnings("unchecked")
    private static <T extends Plugin> Set<PluginRecord<T>> createPlugins(
            Class type, Map<String, Map<String, Object>> confs) {
        Set<PluginRecord<T>> ret = new LinkedHashSet<>();

        var _type = type.getSimpleName();

        try (var scanResult = new ClassGraph()
                .addClassLoader(getPluginsClassloader())
                .enableAnnotationInfo()
                .enableMethodInfo()
                .initializeLoadedClasses()
                .scan()) {
            var registeredPlugins = scanResult
                    .getClassesWithAnnotation(REGISTER_PLUGIN_CLASS_NAME);

            ClassInfoList listOfType;

            if (type.isInterface()) {
                if (type.equals(Authenticator.class)) {
                    var tms = scanResult.getClassesImplementing(TokenManager.class.getName());

                    listOfType = scanResult
                            .getClassesImplementing(type.getName())
                            .exclude(tms);
                } else {
                    listOfType = scanResult.getClassesImplementing(type.getName());
                }
            } else {
                listOfType = scanResult.getSubclasses(type.getName());
            }

            var plugins = registeredPlugins.intersect(listOfType);

            // sort by priority
            plugins.sort((ClassInfo ci1, ClassInfo ci2) -> {
                return Integer.compare(annotationParam(ci1, "priority"),
                        annotationParam(ci2, "priority"));
            });

            plugins.stream().forEachOrdered(plugin -> {
                Object i;

                try {
                    String name = annotationParam(plugin,
                            "name");
                    String description = annotationParam(plugin,
                            "description");
                    Boolean enabledByDefault = annotationParam(plugin,
                            "enabledByDefault");

                    var enabled = PluginRecord.isEnabled(enabledByDefault,
                            confs != null ? confs.get(name) : null);

                    if (enabled) {
                        i = instantiatePlugin(plugin, _type, name, confs);

                        var pr = new PluginRecord(
                                name,
                                description,
                                enabledByDefault,
                                plugin.getName(),
                                (T) i,
                                confs != null
                                        ? confs.get(name)
                                        : null);

                        if (pr.isEnabled()) {
                            ret.add(pr);
                            LOGGER.debug("Registered {} {}: {}",
                                    _type,
                                    name,
                                    description);
                        }
                    } else {
                        LOGGER.debug("{} {} is disabled", _type, name);
                    }
                } catch (ConfigurationException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException t) {
                    LOGGER.error("Error registering {} {}: {}",
                            _type,
                            annotationParam(plugin, "name") != null
                            ? (String) annotationParam(plugin, "name")
                            : plugin.getSimpleName(),
                            getRootException(t).getMessage(),
                            t);
                }
            });
        }

        return ret;
    }

    private static Plugin instantiatePlugin(
            ClassInfo pluginClassInfo,
            String pluginType,
            String pluginName,
            Map confs)
            throws ConfigurationException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        final Plugin ret;

        try {
            ret = (Plugin) pluginClassInfo.loadClass(false)
                    .getDeclaredConstructor()
                    .newInstance();

            invokeInjectMethods(pluginName,
                    pluginType,
                    pluginClassInfo,
                    ret,
                    confs);
        } catch (NoSuchMethodException nme) {
            throw new ConfigurationException(
                    pluginType
                    + " " + pluginName
                    + " does not have default constructor "
                    + pluginClassInfo.getSimpleName()
                    + "()");
        }

        return ret;
    }

    private static void invokeInjectMethods(String pluginName,
            String pluginType,
            ClassInfo pluginClassInfo,
            Object pluingInstance,
            Map confs) throws ConfigurationException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        invokeInjectConfigurationMethods(pluginName,
                pluginType,
                pluginClassInfo,
                pluingInstance,
                confs);

        invokeInjectPluginsRegistryMethods(pluginName,
                pluginType,
                pluginClassInfo,
                pluingInstance);

        invokeInjectConfigurationAndPluginsRegistryMethods(pluginName,
                pluginType,
                pluginClassInfo,
                pluingInstance,
                confs);
    }

    private static void invokeInjectConfigurationMethods(String pluginName,
            String pluginType,
            ClassInfo pluginClassInfo,
            Object pluingInstance,
            Map confs) throws ConfigurationException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        // finds @InjectConfiguration methods
        var mil = pluginClassInfo.getDeclaredMethodInfo();

        for (var mi : mil) {
            if (mi.hasAnnotation(InjectConfiguration.class.getName())
                    && !mi.hasAnnotation(InjectPluginsRegistry.class.getName())) {
                var ai = mi.getAnnotationInfo(InjectConfiguration.class.getName());

                // check configuration scope
                var allConfScope = ai.getParameterValues().stream()
                        .anyMatch(p -> "scope".equals(p.getName())
                        && (ConfigurationScope.class.getName()
                                + "." + ConfigurationScope.ALL.name()).equals(
                                p.getValue().toString()));

                var scopedConf = (Map) (allConfScope
                        ? Bootstrapper.getConfiguration().toMap()
                        : confs != null
                                ? confs.get(pluginName)
                                : null);

                if (scopedConf == null) {
                    LOGGER.warn("{} {} defines method {} with @InjectConfiguration "
                            + "but no configuration found for it",
                            pluginType,
                            pluginName,
                            mi.getName());
                }

                // try to inovke @InjectConfiguration method
                try {
                    pluginClassInfo.loadClass(false)
                            .getDeclaredMethod(mi.getName(),
                                    Map.class)
                            .invoke(pluingInstance, scopedConf);
                } catch (NoSuchMethodException nme) {
                    throw new ConfigurationException(
                            pluginType
                            + " " + pluginName
                            + " has an invalid method with @InjectConfiguration. "
                            + "Method signature must be "
                            + mi.getName()
                            + "(Map<String, Object> configuration)");
                }
            }
        }
    }

    private static void invokeInjectPluginsRegistryMethods(String pluginName,
            String pluginType,
            ClassInfo pluginClassInfo,
            Object pluingInstance) throws ConfigurationException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        // finds @InjectPluginRegistry methods
        var mil = pluginClassInfo.getDeclaredMethodInfo();

        for (var mi : mil) {
            if (mi.hasAnnotation(InjectPluginsRegistry.class.getName())
                    && !mi.hasAnnotation(InjectConfiguration.class.getName())) {
                // try to inovke @InjectPluginRegistry method
                try {
                    pluginClassInfo.loadClass(false)
                            .getDeclaredMethod(mi.getName(),
                                    PluginsRegistry.class)
                            .invoke(pluingInstance,
                                    PluginsRegistryImpl.getInstance());
                } catch (NoSuchMethodException nme) {
                    throw new ConfigurationException(
                            pluginType
                            + " " + pluginName
                            + " has an invalid method with @InjectPluginsRegistry. "
                            + "Method signature must be "
                            + mi.getName()
                            + "(PluginsRegistry pluginsRegistry)");
                }
            }
        }
    }

    private static void invokeInjectConfigurationAndPluginsRegistryMethods(String pluginName,
            String pluginType,
            ClassInfo pluginClassInfo,
            Object pluingInstance,
            Map confs) throws ConfigurationException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        // finds @InjectConfiguration methods
        var mil = pluginClassInfo.getDeclaredMethodInfo();

        for (var mi : mil) {
            if (mi.hasAnnotation(InjectConfiguration.class.getName())
                    && mi.hasAnnotation(InjectPluginsRegistry.class.getName())) {
                var ai = mi.getAnnotationInfo(InjectConfiguration.class.getName());

                // check configuration scope
                var allConfScope = ai.getParameterValues().stream()
                        .anyMatch(p -> "scope".equals(p.getName())
                        && (ConfigurationScope.class.getName()
                                + "." + ConfigurationScope.ALL.name()).equals(
                                p.getValue().toString()));

                var scopedConf = (Map) (allConfScope
                        ? Bootstrapper.getConfiguration().toMap()
                        : confs != null
                                ? confs.get(pluginName)
                                : null);

                if (scopedConf == null) {
                    LOGGER.warn("{} {} defines method {} with @InjectConfiguration "
                            + "but no configuration found for it",
                            pluginType,
                            pluginName,
                            mi.getName());
                }

                // try to inovke @InjectConfiguration method
                try {
                    pluginClassInfo.loadClass(false)
                            .getDeclaredMethod(mi.getName(),
                                    Map.class, PluginsRegistry.class)
                            .invoke(pluingInstance, scopedConf,
                                    PluginsRegistryImpl.getInstance());
                } catch (NoSuchMethodException nme) {
                    throw new ConfigurationException(
                            pluginType
                            + " " + pluginName
                            + " has an invalid method with @InjectConfiguration"
                            + " and @InjectPluginsRegistry."
                            + " Method signature must be "
                            + mi.getName()
                            + "(Map<String, Object> configuration,"
                            + " PluginsRegistry pluginsRegistry)");
                }
            }
        }
    }

    private static Throwable getRootException(Throwable t) {
        if (t.getCause() != null) {
            return getRootException(t.getCause());
        } else {
            return t;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Object> T annotationParam(ClassInfo ci,
            String param) {
        var annotationInfo = ci.getAnnotationInfo(REGISTER_PLUGIN_CLASS_NAME);
        var annotationParamVals = annotationInfo.getParameterValues();

        // The Route annotation has a parameter named "path"
        return (T) annotationParamVals.getValue(param);
    }

    private static URL[] findPluginsJars(Path pluginsDirectory) {
        var urls = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files
                .newDirectoryStream(pluginsDirectory, "*.jar")) {
            for (Path path : directoryStream) {
                var jar = path.toUri().toURL();
                urls.add(jar);
                LOGGER.info("Added to classpath the plugins jar {}", jar);
            }
        } catch (IOException ex) {
            LOGGER.error("Cannot read jars in plugins directory {}",
                    Bootstrapper.getConfiguration().getPluginsDirectory(),
                    ex.getMessage());
        }

        return urls.toArray(new URL[urls.size()]);
    }

    private static Path getPluginsDirectory() {
        var pluginsDir = Bootstrapper.getConfiguration().getPluginsDirectory();

        if (pluginsDir == null) {
            return null;
        }

        if (pluginsDir.startsWith("/")) {
            return Paths.get(pluginsDir);
        } else {
            // this is to allow specifying the plugin directory path 
            // relative to the jar (also working when running from classes)
            URL location = PluginsFactory.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            File locationFile = new File(location.getPath());

            pluginsDir = locationFile.getParent()
                    + File.separator
                    + pluginsDir;

            return FileSystems.getDefault().getPath(pluginsDir);
        }
    }

    private static URLClassLoader PLUGINS_CL_CACHE = null;

    /**
     *
     * @return the URLClassLoader that resolve plugins classes
     */
    private static URLClassLoader getPluginsClassloader() {
        if (PLUGINS_CL_CACHE == null) {
            PLUGINS_CL_CACHE = new URLClassLoader(
                    findPluginsJars(
                            getPluginsDirectory()));
        }

        return PLUGINS_CL_CACHE;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Object>> consumePluginsConfiguration() {
        Map<String, Map<String, Object>> pluginsArgs = Bootstrapper
                .getConfiguration()
                .getPluginsArgs();

        Map<String, Map<String, Object>> confs = new HashMap<>();

        pluginsArgs.forEach((name, params) -> {
            if (params instanceof Map) {
                confs.put(name, (Map) params);
            } else {
                confs.put(name, new HashMap<>());
            }
        });

        return confs;
    }
}
