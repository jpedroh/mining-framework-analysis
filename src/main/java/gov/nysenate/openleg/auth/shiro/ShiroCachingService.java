package gov.nysenate.openleg.auth.shiro;

import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import org.ehcache.Cache;
import org.springframework.stereotype.Service;

import static gov.nysenate.openleg.legislation.CacheType.SHIRO;

/**
 * Created by Chenguang He on 10/19/2016.
 */
@Service
public class ShiroCachingService extends CachingService<Object, Object> {

    @Override
    protected CacheType cacheType() {
        return SHIRO;
    }

    protected Cache<Object, Object> getEhcache() {
        return cache;
    }
}
