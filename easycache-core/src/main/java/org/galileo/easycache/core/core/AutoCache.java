package org.galileo.easycache.core.core;


import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;

/**
 * 默认多级缓存, 可通过配置控制细节
 */
public class AutoCache extends AbsCombinationCache {

    public AutoCache(RemoteConfig remoteConfig, CacheClient localCache, CacheClient remoteCache) {
        super(remoteConfig, (CacheProxy) localCache, (CacheProxy) remoteCache);
        this.cacheClientName = "AutoCache";
    }

    @Override
    public void close() throws Exception {
        try {
            for (CacheProxy cacheProxy : multiCache) {
                cacheProxy.close();
            }
        } catch (Exception e) {
            // ignore
            logger.warn("", e);
        }
    }

    @Override
    public String getCacheInfo() {
        String localCacheInfo = "";
        if (internalCache != null) {
            localCacheInfo = internalCache.getCacheInfo();
        }
        String remoteCacheInfo = "";
        if (externalCache != null) {
            remoteCacheInfo = externalCache.getCacheInfo();
        }
        return String.format("%s(%s, %s)", getCacheClientName(), localCacheInfo, remoteCacheInfo);
    }
}
