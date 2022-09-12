package org.galileo.easycache.anno;


import org.galileo.easycache.common.ExpirePolicy;
import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.BreakdownType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动缓存注解, 修饰在方法上, 表示该方法已经被缓存组件装饰
 * 访问该方法时, 优先访问缓存, 如果命中缓存则直接返回, 否则执行用户方法并存入缓存后再返回
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    /**
     * 命名空间 默认值 dft
     * 如果是Redis, 区分Redis物理集群, 也可以在同一个Redis集群中用于数据的逻辑区分
     *
     * @return
     */
    String namespace() default CacheConstants.DEFAULT_NAMESPACE;

    /**
     * 缓存名称, 区分缓存的功能
     * 如果是Redis, 表示Redis key中不变的部分(不考虑前缀)
     *
     * @return
     */
    String cacheName() default CacheConstants.DEFAULT_CACHE_NAME;

    /**
     * 缓存的具体key
     * 如果是Redis, 表示Redis key中变化的部分(不考虑前缀)
     *
     * @return
     */
    String key() default CacheConstants.DEFAULT_KEY;

    /**
     * key 生成策略,
     * 用于生成 key, 如果指定了自定义的, 则优先级高于 key()
     *
     * @return
     */
    Class<? extends KeyGeneratorPolicy> keyPolicy() default KeyGeneratorPolicy.class;

    /**
     * 缓存过期时间, 单位ms
     *
     * @return
     */
    long expire() default CacheConstants.DEFAULT_EXPIRE;

    /**
     * 缓存过期时间策略, 用于生成过期时间,
     * 如果指定了自定义的, 则优先级高于 expire()
     *
     * @return
     */
    Class<? extends ExpirePolicy> expirePolicy() default ExpirePolicy.class;

    /**
     * 缓存击穿策略
     *
     * @return
     */
    BreakdownType breakDown() default BreakdownType.DEFAULT;

    /**
     * 缓存续期时间
     *
     * @return
     */
    long renewalTime() default CacheConstants.RENEWAL_TIME;

}
