package org.galileo.easycache.springboot.keygenerator;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class SpelKeyGeneratorPolicy extends AbsKeyPolicy {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

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

        EvaluationContext evaluationContext = evaluator
                .createEvaluationContext(target, target.getClass(), method, args);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, target.getClass());

        if (cacheRemoveAll != null) {
            return Arrays.stream(cacheRemoveAll.value()).map(e -> {
                if (StringUtils.isEmpty(e.key())) {
                    return "";
                }
                try {
                    return evaluator.evalValue(e.key(), methodKey, evaluationContext, String.class);
                } catch (Exception ex) {
                    logger.warn("EasyCache SpelKeyGeneratorPolicy cacheRemoveAll evalValue error, key={}, e", e.key(), e);
                    return e.key();
                }
            }).collect(Collectors.toSet());
        }

        try {
            return evaluator.evalValue(key, methodKey, evaluationContext, String.class);
        } catch (Exception e) {
            logger.warn("EasyCache SpelKeyGeneratorPolicy evalValue error, key={}, e", key, e);
            return key;
        }
    }
}
