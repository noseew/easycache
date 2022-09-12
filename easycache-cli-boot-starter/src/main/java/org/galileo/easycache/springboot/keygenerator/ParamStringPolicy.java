package org.galileo.easycache.springboot.keygenerator;

import org.apache.commons.lang3.ClassUtils;
import org.galileo.easycache.core.utils.InnerMD5Utils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ParamStringPolicy extends AbsKeyPolicy {

    private static final String MD5 = "MD5";

    protected static final List<String> IGNORE_NAMES = Arrays
            .asList("javax.servlet.ServletRequest", "javax.servlet.ServletResponse");

    private final List<Class<?>> ignoreClasses = new ArrayList<>();

    private int maxLen = 64;

    public ParamStringPolicy() {
        for (String eventClassName : IGNORE_NAMES) {
            Class<?> classSafe = getClassSafe(eventClassName);
            if (classSafe != null) {
                ignoreClasses.add(classSafe);
            }
        }
    }

    @Override
    public Object generateKey(Object target, Method method, Object... args) {
        if (args == null || args.length == 0) {
            String className = ClassUtils.getShortClassName(target.getClass());
            String methodName = method.getName();
            return className + "_" + methodName;
        }
        StringBuilder sb = new StringBuilder();
        for (Object param : args) {
            if (ignore(param)) {
                continue;
            }
            sb.append(param);
        }
        if (sb.length() > maxLen) {
            return MD5 + InnerMD5Utils.md5Hex(sb.toString(), "UTF-8");
        }
        return sb.toString();
    }

    private boolean ignore(Object param) {
        return this.ignoreClasses.stream()
                .anyMatch(clazz -> org.springframework.util.ClassUtils.isAssignableValue(clazz, param));
    }

    private Class<?> getClassSafe(String className) {
        try {
            return org.springframework.util.ClassUtils.forName(className, null);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }
}
