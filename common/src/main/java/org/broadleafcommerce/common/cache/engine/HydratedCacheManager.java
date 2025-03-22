package org.broadleafcommerce.common.cache.engine;
import java.io.Serializable;

/**
 * 
 * @author jfischer
 *
 */
public interface HydratedCacheManager extends Serializable {
  public Object getHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName);

  public void addHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName, Object elementValue);
}