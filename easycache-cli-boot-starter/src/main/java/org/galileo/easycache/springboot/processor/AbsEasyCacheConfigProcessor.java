package org.galileo.easycache.springboot.processor;


import org.galileo.easycache.core.core.config.Rebinder;
import org.galileo.easycache.core.core.config.EasyCacheConfig;

public abstract class AbsEasyCacheConfigProcessor implements Rebinder {

    /**
     * 设置 cacheConfig 中的parent配置
     *
     * @param cacheConfig
     */
    public static void setParent(EasyCacheConfig cacheConfig) {
        cacheConfig.getRemote().forEach((namespace, namespaceConfig) -> {

            namespaceConfig.setParent(cacheConfig);
            namespaceConfig.setNamespace(namespace);
            namespaceConfig.getCacheName().forEach((cacheName, cacheNameConfig) -> {

                cacheNameConfig.setParent(namespaceConfig);
                cacheNameConfig.getKey().forEach((key, keyConfig) -> {

                    keyConfig.setParent(cacheNameConfig);
                });
            });
        });
    }

}
