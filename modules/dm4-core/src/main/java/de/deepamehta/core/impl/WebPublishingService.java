package de.deepamehta.core.impl;
import de.deepamehta.core.osgi.CoreActivator;
import de.deepamehta.core.service.CoreService;
import de.deepamehta.core.util.JavaUtils;
import de.deepamehta.core.util.UniversalExceptionMapper;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class WebPublishingService {
  private static final String ROOT_APPLICATION_PATH = System.getProperty("dm4.webservice.path", "/");

  private ResourceConfig jerseyApplication;

  private int classCount = 0;

  private int singletonCount = 0;

  private ServletContainer jerseyServlet;

  private boolean isJerseyServletRegistered = false;

  private PersistenceLayer pl;

  private Logger logger = Logger.getLogger(getClass().getName());

  WebPublishingService(PersistenceLayer pl) {
    try {
      logger.info("Setting up the WebPublishingService");
      this.pl = pl;
      TransactionFactory tf = new TransactionFactory(pl);
      this.jerseyApplication = new DefaultResourceConfig();
      Map<String, Object> properties = jerseyApplication.getProperties();
      properties.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, new JerseyRequestFilter(tf, pl.em));
      properties.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, new JerseyResponseFilter(tf, pl.em));
      this.jerseyServlet = new ServletContainer(jerseyApplication);
    } catch (Exception e) {
      throw new RuntimeException("Setting up the WebPublishingService failed", e);
    }
  }

  /**
     * Publishes the bundle's web resources.
     * Web resources are found in the bundle's /web directory.
     */
  StaticResourcesPublication publishWebResources(String uriNamespace, Bundle bundle) throws NamespaceException {
    getHttpService().registerResources(uriNamespace, "/web", new BundleResourcesHTTPContext(bundle));
    return new StaticResourcesPublication(uriNamespace, this);
  }

  /**
     * Publishes a directory of the server's file system.
     *
     * @param   path    An absolute path to the directory to be published.
     */
  StaticResourcesPublication publishFileSystem(String uriNamespace, String path) throws NamespaceException {
    getHttpService().registerResources(uriNamespace, "/", new FileSystemHTTPContext(path));
    return new StaticResourcesPublication(uriNamespace, this);
  }

  void unpublishStaticResources(String uriNamespace) {
    HttpService httpService = getHttpService();
    if (httpService != null) {
      httpService.unregister(uriNamespace);
    } else {
      logger.warning("HTTP service is already gone");
    }
  }

  /**
     * Publishes REST resources. This is done by adding JAX-RS root resource and provider classes/singletons
     * to the Jersey application and reloading the Jersey servlet.
     * <p>
     * Note: synchronizing prevents creation of multiple Jersey servlet instances due to parallel plugin initialization.
     *
     * @param   singletons  the set of root resource and provider singletons, may be empty.
     * @param   classes     the set of root resource and provider classes, may be empty.
     */
  synchronized RestResourcesPublication publishRestResources(List<Object> singletons, List<Class<?>> classes) {
    try {
      addToApplication(singletons, classes);
      if (!isJerseyServletRegistered) {
        if (hasRootResources()) {
          registerJerseyServlet();
        }
      } else {
        reloadJerseyServlet();
      }
      return new RestResourcesPublication(singletons, classes, this);
    } catch (Exception e) {
      unpublishRestResources(singletons, classes);
      throw new RuntimeException("Adding classes/singletons to Jersey application failed", e);
    }
  }

  synchronized void unpublishRestResources(List<Object> singletons, List<Class<?>> classes) {
    removeFromApplication(singletons, classes);
    if (!hasRootResources()) {
      unregisterJerseyServlet();
    } else {
      reloadJerseyServlet();
    }
  }

  boolean isRootResource(Object object) {
    return getUriNamespace(object) != null;
  }

  String getUriNamespace(Object object) {
    Path path = object.getClass().getAnnotation(Path.class);
    return path != null ? path.value() : null;
  }

  boolean isProviderClass(Class clazz) {
    return clazz.isAnnotationPresent(Provider.class);
  }

  private HttpService getHttpService() {
    return CoreActivator.getHttpService();
  }

  private void addToApplication(List<Object> singletons, List<Class<?>> classes) {
    getClasses().addAll(classes);
    getSingletons().addAll(singletons);
    classCount += classes.size();
    singletonCount += singletons.size();
    logResourceInfo();
  }

  private void removeFromApplication(List<Object> singletons, List<Class<?>> classes) {
    getClasses().removeAll(classes);
    getSingletons().removeAll(singletons);
    classCount -= classes.size();
    singletonCount -= singletons.size();
    logResourceInfo();
  }

  private boolean hasRootResources() {
    return singletonCount > 0;
  }

  private void logResourceInfo() {
    logger.fine("##### DM Classes: " + classCount + ", All: " + getClasses().size() + " " + getClasses());
    logger.fine("##### DM Singletons: " + singletonCount + ", All: " + getSingletons().size() + " " + getSingletons());
  }

  private Set<Class<?>> getClasses() {
    return jerseyApplication.getClasses();
  }

  private Set<Object> getSingletons() {
    return jerseyApplication.getSingletons();
  }

  private void registerJerseyServlet() {
    try {
      logger.fine("########## Registering Jersey servlet at HTTP service (URI namespace=\"" + ROOT_APPLICATION_PATH + "\")");
      getHttpService().registerServlet(ROOT_APPLICATION_PATH, jerseyServlet, null, null);
      isJerseyServletRegistered = true;
    } catch (Exception e) {
      throw new RuntimeException("Registering Jersey servlet at HTTP service failed (URI namespace=\"" + ROOT_APPLICATION_PATH + "\")", e);
    }
  }

  private void unregisterJerseyServlet() {
    logger.fine("########## Unregistering Jersey servlet at HTTP service (URI namespace=\"" + ROOT_APPLICATION_PATH + "\")");
    HttpService httpService = getHttpService();
    if (httpService != null) {
      httpService.unregister(ROOT_APPLICATION_PATH);
    } else {
      logger.warning("HTTP service is already gone");
    }
    isJerseyServletRegistered = false;
  }

  private void reloadJerseyServlet() {
    logger.fine("##### Reloading Jersey servlet");
    jerseyServlet.reload();
  }

  private boolean staticResourceFilter(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      pl.em.fireEvent(CoreEvent.STATIC_RESOURCE_FILTER, request, response);
      return true;
    } catch (Throwable e) {
      new UniversalExceptionMapper(e, request).initResponse(response);
      return false;
    }
  }

  private class BundleResourcesHTTPContext implements HttpContext {
    private Bundle bundle;

    private BundleResourcesHTTPContext(Bundle bundle) {
      this.bundle = bundle;
    }

    @Override public URL getResource(String name) {
      if (name.equals("/web") || name.equals("/web/")) {
        name = "/web/index.html";
      }
      return bundle.getResource(name);
    }

    @Override public String getMimeType(String name) {
      return JavaUtils.getFileType(name);
    }

    @Override public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
      return staticResourceFilter(request, response);
    }
  }

  private class FileSystemHTTPContext implements HttpContext {
    private String basePath;

    /**
         * @param   basePath    An absolute path to a directory.
         */
    private FileSystemHTTPContext(String basePath) {
      this.basePath = basePath;
    }

    @Override public URL getResource(String name) {
      try {
        File file = new File(basePath, name);
        if (file.isDirectory()) {
          File index = new File(file, "index.html");
          if (index.exists()) {
            file = index;
          }
        }
        URL url = file.toURI().toURL();
        logger.fine("### Mapping resource name \"" + name + "\" to URL \"" + url + "\"");
        return url;
      } catch (Exception e) {
        throw new RuntimeException("Mapping resource name \"" + name + "\" to URL failed", e);
      }
    }

    @Override public String getMimeType(String name) {
      return JavaUtils.getFileType(name);
    }

    @Override public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
      return staticResourceFilter(request, response);
    }
  }
}