package org.omnifaces.resourcehandler;
import static org.omnifaces.util.Faces.getMimeType;
import static org.omnifaces.util.Utils.toByteArray;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.application.Resource;
import org.omnifaces.component.output.cache.Cache;
import org.omnifaces.component.output.cache.CacheFactory;

/**
 * This {@link Resource} implementation holds all the necessary information about combined resources in order to
 * properly serve combined resources on a single HTTP request.
 * @author Bauke Scholtz
 */
public class CombinedResource extends DynamicResource {
  private static final String CACHE_SCOPE = "application";

  private String resourceId;

  private CombinedResourceInfo info;

  private Integer cacheTTL;

  /**
	 * Constructs a new combined resource based on the given resource name. This constructor is only used by
	 * {@link CombinedResourceHandler#createResource(String, String)}.
	 * @param resourceName The resource name of the combined resource.
	 */
  public CombinedResource(String resourceName, Integer cacheTTL) {
    super(resourceName, CombinedResourceHandler.LIBRARY_NAME, getMimeType(resourceName));
    String[] resourcePathParts = resourceName.split("\\.", 2)[0].split("/");
    resourceId = resourcePathParts[resourcePathParts.length - 1];
    info = CombinedResourceInfo.get(resourceId);

<<<<<<< Unknown file: This is a bug in JDime.
=======
    this.cacheTTL = cacheTTL;
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/right.java
  }

  @Override public long getLastModified() {
    return (info != null) ? info.getLastModified() : super.getLastModified();
  }

  @Override public InputStream getInputStream() throws IOException {
    if (info != null && !info.getResources().isEmpty()) {
      if (cacheTTL == null) {
        return new CombinedResourceInputStream(info.getResources());
      } else {
        return getInputStreamFromCache();
      }
    } else {
      return null;
    }
  }

  /**
	 * Returns the cached input stream, or if there is none, then create one.
	 */
  private InputStream getInputStreamFromCache() throws IOException {
    Cache combinedResourceCache = CacheFactory.getCache(FacesContext.getCurrentInstance(), CACHE_SCOPE);
    byte[] cachedCombinedResource;
    synchronized (CombinedResourceHandler.class) {
      cachedCombinedResource = (byte[]) combinedResourceCache.getObject(resourceId);
    }
    if (cachedCombinedResource == null) {
      cachedCombinedResource = toByteArray(new CombinedResourceInputStream(info.getResources()));
      synchronized (CombinedResourceHandler.class) {
        if (combinedResourceCache.getObject(resourceId) == null) {
          combinedResourceCache.putObject(resourceId, cachedCombinedResource, cacheTTL);
        }
      }
    }
    return new ByteArrayInputStream(cachedCombinedResource);
  }
}