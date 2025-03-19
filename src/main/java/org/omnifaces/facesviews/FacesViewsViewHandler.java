package org.omnifaces.facesviews;
import static javax.servlet.RequestDispatcher.FORWARD_SERVLET_PATH;
import static org.omnifaces.facesviews.FacesViews.FACES_VIEWS_ORIGINAL_SERVLET_PATH;
import static org.omnifaces.facesviews.FacesViews.FACES_VIEWS_RESOURCES;
import static org.omnifaces.facesviews.FacesViews.getFacesServletExtensions;
import static org.omnifaces.facesviews.FacesViews.getViewHandlerMode;
import static org.omnifaces.facesviews.FacesViews.isScannedViewsAlwaysExtensionless;
import static org.omnifaces.facesviews.ViewHandlerMode.STRIP_EXTENSION_FROM_PARENT;
import static org.omnifaces.util.FacesLocal.getApplicationAttribute;
import static org.omnifaces.util.FacesLocal.getRequestAttribute;
import static org.omnifaces.util.ResourcePaths.getExtension;
import static org.omnifaces.util.ResourcePaths.isExtensionless;
import static org.omnifaces.util.ResourcePaths.stripExtension;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * View handler that renders an action URL extensionless if a resource is a mapped one, and faces views has been set to always
 * render extensionless or if the current request is extensionless, otherwise as-is.
 * 
 * <p>
 * For a guide on FacesViews, please see the <a href="package-summary.html">package summary</a>.
 *
 * @author Arjan Tijms
 *
 */
public class FacesViewsViewHandler extends ViewHandlerWrapper {
  private final ViewHandler wrapped;

  public FacesViewsViewHandler(ViewHandler viewHandler) {
    wrapped = viewHandler;
  }

  @Override public String getActionURL(FacesContext context, String viewId) {
    String actionURL = super.getActionURL(context, viewId);
    Map<String, String> mappedResources = getApplicationAttribute(context, FACES_VIEWS_RESOURCES);
    if (mappedResources.containsKey(viewId)) {
      if (isScannedViewsAlwaysExtensionless(context) || isOriginalViewExtensionless(context)) {
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        if (servletContext.getMajorVersion() > 2 && getViewHandlerMode(servletContext) == STRIP_EXTENSION_FROM_PARENT) {
          return removeExtension(context, actionURL, viewId);
        } else {
          return context.getExternalContext().getRequestContextPath() + stripExtension(viewId) + getQueryParameters(actionURL);
        }
      }
    }
    return actionURL;
  }

  private boolean isOriginalViewExtensionless(FacesContext context) {
    String originalViewId = getRequestAttribute(context, FORWARD_SERVLET_PATH);
    if (originalViewId == null) {
      originalViewId = getRequestAttribute(context, FACES_VIEWS_ORIGINAL_SERVLET_PATH);
    }
    return isExtensionless(originalViewId);
  }

  public String removeExtension(FacesContext context, String resource, String viewId) {
    Set<String> extensions = getFacesServletExtensions(context);
    if (!isExtensionless(viewId)) {
      String viewIdExtension = getExtension(viewId);
      if (!extensions.contains(viewIdExtension)) {
        extensions = new HashSet<String>(extensions);
        extensions.add(viewIdExtension);
      }
    }
    int lastSlashPos = resource.lastIndexOf('/');
    int lastQuestionMarkPos = resource.lastIndexOf('?');
    for (String extension : extensions) {
      int extensionPos = resource.lastIndexOf(extension);
      if (extensionPos > lastSlashPos && (lastQuestionMarkPos == -1 || extensionPos < lastQuestionMarkPos)) {
        return resource.substring(0, extensionPos) + resource.substring(extensionPos + extension.length());
      }
    }
    return resource;
  }

  /**
	 * Extracts the query string from a resource.
	 *
	 * @param resource
	 *            A URL string
	 * @return the query string part of the URL
	 */
  public static String getQueryParameters(final String resource) {
    String queryParameters = "";
    int questionMarkPos = resource.indexOf('?');
    if (questionMarkPos != -1) {
      queryParameters = resource.substring(questionMarkPos);
    }
    return queryParameters;
  }

  @Override public ViewHandler getWrapped() {
    return wrapped;
  }
}