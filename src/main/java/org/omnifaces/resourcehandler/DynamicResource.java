package org.omnifaces.resourcehandler;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.faces.application.ResourceHandler.RESOURCE_IDENTIFIER;
import static org.omnifaces.util.Faces.getMapping;
import static org.omnifaces.util.Faces.getRequestContextPath;
import static org.omnifaces.util.Faces.getRequestDomainURL;
import static org.omnifaces.util.Faces.isPrefixMapping;
import static org.omnifaces.util.Utils.formatRFC1123;
import static org.omnifaces.util.Utils.parseRFC1123;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.context.FacesContext;
import org.omnifaces.util.Hacks;

public abstract class DynamicResource extends Resource {
  private static final int RESPONSE_HEADERS_SIZE = 4;

  private long lastModified;

  public DynamicResource(String resourceName, String libraryName, String contentType) {
    setResourceName(resourceName);
    setLibraryName(libraryName);
    setContentType(contentType);
  }

  @Override public String getRequestPath() {
    String mapping = getMapping();
    String path = RESOURCE_IDENTIFIER + "/" + getResourceName();
    return getRequestContextPath() + (isPrefixMapping(mapping) ? (mapping + path) : (path + mapping)) + "?ln=" + getLibraryName() + "&v=" + getLastModified();
  }

  @Override public URL getURL() {
    try {
      return new URL(getRequestDomainURL() + getRequestPath());
    } catch (MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @Override public Map<String, String> getResponseHeaders() {
    Map<String, String> responseHeaders = new HashMap<String, String>(RESPONSE_HEADERS_SIZE);
    responseHeaders.put("Last-Modified", formatRFC1123(new Date(getLastModified())));
    responseHeaders.put("Expires", formatRFC1123(new Date(System.currentTimeMillis() + Hacks.getDefaultResourceMaxAge())));
    responseHeaders.put("Etag", String.format("W/\"%d-%d\"", getResourceName().hashCode(), getLastModified()));
    responseHeaders.put("Pragma", "");
    return responseHeaders;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  @Override public boolean userAgentNeedsUpdate(FacesContext context) {
    String ifModifiedSince = context.getExternalContext().getRequestHeaderMap().get("If-Modified-Since");
    if (ifModifiedSince != null) {
      try {
        return getLastModified() > parseRFC1123(ifModifiedSince).getTime() + SECONDS.toMillis(1);
      } catch (ParseException ignore) {
        return true;
      }
    }
    return true;
  }
}