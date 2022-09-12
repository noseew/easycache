package org.galileo.easycache.core.utils;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.CacheRedisClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.CacheTagType;
import org.galileo.easycache.core.core.AbsCache;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.core.config.RemoteConfig;

public class EasyCacheUtils {

    private EasyCacheUtils() {

    }

    /**
     * 设置缓存
     *
     * @param namespace namespace
     * @param cacheName 缓存名
     * @param key       缓存key, 可以为null
     * @param val       缓存值
     * @param expireMilli     过期时间, 毫秒
     */
    public static void put(String namespace, String cacheName, String key, Object val, long expireMilli) {
        CacheClient cacheClient = getCacheClient(namespace);
        String fullKey = InnerKeyUtils.buildFullKey(getRemoteConfig((CacheProxy) cacheClient), CacheTagType.EASY_CACHE, cacheName, key);
        cacheClient.put(fullKey, ValWrapper.createInstance(expireMilli, val));
    }

    /**
     * 获取缓存
     *
     * @param namespace namespace
     * @param cacheName 缓存名
     * @param key       缓存key, 可以为null
     * @param valClass  值类型
     * @param <T>
     * @return
     */
    public static <T> T get(String namespace, String cacheName, String key, Class<T> valClass) {
        CacheClient cacheClient = getCacheClient(namespace);
        String fullKey = InnerKeyUtils.buildFullKey(getRemoteConfig((CacheProxy) cacheClient), CacheTagType.EASY_CACHE, cacheName, key);
        ValWrapper valWrapper = cacheClient.get(fullKey);
        return valWrapper != null ? (T) valWrapper.getValue() : null;
    }

    /**
     * 删除缓存
     *
     * @param namespace namespace
     * @param cacheName 缓存名
     * @param key       缓存key, 可以为null
     */
    public static void remove(String namespace, String cacheName, String key) {
        CacheClient cacheClient = getCacheClient(namespace);
        String fullKey = InnerKeyUtils.buildFullKey(getRemoteConfig((CacheProxy) cacheClient), CacheTagType.EASY_CACHE, cacheName, key);
        cacheClient.remove(fullKey);
    }

    /**
     * 获取 EasyCacheRedis 客户端
     *
     * @param namespace
     * @return
     */
    public static CacheRedisClient getCacheRedisClient(String namespace) {
        CacheClient cacheClient = getCacheClient(namespace);
        return cacheClient.getCacheRedisClient();
    }

    /**
     * 获取 EasyCache 客户端
     *
     * @param namespace
     * @return
     */
    private static CacheClient getCacheClient(String namespace) {
        CacheProxy cacheProxy = EasyCacheManager.getCache(CacheConstants.cacheBeanName(namespace));
        if (cacheProxy == null) {
            throw new IllegalArgumentException(String.format("namespace [%s] 对应的缓存实例不存在", namespace));
        }
        return cacheProxy;
    }

    private static RemoteConfig getRemoteConfig(CacheProxy cacheProxy) {
        return ((AbsCache) (cacheProxy.unProxy())).getRemoteConfig();
    }
}
