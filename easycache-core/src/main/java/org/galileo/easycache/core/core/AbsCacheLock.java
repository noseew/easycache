package org.galileo.easycache.core.core;


import org.galileo.easycache.common.CacheLock;
import org.galileo.easycache.common.CacheProxy;

public abstract class AbsCacheLock implements CacheLock {

    protected CacheProxy cacheProxy;

    protected AbsCacheLock(CacheProxy cacheProxy) {
        this.cacheProxy = cacheProxy;
    }

}
