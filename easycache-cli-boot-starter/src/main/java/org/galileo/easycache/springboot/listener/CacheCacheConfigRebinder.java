package org.galileo.easycache.springboot.listener;

import com.google.common.collect.Lists;
import org.galileo.easycache.core.core.config.Rebinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 注册 CacheName 配置 Bean
 */
@ConditionalOnProperty(value = "easycache.enabled", havingValue = "true", matchIfMissing = false)
public class CacheCacheConfigRebinder 
        implements ApplicationListener<ApplicationEvent>, ApplicationContextAware, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(CacheCacheConfigRebinder.class);

    public static final List<String> EVENT_CLASS_NAMES = Arrays
            .asList("org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent",
                    "org.springframework.cloud.context.environment.EnvironmentChangeEvent");
    private final List<Class<?>> eventClasses = new ArrayList<>();

    private AtomicBoolean binding = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    private Class<?> getClassSafe(String className) {
        try {
            return ClassUtils.forName(className, (ClassLoader) null);
        } catch (ClassNotFoundException var3) {
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (String eventClassName : EVENT_CLASS_NAMES) {
            Class<?> classSafe = getClassSafe(eventClassName);
            if (classSafe != null) {
                eventClasses.add(classSafe);
            }
        }
    }

    private boolean shouldTriggerRefresh(ApplicationEvent event) {
        return this.eventClasses.stream().anyMatch(clazz -> ClassUtils.isAssignableValue(clazz, event));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (!shouldTriggerRefresh(applicationEvent)) {
            return;
        }
        if (binding.compareAndSet(false, true)) {
            logger.info("EasyCache 配置重新绑定 start ...");
            rebindersRebind();
            logger.info("EasyCache 配置重新绑定 end ...");
            binding.set(false);
        }
    }

    private void rebindersRebind() {
        try {
            Map<String, Rebinder> rebinderMap = applicationContext.getBeansOfType(Rebinder.class);
            List<String> success = new ArrayList<>();
            List<String> fail = new ArrayList<>();

            ArrayList<Rebinder> rebinders = Lists.newArrayList(rebinderMap.values());
            AnnotationAwareOrderComparator.sort(rebinders);
            for (Rebinder rebinder : rebinders) {
                try {
                    rebinder.rebind();
                    success.add(rebinder.getClass().getName());
                } catch (Exception ex) {
                    fail.add(rebinder.getClass().getName());
                    logger.warn("EasyCache rebind 异常 {}", rebinder.getClass().getName(), ex);
                }
            }
            if (!success.isEmpty()) {
                String msg = Arrays.toString(success.toArray(new String[0]));
                logger.info("EasyCache rebind 成功, 包括 = {}", msg);
            }
            if (!fail.isEmpty()) {
                String msg = Arrays.toString(fail.toArray(new String[0]));
                logger.warn("EasyCache rebind 异常, 包括 = {}", msg);
            }
        } catch (Exception ex) {
            logger.warn("EasyCache Rebinder 异常 ", ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
