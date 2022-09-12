package org.galileo.easycache.core.core;

import org.galileo.easycache.common.CacheInit;
import org.galileo.easycache.common.CacheLock;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.Filter;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbsCache implements CacheProxy, CacheInit {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected final AtomicBoolean init = new AtomicBoolean(false);

    public static final String keyRemoveTopic = "EasyCache_keyRemoveTopic";

    /**
     * 缓存实例对应的配置
     */
    protected NamespaceConfig config;
    protected CacheType cacheType;
    protected CacheLock cacheLock;
    protected String cacheClientName;

    protected CacheProxy proxy;
    protected Filter filter;

    protected AtomicBoolean closed = new AtomicBoolean(false);

    protected AbsCache(NamespaceConfig config, CacheType cacheType) {
        this.config = config;
        this.cacheType = cacheType;
    }

    public NamespaceConfig getConfig() {
        return this.config;
    }

    @Override
    public CacheType getCacheType() {
        return this.cacheType;
    }

    public String getCacheClientName() {
        return cacheClientName;
    }

    public String getCacheInfo() {
        return getCacheType().getVal() + " " + getCacheClientName();
    }

    /**
     * 获取锁
     * 会根据参数优先选择一个锁, 如果没有对应的, 则选一个可用的锁
     *
     * @param biasBlock 是否 优先选择阻塞锁(JVM锁)
     * @return
     */
    @Override
    public CacheLock getCacheLocker(boolean biasBlock) {
        return cacheLock;
    }

    @Override
    public CacheProxy getProxy() {
        return proxy;
    }

    @Override
    public void setProxy(CacheProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
