package org.galileo.easycache.springboot.keygenerator;

import com.googlecode.aviator.AviatorEvaluator;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Deprecated 牺牲空间换时间, 会大量占用元空间内存
 */
@Deprecated
public class AviatorKeyGeneratorPolicy extends AbsKeyPolicy {

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        String key = AnnoAttributeUtil.getKey(cached, cacheRemove, cacheUpdate);
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        Map<String, Object> map = objectToMap(method, args);
        if (cacheRemoveAll != null) {
            return Arrays.stream(cacheRemoveAll.value()).map(e -> {
                if (StringUtils.isEmpty(e.key())) {
                    return "";
                }
                return AviatorEvaluator.execute(e.key(), map);
            }).collect(Collectors.toSet());
        }
        return AviatorEvaluator.execute(key, map);
    }
}
