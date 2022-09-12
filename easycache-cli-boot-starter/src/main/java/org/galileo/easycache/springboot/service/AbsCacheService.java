package org.galileo.easycache.springboot.service;

import org.apache.commons.lang3.ClassUtils;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ExpirePolicy;
import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.CacheTagType;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.core.config.CacheNameConfig;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.core.core.config.KeyConfig;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.utils.InnerKeyUtils;
import org.galileo.easycache.springboot.keygenerator.AutoPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbsCacheService implements ApplicationContextAware {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    @Autowired
    protected AutoPolicy autoPolicy;

    @Autowired
    protected EasyCacheConfig easyCacheConfig;

    @Autowired
    protected CacheLoadService cacheLoadService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String buildFullKey(String namespace, String cacheName, String key) {
        return InnerKeyUtils.buildFullKey(easyCacheConfig, namespace, CacheTagType.EASY_CACHE, cacheName, key);
    }

    public Set<String> buildFullKeys(String namespace, String cacheName, Set<String> keys) {
        return keys.stream().map(e -> buildFullKey(namespace, cacheName, e)).collect(Collectors.toSet());
    }

    public long getExpireByPolicy(Class<? extends ExpirePolicy> expirePolicyClass, Object target, Method method, Object[] args) {
        try {
            if (expirePolicyClass.equals(ExpirePolicy.class)) {
                return -1;
            }
            ExpirePolicy expirePolicy = applicationContext.getBean(expirePolicyClass);
            return expirePolicy.expire(target, method, args);
        } catch (Exception e) {
            logger.warn("EasyCache getExpireByPolicy error", e);
        }
        return -1;
    }

    public String getDynaKey(Class<? extends KeyGeneratorPolicy> keyPolicyClass, Object target, Method method, Object[] args) {
        try {
            Set<String> dynaKeys = getDynaKeys(keyPolicyClass, target, method, args);
            if (dynaKeys.isEmpty()) {
                return "";
            }
            return dynaKeys.iterator().next();
        } catch (Exception e) {
            logger.warn("EasyCache getDynaKey error", e);
        }
        return "";
    }

    public Set<String> getDynaKeys(Class<? extends KeyGeneratorPolicy> keyPolicyClass, Object target, Method method, Object[] args) {
        KeyGeneratorPolicy keyPolicy = null;
        if (!keyPolicyClass.equals(KeyGeneratorPolicy.class)) {
            try {
                keyPolicy =  applicationContext.getBean(keyPolicyClass);
            } catch (Exception e) {
                logger.warn("EasyCache keyPolicyClass bean 不存在 {}", keyPolicyClass.getName(), e);
            }
        }
        keyPolicy = keyPolicy == null ? autoPolicy : keyPolicy;
        Object key = keyPolicy.generateKey(target, method, args);

        Set<String> keySet = new HashSet<>();
        if (key == null) {
            return keySet;
        }
        if (key instanceof String) {
            keySet.add(key.toString());
        } else if (key instanceof Iterable) {
            Iterable<String> keys = (Iterable<String>) key;
            keys.forEach(keySet::add);
        } else if (key.getClass().isArray()) {
            Object[] array = (Object[]) key;
            for (Object k : array) {
                keySet.add(k.toString());
            }
        }
        return keySet;
    }

    public CacheProxy getCacheInstance(String namespace) {
        try {
            return (CacheProxy) applicationContext.getBean(namespace);
        } catch (Exception e) {
            logger.warn("EasyCache get CacheBean of 'namespace' error, {}", namespace);
        }
        CacheProxy cache = EasyCacheManager.getCache(namespace);
        if (cache == null) {
            cache = EasyCacheManager.getCache(CacheConstants.DEFAULT_NAMESPACE);
        }
        return cache;
    }

    public String getNamespace(String namespace) {
        try {
            Map<String, NamespaceConfig> namespaceConfigMap = easyCacheConfig.getNs();
            if (CacheConstants.DEFAULT_NAMESPACE.equals(namespace)
                    && namespaceConfigMap.size() == 1) {
                return namespaceConfigMap.keySet().iterator().next();
            }
        } catch (Exception e) {
            logger.warn("EasyCache getNamespace error", e);
        }
        return namespace;
    }

    public String getCacheName(String cacheName, Object target, Method method) {
        if (CacheConstants.DEFAULT_CACHE_NAME.equals(cacheName)) {
            return ClassUtils.getShortClassName(target.getClass()) + "_" + method.getName();
        }
        return cacheName;
    }

    /**
     * 在事务中执行
     *
     * @param mainTask         主任务
     * @param compensationTask 失败补偿任务
     * @param injectTrans      是否要在事务中
     */
    public void executeWithinTrans(Runnable mainTask, Runnable compensationTask, boolean injectTrans) {
        // 在事务中
        if (injectTrans && TransactionSynchronizationManager.isActualTransactionActive()) {
            // 创建事务回调
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronization.STATUS_ROLLED_BACK == status) {
                        // 提交事务后执行, 如果事务回滚了则执行
                        compensationTask.run();
                    }
                }

                @Override
                public void beforeCommit(boolean readOnly) {
                    // 提交事务前执行
                    mainTask.run();
                }
            });
        } else {
            // 没有事务直接执行
            mainTask.run();
        }
    }

    public long getExpire(String namespace, String cacheName, String dynaKey, long expireExpress, long expireByPolicy) {

        // 按照优先级 ExpirePolicy > keyConfigProperty > keyAnnoExpress > CacheNameConfig ... > dft
        if (expireByPolicy > 0) {
            return expireByPolicy;
        }
        long expire = -1;
        NamespaceConfig namespaceConfig = easyCacheConfig.getNs().get(namespace);
        CacheNameConfig cacheNameConfig = namespaceConfig.getRemote().getCacheName().get(cacheName);
        if (cacheNameConfig != null) {
            KeyConfig keyConfig = cacheNameConfig.getKey().get(dynaKey);
            if (keyConfig != null) {
                expire = keyConfig.getExpire().toMillis();
            } else if (expireExpress > 0) {
                expire = expireExpress;
            } else {
                expire = cacheNameConfig.getExpire().toMillis();
            }
        }
        if (expire > 0) {
            return expire;
        }
        if (expireExpress > 0) {
            return expireExpress;
        }
        if (namespaceConfig.getExpire().toMillis() > 0) {
            return namespaceConfig.getExpire().toMillis();
        }

        return CacheConstants.DEFAULT_EXPIRE;
    }

    public ValWrapper getCacheValWrapper(CacheProxy cache, String fullKey) {
        try {
            return cache.get(fullKey);
        } catch (Exception e) {
            logger.error("EasyCache cache get error ", e);
        }
        return null;
    }

    public void removeCache(CacheProxy cache, String fullKey) {
        try {
            cache.remove(fullKey);
        } catch (Exception e) {
            logger.error("EasyCache cache remove error ", e);
        }
    }

    public void renewalVal(ValWrapper valWrapper, long renewalMiil) {
        valWrapper.setRealExpireTs(valWrapper.getRealExpireTs() + renewalMiil);
    }

    public void putCache(CacheProxy cache, String fullKey, ValWrapper valWrapper) {
        try {
            cache.put(fullKey, valWrapper);
        } catch (Exception e) {
            logger.error("EasyCache cache put error ", e);
        }
    }

    public void setRealExpireTs(ValWrapper valWrapper, boolean renewal) {
        valWrapper.setRealExpireTs(valWrapper.getExpireTs());
        if (!renewal) {
            return;
        }
        long realExpire = easyCacheConfig.getBreakdownDefend().getRenewalTime().toMillis();
        valWrapper.setRealExpireTs(System.currentTimeMillis() + realExpire);
    }
}
