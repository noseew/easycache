package org.galileo.easycache.springboot.processor;


import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.core.core.config.Rebinder;

public abstract class AbsEasyCacheConfigProcessor implements Rebinder {

    /**
     * 设置 cacheConfig 中的parent配置
     *
     * @param cacheConfig
     */
    public static void setParent(EasyCacheConfig cacheConfig) {
        cacheConfig.getNs().forEach((namespace, nsConfig) -> {

            nsConfig.setParent(cacheConfig);
            nsConfig.setNamespace(namespace);
            nsConfig.getRemote().setParent(nsConfig);
            nsConfig.getRemote().getCacheName().forEach((cacheName, cacheNameConfig) -> {

                cacheNameConfig.setParent(nsConfig);
                cacheNameConfig.getKey().forEach((key, keyConfig) -> {

                    keyConfig.setParent(cacheNameConfig);
                });
            });
        });
    }

}
