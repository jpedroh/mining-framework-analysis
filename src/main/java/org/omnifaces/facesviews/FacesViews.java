package org.omnifaces.facesviews;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Locale.US;
import static java.util.regex.Pattern.quote;
import static org.omnifaces.util.Faces.getApplicationAttribute;
import static org.omnifaces.util.Platform.getFacesServletRegistration;
import static org.omnifaces.util.ResourcePaths.getExtension;
import static org.omnifaces.util.ResourcePaths.isDirectory;
import static org.omnifaces.util.ResourcePaths.stripExtension;
import static org.omnifaces.util.ResourcePaths.stripPrefixPath;
import static org.omnifaces.util.Servlets.getApplicationAttribute;
import static org.omnifaces.util.Servlets.getRequestBaseURL;
import static org.omnifaces.util.Utils.csvToList;
import static org.omnifaces.util.Utils.isEmpty;
import static org.omnifaces.util.Utils.reverse;
import static org.omnifaces.util.Utils.startsWithOneOf;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;

public final class FacesViews {
  private FacesViews() {
  }

  public static final String WEB_INF_VIEWS = "/WEB-INF/faces-views/";

  public static final String FACES_VIEWS_ENABLED_PARAM_NAME = "org.omnifaces.FACES_VIEWS_ENABLED";

  public static final String FACES_VIEWS_SCAN_PATHS_PARAM_NAME = "org.omnifaces.FACES_VIEWS_SCAN_PATHS";

  public static final String FACES_VIEWS_SCANNED_VIEWS_EXTENSIONLESS_PARAM_NAME = "org.omnifaces.FACES_VIEWS_SCANNED_VIEWS_ALWAYS_EXTENSIONLESS";

  public static final String FACES_VIEWS_EXTENSION_ACTION_PARAM_NAME = "org.omnifaces.FACES_VIEWS_EXTENSION_ACTION";

  public static final String FACES_VIEWS_PATH_ACTION_PARAM_NAME = "org.omnifaces.FACES_VIEWS_PATH_ACTION";

  public static final String FACES_VIEWS_DISPATCH_METHOD_PARAM_NAME = "org.omnifaces.FACES_VIEWS_DISPATCH_METHOD";

  public static final String FACES_VIEWS_FILTER_AFTER_DECLARED_FILTERS_PARAM_NAME = "org.omnifaces.FACES_VIEWS_FILTER_AFTER_DECLARED_FILTERS";

  public static final String FACES_VIEWS_VIEW_HANDLER_MODE_PARAM_NAME = "org.omnifaces.FACES_VIEWS_VIEW_HANDLER_MODE";

  public static final String SCAN_PATHS = "org.omnifaces.facesviews.scanpaths";

  public static final String PUBLIC_SCAN_PATHS = "org.omnifaces.facesviews.public.scanpaths";

  public static final String SCANNED_VIEWS_EXTENSIONLESS = "org.omnifaces.facesviews.scannedviewsextensionless";

  public static final String FACES_SERVLET_EXTENSIONS = "org.omnifaces.facesviews.facesservletextensions";

  public static final String FACES_VIEWS_RESOURCES = "org.omnifaces.facesviews";

  public static final String FACES_VIEWS_REVERSE_RESOURCES = "org.omnifaces.facesviews.reverse.resources";

  public static final String FACES_VIEWS_RESOURCES_EXTENSIONS = "org.omnifaces.facesviews.extensions";

  public static final String FACES_VIEWS_ORIGINAL_SERVLET_PATH = "org.omnifaces.facesviews.original.servlet_path";

  public static void scanViewsFromRootPaths(ServletContext servletContext, Map<String, String> collectedViews, Set<String> collectedExtensions) {
    for (String rootPath : getRootPaths(servletContext)) {
      String extensionToScan = null;
      if (rootPath.contains("*")) {
        String[] pathAndExtension = rootPath.split(quote("*"));
        rootPath = pathAndExtension[0];
        extensionToScan = pathAndExtension[1];
      }
      rootPath = normalizeRootPath(rootPath);
      scanViews(servletContext, rootPath, servletContext.getResourcePaths(rootPath), collectedViews, extensionToScan, collectedExtensions);
    }
  }

  public static Set<String> getRootPaths(ServletContext servletContext) {
    @SuppressWarnings(value = { "unchecked" }) Set<String> rootPaths = (Set<String>) servletContext.getAttribute(SCAN_PATHS);
    if (rootPaths == null) {
      rootPaths = new HashSet<String>(csvToList(servletContext.getInitParameter(FACES_VIEWS_SCAN_PATHS_PARAM_NAME)));
      rootPaths.add(WEB_INF_VIEWS);
      servletContext.setAttribute(SCAN_PATHS, unmodifiableSet(rootPaths));
    }
    return rootPaths;
  }

