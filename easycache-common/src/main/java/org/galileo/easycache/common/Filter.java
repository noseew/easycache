package org.galileo.easycache.common;

/**
 * 缓存实例(包括redis集群, 本地缓存等抽象缓存实例)操作的过滤器
 * 多个filter会形成一个filter chain, 具体有哪些filter会根据具体的缓存建造者自己来制定
 */
public interface Filter<T> {
    /**
     * 过滤filter的链调用方法
     * 
     * @param context 调用上下文
     * @return
     */
    Object invoke(T context);
}
