package org.galileo.easycache.core.collect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppManagerCollector extends AbstractCollector<AppInfo, Map<String, AppInfo>> {

    // key=namespace, val=redis实例信息
    private Map<String, AppInfo> appInfoMap = new ConcurrentHashMap<>();
    
    @Override
    public void collect(AppInfo data) {
        
    }

    @Override
    public Map<String, AppInfo> take() {
        return null;
    }
}
