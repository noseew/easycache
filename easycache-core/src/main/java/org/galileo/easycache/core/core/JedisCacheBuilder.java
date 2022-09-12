package org.galileo.easycache.core.core;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.core.core.config.NamespaceConfig;

/**
 */
public class JedisCacheBuilder extends ExternalCacheBuilder<JedisCacheBuilder> {


    public JedisCacheBuilder(NamespaceConfig namespaceConfig) {
        super(namespaceConfig);
    }

    public static JedisCacheBuilder createBuilder(NamespaceConfig namespaceConfig) {
        return new JedisCacheBuilder(namespaceConfig);
    }

    @Override
    public CacheClient buildCache() {
        JedisCache cache = new JedisCache(namespaceConfig);
        addTailFilter(cache);
        return createCacheProxy(cache);
    }

}
