package org.galileo.easycache.springboot.config;

import org.galileo.easycache.springboot.service.CacheRemoveService;
import org.galileo.easycache.springboot.service.CacheUpdateService;
import org.galileo.easycache.springboot.service.CachedService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "easycache.enabled", havingValue = "true", matchIfMissing = false)
public class EasyCacheComponentConfiguration {

    @Bean
    public CachedService cachedService() {
        return new CachedService();
    }

    @Bean
    public CacheRemoveService cacheRemoveService() {
        return new CacheRemoveService();
    }

    @Bean
    public CacheUpdateService cCacheUpdateService() {
        return new CacheUpdateService();
    }
}
