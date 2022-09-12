package org.galileo.easycache.core.core;


import org.galileo.easycache.core.core.config.InternalConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.utils.InnerAssertUtils;

import java.time.Duration;

public abstract class InternalCacheBuilder<T extends InternalCacheBuilder<T>> extends AbstractCacheBuilder<T> {

    protected InternalConfig internalConfig;
    protected int maximumSize;
    protected Duration expireAfterWrite;

    protected InternalCacheBuilder(InternalConfig internalConfig) {
        super(internalConfig.getParent());
        this.internalConfig = internalConfig;
    }

    public T buildMaxSize(int maximumSize) {
        InnerAssertUtils.isTrue(maximumSize > 0, "'maximumSize' can't be negative or zero");
        this.maximumSize = maximumSize;
        return (T) this;
    }

    public T buildExpire(Duration expireAfterWrite) {
        InnerAssertUtils.isTrue(expireAfterWrite == null || expireAfterWrite.toMillis() > 0, "'expireAfterWrite' can't be negative or zero");
        this.expireAfterWrite = expireAfterWrite;
        return (T) this;
    }

    @Override
    protected void preBuild() {
    }
}
