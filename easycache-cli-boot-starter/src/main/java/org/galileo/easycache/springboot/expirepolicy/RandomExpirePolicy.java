package org.galileo.easycache.springboot.expirepolicy;

import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomExpirePolicy extends AbsExpirePolicy {

    private double rate = 0.3;

    @Override
    public long expire(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        long expire = AnnoAttributeUtil.getExpire(cached, cacheUpdate);
        if (expire <= 5 * 1000) {
            return expire;
        }
        return ThreadLocalRandom.current().nextLong(expire - (long) (expire * rate));
    }
}
