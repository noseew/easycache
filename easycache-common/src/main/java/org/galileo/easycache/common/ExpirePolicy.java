package org.galileo.easycache.common;

import java.lang.reflect.Method;

/**
 * 缓存过期策略接口
 * 用于用户自定义注解的过期时间
 */
public interface ExpirePolicy {
    /**
     * 返回指定缓存的过期时间
     *
     * @param target 用户方法所属对象
     * @param method 用户缓存方法
     * @param args   用户方法参数
     * @return 过期时间毫秒值
     */
    long expire(Object target, Method method, Object... args);
}
