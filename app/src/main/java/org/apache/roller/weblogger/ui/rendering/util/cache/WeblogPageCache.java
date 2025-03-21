package org.apache.roller.weblogger.ui.rendering.util.cache;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.ui.rendering.util.WeblogPageRequest;
import org.apache.roller.weblogger.util.Utilities;
import org.apache.roller.weblogger.util.cache.Cache;
import org.apache.roller.weblogger.util.cache.CacheManager;
import org.apache.roller.weblogger.util.cache.LazyExpiringCacheEntry;

/**
 * Cache for weblog page content.
 */
public final class WeblogPageCache {
  private static Log log = LogFactory.getLog(WeblogPageCache.class);

  public static final String CACHE_ID = "cache.weblogpage";

  private boolean cacheEnabled = true;

  private Cache contentCache = null;

  private static final WeblogPageCache singletonInstance = new WeblogPageCache();

  private WeblogPageCache() {
    cacheEnabled = WebloggerConfig.getBooleanProperty(CACHE_ID + ".enabled");
    Map cacheProps = new HashMap();
    cacheProps.put("id", CACHE_ID);
    Enumeration allProps = WebloggerConfig.keys();
    String prop = null;
    while (allProps.hasMoreElements()) {
      prop = (String) allProps.nextElement();
      if (prop.startsWith(CACHE_ID + ".")) {
        cacheProps.put(prop.substring(CACHE_ID.length() + 1), WebloggerConfig.getProperty(prop));
      }
    }
    log.info(cacheProps);
    if (cacheEnabled) {
      contentCache = CacheManager.constructCache(null, cacheProps);
    } else {
      log.warn("Caching has been DISABLED");
    }
  }

  public static WeblogPageCache getInstance() {
    return singletonInstance;
  }

  public Object get(String key, long lastModified) {
    if (!cacheEnabled) {
      return null;
    }
    Object entry = null;
    LazyExpiringCacheEntry lazyEntry = (LazyExpiringCacheEntry) this.contentCache.get(key);
    if (lazyEntry != null) {
      entry = lazyEntry.getValue(lastModified);
      if (entry != null) {
        log.debug("HIT " + key);
      } else {
        log.debug("HIT-EXPIRED " + key);
      }
    } else {
      log.debug("MISS " + key);
    }
    return entry;
  }

  public void put(String key, Object value) {
    if (!cacheEnabled) {
      return;
    }
    contentCache.put(key, new LazyExpiringCacheEntry(value));
    log.debug("PUT " + key);
  }

  public void remove(String key) {
    if (!cacheEnabled) {
      return;
    }
    contentCache.remove(key);
    log.debug("REMOVE " + key);
  }

  public void clear() {
    if (!cacheEnabled) {
      return;
    }
    contentCache.clear();
    log.debug("CLEAR");
  }

  /**
     * Generate a cache key from a parsed weblog page request.
     * This generates a key of the form ...
     *
     * <handle>/<ctx>[/anchor][/language][/user]
     *   or
     * <handle>/<ctx>[/weblogPage][/date][/category][/language][/user]
     *
     *
     * examples ...
     *
     * foo/en
     * foo/entry_anchor
     * foo/20051110/en
     * foo/MyCategory/en/user=myname
     *
     */
  public String generateKey(WeblogPageRequest pageRequest) {
    StringBuilder key = new StringBuilder(128);
    key.append(this.CACHE_ID).append(':');
    key.append(pageRequest.getWeblogHandle());
    if (pageRequest.getWeblogAnchor() != null) {
      String anchor = null;
      try {
        anchor = URLEncoder.encode(pageRequest.getWeblogAnchor(), "UTF-8");
      } catch (UnsupportedEncodingException ex) {
      }
      key.append("/entry/").append(anchor);
    } else {
      if (pageRequest.getWeblogPageName() != null) {
        key.append("/page/").append(pageRequest.getWeblogPageName());
      }
      if (pageRequest.getWeblogDate() != null) {
        key.append('/').append(pageRequest.getWeblogDate());
      }
      if (pageRequest.getWeblogCategoryName() != null) {
        String cat = null;
        try {
          cat = URLEncoder.encode(pageRequest.getWeblogCategoryName(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        key.append('/').append(cat);
      }
      if ("tags".equals(pageRequest.getContext())) {
        key.append("/tags/");
        if (pageRequest.getTags() != null && !pageRequest.getTags().isEmpty()) {
          Set ordered = new TreeSet(pageRequest.getTags());
          String[] tags = (String[]) ordered.toArray(new String[ordered.size()]);
          key.append(Utilities.stringArrayToString(tags, "+"));
        }
      }
    }
    if (pageRequest.getLocale() != null) {
      key.append('/').append(pageRequest.getLocale());
    }
    if (pageRequest.getWeblogAnchor() == null) {
      key.append("/page=").append(pageRequest.getPageNum());
    }
    if (pageRequest.getAuthenticUser() != null) {
      key.append("/user=").append(pageRequest.getAuthenticUser());
    }
    key.append("/deviceType=").append(pageRequest.getDeviceType().toString());
    if (pageRequest.getWeblogPageName() != null && !pageRequest.getCustomParams().isEmpty()) {
      String queryString = paramsToString(pageRequest.getCustomParams());
      key.append("/qp=").append(queryString);
    }
    return key.toString();
  }

  private String paramsToString(Map<String, String[]> map) {
    if (map == null) {
      return null;
    }
    StringBuilder string = new StringBuilder();
    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      if (entry.getKey() != null) {
        string.append(',').append(entry.getKey()).append('=').append(entry.getValue()[0]);
      }
    }
    return Utilities.toBase64(string.toString().substring(1).getBytes());
  }
}