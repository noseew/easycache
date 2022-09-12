package org.galileo.easycache.core.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.core.core.config.CacheReporterConfig;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.event.CacheOpEventPublisher;
import org.galileo.easycache.core.filter.RedisCacheManagerListener;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 */
public class AutoCacheBuilder extends AbstractCacheBuilder<AutoCacheBuilder> {

    private final CacheProxy local;
    private final CacheProxy remote;


    private AutoCacheBuilder(RemoteConfig remoteConfig, CacheProxy local, CacheProxy remote) {
        super(remoteConfig);
        this.local = local;
        this.remote = remote;
    }

    public static AutoCacheBuilder createBuilder(RemoteConfig remoteConfig, CacheProxy local, CacheProxy remote) {
        return new AutoCacheBuilder(remoteConfig, local, remote);
    }

    @Override
    public CacheClient buildCache() {
        AutoCache cache = new AutoCache(remoteConfig, local, remote);
        addHeadFilter(cache);
        return createCacheProxy(cache);
    }

    @Override
    protected void preBuild() {
        String namespace = remoteConfig.getNamespace();
        // 添加 拦截器
        // 事件发布器
        CacheOpEventPublisher cacheOpEventPublisher = new CacheOpEventPublisher(namespace);
        addFilters(cacheOpEventPublisher);
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors * 4, 5 * 60 * 1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(5000),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("CacheOpEventPublisher-").build(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        cacheOpEventPublisher.setExecutor(executor);

        // 添加事件监听器, 缓存key管理监听
        CacheReporterConfig cacheReporter = remoteConfig.getParent().getCacheReporter();
        if (cacheReporter != null && cacheReporter.isEnabled()) {
            RedisCacheManagerListener cacheManagerListener = new RedisCacheManagerListener();
            EasyCacheManager.addEventListener(cacheManagerListener);
        }
    }

}