  public static Set<String> getPublicRootPaths(ServletContext servletContext) {
    @SuppressWarnings(value = { "unchecked" }) Set<String> publicRootPaths = (Set<String>) servletContext.getAttribute(PUBLIC_SCAN_PATHS);
    if (publicRootPaths == null) {
      Set<String> rootPaths = getRootPaths(servletContext);
      publicRootPaths = new HashSet<String>();
      for (String rootPath : rootPaths) {
        if (rootPath.contains("*")) {
          String[] pathAndExtension = rootPath.split(quote("*"));
          rootPath = pathAndExtension[0];
        }
        rootPath = normalizeRootPath(rootPath);
        if (!"/".equals(rootPath) && !startsWithOneOf(rootPath, "/WEB-INF/", "/META-INF/")) {
          publicRootPaths.add(rootPath);
        }
      }
      servletContext.setAttribute(PUBLIC_SCAN_PATHS, unmodifiableSet(publicRootPaths));
    }
    return publicRootPaths;
  }

  public static String normalizeRootPath(String rootPath) {
    String normalizedPath = rootPath;
    if (!normalizedPath.startsWith("/")) {
      normalizedPath = "/" + normalizedPath;
    }
    if (!normalizedPath.endsWith("/")) {
      normalizedPath = normalizedPath + "/";
    }
    return normalizedPath;
  }

  public static boolean isResourceInPublicPath(ServletContext servletContext, String resource) {
    Set<String> publicPaths = getPublicRootPaths(servletContext);
    for (String path : publicPaths) {
      if (resource.startsWith(path)) {
        return true;
      }
    }
    return false;
  }

  public static ExtensionAction getExtensionAction(ServletContext servletContext) {
    String extensionActionString = servletContext.getInitParameter(FACES_VIEWS_EXTENSION_ACTION_PARAM_NAME);
    if (isEmpty(extensionActionString)) {
      return ExtensionAction.REDIRECT_TO_EXTENSIONLESS;
    }
    try {
      return ExtensionAction.valueOf(extensionActionString.toUpperCase(US));
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Value \'%s\' is not valid for context parameter for \'%s\'", extensionActionString, FACES_VIEWS_EXTENSION_ACTION_PARAM_NAME), e);
    }
  }

  public static PathAction getPathAction(ServletContext servletContext) {
    String pathActionString = servletContext.getInitParameter(FACES_VIEWS_PATH_ACTION_PARAM_NAME);
    if (isEmpty(pathActionString)) {
      return PathAction.SEND_404;
    }
    try {
      return PathAction.valueOf(pathActionString.toUpperCase(US));
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Value \'%s\' is not valid for context parameter for \'%s\'", pathActionString, FACES_VIEWS_PATH_ACTION_PARAM_NAME), e);
    }
  }

  public static FacesServletDispatchMethod getFacesServletDispatchMethod(ServletContext servletContext) {
    String dispatchMethodString = servletContext.getInitParameter(FACES_VIEWS_DISPATCH_METHOD_PARAM_NAME);
    if (isEmpty(dispatchMethodString)) {
      return FacesServletDispatchMethod.DO_FILTER;
    }
    try {
      return FacesServletDispatchMethod.valueOf(dispatchMethodString.toUpperCase(US));
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Value \'%s\' is not valid for context parameter for \'%s\'", dispatchMethodString, FACES_VIEWS_DISPATCH_METHOD_PARAM_NAME), e);
    }
  }

  public static ViewHandlerMode getViewHandlerMode(FacesContext context) {
    return getViewHandlerMode((ServletContext) context.getExternalContext().getContext());
  }

  public static ViewHandlerMode getViewHandlerMode(ServletContext servletContext) {
    String viewHandlerModeString = servletContext.getInitParameter(FACES_VIEWS_VIEW_HANDLER_MODE_PARAM_NAME);
    if (isEmpty(viewHandlerModeString)) {
      return ViewHandlerMode.STRIP_EXTENSION_FROM_PARENT;
    }
    try {
      return ViewHandlerMode.valueOf(viewHandlerModeString.toUpperCase(US));
    } catch (Exception e) {
      throw new IllegalStateException(String.format("Value \'%s\' is not valid for context parameter for \'%s\'", viewHandlerModeString, FACES_VIEWS_VIEW_HANDLER_MODE_PARAM_NAME), e);
    }
  }

  public static boolean isFilterAfterDeclaredFilters(ServletContext servletContext) {
    String filterAfterDeclaredFilters = servletContext.getInitParameter(FACES_VIEWS_FILTER_AFTER_DECLARED_FILTERS_PARAM_NAME);
    if (filterAfterDeclaredFilters == null) {
      return true;
    }
    return Boolean.valueOf(filterAfterDeclaredFilters);
  }

  public static boolean isScannedViewsAlwaysExtensionless(final FacesContext context) {
    ExternalContext externalContext = context.getExternalContext();
    Map<String, Object> applicationMap = externalContext.getApplicationMap();
    Boolean scannedViewsExtensionless = (Boolean) applicationMap.get(SCANNED_VIEWS_EXTENSIONLESS);
    if (scannedViewsExtensionless == null) {
      if (externalContext.getInitParameter(FACES_VIEWS_SCANNED_VIEWS_EXTENSIONLESS_PARAM_NAME) == null) {
        scannedViewsExtensionless = true;
      } else {
        scannedViewsExtensionless = Boolean.valueOf(externalContext.getInitParameter(FACES_VIEWS_SCANNED_VIEWS_EXTENSIONLESS_PARAM_NAME));
      }
      applicationMap.put(SCANNED_VIEWS_EXTENSIONLESS, scannedViewsExtensionless);
    }
    return scannedViewsExtensionless;
  }

