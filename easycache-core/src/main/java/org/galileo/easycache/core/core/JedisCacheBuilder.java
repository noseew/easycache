package org.galileo.easycache.core.core;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.core.core.config.RemoteConfig;

/**
 */
public class JedisCacheBuilder extends ExternalCacheBuilder<JedisCacheBuilder> {


    public JedisCacheBuilder(RemoteConfig remoteConfig) {
        super(remoteConfig);
    }

    public static JedisCacheBuilder createBuilder(RemoteConfig namespaceConfig) {
        return new JedisCacheBuilder(namespaceConfig);
    }

    @Override
    public CacheClient buildCache() {
        JedisCache cache = new JedisCache(namespaceConfig.getRemote());
        addTailFilter(cache);
        return createCacheProxy(cache);
    }

}
