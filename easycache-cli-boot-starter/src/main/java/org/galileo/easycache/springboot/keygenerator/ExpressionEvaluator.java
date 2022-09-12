package org.galileo.easycache.springboot.keygenerator;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExpressionEvaluator
 *
 * @author by geyan
 * @Date 2021/11/9 11:06 上午
 */
public class ExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);

    /**
     * Create the suitable {@link EvaluationContext} for the specified event handling on the
     * specified method.
     *
     * @param target      the object
     * @param targetClass the target class
     * @param method      the method
     * @param args        the args
     * @return the evaluation context
     */
    public EvaluationContext createEvaluationContext(Object target, Class<?> targetClass,
                                                     Method method, Object[] args) {

        Method targetMethod = getTargetMethod(targetClass, method);
        CacheRootObject rootObj = new CacheRootObject(method, args, target, targetClass);
        return new MethodBasedEvaluationContext(rootObj, targetMethod, args, getParameterNameDiscoverer());
    }

    /**
     * Specify if the condition defined by the specified expression matches.
     *
     * @param evalExpression the condition expression
     * @param elementKey          the element key
     * @param evalContext         the eval context
     * @param clazz               the clazz
     * @return the t
     */
    public <T> T evalValue(String evalExpression, AnnotatedElementKey elementKey,
                       EvaluationContext evalContext, Class<T> clazz) {
        return getExpression(this.expressionCache, elementKey, evalExpression)
                .getValue(evalContext, clazz);
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        return targetMethodCache.computeIfAbsent(methodKey, k -> AopUtils.getMostSpecificMethod(method, targetClass));
    }
}
