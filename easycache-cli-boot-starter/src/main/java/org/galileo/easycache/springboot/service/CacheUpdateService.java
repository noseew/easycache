package org.galileo.easycache.springboot.service;

import org.aopalliance.intercept.MethodInvocation;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.common.CacheLock;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.ConsistencyType;

import java.lang.reflect.Method;

public class CacheUpdateService extends AbsCacheService {

    public Object cacheUpdateProcess(CacheUpdate cacheUpdate, MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Object[] args = invocation.getArguments();

        // 注解基本信息
        String namespace = getNamespace(cacheUpdate.namespace());
        String cacheName = getCacheName(cacheUpdate.cacheName(), target, method);
        String dynaKey = getDynaKey(cacheUpdate.keyPolicy(), target, method, args);
        String fullKey = buildFullKey(namespace, cacheName, dynaKey);

        if (easyCacheConfig.getNs().get(namespace) == null) {
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

        long expireByPolicy = getExpireByPolicy(cacheUpdate.expirePolicy(), target, method, args);
        long expire = getExpire(namespace, cacheName, dynaKey, cacheUpdate.expire(), expireByPolicy);

        return cacheUpdateProcess(cache, fullKey, expire, cacheUpdate.consistency(), invocation);
    }

    private Object cacheUpdateProcess(CacheProxy cache, String fullKey, long expire, ConsistencyType consistencyType,
            MethodInvocation invocation) throws Throwable {

        boolean injectTrans = consistencyType.equals(ConsistencyType.STRONG);
        Object result = invocation.proceed();
        executeWithinTrans(() -> {
            CacheLock redisLocker = cache.getCacheLocker(false);
            boolean locked = false;
            try {
                locked = redisLocker.lock(CacheLock.lockKey(fullKey), easyCacheConfig.getPierceDefend().getLockTime().toMillis());
            } catch (Exception ignore) {
                // ignore
            }
            if (locked) {
                try {
                    ValWrapper valWrapper = ValWrapper.createInstance(expire, result);
                    setRealExpireTs(valWrapper, false);
                    cache.put(fullKey, valWrapper);
                } catch (Exception e) {
                    logger.error("EasyCache cache put error ", e);
                    if (injectTrans) {
                        if (easyCacheConfig.isDebug()) {
                            logger.debug("EasyCache 事务内更新失败, 抛出异常, key={} ", fullKey);
                        }
                        throw e;
                    }
                } finally {
                    redisLocker.unlock(CacheLock.lockKey(fullKey));
                }
            }
        }, () -> {
            removeCache(cache, fullKey);
        }, injectTrans);
        return result;

    }

}
