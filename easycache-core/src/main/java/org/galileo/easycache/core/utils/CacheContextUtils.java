package org.galileo.easycache.core.utils;

import org.galileo.easycache.core.filter.FilterContext;
import org.galileo.easycache.core.filter.LoadValContext;

public class CacheContextUtils {

    private CacheContextUtils() {

    }

    private static ThreadLocal<FilterContext> cacheFilter = new InheritableThreadLocal<>();

    private static ThreadLocal<LoadValContext> loadVal = new InheritableThreadLocal<>();

    public static void addFilterContext(FilterContext context) {
        cacheFilter.set(context);
    }

    public static FilterContext getFilterContext() {
        return cacheFilter.get();
    }

    public static void removeFilterContext() {
        cacheFilter.remove();
    }

    public static void addLoadValContext(LoadValContext context) {
        loadVal.set(context);
    }

    public static LoadValContext getLoadValContext() {
        return loadVal.get();
    }

    public static void removeLoadVal() {
        loadVal.remove();
    }

}
