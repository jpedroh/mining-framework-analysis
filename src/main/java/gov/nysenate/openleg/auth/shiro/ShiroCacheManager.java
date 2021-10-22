package gov.nysenate.openleg.auth.shiro;

import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Chenguang He on 10/19/2016.
 */
public class ShiroCacheManager extends org.apache.shiro.cache.AbstractCacheManager {
    @Autowired
    private ShiroCachingService shiroCachingService;

    @Override
    protected org.apache.shiro.cache.Cache<Object, Object> createCache(String s) throws CacheException {
        return new ShiroCache(shiroCachingService.getEhcache());
    }
}
