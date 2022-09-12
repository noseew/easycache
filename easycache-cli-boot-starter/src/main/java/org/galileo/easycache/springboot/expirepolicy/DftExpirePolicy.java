package org.galileo.easycache.springboot.expirepolicy;

import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class DftExpirePolicy extends AbsExpirePolicy {

    @Override
    public long expire(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        return AnnoAttributeUtil.getExpire(cached, cacheUpdate);
    }
}
