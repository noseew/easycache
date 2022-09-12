package org.galileo.easycache.core.collect;

import org.galileo.easycache.core.report.ReportData;

public class MetricInfo extends ReportData {
    private String key;
    private String cacheName;
    private String cacheType;
    private int qps;
    private int getHitCount;
    private int getMissCount;
    private int getRenewalCount;
    private int removeCount;
    private long loadTime;
    private int size;
    
    
}
