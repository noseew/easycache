package org.galileo.easycache.core.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManagerCollector extends AbstractCollector<CacheInfo, Map<String, CacheInfo>> {

    // key=key, val=缓存简要信息
    private Map<String, CacheInfo> cacheInfoMap = new ConcurrentHashMap<>();
    
    @Override
    public void collect(CacheInfo data) {
        
    }

    @Override
    public Map<String, CacheInfo> take() {
        return null;
    }
}
