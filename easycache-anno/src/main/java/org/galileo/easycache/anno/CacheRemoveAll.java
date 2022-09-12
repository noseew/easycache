package org.galileo.easycache.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated
public @interface CacheRemoveAll {

    CacheRemove[] value();
}
