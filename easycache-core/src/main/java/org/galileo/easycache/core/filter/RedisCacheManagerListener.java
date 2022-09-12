package org.galileo.easycache.core.filter;

import com.google.common.collect.Sets;
import org.galileo.easycache.core.core.AbsCache;
import org.galileo.easycache.core.core.config.NamespaceConfig;
import org.galileo.easycache.core.core.config.RemoteConfig;
import org.galileo.easycache.core.event.AbsCacheOpEventListener;
import org.galileo.easycache.core.event.CachePutEvent;
import org.galileo.easycache.core.event.CacheRemoveEvent;
import org.galileo.easycache.core.event.OpEvent;
import org.galileo.easycache.core.event.SetPutEvent;
import org.galileo.easycache.core.event.SetRemoveEvent;
import org.galileo.easycache.core.event.ZSetPutEvent;
import org.galileo.easycache.core.event.ZSetRemoveEvent;

import java.util.Set;

public class RedisCacheManagerListener extends AbsCacheOpEventListener {

    private RemoteConfig getRedisConfig(AbsCache cache) {
        return cache.getRemoteConfig();
    }

    @Override
    public void on(OpEvent event) {
        System.out.println("收到事件 " + event.toString());
//        FilterContext context = event.getContext();
//        OpType opType = context.getOpType();
//        if (OpType.PUT == opType) {
//            ValWrapper valWrapper = context.getValWrapper();
//            if (valWrapper == null) {
//                return;
//            }
//            String key = context.getKey();
//            int size = valWrapper.getSize();
//            NamespaceConfig redisConfig = getRedisConfig((AbsCache) context.getCache());
//        }
//        if (OpType.REMOVE == opType) {
//            NamespaceConfig redisConfig = getRedisConfig((AbsCache) context.getCache());
//            String key = context.getKey();
//            Set<String> keys = context.getKeys();
//
//        }

    }

    @Override
    public Set<Class<? extends OpEvent>> getEventType() {
        return Sets.newHashSet(
                CachePutEvent.class, CacheRemoveEvent.class,
                SetPutEvent.class, SetRemoveEvent.class,
                ZSetPutEvent.class, ZSetRemoveEvent.class);
    }
}
