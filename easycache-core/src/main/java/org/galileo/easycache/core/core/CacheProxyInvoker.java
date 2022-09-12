package org.galileo.easycache.core.core;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.galileo.easycache.core.exception.CacheInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法调用器
 */
public abstract class CacheProxyInvoker {

    public static final CacheProxyInvoker reflectInvoker = new Jdk();

    private static Logger logger = LoggerFactory.getLogger(CacheProxyInvoker.class);

    protected CacheProxyInvoker(Object target) {

    }

    public abstract Object invoke(Object object, Method method, Object... args);

    /**
     * 基于 asm 实现的反射调用
     */
    public static class Asm extends CacheProxyInvoker {

        private Map<Object, MethodAccess> accessMap = new ConcurrentHashMap<>();

        public Asm(Object target) {
            super(target);
            accessMap.put(target, MethodAccess.get(target.getClass()));
        }

        @Override
        public Object invoke(Object object, Method method, Object... args) {
            if (method.isDefault()) {
                return reflectInvoker.invoke(object, method, args);
            }
            MethodAccess methodAccess = accessMap.computeIfAbsent(object, k -> MethodAccess.get(k.getClass()));
            return methodAccess.invoke(object, method.getName(), args);
        }
    }

    /**
     * 基于java原生的反射调用
     */
    public static class Jdk extends CacheProxyInvoker {

        public Jdk() {
            super(null);
        }

        @Override
        public Object invoke(Object object, Method method, Object... args) {
            try {
                return method.invoke(object, args);
            } catch (IllegalAccessException e) {
                logger.warn("EasyCache Jdk invoke error ", e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof CacheInterruptException) {
                    throw (CacheInterruptException) e.getTargetException();
                }
                logger.warn("EasyCache invoke error", e);
            } catch (Exception e) {
                throw e;
            }
            return null;
        }
    }

}

