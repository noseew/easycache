package org.galileo.easycache.springboot.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EasyCacheAutoConfiguration.class)
public @interface EnableEasyCache {
    String[] basePackages() default {};
}
