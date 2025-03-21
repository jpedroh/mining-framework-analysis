package de.deepamehta.core.service;
import java.io.InputStream;
import java.io.IOException;


<<<<<<< /usr/src/app/output/jri/deepamehta/4da6c663b1ea70331355dedf74ede6c94179a97b/modules/dm4-core/src/main/java/de/deepamehta/core/service/Plugin.java/left.java
/**
 * Base class for plugin developers to derive their plugins from.
 */
public class Plugin implements BundleActivator, EventHandler {
  private static final String PLUGIN_CONFIG_FILE = "/plugin.properties";

  private static final String STANDARD_PROVIDER_PACKAGE = "de.deepamehta.plugins.webservice.provider";

  private static final String PLUGIN_READY = "dm4/core/plugin_ready";

  private static final Set<String> readyPlugins = new HashSet();

  private BundleContext context;

  private String pluginId;

  private String pluginName;

  private String pluginClass;

  private String pluginPackage;

  private Bundle pluginBundle;

  private Topic pluginTopic;

  private Properties configProperties;

  private Map<String, Boolean> dependencyState;

  protected DeepaMehtaService dms;

  private HttpService httpService;

  private EventAdmin eventService;

  private List<ServiceTracker> serviceTrackers = new ArrayList();

  private Logger logger = Logger.getLogger(getClass().getName());

  public String getId() {
    return pluginId;
  }

  public String getName() {
    return pluginName;
  }

  /**
     * Returns a plugin configuration property (as read from file "plugin.properties")
     * or <code>null</code> if no such property exists.
     */
  public String getConfigProperty(String key) {
    return getConfigProperty(key, null);
  }

