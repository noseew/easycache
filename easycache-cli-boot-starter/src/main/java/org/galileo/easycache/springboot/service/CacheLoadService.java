package org.galileo.easycache.springboot.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheLoadService {

    private Map<String, CacheNameCounter> cacheLoadTime = new ConcurrentHashMap<>();
    /**
     * 只记录近100次的平均过期时间
     */
    private int maxTimes = 100;

    public long getTime(String namespace, String cacheName) {
        CacheNameCounter counter = cacheLoadTime.get(namespace + cacheName);
        if (counter != null) {
            return counter.avg;
        }
        return 0;
    }

    public void setTime(String namespace, String cacheName, long mill) {
        if (mill <=0) {
            return;
        }
        CacheNameCounter counter = cacheLoadTime.computeIfAbsent(namespace + cacheName, k -> new CacheNameCounter());
        // new avg = (avg * times + mill) / (times + 1) = (mill - avg) / (times + 1)
        if (counter.times++ > maxTimes) {
            counter.times = maxTimes;
        }
        counter.avg = (mill - counter.avg) / counter.times;
    }

    public static class CacheNameCounter {
        int times = 1;
        long avg;
    }
}
