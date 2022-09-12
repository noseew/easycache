package org.galileo.easycache.anno;


import org.galileo.easycache.common.RefreshPolicy;
import org.galileo.easycache.common.constants.CacheConstants;

import java.lang.annotation.*;

/**
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheRefresh {

    boolean cacheNullValue() default CacheConstants.DEFAULT_CACHE_NULL_VALUE;

    Class<? extends RefreshPolicy> refreshPolicy() default RefreshPolicy.class;

}
