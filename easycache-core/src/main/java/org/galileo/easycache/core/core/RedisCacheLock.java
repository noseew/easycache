package org.galileo.easycache.core.core;

import org.galileo.easycache.common.ValWrapper;

public class RedisCacheLock extends AbsCacheLock {

    public RedisCacheLock(AbsExternalCache cache) {
        super(cache);
    }

    @Override
    public boolean lock(String key, long mill) {
        if (key == null) {
            return false;
        }
        String body = String.valueOf(Thread.currentThread().getId());
        ValWrapper wrapper = ValWrapper.createInstance(mill, body);
        if (cacheProxy.putIfAbsent(key, wrapper)) {
            return true;
        }
        ValWrapper exitValue = cacheProxy.get(key);
        if (exitValue == null || exitValue.getRealExpireTs() <= System.currentTimeMillis()) {
            return cacheProxy.putIfAbsent(key, wrapper);
        }
        if (exitValue.getValue().toString().startsWith(body)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean lock(String key, long mill, int maxItem) {
        return lock(key, mill);
    }

    @Override
    public boolean unlock(String key) {
        if (key == null) {
            return false;
        }
        String body = String.valueOf(Thread.currentThread().getId());
        ValWrapper exitValue = cacheProxy.get(key);
        if (exitValue == null) {
            return true;
        }
        if (exitValue.getValue().toString().startsWith(body)) {
            cacheProxy.remove(key);
            return true;
        }
        return false;
    }
}