  public static void scanViews(ServletContext servletContext, String rootPath, Set<String> resourcePaths, Map<String, String> collectedViews, String extensionToScan, Set<String> collectedExtensions) {
    if (!isEmpty(resourcePaths)) {
      for (String resourcePath : resourcePaths) {
        if (isDirectory(resourcePath)) {
          if (canScanDirectory(rootPath, resourcePath)) {
            scanViews(servletContext, rootPath, servletContext.getResourcePaths(resourcePath), collectedViews, extensionToScan, collectedExtensions);
          }
        } else {
          if (canScanResource(resourcePath, extensionToScan)) {
            String resource = stripPrefixPath(rootPath, resourcePath);
            collectedViews.put(resource, resourcePath);
            collectedViews.put(stripExtension(resource), resourcePath);
            if (collectedExtensions != null) {
              collectedExtensions.add("*" + getExtension(resourcePath));
            }
          }
        }
      }
    }
  }

  public static boolean canScanDirectory(String rootPath, String directory) {
    if (!"/".equals(rootPath)) {
      return true;
    }
    return !startsWithOneOf(directory, "/WEB-INF/", "/META-INF/", "/resources/");
  }

  public static boolean canScanResource(String resource, String extensionToScan) {
    if (extensionToScan == null) {
      return true;
    }
    return resource.endsWith(extensionToScan);
  }

  public static Map<String, String> scanViews(ServletContext servletContext) {
    Map<String, String> collectedViews = new HashMap<String, String>();
    scanViewsFromRootPaths(servletContext, collectedViews, null);
    return collectedViews;
  }

  public static void tryScanAndStoreViews(ServletContext context) {
    if (getApplicationAttribute(context, FACES_VIEWS_RESOURCES) == null) {
      scanAndStoreViews(context);
    }
  }

  public static Map<String, String> scanAndStoreViews(ServletContext context) {
    Map<String, String> views = scanViews(context);
    if (!views.isEmpty()) {
      context.setAttribute(FACES_VIEWS_RESOURCES, unmodifiableMap(views));
      context.setAttribute(FACES_VIEWS_REVERSE_RESOURCES, unmodifiableMap(reverse(views)));
    }
    return views;
  }

  public static String stripFacesViewsPrefix(final String resource) {
    return stripPrefixPath(WEB_INF_VIEWS, resource);
  }

  public static String getMappedPath(String path) {
    String facesViewsPath = path;
    Map<String, String> mappedResources = getApplicationAttribute(FACES_VIEWS_RESOURCES);
    if (mappedResources != null && mappedResources.containsKey(path)) {
      facesViewsPath = mappedResources.get(path);
    }
    return facesViewsPath;
  }

  public static void mapFacesServlet(ServletContext servletContext, Set<String> extensions) {
    ServletRegistration facesServletRegistration = getFacesServletRegistration(servletContext);
    if (facesServletRegistration != null) {
      Collection<String> mappings = facesServletRegistration.getMappings();
      for (String extension : extensions) {
        if (!mappings.contains(extension)) {
          facesServletRegistration.addMapping(extension);
        }
      }
    }
  }

  public static Set<String> getFacesServletExtensions(FacesContext context) {
    return getFacesServletExtensions((ServletContext) context.getExternalContext().getContext());
  }

  public static Set<String> getFacesServletExtensions(ServletContext servletContext) {
    @SuppressWarnings(value = { "unchecked" }) Set<String> extensions = (Set<String>) servletContext.getAttribute(FACES_SERVLET_EXTENSIONS);
    if (extensions == null) {
      extensions = new HashSet<String>();
      ServletRegistration facesServletRegistration = getFacesServletRegistration(servletContext);
      if (facesServletRegistration != null) {
        Collection<String> mappings = facesServletRegistration.getMappings();
        for (String mapping : mappings) {
          if (mapping.startsWith("*")) {
            extensions.add(mapping.substring(1));
          }
        }
      }
      servletContext.setAttribute(FACES_SERVLET_EXTENSIONS, unmodifiableSet(extensions));
    }
    return extensions;
  }

  public static String getExtensionlessURLWithQuery(HttpServletRequest request) {
    return getExtensionlessURLWithQuery(request, request.getServletPath());
  }

  public static String getExtensionlessURLWithQuery(HttpServletRequest request, String resource) {
    String queryString = !isEmpty(request.getQueryString()) ? "?" + request.getQueryString() : "";
    String baseURL = getRequestBaseURL(request);
    if (baseURL.endsWith("/")) {
      baseURL = baseURL.substring(0, baseURL.length() - 1);
    }
    return baseURL + stripExtension(resource) + queryString;
  }
}