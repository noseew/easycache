package org.galileo.easycache.common;

public interface RefreshPolicy {

    // 当前 cacheName 最多自动更新缓存的数量
    int limit();

    // 最后一次访问后多少毫秒停止刷新
    long stopRefreshAfterLastAccessMill();

}
