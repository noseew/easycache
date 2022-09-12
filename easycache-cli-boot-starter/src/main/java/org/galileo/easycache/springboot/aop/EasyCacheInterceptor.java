package org.galileo.easycache.springboot.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ObjectUtils;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheRemoveAll;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.springboot.service.CacheRemoveService;
import org.galileo.easycache.springboot.service.CacheUpdateService;
import org.galileo.easycache.springboot.service.CachedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

public class EasyCacheInterceptor implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger(EasyCacheInterceptor.class);

    @Autowired
    private EasyCacheConfig cacheConfig;

    @Autowired
    private CachedService cachedService;

    @Autowired
    private CacheUpdateService cacheUpdateService;

    @Autowired
    private CacheRemoveService cacheRemoveService;

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (!cacheConfig.isEnabled()) {
            if (cacheConfig.isDebug()) {
                logger.debug("EasyCache 未开启, 缓存未生效");
            }
            return invocation.proceed();
        }

        Method method = invocation.getMethod();
        Cached cached = method.getAnnotation(Cached.class);
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        CacheUpdate cacheUpdate = method.getAnnotation(CacheUpdate.class);
        if (!ObjectUtils.anyNotNull(cached, cacheRemove, cacheUpdate, cacheRemoveAll)) {
            if (cacheConfig.isDebug()) {
                logger.debug("EasyCache 注解为空, {} 缓存未生效", method.getName());
            }
            return invocation.proceed();
        }
        if (AnnoAttributeUtil.notNullSize(cached, cacheRemove, cacheUpdate, cacheRemoveAll) > 1) {
            logger.error("EasyCache 统一方法上不允许有多个缓存注解");
            return invocation.proceed();
        }

        if (cached != null) {
            return cachedService.cachedProcess(cached, invocation);
        }
        if (cacheRemove != null) {
            return cacheRemoveService.cacheRemoveProcess(cacheRemove, invocation);
        }
        if (cacheUpdate != null) {
            return cacheUpdateService.cacheUpdateProcess(cacheUpdate, invocation);
        }

        return invocation.proceed();
    }

}
