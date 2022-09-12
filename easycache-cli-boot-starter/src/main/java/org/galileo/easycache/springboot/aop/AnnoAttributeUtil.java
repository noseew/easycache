package org.galileo.easycache.springboot.aop;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.galileo.easycache.common.ExpirePolicy;
import org.galileo.easycache.common.KeyGeneratorPolicy;
import org.galileo.easycache.common.enums.BreakdownType;
import org.galileo.easycache.common.enums.ConsistencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class AnnoAttributeUtil {

    private static Logger logger = LoggerFactory.getLogger(AnnoAttributeUtil.class);

    private AnnoAttributeUtil() {

    }

    public static <A extends Annotation> int notNullSize(A... annos) {
        if (annos == null || annos.length == 0) {
            return 0;
        }
        int size = 0;
        for (A anno : annos) {
            if (anno != null) {
                size++;
            }
        }
        return size;
    }

    public static <A extends Annotation> String getNamespace(A... annos) {
        return getAttributeByAnno(null, a -> {
            try {
                return (String) MethodUtils.invokeMethod(a, "namespace");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
                logger.warn("", e);
            }
            return "";
        }, annos);
    }

    public static <A extends Annotation> ConsistencyType getConsistency(A... annos) {
        return getAttributeByAnno(ConsistencyType.EVENTUAL, a -> {
            try {
                return (ConsistencyType) MethodUtils.invokeMethod(a, "consistency");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
                logger.warn("", e);
            }
            return ConsistencyType.EVENTUAL;
        }, annos);
    }

    public static <A extends Annotation> BreakdownType getBreakdown(A... annos) {
        return getAttributeByAnno(BreakdownType.NONE, a -> {
            try {
                return (BreakdownType) MethodUtils.invokeMethod(a, "breakDown");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
                logger.warn("", e);
            }
            return BreakdownType.NONE;
        }, annos);
    }

    public static <A extends Annotation> String getCacheName(A... annos) {
        return getAttributeByAnno(null, a -> {
            try {
                return (String) MethodUtils.invokeMethod(a, "cacheName");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
                logger.warn("", e);
            }
            return "";
        }, annos);
    }

    public static <A extends Annotation> String getKey(A... annos) {
        return getAttributeByAnno(null, a -> {
            try {
                return (String) MethodUtils.invokeMethod(a, "key");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
            return "";
        }, annos);
    }

    public static <A extends Annotation> Class<? extends KeyGeneratorPolicy> getKeyPolicy(A... annos) {
        return getAttributeByAnno(KeyGeneratorPolicy.class, a -> {
            try {
                return (Class) MethodUtils.invokeMethod(a, "keyPolicy");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
            return KeyGeneratorPolicy.class;
        }, annos);
    }

    public static <A extends Annotation> Long getExpire(A... annos) {
        return getAttributeByAnno(-1L, a -> {
            try {
                return (long) MethodUtils.invokeMethod(a, "expire");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
            return -1L;
        }, annos);
    }

    public static <A extends Annotation> Class<? extends ExpirePolicy> getExpirePolicy(A... annos) {
        return getAttributeByAnno(ExpirePolicy.class, a -> {
            try {
                return (Class) MethodUtils.invokeMethod(a, "expirePolicy");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
            return ExpirePolicy.class;
        }, annos);
    }

    private static <R, A extends Annotation> R getAttributeByAnno(R dftValWhenNull, Function<A, R> getVal, A... annos) {
        if (annos == null || annos.length == 0 || !ObjectUtils.anyNotNull(annos)) {
            return dftValWhenNull;
        }
        for (A anno : annos) {
            if (anno == null) {
                continue;
            }
            return getVal.apply(anno);
        }
        return dftValWhenNull;
    }
}
