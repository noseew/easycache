package org.galileo.easycache.core.core;


import org.galileo.easycache.common.FilterProxy;
import org.galileo.easycache.common.ValParam;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.config.InternalConfig;
import org.galileo.easycache.core.event.PubSub;
import org.galileo.easycache.core.exception.CacheInterruptException;

import java.util.Collection;
import java.util.Set;

public abstract class AbsInternalCache extends AbsCache {

    protected InternalConfig internalConfig;
    
    protected AbsExternalCache remoteCache;

    protected AbsInternalCache(InternalConfig internalConfig) {
        super(internalConfig.getParent(), CacheType.LOCAL);
        this.internalConfig = internalConfig;
        this.cacheLock = new JvmCacheLock();
        
        registerRemoveConsumer();
    }

    @Override
    public void init() {
    }

    @Override
    @FilterProxy(opType = OpType.GET)
    public ValWrapper get(String key) {
        try {
            return doGet(key);
        } catch (Exception e) {
            logger.error("EasyCache doGet error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doGet error, key=" + key + e.getMessage());
        }
    }

    public abstract ValWrapper doGet(String key);

    @Override
    @FilterProxy(opType = OpType.PUT)
    public <K, V> void put(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper) {
        try {
            long expire = valWrapper.getRealExpireTs() - System.currentTimeMillis();
            long configMaxExpire = internalConfig.getExpireAfterWrite().toMillis();
            doPut(key, valWrapper, Math.min(expire, configMaxExpire));
        } catch (Exception e) {
            logger.error("EasyCache doPut error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doPut error, key=" + key + e.getMessage());
        }
    }

    public abstract void doPut(String key, ValWrapper valWrapper, long expire);

    @Override
    @FilterProxy(opType = OpType.PUT)
    public boolean putIfAbsent(String key, @ValParam(paramType = ValWrapper.class) ValWrapper valWrapper) {
        try {
            long expire = valWrapper.getRealExpireTs() - System.currentTimeMillis();
            long configMaxExpire = internalConfig.getExpireAfterWrite().toMillis();
            return this.doPutIfAbsent(key, valWrapper, Math.min(expire, configMaxExpire));
        } catch (Exception e) {
            logger.error("EasyCache doPutIfAbsent error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doPutIfAbsent error, key=" + key + e.getMessage());
        }
    }

    public abstract boolean doPutIfAbsent(String key, ValWrapper valWrapper, long expire);

    @Override
    @FilterProxy(opType = OpType.REMOVE)
    public boolean remove(String key) {
        try {
            return this.doRemove(key);
        } catch (Exception e) {
            logger.error("EasyCache doRemove error, key={}", key, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doRemove error, key=" + key + e.getMessage());
        }
    }

    public abstract boolean doRemove(String key);

    @Override
    @FilterProxy(opType = OpType.REMOVE)
    public <K> boolean removeAll(Set<String> keys) {
        try {
            return this.doRemoveAll(keys);
        } catch (Exception e) {
            logger.error("EasyCache doRemoveAll error, key={}", keys, e);
            throw new CacheInterruptException(CacheConstants.CODE_ERROR_OTHER,
                    "doRemoveAll error, key=" + keys + e.getMessage());
        }
    }

    public abstract boolean doRemoveAll(Collection<String> keys);

    public AbsExternalCache getRemoteCache() {
        return remoteCache;
    }

    public void setRemoteCache(AbsExternalCache remoteCache) {
        this.remoteCache = remoteCache;
    }
    
    public void registerRemoveConsumer() {
        if (remoteCache != null) {
            remoteCache.addRemoveConsumer(pubsubBody -> {
                if (pubsubBody.getType() == PubSub.TypeRemoveKey) {
                    this.removeAll(pubsubBody.getKeys());
                }
            });
            logger.debug("EasyCache AbsInternalCache 开始订阅 '{}', 监听执行 removeAll", PubSub.TypeRemoveKey);
        }
    }

}
