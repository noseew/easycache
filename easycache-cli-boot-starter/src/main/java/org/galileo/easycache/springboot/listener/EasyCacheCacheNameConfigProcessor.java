package org.galileo.easycache.springboot.listener;

import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.springboot.aop.AnnoAttributeUtil;
import org.galileo.easycache.springboot.utils.CacheConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 注册 CacheName 配置 Bean
 */
@ConditionalOnProperty(value = "easycache.enabled", havingValue = "true", matchIfMissing = false)
public class EasyCacheCacheNameConfigProcessor implements ApplicationListener<ApplicationStartedEvent>, Ordered {

    private static Logger logger = LoggerFactory.getLogger(EasyCacheCacheNameConfigProcessor.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        ConfigurableApplicationContext applicationContext = applicationStartedEvent.getApplicationContext();
        try {
            if (!applicationContext.containsBean(CacheConstants.CACHE_CONFIG)) {
                return;
            }

            // CacheName 的 注解 配置
            getCacheNameAnnoConfig(CacheConfigManager.findClass);
            cacheAnnoCheck(applicationContext);

        } catch (Exception e) {
            logger.warn("EasyCache RegisterCacheNameListener error", e);
        }
    }

    private void getCacheNameAnnoConfig(Map<Class, List<Annotation>> findClasses) {
        // key=namespace, val={key=cacheName, val=annoConfigList}
        findClasses.forEach((clazz, annoList) -> {
            annoList.forEach(anno -> {
                try {
                    String namespace = AnnoAttributeUtil.getNamespace(anno);
                    Map<String, List<Annotation>> cacheNameConfigMap = CacheConfigManager.cacheNameConfigAnnos
                            .computeIfAbsent(namespace, k -> new HashMap<>());
                    String cacheName = AnnoAttributeUtil.getCacheName(anno);
                    List<Annotation> cacheAnnoList = cacheNameConfigMap
                            .computeIfAbsent(cacheName, k -> new ArrayList<>());
                    cacheAnnoList.add(anno);
                } catch (Exception e) {
                    logger.warn("EasyCache getCacheNameAnnoConfig error ", e);
                }
            });
        });

    }

    private void cacheAnnoCheck(ApplicationContext context) {
        // 注解匹配校验
        CacheConfigManager.cacheNameConfigAnnos.forEach((namespace, cacheNameConfigMap) -> {
            String cacheBeanName = CacheConstants.cacheBeanName(namespace);
            if (!context.containsBean(cacheBeanName)) {
                logger.warn("EasyCache cacheBeanName '{}' spring bean 不存在", cacheBeanName);
                return;
            }
            cacheNameConfigMap.forEach((cacheName, cacheNameConfigList) -> {
                Map<Class, List<Annotation>> cacheAnnoMap = cacheNameConfigList.stream()
                        .collect(Collectors.groupingBy(Annotation::annotationType));
                if (cacheAnnoMap.get(Cached.class) == null) {
                    logger.warn("EasyCache cacheName '{}' 缺少对应 @Cached 注解", cacheName);
                }
            });
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 9;
    }
}
