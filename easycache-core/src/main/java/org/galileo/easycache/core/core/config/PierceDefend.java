package org.galileo.easycache.core.core.config;


import org.galileo.easycache.common.constants.CacheConstants;

import java.time.Duration;

public class PierceDefend {
    /**
     * 是否存null值
     */
    private boolean cacheNullValue = CacheConstants.DEFAULT_CACHE_NULL_VALUE;
    /**
     * null值存储过期时间
     */
    private Duration nullValueExpire = Duration.ofMillis(CacheConstants.NULL_VALUE_EXPIRE);
    /**
     * 穿透后加锁时间
     */
    private Duration lockTime = Duration.ofSeconds(3);

    public Duration getLockTime() {
        return lockTime;
    }

    public void setLockTime(Duration lockTime) {
        this.lockTime = lockTime;
    }

    public boolean isCacheNullValue() {
        return cacheNullValue;
    }

    public void setCacheNullValue(boolean cacheNullValue) {
        this.cacheNullValue = cacheNullValue;
    }

    public Duration getNullValueExpire() {
        return nullValueExpire;
    }

    public void setNullValueExpire(Duration nullValueExpire) {
        this.nullValueExpire = nullValueExpire;
    }
}
