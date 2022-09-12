package org.galileo.easycache.springboot.processor;

import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.springboot.utils.InnerSpringContextUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;

public class EasyCacheConfigParentProcessor
        extends AbsEasyCacheConfigProcessor
        implements BeanPostProcessor, EnvironmentAware, PriorityOrdered {

    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (EasyCacheConfig.class == bean.getClass()) {
            EasyCacheConfig cacheConfig = (EasyCacheConfig) bean;

            setParent(cacheConfig);

            if (StringUtils.equals(cacheConfig.getAppName(), CacheConstants.DEFAULT) || StringUtils.isEmpty(cacheConfig.getAppName())) {
                cacheConfig.setAppName(environment.getProperty("spring.application.name"));
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        // 优先级比 ConfigurationPropertiesBindingPostProcessor 低
        return 0;
    }

    @Override
    public void rebind() {
        EasyCacheConfig cacheConfig = InnerSpringContextUtils.getBean(EasyCacheConfig.class);
        setParent(cacheConfig);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
