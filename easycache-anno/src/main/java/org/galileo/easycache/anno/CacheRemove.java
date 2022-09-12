package org.galileo.easycache.anno;


import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.ConsistencyType;

import java.lang.annotation.*;

/**
 * 缓存删除接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(CacheRemoveAll.class)
public @interface CacheRemove {

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
     * 该值必填, 因为需要指定具体删除的缓存是什么
     *
     * @return
     */
    String cacheName();

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
     * 一致性策略
     * 
     * @return
     */
    ConsistencyType consistency() default ConsistencyType.DEFAULT;
}