  /**
     * Uses the plugin bundle's class loader to load a class by name.
     *
     * @return  the class, or <code>null</code> if the class is not found.
     */
  public Class loadClass(String className) {
    try {
      return pluginBundle.loadClass(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  /**
     * Returns the migration class name for the given migration number.
     *
     * @return  the fully qualified migration class name, or <code>null</code> if the migration package is unknown.
     *          This is the case if the plugin bundle contains no Plugin subclass and the "pluginPackage" config
     *          property is not set.
     */
  public String getMigrationClassName(int migrationNr) {
    if (pluginPackage.equals("de.deepamehta.core.service")) {
      return null;
    }
    return pluginPackage + ".migrations.Migration" + migrationNr;
  }

  public void setMigrationNr(int migrationNr) {
    pluginTopic.setChildTopicValue("dm4.core.plugin_migration_nr", new SimpleValue(migrationNr));
  }

  /**
     * Uses the plugin bundle's class loader to find a resource.
     *
     * @return  A InputStream object or null if no resource with this name is found.
     */
  public InputStream getResourceAsStream(String name) throws IOException {
    URL url = pluginBundle.getResource(name);
    if (url != null) {
      return url.openStream();
    } else {
      return null;
    }
  }

  @Override public String toString() {
    return "plugin \"" + pluginName + "\"";
  }

  @Override public void start(BundleContext context) {
    try {
      this.context = context;
      this.pluginBundle = context.getBundle();
      this.pluginId = pluginBundle.getSymbolicName();
      this.pluginName = (String) pluginBundle.getHeaders().get("Bundle-Name");
      this.pluginClass = (String) pluginBundle.getHeaders().get("Bundle-Activator");
      logger.info("========== Starting " + this + " ==========");
      this.configProperties = readConfigFile();
      this.pluginPackage = getConfigProperty("pluginPackage", getClass().getPackage().getName());
      this.dependencyState = initDependencies();
      if (dependencyState.size() > 0) {
        registerEventListener();
      }
      createServiceTracker(DeepaMehtaService.class.getName());
      createServiceTracker(HttpService.class.getName());
      createServiceTracker(EventAdmin.class.getName());
      createServiceTrackers();
    } catch (Exception e) {
      logger.severe("Starting " + this + " failed:");
      e.printStackTrace();
    }
  }

  @Override public void stop(BundleContext context) {
    logger.info("========== Stopping " + this + " ==========");
    for (ServiceTracker serviceTracker : serviceTrackers) {
      serviceTracker.close();
    }
  }

  public void postInstallPluginHook() {
  }

  public void allPluginsReadyHook() {
  }

  public void serviceArrived(PluginService service) {
  }

  public void serviceGone(PluginService service) {
  }

  public void preCreateHook(TopicModel model, ClientState clientState) {
  }

  public void postCreateHook(Topic topic, ClientState clientState, Directives directives) {
  }

  public void preUpdateHook(Topic topic, TopicModel newModel, Directives directives) {
  }

  public void postUpdateHook(Topic topic, TopicModel newModel, TopicModel oldModel, ClientState clientState, Directives directives) {
  }

  public void postRetypeAssociationHook(Association assoc, String oldTypeUri, Directives directives) {
  }

  public void preDeleteAssociationHook(Association assoc, Directives directives) {
  }

  public void postDeleteAssociationHook(Association assoc, Directives directives) {
  }

  public void preSendTopicHook(Topic topic, ClientState clientState) {
  }

  public void preSendTopicTypeHook(TopicType topicType, ClientState clientState) {
  }

  public void preSendAssociationHook(Association assoc, ClientState clientState) {
  }

  public void providePropertiesHook(Topic topic) {
  }

  public void providePropertiesHook(Association assoc) {
  }

  /**
     * Allows a plugin to modify type definitions -- exisisting ones <i>and</i> future ones.
     * Plugins get a opportunity to visit (and modify) each type definition extacly once.
     * <p>
     * This hook is triggered in 2 situations:
     * <ul>
     *  <li>for each type that <i>exists</i> already while a plugin clean install.
     *  <li>for types created (interactively by the user, or programmatically by a migration) <i>after</i>
     *      the plugin has been installed.
     * </ul>
     * This hook is typically used by plugins which provide cross-cutting concerns by affecting <i>all</i>
     * type definitions of a DeepaMehta installation. Typically such a plugin adds new data fields to types
     * or relates types with specific topics.
     * <p>
     * Examples of plugins which use this hook:
     * <ul>
     *  <li>The "DeepaMehta 4 Workspaces" plugin adds a "Workspaces" field to all types.
     *  <li>The "DeepaMehta 4 Time" plugin adds timestamp fields to all types.
     *  <li>The "DeepaMehta 4 Access Control" plugin adds a "Creator" field to all types and relates them to a user.
     * </ul>
     *
     * @param   topicType   the type to be modified. The passed object is actually an instance of a {@link TopicType}
     *                      subclass that is backed by the database. That is, modifications by e.g.
     *                      {@link TopicType#addDataField} are persistent.
     *                      <p>
     *                      Note: at the time the hook is triggered the type exists already in the database, in
     *                      particular the underlying type topic has an ID already. That is, the type is ready for
     *                      e.g. being related to other topics.
     */
  public void modifyTopicTypeHook(TopicType topicType, ClientState clientState) {
  }

  public CommandResult executeCommandHook(String command, CommandParams params, ClientState clientState) {
    return null;
  }

  private void createServiceTrackers() {
    String consumedServiceInterfaces = getConfigProperty("consumedServiceInterfaces");
    if (consumedServiceInterfaces != null) {
      String[] serviceInterfaces = consumedServiceInterfaces.split(", *");
      for (int i = 0; i < serviceInterfaces.length; i++) {
        createServiceTracker(serviceInterfaces[i]);
      }
    }
  }

  private void createServiceTracker(final String serviceInterface) {
    ServiceTracker serviceTracker = new ServiceTracker(context, serviceInterface, null) {
      @Override public Object addingService(ServiceReference serviceRef) {
        Object service = super.addingService(serviceRef);
        if (service instanceof DeepaMehtaService) {
          logger.info("Adding DeepaMehta 4 core service to plugin \"" + pluginName + "\"");
          dms = (DeepaMehtaService) service;
          checkServiceAvailability();
        } else {
          if (service instanceof HttpService) {
            logger.info("Adding HTTP service to plugin \"" + pluginName + "\"");
            httpService = (HttpService) service;
            checkServiceAvailability();
          } else {
            if (service instanceof EventAdmin) {
              logger.info("Adding Event Admin service to plugin \"" + pluginName + "\"");
              eventService = (EventAdmin) service;
              checkServiceAvailability();
            } else {
              if (service instanceof PluginService) {
                logger.info("Adding plugin service \"" + serviceInterface + "\" to plugin \"" + pluginName + "\"");
                serviceArrived((PluginService) service);
              }
            }
          }
        }
        return service;
      }

      @Override public void removedService(ServiceReference ref, Object service) {
        if (service == dms) {
          logger.info("Removing DeepaMehta 4 core service from plugin \"" + pluginName + "\"");
          unregisterPlugin();
          dms = null;
        } else {
          if (service == httpService) {
            logger.info("Removing HTTP service from plugin \"" + pluginName + "\"");
            unregisterWebResources();
            unregisterRestResources();
            httpService = null;
          } else {
            if (service == eventService) {
              logger.info("Removing Event Admin service from plugin \"" + pluginName + "\"");
              eventService = null;
            } else {
              if (service instanceof PluginService) {
                logger.info("Removing plugin service \"" + serviceInterface + "\" from plugin \"" + pluginName + "\"");
                serviceGone((PluginService) service);
              }
            }
          }
        }
        super.removedService(ref, service);
      }
    };
    serviceTrackers.add(serviceTracker);
    serviceTracker.open();
  }

  /**
     * Checks if both required OSGi services (DeepaMehtaService and HttpService) are available,
     * and if so, initializes the plugin. ### FIXDOC
     */
  private void checkServiceAvailability() {
    if (dms != null && httpService != null && eventService != null && dependenciesAvailable()) {
      initPlugin();
      pluginReady();
      postPluginReadyEvent();
      dms.checkAllPluginsReady();
    }
  }

  private void pluginReady() {
    readyPlugins.add(pluginId);
  }

  private boolean isPluginReady(String pluginId) {
    return readyPlugins.contains(pluginId);
  }

  /**
     * Initializes the plugin. This comprises:
     * - install the plugin in the database
     * - register the plugin at the DeepaMehta core service
     * - register the plugin's OSGi service at the OSGi framework
     * - register the plugin's static web resources at the OSGi HTTP service
     * - register the plugin's REST resources at the OSGi HTTP service
     *
     * These are the tasks which rely on both, the DeepaMehtaService and the HttpService.
     * This method is called once both services become available.
     */
  private void initPlugin() {
    try {
      logger.info("----- Initializing " + this + " -----");
      installPlugin();
      registerPlugin();
      registerPluginService();
      registerWebResources();
      registerRestResources();
      logger.info("----- Initialization of " + this + " complete -----");
    } catch (Exception e) {
      throw new RuntimeException("Initialization of " + this + " failed", e);
    }
  }

  /**
     * Installs the plugin in the database. This comprises:
     * - create topic of type "Plugin"
     * - run migrations
     * - trigger POST_INSTALL_PLUGIN hook
     * - trigger MODIFY_TOPIC_TYPE hook (multiple times)
     */
  private void installPlugin() {
    DeepaMehtaTransaction tx = dms.beginTx();
    try {
      boolean isCleanInstall = initPluginTopic();
      runPluginMigrations(isCleanInstall);
      if (isCleanInstall) {
        postInstallPluginHook();
        introduceTypesToPlugin();
      }
      tx.success();
    } catch (Exception e) {
      logger.warning("ROLLBACK! (" + this + ")");
      throw new RuntimeException("Installation of " + this + " failed", e);
    } finally {
      tx.finish();
    }
  }

  /**
     * Registers the plugin's OSGi service at the OSGi framework.
     * If the plugin doesn't provide a service nothing is performed.
     */
  private void registerPluginService() {
    String serviceInterface = getConfigProperty("providedServiceInterface");
    if (serviceInterface == null) {
      return;
    }
    try {
      logger.info("Registering service \"" + serviceInterface + "\" of " + this + " at OSGi framework");
      context.registerService(serviceInterface, this, null);
    } catch (Exception e) {
      throw new RuntimeException("Registering service of " + this + " at OSGi framework failed " + "(serviceInterface=\"" + serviceInterface + "\")", e);
    }
  }

  /**
     * Registers the plugin at the DeepaMehta core service. From that moment the plugin takes part of the
     * core service control flow, that is the plugin's hooks are triggered.
     */
  private void registerPlugin() {
    logger.info("Registering " + this + " at DeepaMehta 4 core service");
    dms.registerPlugin(this);
  }

  private void unregisterPlugin() {
    logger.info("Unregistering " + this + " at DeepaMehta 4 core service");
    dms.unregisterPlugin(pluginId);
  }

  private void registerWebResources() {
    String namespace = getWebResourcesNamespace();
    try {
      logger.info("Registering web resources of " + this + " at namespace " + namespace);
      httpService.registerResources(namespace, "/web", new PluginHTTPContext());
    } catch (NamespaceException e) {
      throw new RuntimeException("Registering web resources of " + this + " failed " + "(namespace=" + namespace + ")", e);
    }
  }

  private void unregisterWebResources() {
    String namespace = getWebResourcesNamespace();
    logger.info("Unregistering web resources of " + this);
    httpService.unregister(namespace);
  }

  private String getWebResourcesNamespace() {
    return getConfigProperty("webResourcesNamespace", "/" + pluginId);
  }

  private class PluginHTTPContext implements HttpContext {
    private HttpContext httpContext;

    private PluginHTTPContext() {
      httpContext = httpService.createDefaultHttpContext();
    }

    @Override public URL getResource(String name) {
      try {
        URL url;
        if (name.equals("web/")) {
          url = new URL("bundle://" + pluginBundle.getBundleId() + ".0:1/web/index.html");
        } else {
          url = httpContext.getResource(name);
        }
        return url;
      } catch (MalformedURLException e) {
        throw new RuntimeException("Mapping resource name \"" + name + "\" for plugin \"" + pluginName + "\" to an URL failed");
      }
    }

    @Override public String getMimeType(String name) {
      return httpContext.getMimeType(name);
    }

    @Override public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
      return httpContext.handleSecurity(request, response);
    }
  }

  private void registerRestResources() {
    String namespace = getConfigProperty("restResourcesNamespace");
    try {
      if (namespace != null) {
        logger.info("Registering REST resources of " + this + " at namespace " + namespace);
        if (pluginPackage.equals("de.deepamehta.core.service")) {
          throw new RuntimeException("Resource classes can\'t be located (plugin package is unknown). " + "You must implement a Plugin subclass OR configure \"pluginPackage\" in plugin.properties");
        }
        Dictionary initParams = new Hashtable();
        if (loadClass(pluginPackage + ".Application") != null) {
          initParams.put("javax.ws.rs.Application", pluginPackage + ".Application");
        } else {
          initParams.put("com.sun.jersey.config.property.packages", packagesToScan());
        }
        httpService.registerServlet(namespace, new ServletContainer(), initParams, null);
      }
    } catch (Exception e) {
      unregisterWebResources();
      throw new RuntimeException("Registering REST resources of " + this + " failed " + "(namespace=" + namespace + ")", e);
    }
  }

  private void unregisterRestResources() {
    String namespace = getConfigProperty("restResourcesNamespace");
    if (namespace != null) {
      logger.info("Unregistering REST resources of " + this);
      httpService.unregister(namespace);
    }
  }

  /**
     * Returns the packages Jersey have to scan (for root resource and provider classes) for this plugin.
     * These comprise:
     * 1) The plugin's "resources" package.
     * 2) The plugin's "provider" package.
     * 3) The deepamehta-webservice's "provider" package.
     *    This contains providers for DeepaMehta's core model classes, e.g. "Topic".
     */
  private String packagesToScan() {
    StringBuilder packages = new StringBuilder(pluginPackage + ".resources;");
    String pluginProviderPackage = pluginPackage + ".provider";
    if (!pluginProviderPackage.equals(STANDARD_PROVIDER_PACKAGE)) {
      packages.append(pluginProviderPackage + ";");
    }
    packages.append(STANDARD_PROVIDER_PACKAGE);
    return packages.toString();
  }

  private Properties readConfigFile() {
    try {
      Properties properties = new Properties();
      InputStream in = getResourceAsStream(PLUGIN_CONFIG_FILE);
      if (in != null) {
        logger.info("Reading config file \"" + PLUGIN_CONFIG_FILE + "\" for " + this);
        properties.load(in);
      } else {
        logger.info("Using default configuration for " + this + " (no config file found, " + "tried \"" + PLUGIN_CONFIG_FILE + "\")");
      }
      return properties;
    } catch (Exception e) {
      throw new RuntimeException("Reading config file for " + this + " failed", e);
    }
  }

  private String getConfigProperty(String key, String defaultValue) {
    return configProperties.getProperty(key, defaultValue);
  }

  private Map<String, Boolean> initDependencies() {
    Map<String, Boolean> dependencyState = new HashMap();
    String importModels = getConfigProperty("importModels");
    if (importModels != null) {
      String[] pluginIDs = importModels.split(", *");
      for (int i = 0; i < pluginIDs.length; i++) {
        if (!isPluginReady(pluginIDs[i])) {
          dependencyState.put(pluginIDs[i], false);
        }
      }
    }
    return dependencyState;
  }

  private boolean hasDependency(String pluginId) {
    return dependencyState.get(pluginId) != null;
  }

  private boolean dependenciesAvailable() {
    for (boolean available : dependencyState.values()) {
      if (!available) {
        return false;
      }
    }
    return true;
  }

  private void registerEventListener() {
    String[] topics = new String[] { PLUGIN_READY };
    Hashtable properties = new Hashtable();
    properties.put(EventConstants.EVENT_TOPIC, topics);
    context.registerService(EventHandler.class.getName(), this, properties);
  }

  @Override public void handleEvent(Event event) {
    try {
      if (event.getTopic().equals(PLUGIN_READY)) {
        String pluginId = (String) event.getProperty(EventConstants.BUNDLE_SYMBOLICNAME);
        if (hasDependency(pluginId)) {
          logger.info("### Receiving PLUGIN_READY event from \"" + pluginId + "\" for " + this);
          dependencyState.put(pluginId, true);
          checkServiceAvailability();
        }
      } else {
        throw new RuntimeException("Unexpected event: " + event);
      }
    } catch (Exception e) {
      logger.severe("Handling OSGi event for " + this + " failed (event=" + event + ")");
      e.printStackTrace();
    }
  }

  private void postPluginReadyEvent() {
    Properties properties = new Properties();
    properties.put(EventConstants.BUNDLE_SYMBOLICNAME, pluginId);
    eventService.postEvent(new Event(PLUGIN_READY, properties));
  }

  /**
     * Creates a Plugin topic in the DB, if not already exists.
     * <p>
     * A Plugin topic represents an installed plugin and is used to track its version.
     *
     * @return  <code>true</code> if a Plugin topic has been created (means: this is a plugin clean install),
     *          <code>false</code> otherwise.
     */
  private boolean initPluginTopic() {
    pluginTopic = findPluginTopic();
    if (pluginTopic != null) {
      logger.info("Installing " + this + " ABORTED -- already installed");
      return false;
    } else {
      logger.info("Installing " + this);
      pluginTopic = dms.createTopic(new TopicModel(pluginId, "dm4.core.plugin", new CompositeValue().put("dm4.core.plugin_name", pluginName).put("dm4.core.plugin_symbolic_name", pluginId).put("dm4.core.plugin_migration_nr", 0)), null);
      return true;
    }
  }

  private Topic findPluginTopic() {
    return dms.getTopic("uri", new SimpleValue(pluginId), false, null);
  }

  /**
     * Determines the migrations to be run for this plugin and run them.
     */
  private void runPluginMigrations(boolean isCleanInstall) {
    int migrationNr = pluginTopic.getChildTopicValue("dm4.core.plugin_migration_nr").intValue();
    int requiredMigrationNr = Integer.parseInt(getConfigProperty("requiredPluginMigrationNr", "0"));
    int migrationsToRun = requiredMigrationNr - migrationNr;
    logger.info("Running " + migrationsToRun + " plugin migrations (migrationNr=" + migrationNr + ", requiredMigrationNr=" + requiredMigrationNr + ")");
    for (int i = migrationNr + 1; i <= requiredMigrationNr; i++) {
      dms.runPluginMigration(this, i, isCleanInstall);
    }
  }

  private void introduceTypesToPlugin() {
    for (String topicTypeUri : dms.getTopicTypeUris()) {
      try {
        modifyTopicTypeHook(dms.getTopicType(topicTypeUri, null), null);
      } catch (Exception e) {
        throw new RuntimeException("Introducing topic type \"" + topicTypeUri + "\" to " + this + " failed", e);
      }
    }
  }
}
=======
public interface Plugin {
  InputStream getResourceAsStream(String name) throws IOException;
}
>>>>>>> /usr/src/app/output/jri/deepamehta/4da6c663b1ea70331355dedf74ede6c94179a97b/modules/dm4-core/src/main/java/de/deepamehta/core/service/Plugin.java/right.java
