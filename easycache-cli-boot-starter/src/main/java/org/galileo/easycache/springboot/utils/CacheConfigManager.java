package org.galileo.easycache.springboot.utils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheConfigManager {

    private CacheConfigManager() {

    }

    public static final Map<Class, List<Annotation>> findClass = new ConcurrentHashMap<>();

    public static final Map<String, Map<String, List<Annotation>>> cacheNameConfigAnnos = new ConcurrentHashMap<>();

    public static void close() {
        findClass.clear();
        cacheNameConfigAnnos.clear();
    }
}
