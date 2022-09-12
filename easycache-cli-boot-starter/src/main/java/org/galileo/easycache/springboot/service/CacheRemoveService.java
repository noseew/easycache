package org.galileo.easycache.springboot.service;

import org.aopalliance.intercept.MethodInvocation;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.ConsistencyType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class CacheRemoveService extends AbsCacheService {

    public Object cacheRemoveProcess(CacheRemove cacheRemove, MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Object[] args = invocation.getArguments();

        // 注解基本信息
        String namespace = getNamespace(cacheRemove.namespace());
        String cacheName = getCacheName(cacheRemove.cacheName(), target, method);
        Set<String> dynaKeys = getDynaKeys(cacheRemove.keyPolicy(), target, method, args);
        Set<String> fullKeys = buildFullKeys(namespace, cacheName, dynaKeys);

        if (easyCacheConfig.getRemote().get(namespace) == null) {
            logger.warn("EasyCache 'namespace' 配置不存在 {}", namespace);
            return invocation.proceed();
        }

        // 缓存实例bean
        String cacheBeanName = CacheConstants.cacheBeanName(namespace);
        CacheProxy cache = getCacheInstance(cacheBeanName);
        if (cache == null) {
            logger.warn("EasyCache 'cacheBeanName' bean不存在 {}", cacheBeanName);
            return invocation.proceed();
        }

        return cacheRemoveProcess(cache, fullKeys, cacheRemove.consistency(), invocation);
    }


    private Object cacheRemoveProcess(CacheProxy cache, Set<String> fullKeys, ConsistencyType consistencyType,
            MethodInvocation invocation) throws Throwable {

        boolean injectTrans = consistencyType.equals(ConsistencyType.STRONG);
        Object result = invocation.proceed();
        executeWithinTrans(() -> {
            try {
                if (fullKeys.size() == 1) {
                    cache.remove(fullKeys.iterator().next());
                } else {
                    cache.removeAll(fullKeys);
                }
            } catch (Exception e) {
                logger.error("EasyCache 注解处理removeAll error ", e);
                if (injectTrans) {
                    if (easyCacheConfig.isDebug()) {
                        logger.debug("EasyCache 事务内删除失败, 抛出异常, key={} ", Arrays.toString(fullKeys.toArray()));
                    }
                    throw e;
                }
            }
        }, () -> {}, injectTrans);
        return result;
    }

}
