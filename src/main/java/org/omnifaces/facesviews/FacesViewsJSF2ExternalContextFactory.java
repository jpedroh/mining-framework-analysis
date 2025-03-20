package org.omnifaces.facesviews;
import static org.omnifaces.facesviews.FacesViews.getMappedPath;
import static org.omnifaces.facesviews.FacesViews.scanAndStoreViews;
import static org.omnifaces.util.Faces.getServletContext;
import static org.omnifaces.util.Faces.isDevelopment;
import java.net.MalformedURLException;
import java.net.URL;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.faces.context.ExternalContextWrapper;

/**
 * External context factory that installs an external context which locates resources just
 * like the {@link FacesViewsResolver} does.
 * <p>
 * <b>This is only needed for JSF 2.0 implementations and is not needed for JSF 2.1+.</b>
 *
 * @since 1.6
 * @author Arjan Tijms
 *
 */
public class FacesViewsJSF2ExternalContextFactory extends ExternalContextFactory {
  private ExternalContextFactory parent;

  public FacesViewsJSF2ExternalContextFactory(ExternalContextFactory parent) {
    this.parent = parent;
  }

  @Override public ExternalContext getExternalContext(Object context, Object request, Object response) throws FacesException {
    return new FacesViewsJSF2ExternalContext(getWrapped().getExternalContext(context, request, response));
  }

  @Override public ExternalContextFactory getWrapped() {
    return parent;
  }

  public static class FacesViewsJSF2ExternalContext extends ExternalContextWrapper {
    private ExternalContext wrapped;

    public FacesViewsJSF2ExternalContext(ExternalContext wrapped) {
      this.wrapped = wrapped;
    }

    @Override public URL getResource(String path) throws MalformedURLException {
      URL resource = super.getResource(getMappedPath(path));
      if (resource == null && isDevelopment()) {
        scanAndStoreViews(getServletContext());
        resource = super.getResource(getMappedPath(path));
      }
      return resource;
    }

    @Override public ExternalContext getWrapped() {
      return wrapped;
    }
  }
}