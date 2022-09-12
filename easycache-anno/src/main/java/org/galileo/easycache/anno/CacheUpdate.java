package org.galileo.easycache.anno;

import org.galileo.easycache.common.ExpirePolicy;
import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.enums.ConsistencyType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheUpdate {

    String namespace() default CacheConstants.DEFAULT_NAMESPACE;

    String cacheName();

    String key() default CacheConstants.DEFAULT_KEY;

    Class<? extends KeyGeneratorPolicy> keyPolicy() default KeyGeneratorPolicy.class;

    long expire() default CacheConstants.DEFAULT_EXPIRE;

    Class<? extends ExpirePolicy> expirePolicy() default ExpirePolicy.class;

    ConsistencyType consistency() default ConsistencyType.DEFAULT;
}
