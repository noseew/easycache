package org.galileo.easycache.springboot.springdata;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.Optional;

/**
 * RedisUtil
 */
public class InterRedisUtil {
    /**
     * Hide Utility Class Constructor
     */
    private InterRedisUtil() {
    }

    /**
     * 创建并获取连接工厂, 会帮你初始化
     * 注意返回的对象不能注册到SpringBean中, 否则会重复初始化
     * 如果想注册到SpringBean中, 可以使用 {@link InterRedisUtil#createBeanLettuceConnectionFactory(org.springframework.boot.autoconfigure.data.redis.RedisProperties)}
     * getLettuceConnectionFactory
     *
     * @param properties RedisProperties
     * @return LettuceConnectionFactory
     */
    public static LettuceConnectionFactory getLettuceConnectionFactory(RedisProperties properties) {
        LettuceConnectionFactory lettuceConnectionFactory = createBeanLettuceConnectionFactory(properties);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    /**
     * 创建并获取连接工厂, 可以注册该Bean到Spring容器中
     * 不会帮你初始化, 交由Spring初始化, 所以返回值不能直接使用, 需要初始化或者注册到SpringBean容器中才能使用
     * 如果想直接使用工厂不想注册到SpringBean中, 可以使用 {@link InterRedisUtil#getLettuceConnectionFactory(org.springframework.boot.autoconfigure.data.redis.RedisProperties)}
     *
     * @param properties
     * @return
     */
    public static LettuceConnectionFactory createBeanLettuceConnectionFactory(RedisProperties properties) {
        RedisClusterConfiguration clusterConfiguration = getClusterConfiguration(properties);
        LettuceConnectionFactory lettuceConnectionFactory;
        if (clusterConfiguration != null) {
            LettuceClientConfiguration clientConfig = lettuceTopologyConfiguration(getGenericObjectPoolConfig(properties), properties);
            lettuceConnectionFactory = new LettuceConnectionFactory(clusterConfiguration, clientConfig);
        } else {
            lettuceConnectionFactory = new LettuceConnectionFactory(getStandaloneConfiguration(properties),
                    getLettuceClientConfiguration(properties));
        }
        return lettuceConnectionFactory;
    }

    /**
     * 创建并获取连接工厂, 会帮你初始化
     * 注意返回的对象不能注册到SpringBean中, 否则会重复初始化
     * 如果想注册到SpringBean中, 可以使用 {@link InterRedisUtil#createBeanJedisConnectionFactory(org.springframework.boot.autoconfigure.data.redis.RedisProperties)}
     * getLettuceConnectionFactory
     *
     * @param properties RedisProperties
     * @return LettuceConnectionFactory
     */
    public static JedisConnectionFactory getJedisConnectionFactory(RedisProperties properties) {
        JedisConnectionFactory jedisConnectionFactory = createBeanJedisConnectionFactory(properties);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    /**
     * 创建并获取连接工厂, 可以注册该Bean到Spring容器中
     * 不会帮你初始化, 交由Spring初始化, 所以返回值不能直接使用, 需要初始化或者注册到SpringBean容器中才能使用
     * 如果想直接使用工厂不想注册到SpringBean中, 可以使用 {@link InterRedisUtil#getJedisConnectionFactory(org.springframework.boot.autoconfigure.data.redis.RedisProperties)}
     *
     * @param properties
     * @return
     */
    public static JedisConnectionFactory createBeanJedisConnectionFactory(RedisProperties properties) {
        RedisClusterConfiguration clusterConfiguration = getClusterConfiguration(properties);
        JedisConnectionFactory jedisConnectionFactory;
        if (clusterConfiguration != null) {
            jedisConnectionFactory = new JedisConnectionFactory(clusterConfiguration,
                    getJedisClientConnectionFactory(properties));
        } else {
            jedisConnectionFactory = new JedisConnectionFactory(getStandaloneConfiguration(properties),
                    getJedisClientConnectionFactory(properties));
        }
        return jedisConnectionFactory;
    }

    private static LettuceClientConfiguration getLettuceClientConfiguration(RedisProperties properties) {
        return LettucePoolingClientConfiguration.builder().commandTimeout(properties.getTimeout())
                .poolConfig(getGenericObjectPoolConfig(properties)).build();
    }

    private static GenericObjectPoolConfig<Object> getGenericObjectPoolConfig(RedisProperties properties) {
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(properties.getLettuce().getPool().getMinIdle());
        poolConfig.setMaxIdle(properties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMaxTotal(properties.getLettuce().getPool().getMaxActive());
        poolConfig.setMaxWaitMillis(properties.getLettuce().getPool().getMaxWait().toMillis());
        return poolConfig;
    }

    /**
     * Lettuce 配置集群拓扑自动刷新
     *
     * @param poolConfig
     * @return
     */
    private static LettuceClientConfiguration lettuceTopologyConfiguration(GenericObjectPoolConfig<Object> poolConfig, RedisProperties properties) {
        RedisProperties.Lettuce.Cluster.Refresh refresh = Optional.ofNullable(properties.getLettuce())
                .map(RedisProperties.Lettuce::getCluster)
                .map(RedisProperties.Lettuce.Cluster::getRefresh)
                .orElse(null);
        if (refresh == null || !refresh.isAdaptive()) {
            return LettucePoolingClientConfiguration.builder()
                    .commandTimeout(properties.getTimeout())
                    .poolConfig(poolConfig)
                    .build();
        }
        Duration period = refresh.getPeriod();
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                // 开启所有自适应刷新, MOVED, ASK, PERSISTENT 都会触发
                .enableAllAdaptiveRefreshTriggers()
                // 周期刷新, 默认60s
                .enablePeriodicRefresh(period != null ? period : Duration.ofSeconds(ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD))
                .build();
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
                .build();
    }

    private static JedisClientConfiguration getJedisClientConnectionFactory(RedisProperties properties) {
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(properties.getJedis().getPool().getMinIdle());
        poolConfig.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        poolConfig.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        poolConfig.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis());
        return JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build();
    }

    private static RedisStandaloneConfiguration getStandaloneConfiguration(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(properties.getHost(),
                properties.getPort());
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    private static RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        if (clusterProperties == null) {
            return null;
        }
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }
}
