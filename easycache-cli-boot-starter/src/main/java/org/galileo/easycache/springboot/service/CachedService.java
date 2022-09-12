package org.galileo.easycache.springboot.service;

import org.aopalliance.intercept.MethodInvocation;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.common.CacheLock;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.BreakdownType;
import org.galileo.easycache.core.core.config.PierceDefend;

import java.lang.reflect.Method;

public class CachedService extends AbsCacheService {

    public Object cachedProcess(Cached cached, MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Object[] args = invocation.getArguments();

        // 注解基本信息
        String namespace = getNamespace(cached.namespace());
        String cacheName = getCacheName(cached.cacheName(), target, method);
        String dynaKey = getDynaKey(cached.keyPolicy(), target, method, args);
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

        long expireByPolicy = getExpireByPolicy(cached.expirePolicy(), target, method, args);
        long expire = getExpire(namespace, cacheName, dynaKey, cached.expire(), expireByPolicy);
//        try {
//            LoadValContext loadValContext = new LoadValContext(namespace, cacheName, fullKey);
//            CacheContextUtils.addLoadValContext(loadValContext);
            return cachedProcess(cache, fullKey, expire, cached.breakDown(), invocation);
//        } finally {
//            CacheContextUtils.removeLoadVal();
//        }
    }

    private Object cachedProcess(CacheProxy cache, String fullKey, long expire, BreakdownType breakdownType,
            MethodInvocation invocation) throws Throwable {
        ValWrapper valWrapper = getCacheValWrapper(cache, fullKey);
        String lockKey = CacheLock.lockKey(fullKey);

        if (valWrapper != null) {
            if (valWrapper.getExpireTs() > System.currentTimeMillis()) {
                // 未过期
                if (easyCacheConfig.isDebug() && valWrapper.getValue() == null) {
                    logger.debug("EasyCache 允许存储空值, 命中空值并返回 key={} ", fullKey);
                }
                return valWrapper.getValue();
            }
            if (BreakdownType.RENEWAL.equals(breakdownType)) {
                CacheLock redisLocker = cache.getCacheLocker(false);
                long lockTime = getLoadTime(200, easyCacheConfig.getPierceDefend().getLockTime().toMillis());
                boolean locked = false;
                try {
                    locked = redisLocker.lock(lockKey, lockTime);
                } catch (Exception ignore) {
                    // ignore
                }
                if (!locked) {
                    if (easyCacheConfig.isDebug()) {
                        logger.debug("EasyCache 续期缓存生效, 返回'旧'值, key={} ", fullKey);
                    }
                    return valWrapper.getValue();
                }
                try {
                    if (easyCacheConfig.isDebug()) {
                        logger.debug("EasyCache 续期缓存生效, 续期并加载缓存, key={} ", fullKey);
                    }
//                    preLoadVal();
                    // 续期先
                    renewalVal(valWrapper, lockTime + 1000);
                    putCache(cache, fullKey, valWrapper);
                    // 更新
                    return processedAndFillCache(true, expire, invocation, cache, fullKey);
                } finally {
//                    postLoadVal();
                    redisLocker.unlock(lockKey);
                }
            }
        }

        CacheLock jvmLocker = cache.getCacheLocker(true);
        long waitTime = getLoadTime(10, easyCacheConfig.getPierceDefend().getLockTime().toMillis());
        boolean locked = false;
        try {
            locked = jvmLocker.lock(lockKey, waitTime + 10);
        } catch (Exception ignore) {
            // ignore
        }
        valWrapper = getCacheValWrapper(cache, fullKey);
        if (!locked) {
            if (valWrapper != null && valWrapper.getExpireTs() > System.currentTimeMillis()) {
                // 拿到第一批次已经填充的新值
                return valWrapper.getValue();
            }
            if (easyCacheConfig.isDebug()) {
                logger.debug("EasyCache 穿透未获取到锁, 继续穿透, key={} ", fullKey);
            }
            // 允许穿透
            return processedAndFillCache(false, expire, invocation, cache, fullKey);
        }
        try {
            if (valWrapper != null && valWrapper.getExpireTs() > System.currentTimeMillis()) {
                // 第二批次获取锁的同志, 拿到第一批次已经填充的新值
                return valWrapper.getValue();
            }
            if (easyCacheConfig.isDebug()) {
                logger.debug("EasyCache 缓存穿透, key={} ", fullKey);
            }
//            preLoadVal();
            // 清除可能的旧值
            removeCache(cache, fullKey);
            return processedAndFillCache(false, expire, invocation, cache, fullKey);
        } finally {
//            postLoadVal();
            jvmLocker.unlock(lockKey);
        }
    }

    private Object processedAndFillCache(boolean renewal, long expire, MethodInvocation invocation, CacheProxy cache, String fullKey) throws Throwable {
        Object result = invocation.proceed();
        if (result != null) {
            ValWrapper valWrapper = ValWrapper.createInstance(expire, result);
            setRealExpireTs(valWrapper, renewal);
            cache.put(fullKey, valWrapper);
            return result;
        }
        PierceDefend pierceDefend = easyCacheConfig.getPierceDefend();
        boolean cacheNullValue = pierceDefend.isCacheNullValue();
        if (cacheNullValue) {
            if (easyCacheConfig.isDebug()) {
                logger.debug("EasyCache 允许存储空值 key={} ", fullKey);
            }
            ValWrapper valWrapper = ValWrapper.createInstance(pierceDefend.getNullValueExpire().toMillis(), result);
            cache.put(fullKey, valWrapper);
        }
        return result;
    }

}
