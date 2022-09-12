package org.galileo.easycache.springboot.keygenerator;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class LiteralKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        return key;
    }
}
