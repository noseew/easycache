package org.galileo.easycache.core.utils;


import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.enums.CacheTagType;
import org.galileo.easycache.core.core.config.EasyCacheConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;

public class InnerKeyUtils {

    private static final String PLACEHOLDER = ":";

    private InnerKeyUtils() {

    }

    public static String buildFullKey(RemoteConfig namespaceConfig, CacheTagType cacheTagType, String cacheName) {
        return buildFullKey(namespaceConfig, cacheTagType, cacheName, null);
    }

    public static String buildFullKey(RemoteConfig namespaceConfig, CacheTagType cacheTagType, String cacheName, String key) {
        EasyCacheConfig cacheConfig = namespaceConfig.getParent().getParent();
        return buildFullKey(cacheConfig, namespaceConfig.getNamespace(), cacheTagType, cacheName, key);
    }

    public static String buildFullKey(EasyCacheConfig cacheConfig, String namespace, CacheTagType cacheTagType, String cacheName, String key) {
        String appName = cacheConfig.getAppName();
        String trafficTag = cacheConfig.getTrafficTag();
        /*
        appname + namespace + ec + trafficTag + cacheName + key
         */
        if (StringUtils.isNotEmpty(trafficTag)) {
            return InnerKeyUtils.append(appName, namespace, cacheTagType.getVal(), trafficTag, cacheName, key);
        }
        return InnerKeyUtils.append(appName, namespace, cacheTagType.getVal(), cacheName, key);
    }

    private static String append(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return String.valueOf(args[0]);
        }
        StringBuilder sb = new StringBuilder(args.length);
        for (Object arg : args) {
            if (arg != null && StringUtils.isNotEmpty(arg.toString())) {
                sb.append(arg).append(PLACEHOLDER);
            }
        }
        return sb.substring(0, sb.length() - 1);
    }
}
