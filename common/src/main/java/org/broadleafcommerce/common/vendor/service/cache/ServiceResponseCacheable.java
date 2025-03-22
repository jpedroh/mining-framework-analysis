package org.broadleafcommerce.common.vendor.service.cache;
import javax.cache.Cache;

/**
 * @author jfischer
 *
 */
public interface ServiceResponseCacheable {
  public void clearCache();

  public Cache getCache();
}