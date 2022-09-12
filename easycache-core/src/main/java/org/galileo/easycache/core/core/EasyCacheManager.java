package org.galileo.easycache.core.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.RefreshPolicy;
import org.galileo.easycache.common.SerialPolicy;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.event.EventListener;
import org.galileo.easycache.core.filter.AbsInvokeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EasyCacheManager {

    private static Logger logger = LoggerFactory.getLogger(EasyCacheManager.class);

    private EasyCacheManager() {

    }

    private static Map<String, SerialPolicy> serialPolicyMap = new ConcurrentHashMap<>();

    private static Map<String, RefreshPolicy> refreshPolicyMap = new ConcurrentHashMap<>();

    private static Map<String, CacheProxy> cacheMap = new ConcurrentHashMap<>();

    private static List<AbsInvokeFilter> allFilterList = new CopyOnWriteArrayList<>();

    private static List<EventListener> listeners = new CopyOnWriteArrayList<>();

    static {
        serialPolicyMap.put(SerialPolicy.Gzip, new GZIPSerializerWrapper());
        serialPolicyMap.put(SerialPolicy.Jackson, new JacksonSerializer());
        serialPolicyMap.put(SerialPolicy.STRING, new StringSerializerWrapper(StandardCharsets.UTF_8));
        serialPolicyMap.put(SerialPolicy.Protostuff, new ProtostuffSerial(ValWrapper.class));
    }

    public static List<EventListener> getEventListenerList() {
        return listeners;
    }

    public static void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public static List<AbsInvokeFilter> getAllFilterList() {
        return allFilterList;
    }

    public static void addFilter(AbsInvokeFilter filter) {
        allFilterList.add(filter);
    }

    public static void addSerialPolicy(String name, SerialPolicy policy) {
        if (StringUtils.isEmpty(name) || policy == null) {
            return;
        }
        serialPolicyMap.put(name, policy);
    }

    public static SerialPolicy getSerialPolicy(String name, SerialPolicy defaultIfAbsent) {
        if (StringUtils.isEmpty(name)) {
            return defaultIfAbsent;
        }
        return serialPolicyMap.get(name);
    }

    public static void addRefreshPolicy(String name, RefreshPolicy policy) {
        if (StringUtils.isEmpty(name) || policy == null) {
            return;
        }
        refreshPolicyMap.put(name, policy);
    }

    public static RefreshPolicy getRefreshPolicy(String name, RefreshPolicy defaultIfAbsent) {
        if (StringUtils.isEmpty(name)) {
            return defaultIfAbsent;
        }
        return refreshPolicyMap.get(name);
    }

    public static CacheProxy getCache(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return cacheMap.get(name);
    }

    public static void addCache(String name, CacheProxy cache) {
        if (StringUtils.isEmpty(name) || cache == null) {
            return;
        }
        cacheMap.put(name, cache);
    }

    public static void close() {
        serialPolicyMap.clear();
        refreshPolicyMap.clear();
        cacheMap.forEach((k, v) -> {
            try {
                v.close();
            } catch (Exception e) {
                // ignore
            }
        });
        cacheMap.clear();
        allFilterList.clear();
        listeners.clear();
    }

    static {
        Thread thread = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("EasyCacheManager-release-hook")
                .build()
                .newThread(() -> {
            try {
                close();
            } catch (Exception ex) {
                logger.error("EasyCacheManager-release-hook error ", ex);
            }
        });
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
