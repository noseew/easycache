package org.galileo.easycache.core.core.config;

import org.galileo.easycache.common.constants.CacheConstants;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存实例配置
 * 用于创建一个缓存实例, 也就是 Cache 对象
 */
public class CacheNameConfig extends InheritableConfig<NamespaceConfig> {

    /**
     *
     */
    private String cacheName = CacheConstants.DEFAULT_CACHE_NAME;

    /**
     *
     */
    private Map<String, KeyConfig> key = new HashMap<>();

    /**
     *
     */
    private Annotation annotation;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Map<String, KeyConfig> getKey() {
        return key;
    }

    public void setKey(Map<String, KeyConfig> key) {
        this.key = key;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

}
