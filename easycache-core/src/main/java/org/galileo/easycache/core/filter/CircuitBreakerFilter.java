package org.galileo.easycache.core.filter;

import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.SubNamespace;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.EasyCacheManager;
import org.galileo.easycache.core.core.config.CircuitBreakerConfig;
import org.galileo.easycache.core.core.config.Rebinder;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * 用于对缓存实例进行熔断降级, 仅会对远程缓存生效, 降级为本地缓存如果本地缓存存在的话
 * 为了支持熔断组件的配置动态刷新, 实现了 Rebinder 接口
 */
public class CircuitBreakerFilter extends AbsInvokeFilter implements Rebinder {

    private EasyCacheConfig cacheConfig;
    private CircuitBreaker circuitBreaker;
    private CacheProxy fallbackCache;

    public CircuitBreakerFilter(EasyCacheConfig cacheConfig, String namespace) {
        super("CircuitBreakerFilter", namespace, null);
        this.cacheConfig = cacheConfig;
        initFallbackCache();
        initCircuitBreaker();
    }

    @Override
    public Object invoke(FilterContext context) {
        CircuitBreakerConfig cbConfig = cacheConfig.getCircuitBreaker();
        if (cbConfig.isEnabled() && canProcess(context)) {
            return circuitBreakerInvoke(context, () -> super.invoke(context));
        }
        return super.invoke(context);
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return context.getOpType().isCache();
    }

    private Object circuitBreakerInvoke(FilterContext context, Supplier<Object> supplier) {
        try {
            return circuitBreaker.decorateSupplier(supplier).get();
        } catch (CallNotPermittedException e) {
            if (initFallbackCache() == null) {
                return null;
            }
            try {
                CircuitBreakerConfig cbConfig = cacheConfig.getCircuitBreaker();
                logger.warn("EasyCache 缓存组件调用异常, 触发降级, 使用 {} ", fallbackCache.unProxy().getClass(), e);
                ValWrapper valWrapper = context.getValWrapper();
                if (valWrapper != null && context.getOpType().equals(OpType.PUT)) {
                    long expire = Math.min(cbConfig.getExpireAfterWrite().toMillis(), valWrapper.getExpire());
                    valWrapper.setRealExpireTs(System.currentTimeMillis() + expire);
                }
                return MethodUtils.invokeMethod(fallbackCache, context.getMethod().getName(), context.getArgs());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                logger.warn("EasyCache 缓存组件调用异常, 降级失败 ", ex);
            }
        } catch (Exception e) {
            logger.warn("EasyCache CircuitBreakerFilter decorateSupplier error ", e);
        }
        return null;
    }

    private CacheProxy initFallbackCache() {
        if (this.fallbackCache == null) {
            fallbackCache = EasyCacheManager.getCache(getForNamespace() + SubNamespace.LOCAL_POSTFIX);
            if (fallbackCache == null) {
                fallbackCache = EasyCacheManager.getCache(SubNamespace.DFT_LOCAL);
            }
        }
        return fallbackCache;
    }

    private synchronized void initCircuitBreaker() {
        CircuitBreakerConfig config = cacheConfig.getCircuitBreaker();
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig resiConfig = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
                .custom()
                .failureRateThreshold(config.getFailureRateThreshold())
                .slowCallRateThreshold(config.getSlowCallRateThreshold())
                .slowCallDurationThreshold(config.getSlowCallDurationThreshold())
                .permittedNumberOfCallsInHalfOpenState(config.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.valueOf(config.getSlidingWindowType()))
                .slidingWindowSize(config.getSlidingWindowSize())
                .minimumNumberOfCalls(config.getMinimumNumberOfCalls())
                .waitDurationInOpenState(config.getWaitDurationInOpenState())
                .automaticTransitionFromOpenToHalfOpenEnabled(config.isAutomaticTransitionFromOpenToHalfOpenEnabled())
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(resiConfig);
        this.circuitBreaker = registry.circuitBreaker("cacheCircuitBreaker");
    }

    @Override
    public void rebind() {
        initCircuitBreaker();
        logger.debug("EasyCache CircuitBreakerFilter reBind finish");
    }
}
