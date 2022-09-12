package org.galileo.easycache.core.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricCollector extends AbstractCollector<MetricInfo, Map<String, MetricInfo>> {

    // key=key, val=缓存操作记录
    private Map<String, MetricInfo> metricInfoMap = new ConcurrentHashMap<>();
    
    @Override
    public void collect(MetricInfo data) {
        
    }

    @Override
    public Map<String, MetricInfo> take() {
        return null;
    }
}
