package de.deepamehta.core.osgi;
import de.deepamehta.core.service.DeepaMehtaService;
import de.deepamehta.core.service.SecurityHandler;
import de.deepamehta.core.impl.PluginImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for all DeepaMehta plugins.
 * All DeepaMehta plugins are derived from this class, directly or indirectly.
 * ### FIXDOC: subclassing is not required if the plugin has no server-side part.
 */
public class PluginActivator implements BundleActivator, PluginContext {
  protected DeepaMehtaService dms;

  protected Bundle bundle;

  private BundleContext bundleContext;

  private PluginImpl plugin;

  private String pluginName;

  private Logger logger = Logger.getLogger(getClass().getName());

  @Override public void start(BundleContext context) {
    try {
      this.bundleContext = context;
      this.bundle = context.getBundle();
      this.pluginName = (String) bundle.getHeaders().get("Bundle-Name");
      logger.info("========== Starting " + this + " ==========");
      plugin = new PluginImpl(this);
      plugin.start();
    } catch (Throwable e) {
      logger.log(Level.SEVERE, "Starting " + this + " failed", e);
    }
  }

  @Override public void stop(BundleContext context) {
    try {
      if (plugin == null) {
        logger.info("Stopping " + this + " ABORTED -- it was not successfully started");
        return;
      }
      logger.info("========== Stopping " + this + " ==========");
      plugin.stop();
    } catch (Throwable e) {
      logger.log(Level.SEVERE, "Stopping " + this + " failed", e);
    }
  }

  @Override public void init() {
  }

  @Override public void shutdown() {
  }

  @Override public void postInstall() {
  }

  @Override public final String getPluginName() {
    return pluginName;
  }

  @Override public final BundleContext getBundleContext() {
    return bundleContext;
  }

  @Override public final void setCoreService(DeepaMehtaService dms) {
    this.dms = dms;
  }

  @Override public String toString() {
    return "plugin \"" + pluginName + "\"";
  }

  protected final String getUri() {
    return plugin.getUri();
  }

  protected final InputStream getStaticResource(String name) {
    return plugin.getStaticResource(name);
  }

  /**
     * @param   securityHandler     Optional. If null no security is provided.
     */
  protected final void publishDirectory(String directoryPath, String uriNamespace, SecurityHandler securityHandler) {
    plugin.publishDirectory(directoryPath, uriNamespace, securityHandler);
  }
}