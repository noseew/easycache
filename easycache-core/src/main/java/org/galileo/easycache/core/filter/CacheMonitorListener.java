package org.galileo.easycache.core.filter;

import com.google.common.collect.Sets;
import org.galileo.easycache.common.enums.OpType;
import org.galileo.easycache.core.core.AbsCache;
import org.galileo.easycache.core.event.AbsCacheOpEventListener;
import org.galileo.easycache.core.event.OpEvent;

import java.util.Set;

public class CacheMonitorListener extends AbsCacheOpEventListener {

    @Override
    public void on(OpEvent event) {
        FilterContext context = event.getContext();
        OpType opType = context.getOpType();
        if (OpType.GET != opType) {
            return;
        }
        AbsCache cache = (AbsCache) context.getCache();

        Monitor monitor = new Monitor();
        monitor.setKey(context.getKey());
        monitor.setCacheType(cache.getCacheType().getVal());
        monitor.setTimestamp(System.currentTimeMillis());
        monitor.setHit(context.isHit());
    }

    @Override
    public Set<Class<? extends  OpEvent>> getEventType() {
        return Sets.newHashSet();
    }

    static class Monitor {
        String key;
        String cacheType;
        long timestamp;
        boolean hit;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getCacheType() {
            return cacheType;
        }

        public void setCacheType(String cacheType) {
            this.cacheType = cacheType;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isHit() {
            return hit;
        }

        public void setHit(boolean hit) {
            this.hit = hit;
        }
    }
}
