package org.galileo.easycache.common;

import java.lang.annotation.*;

/**
 * 内部使用,
 * 用于标识设置缓存的参数的值
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValParam {
    Class<?> paramType() default NullClass.class;
}
