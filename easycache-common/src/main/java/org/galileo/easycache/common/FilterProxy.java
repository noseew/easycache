package org.galileo.easycache.common;


import org.galileo.easycache.common.enums.OpType;

import java.lang.annotation.*;

/**
 * 内部使用, 用于决定 Cache 实例中, 哪些方法需要被 filter 拦截
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FilterProxy {

    OpType opType();

    boolean hasValParam() default false;

}
