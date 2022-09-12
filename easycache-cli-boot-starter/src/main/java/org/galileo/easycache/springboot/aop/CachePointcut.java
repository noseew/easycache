package org.galileo.easycache.springboot.aop;

import com.google.common.collect.Lists;
import org.galileo.easycache.springboot.utils.CacheConfigManager;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CachePointcut extends StaticMethodMatcherPointcut implements ClassFilter {

    private final String[] basePackages;
    private final Set<Class<? extends Annotation>> annotations = new HashSet<>();

    public CachePointcut(String[] basePackages) {
        setClassFilter(this);
        this.basePackages = basePackages;
    }

    public void addAnnotation(Class<? extends Annotation> annotation) {
        Objects.requireNonNull(annotation, "annotation can not be null");
        annotations.add(annotation);
    }

    @Override
    public boolean matches(Class clazz) {
        boolean b = matchesImpl(clazz);
        return b;
    }

    private boolean matchesImpl(Class clazz) {
        if (matchesThis(clazz)) {
            return true;
        }
        Class[] cs = clazz.getInterfaces();
        if (cs != null) {
            for (Class c : cs) {
                if (matchesImpl(c)) {
                    return true;
                }
            }
        }
        if (!clazz.isInterface()) {
            Class sp = clazz.getSuperclass();
            if (sp != null && matchesImpl(sp)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesThis(Class clazz) {
        String name = clazz.getName();
        if (exclude(name)) {
            return false;
        }
        return include(name);
    }

    private boolean include(String name) {
        if (basePackages != null) {
            for (String p : basePackages) {
                if (name.startsWith(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean exclude(String name) {
        if (name.startsWith("java")) {
            return true;
        }
        if (name.startsWith("org.springframework")) {
            return true;
        }
        if (name.indexOf("$$EnhancerBySpringCGLIB$$") >= 0) {
            return true;
        }
        if (name.indexOf("$$FastClassBySpringCGLIB$$") >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean matches(Method method, Class targetClass) {
        boolean b = matchesImpl(method, targetClass);
        return b;
    }

    private boolean matchesImpl(Method method, Class targetClass) {
        if (!matchesThis(method.getDeclaringClass())) {
            return false;
        }
        if (exclude(targetClass.getName())) {
            return false;
        }
        for (Class<? extends Annotation> annotation : annotations) {
            Annotation cacheAnno = method.getAnnotation(annotation);
            if (cacheAnno != null) {
                List<Annotation> annotations = CacheConfigManager.findClass
                        .computeIfAbsent(targetClass, k -> Lists.newArrayList());
                annotations.add(cacheAnno);
                return true;
            }
        }
        return false;
    }
}