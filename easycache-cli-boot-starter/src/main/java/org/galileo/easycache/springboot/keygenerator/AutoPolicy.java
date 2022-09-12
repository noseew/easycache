package org.galileo.easycache.springboot.keygenerator;

import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class AutoPolicy extends AbsKeyPolicy {

    @Autowired
    private KeyGeneratorPolicy paramStringPolicy;
    @Autowired
    private KeyGeneratorPolicy literalKeyGeneratorPolicy;
    @Autowired
    private KeyGeneratorPolicy spelKeyGeneratorPolicy;
    @Autowired
    private KeyGeneratorPolicy noneKeyGeneratorPolicy;

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        if (cacheRemoveAll != null) {
            return Arrays.stream(cacheRemoveAll.value()).map(e -> parseKey(target, method, e.key(), args))
                    .collect(Collectors.toSet());
        }
        return parseKey(target, method, key, args);
    }

    private Object parseKey(Object target, Method method, String key, Object[] args) {
        if (StringUtils.isEmpty(key) || CacheConstants.DEFAULT_KEY.equals(key)) {
            if (args == null || args.length == 0) {
                return noneKeyGeneratorPolicy.generateKey(target, method, args);
            }
            return paramStringPolicy.generateKey(target, method, args);
        }
        try {
            return spelKeyGeneratorPolicy.generateKey(target, method, args);
        } catch (Exception e) {
            return literalKeyGeneratorPolicy.generateKey(target, method, args);
        }
    }
}
