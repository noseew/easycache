package org.galileo.easycache.springboot.listener;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.constants.SubNamespace;
import org.galileo.easycache.common.enums.CacheExternalType;
import org.galileo.easycache.common.enums.CacheInternalType;
import org.galileo.easycache.core.core.*;
import org.galileo.easycache.core.core.config.InternalConfig;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.springboot.springdata.SpringDataRedisCacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * 注册 Namespace 配置的 Cache bean
 */
@ConditionalOnProperty(value = "easycache.enabled", havingValue = "true", matchIfMissing = false)
public class RegisterNamespaceListener
        implements ApplicationListener<ApplicationStartedEvent>, Ordered {

    private static Logger logger = LoggerFactory.getLogger(RegisterNamespaceListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        try {
            ConfigurableApplicationContext applicationContext = applicationStartedEvent.getApplicationContext();
            if (!applicationContext.containsBean(CacheConstants.CACHE_CONFIG)) {
                return;
            }
            EasyCacheConfig cacheConfig = applicationContext.getBean(EasyCacheConfig.class);
            String localCacheBeanName = CacheConstants.cacheBeanName(CacheConstants.DEFAULT_NAMESPACE);
            CacheClient localCache = registerInternalCache(localCacheBeanName + SubNamespace.LOCAL_POSTFIX, cacheConfig, applicationContext);

            cacheConfig.getRemote().forEach((ns, nsConfig) -> {
                String cacheBeanName = CacheConstants.cacheBeanName(ns);
                CacheClient remoteCache = registerExternalCache(cacheBeanName + SubNamespace.REMOTE_POSTFIX, nsConfig, applicationContext);
                registerCombinationCache(cacheBeanName, nsConfig, localCache, remoteCache, applicationContext);
            });
        } catch (Exception e) {
            logger.error("EasyCache RegisterNamespaceListener error", e);
        }
    }

    private CacheClient registerExternalCache(String namespace, NamespaceConfig nsconfig, ConfigurableApplicationContext applicationContext) {
        if (nsconfig == null) {
            throw new IllegalArgumentException("nsconfig 配置为空");
        }
        CacheClient cacheClient = checkCacheBean(namespace, applicationContext);
        if (cacheClient != null) {
            return cacheClient;
        }
        String externalType = nsconfig.getType();
        if (CacheExternalType.REDIS.eq(externalType)) {
            JedisCacheBuilder jedisCacheBuilder = JedisCacheBuilder.createBuilder(nsconfig);
            CacheClient jedisCacheClient = jedisCacheBuilder.buildCache();
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            beanFactory.registerSingleton(namespace, jedisCacheClient);
            CacheProxy jedisCacheProxy = (CacheProxy) jedisCacheClient;
            EasyCacheManager.addCache(namespace, jedisCacheProxy);
            String cacheInfo = ((AbsCache) jedisCacheProxy.unProxy()).getCacheInfo();
            logger.info("EasyCache 注册CacheBean成功, namespace={}, 使用 {} ", namespace, cacheInfo);
            return jedisCacheClient;
        }

        // 使用 SpringDataRedis, 配置的 RedisProperties BeanName,
        // 规则: easycache.namespace.{dft}.remote.type = redis#redisProperties
        if (externalType.startsWith(CacheExternalType.REDIS.getVal() + "#")) {
            int indexOf = externalType.indexOf("#");
            String redisBeanName = externalType.substring(indexOf + 1);
            if (!applicationContext.containsBean(redisBeanName)) {
                logger.error("EasyCache 注册CacheBean失败, namespace={}, 配置bean不存在 {}, redisBeanName {}", namespace, externalType, redisBeanName);
                throw new IllegalArgumentException("redis 配置bean不存在 " + redisBeanName);
            }
            RedisProperties redisProperties = (RedisProperties) applicationContext.getBean(redisBeanName);
            CacheClient springDataCache = SpringDataRedisCacheBuilder.createBuilder(nsconfig, redisProperties)
                    .buildCache();
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            beanFactory.registerSingleton(namespace, springDataCache);
            String cacheInfo = ((AbsCache) ((CacheProxy) springDataCache).unProxy()).getCacheInfo();
            logger.info("EasyCache 注册CacheBean成功, namespace={}, 使用 {}", namespace, cacheInfo);
            return springDataCache;
        }
        return null;
    }

    private CacheClient registerInternalCache(String namespace, EasyCacheConfig cacheConfig, ConfigurableApplicationContext applicationContext) {
        InternalConfig internalConfig = cacheConfig.getLocal();
        if (internalConfig == null) {
            throw new IllegalArgumentException("cache Local 配置为空");
        }
        CacheClient cacheClient = checkCacheBean(namespace, applicationContext);
        if (cacheClient != null) {
            return cacheClient;
        }
        String internalType = internalConfig.getType();
        if (CacheInternalType.CAFFEINE.eq(internalType)) {
            CaffeineCacheBuilder caffeineCacheBuilder = CaffeineCacheBuilder.createBuilder(internalConfig);
            CacheClient caffeineCacheClient = caffeineCacheBuilder.buildCache();
            applicationContext.getBeanFactory().registerSingleton(namespace, caffeineCacheClient);
            EasyCacheManager.addCache(namespace, (CacheProxy) caffeineCacheClient);
            String cacheInfo = ((AbsCache) ((CacheProxy) caffeineCacheClient).unProxy()).getCacheInfo();
            logger.info("EasyCache 注册CacheBean成功, namespace={}, 使用 {}", namespace, cacheInfo);
            return caffeineCacheClient;
        }
        logger.warn("EasyCache 注册CacheBean失败, namespace={}, internalType 有误 {}", namespace, internalType);

        throw new IllegalArgumentException("cache Local 配置有误");
    }

    private CacheClient registerCombinationCache(String namespace, NamespaceConfig nsconfig, CacheClient local, CacheClient remote, ConfigurableApplicationContext applicationContext) {
        CacheClient cacheClient = checkCacheBean(namespace, applicationContext);
        if (cacheClient != null) {
            return cacheClient;
        }
        if (local == null && remote == null) {
            logger.warn("EasyCache Termination of the registration, localCache or remoteCache is null, local={}, remote={}", local, remote);
            return null;
        }
        CacheProxy localProxy = (CacheProxy) local;
        CacheProxy remoteProxy = (CacheProxy) remote;
        CacheClient autoCache = AutoCacheBuilder.createBuilder(nsconfig, localProxy, remoteProxy).buildCache();
        CacheProxy autoCacheProxy = (CacheProxy) autoCache;
        applicationContext.getBeanFactory().registerSingleton(namespace, autoCache);
        EasyCacheManager.addCache(namespace, autoCacheProxy);
        String cacheInfo = ((AbsCache) autoCacheProxy.unProxy()).getCacheInfo();

        // 设置local订阅remote
        AbsInternalCache internalCache = (AbsInternalCache) localProxy.unProxy();
        internalCache.setRemoteCache((AbsExternalCache) remoteProxy.unProxy());
        internalCache.registerRemoveConsumer();
        logger.info("EasyCache 注册CacheBean成功, namespace={}, 使用 {}", namespace, cacheInfo);
        return autoCache;
    }

    private CacheClient checkCacheBean(String namespace, ConfigurableApplicationContext applicationContext) {
        if (applicationContext.containsBean(namespace)) {
            Object bean = applicationContext.getBean(namespace);
            if (bean instanceof CacheClient) {
                EasyCacheManager.addCache(namespace, (CacheProxy) bean);
                return (CacheClient) bean;
            }
            logger.error("EasyCache Termination of the registration, bean name namespace='{}' already exists, It is {}", namespace, bean);
            throw new IllegalArgumentException(String.format("bean name '%s' already exists", namespace));
        }
        if (EasyCacheManager.getCache(namespace) != null) {
            logger.warn("EasyCache Termination of the registration, bean name namespace='{}' already exists;", namespace);
            return EasyCacheManager.getCache(namespace);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
