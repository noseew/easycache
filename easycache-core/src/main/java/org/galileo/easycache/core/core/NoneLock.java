package org.galileo.easycache.core.core;


public class NoneLock extends AbsCacheLock {

    public NoneLock() {
        super(null);
    }

    @Override
    public boolean lock(String key, long mill) {
        return true;
    }

    @Override
    public boolean lock(String key, long mill, int maxItem) {
        return true;
    }

    @Override
    public boolean unlock(String key) {
        return true;
    }
}
