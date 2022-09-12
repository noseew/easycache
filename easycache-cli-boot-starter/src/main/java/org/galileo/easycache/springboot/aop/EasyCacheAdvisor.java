package org.galileo.easycache.springboot.aop;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EasyCacheAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private String[] basePackages;
    private Set<Class<? extends Annotation>> annotations = new HashSet<>();

    @Override
    public Pointcut getPointcut() {
        CachePointcut pointcut = new CachePointcut(basePackages);
        if (CollectionUtils.isNotEmpty(annotations)) {
            annotations.forEach(pointcut::addAnnotation);
        }
        return pointcut;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public void setScanAnnotations(Class<? extends Annotation>... annotations) {
        Objects.requireNonNull(annotations, "annotations can not be null");
        this.annotations.addAll(Arrays.asList(annotations));
    }
}
