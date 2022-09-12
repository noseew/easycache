package org.galileo.easycache.springboot.processor;

import org.galileo.easycache.springboot.config.EasyCacheAutoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.config.TransactionManagementConfigUtils;

@Component
public class EasyCacheAdvisorOrderProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 调整事务缓存的advisor顺序, 要比 默认AOP事务的advisor顺序要低
        if (registry.containsBeanDefinition("transactionInterceptor")
            && registry.containsBeanDefinition(TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
                && registry.containsBeanDefinition(EasyCacheAutoConfiguration.CACHE_TRANSACTION_ADVISOR_BEAN_NAME)) {
            BeanDefinition cacheBeanDefinition = registry
                    .getBeanDefinition(EasyCacheAutoConfiguration.CACHE_TRANSACTION_ADVISOR_BEAN_NAME);
            registry.removeBeanDefinition(EasyCacheAutoConfiguration.CACHE_TRANSACTION_ADVISOR_BEAN_NAME);
            registry.registerBeanDefinition(EasyCacheAutoConfiguration.CACHE_TRANSACTION_ADVISOR_BEAN_NAME,
                    cacheBeanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }


}
