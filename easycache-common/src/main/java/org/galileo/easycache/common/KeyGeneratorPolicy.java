package org.galileo.easycache.common;

import java.lang.reflect.Method;

/**
 * 缓存注解的key生成策略接口
 */
public interface KeyGeneratorPolicy {
    /**
     * 生成缓存的动态key
     * 
     * @param target
     * @param method
     * @param args
     * @return 可以是 String 单个key, 也可以是 Set<String>或String[]集合类型的key
     */
    Object generateKey(Object target, Method method, Object... args);
}
