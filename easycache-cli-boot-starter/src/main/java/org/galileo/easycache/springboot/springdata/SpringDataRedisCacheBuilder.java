package org.galileo.easycache.springboot.springdata;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.core.core.ExternalCacheBuilder;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 基于 SpringDataRedis 实现的 Cache
 */
public class SpringDataRedisCacheBuilder extends ExternalCacheBuilder<SpringDataRedisCacheBuilder> {

    private RedisConnectionFactory connectionFactory;
    private final RedisProperties redisProperties;

    public SpringDataRedisCacheBuilder(RemoteConfig remoteConfig, RedisProperties redisProperties) {
        super(remoteConfig);
        this.redisProperties = redisProperties;
    }

    public static SpringDataRedisCacheBuilder createBuilder(RemoteConfig remoteConfig,
                                                            RedisProperties redisProperties) {
        return new SpringDataRedisCacheBuilder(remoteConfig, redisProperties);
    }

    public SpringDataRedisCacheBuilder redisConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    @Override
    public CacheClient buildCache() {
        if (connectionFactory == null) {
            if (hasJedis(redisProperties)) {
                connectionFactory = InterRedisUtil.getJedisConnectionFactory(redisProperties);
            } else {
                connectionFactory = InterRedisUtil.getLettuceConnectionFactory(redisProperties);
            }
        }

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        SpringDataCache cache = new SpringDataCache(remoteConfig, redisTemplate);
        addTailFilter(cache);
        return createCacheProxy(cache);
    }

    private static boolean hasJedis(RedisProperties redisProperties) {
        return redisProperties != null && redisProperties.getJedis() != null
                && redisProperties.getJedis().getPool() != null;
    }

    private static boolean hasLettuce(RedisProperties redisProperties) {
        return redisProperties != null && redisProperties.getLettuce() != null
                && redisProperties.getLettuce().getPool() != null;
    }

}
