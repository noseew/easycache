package org.galileo.easycache.springboot.listener;

import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.springboot.utils.CacheConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class ReleaseSourceListener {

    private static Logger logger = LoggerFactory.getLogger(ReleaseSourceListener.class);

    @EventListener(value = {
            ContextClosedEvent.class, ApplicationFailedEvent.class
    })
    public void on() {
        try {
            EasyCacheManager.close();
            CacheConfigManager.close();
        } catch (Exception e) {
            logger.warn("EasyCache ReleaseSourceListener error ", e);
        }
    }
}
