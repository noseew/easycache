package org.galileo.easycache.core.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.core.config.InternalConfig;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Caffeine 实现的 Cache
 */
public class CaffeineCacheBuilder extends InternalCacheBuilder {

    private InternalConfig caffeineConfig;

    private CaffeineCacheBuilder(InternalConfig internalConfig) {
        super(internalConfig);
        this.caffeineConfig = internalConfig;
        this.maximumSize = caffeineConfig.getMaximumSize();
        this.expireAfterWrite = caffeineConfig.getExpireAfterWrite();
    }

    public static CaffeineCacheBuilder createBuilder(InternalConfig internalConfig) {
        return new CaffeineCacheBuilder(internalConfig);
    }

    @Override
    public CacheClient buildCache() {
        caffeineConfig.setExpireAfterWrite(this.expireAfterWrite);
        caffeineConfig.setMaximumSize(this.maximumSize);

        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.maximumSize(caffeineConfig.getMaximumSize());
        // 自定义单个key过期时间配置
        builder.expireAfter(new Expiry<Object, ValWrapper>() {
            private long getRestTimeInNanos(ValWrapper value) {
                long ttl = Math.min(caffeineConfig.getExpireAfterWrite().toMillis(), value.getExpire());
                return TimeUnit.MILLISECONDS.toNanos(ttl);
            }

            @Override
            public long expireAfterCreate(Object key, ValWrapper value, long currentTime) {
                return getRestTimeInNanos(value);
            }

            @Override
            public long expireAfterUpdate(Object key, ValWrapper value,
                                          long currentTime, long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(Object key, ValWrapper value,
                                        long currentTime, long currentDuration) {
                return getRestTimeInNanos(value);
            }
        });
        CaffeineCache cache = new CaffeineCache(builder.build(), caffeineConfig);
        addTailFilter(cache);
        return createCacheProxy(cache);
    }

}
