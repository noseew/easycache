package org.galileo.easycache.core.core;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.galileo.easycache.common.*;
import org.galileo.easycache.common.enums.CacheTagType;
import org.galileo.easycache.common.enums.CacheType;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.filter.AbsInvokeFilter;
import org.galileo.easycache.core.filter.FilterContext;
import org.galileo.easycache.core.utils.InnerKeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于组装Cache调用关系, 并生成代理对象
 */
public class CacheProxyFactory {

    private static Logger logger = LoggerFactory.getLogger(CacheProxyFactory.class);

    /**
     * 真正的缓存实例对象
     */
    private CacheProxy target;
    private AbsInvokeFilter filter;

    public CacheProxy getTarget() {
        return this.target;
    }

    public CacheProxyFactory(AbsInvokeFilter filter) {
        this.filter = filter;
    }

    @SuppressWarnings("java:S1488")
    public CacheProxy createCacheProxy(CacheProxy target) {
        this.target = target;
        CacheProxy proxy = (CacheProxy) Proxy
                .newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{CacheProxy.class, CacheRedisClient.class},
                        new InvocationHandler() {

                            private final Map<Method, FilterProxy> filterProxyMap = new ConcurrentHashMap<>();
                            private CacheProxyInvoker cacheProxyInvoker = CacheProxyInvoker.reflectInvoker;

                            /**
                             * 代理的目的就是经过filters
                             * 代理的条件是
                             * 1. 有方法注解 FilterProxy
                             * 2. 缓存实例有 filter
                             *
                             * @param proxy
                             * @param method
                             * @param args
                             * @return
                             * @throws Throwable
                             */
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                FilterProxy filterProxy = filterProxyMap.computeIfAbsent(method, k -> {
                                    return MethodUtils.getAnnotation(method, FilterProxy.class, true, true);
                                });

                                OpType opType = filterProxy != null ? filterProxy.opType() : OpType.NOP;

                                if (OpType.NOP == opType || filter == null) {
                                    return cacheProxyInvoker.invoke(target, method, args);
                                }

                                // Redis 客户端操作
                                if (target.getCacheType() == CacheType.REMOTE && opType.isRedisCollection()) {
                                    NamespaceConfig namespaceConfig = ((AbsExternalCache) target.unProxy()).getNamespaceConfig();
                                    String cacheName = args[0].toString();
                                    CacheTagType cacheTagType = opType.isSet() ? CacheTagType.SET : CacheTagType.ZSET;
                                    args[0] = InnerKeyUtils.buildFullKey(namespaceConfig, cacheTagType, cacheName);
                                    if (namespaceConfig.getParent().isDebug()) {
                                        logger.debug("EasyCache Redis 客户端操作 cacheName={} 替换成key={}", cacheName, args[0]);
                                    }
                                }

                                // 构建缓存filter上下文, 只有需要经过filter的时候, 才需要该上下文
                                FilterContext filterContext = buildFilterContext(target, method, args, filterProxy);
                                try {
                                    // 开始经过filters
                                    return filter.invoke(filterContext);
                                } catch (Exception e) {
                                    logger.error("EasyCache invoke error e", e);
                                    throw e;
                                }
                            }
                        });
        target.setProxy(proxy);
        target.setFilter(filter);
        return proxy;
    }

    /**
     * 构建缓存filter上下文
     * 
     * @param target
     * @param method
     * @param args
     * @param filterProxy
     * @return
     */
    protected FilterContext buildFilterContext(CacheProxy target, Method method, Object[] args,
            FilterProxy filterProxy) {

        String key = null;
        Set keys = null;
        if (args[0] instanceof String) {
            key = (String) args[0];
        } else if (args[0] instanceof Set) {
            keys = (Set) args[0];
        }

        FilterContext context = new FilterContext(target, ((AbsCache) target).getNamespaceConfig(), key, keys, filterProxy.opType(), method, args);
        if (filterProxy.hasValParam()) {
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(ValParam.class)) {
                    ValParam valParam = parameter.getAnnotation(ValParam.class);
                    if (valParam.paramType() == ValWrapper.class) {
                        context.setValWrapper((ValWrapper) arg);
                        break;
                    }
                    if (valParam.paramType() != NullClass.class) {
                        context.setVal(arg);
                        break;
                    }
                }
            }
        }
        return context;
    }

    @Override
    public String toString() {
        return "$ProxyFactory of the " + target.getClass().toString();
    }
}
