package org.galileo.easycache.common;

/**
 * 用于创建 Cache 实例的工具类
 */
public interface CacheBuilder {
    /**
     * 构建 缓存实例对象
     * 
     * @return
     */
    CacheClient buildCache();
}
