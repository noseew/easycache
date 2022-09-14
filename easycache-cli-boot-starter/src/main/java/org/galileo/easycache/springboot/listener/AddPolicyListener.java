package org.galileo.easycache.springboot.listener;

import org.galileo.easycache.common.RefreshPolicy;
import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.event.AbsCacheOpEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 将 Bean 注册到 SimpleCacheManager 中
 */
@ConditionalOnProperty(value = "easycache.enabled", havingValue = "true", matchIfMissing = false)
public class AddPolicyListener implements ApplicationListener<ApplicationStartedEvent>, Ordered {

    private static Logger logger = LoggerFactory.getLogger(AddPolicyListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        try {
            ConfigurableApplicationContext applicationContext = applicationStartedEvent.getApplicationContext();
            Map<String, SerialPolicy> serialPolicyMap = applicationContext.getBeansOfType(SerialPolicy.class);
            if (!serialPolicyMap.isEmpty()) {
                serialPolicyMap.forEach(EasyCacheManager::addSerialPolicy);
            }

            Map<String, RefreshPolicy> refreshPolicyMap = applicationContext.getBeansOfType(RefreshPolicy.class);
            if (!serialPolicyMap.isEmpty()) {
                refreshPolicyMap.forEach(EasyCacheManager::addRefreshPolicy);
            }

            Map<String, AbsCacheOpEventListener> eventListenerMap = applicationContext.getBeansOfType(AbsCacheOpEventListener.class);
            if (!eventListenerMap.isEmpty()) {
                List<AbsCacheOpEventListener> listeners = new ArrayList<>(eventListenerMap.values());
                AnnotationAwareOrderComparator.sort(listeners);
                listeners.forEach(EasyCacheManager::addEventListener);
            }

        } catch (Exception e) {
            logger.warn("EasyCache Add Policy Listener error ", e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 20;
    }
}
