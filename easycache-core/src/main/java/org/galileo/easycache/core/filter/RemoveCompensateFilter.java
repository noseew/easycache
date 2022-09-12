package org.galileo.easycache.core.filter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.enums.OpType;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 删除补偿filter, 具体策略由子类实现
 */
public abstract class RemoveCompensateFilter extends AbsInvokeFilter {

    private ScheduledThreadPoolExecutor scheduled;
    /**
     * 用于记录重试期间, 已经成功处理的key
     * 1. 尽可能防止出现已经成功处理的key又被再次删除
     * 2. 尽可能降低处理队列的空间, 提高空间利用率, 能处理更多的key
     */
    protected ConcurrentLRUHashMap<Object, OpType> successKey;
    /**
     * 异步队列数量
     */
    private int maxSize = 1000;
    /**
     * 最大重试次数
     */
    private int maxTimes = 3;
    /**
     * 第一次重试间隔时间, 单位秒
     */
    private int initDelaySeconds = 3;

    protected RemoveCompensateFilter(int maxSize, int maxTimes, int initDelaySeconds, String name, String namespace) {
        super(name, namespace, null);
        this.maxSize = maxSize;
        this.maxTimes = maxTimes;
        this.initDelaySeconds = initDelaySeconds;
        scheduled = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setDaemon(true).setNameFormat(name + "-").build());
        successKey = new ConcurrentLRUHashMap<>(maxSize);
    }

    public void taskHandler(RemovedKey removedKey) {
        CacheProxy target = getTarget();
        if (scheduled.getQueue().size() < maxSize) {
            logger.warn("EasyCache key 删除失败, 等待重试; {}", removedKey);
            successKey.remove(removedKey.keyObj);
            addTask(removedKey, target, initDelaySeconds);
        } else {
            logger.warn("EasyCache key 删除失败, {}, 待删除队列已满 {}", removedKey, scheduled.getQueue().size());
        }
    }

    private void addTask(RemovedKey removedKey, CacheProxy target, int delay) {
        scheduled.schedule(() -> {
            try {
                if (successKey.remove(removedKey.keyObj) != null) {
                    // 重试期间, 该缓存已经成功处理, 无需再次处理
                    return;
                }
                if (StringUtils.isNotEmpty(removedKey.key)) {
                    target.remove(removedKey.key);
                }
                if (CollectionUtils.isNotEmpty(removedKey.keys)) {
                    target.removeAll(removedKey.keys);
                }
            } catch (Exception ex) {
                if (removedKey.times <= maxTimes) {
                    removedKey.times++;
                    if (successKey.remove(removedKey.keyObj) != null) {
                        // 重试期间, 该缓存已经成功处理, 无需再次处理
                        return;
                    }
                    addTask(removedKey, target, delay * 2);
                    logger.warn("EasyCache key 再次删除失败, 等待重试; {}", removedKey, ex);
                } else {
                    logger.error("EasyCache key 删除失败, 已重试最大次数 {}", removedKey, ex);
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    public static class ConcurrentLRUHashMap<K, V> extends LinkedHashMap<K, V> {
        /**
         * 最大容量
         * LRU淘汰的最大容量
         */
        private final int maxCapacity;

        private static final float DEFAULT_LOAD_FACTOR = 0.75f;

        /**
         * 加个锁, 目的是实现线程安全的 LRU 数据结构
         */
        private final Lock lock = new ReentrantLock();

        public ConcurrentLRUHashMap(int maxCapacity) {
            super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
            this.maxCapacity = maxCapacity;
        }

        /**
         * 是否满足淘汰条件
         *
         * @param eldest
         * @return
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxCapacity;
        }

        @Override
        public boolean containsKey(Object key) {
            lock.lock();
            try {
                return super.containsKey(key);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V get(Object key) {
            lock.lock();
            try {
                return super.get(key);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public V put(K key, V value) {
            lock.lock();
            try {
                return super.put(key, value);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int size() {
            lock.lock();
            try {
                return super.size();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void clear() {
            lock.lock();
            try {
                super.clear();
            } finally {
                lock.unlock();
            }
        }

        public Collection<Map.Entry<K, V>> getAll() {
            lock.lock();
            try {
                return new ArrayList<>(super.entrySet());
            } finally {
                lock.unlock();
            }
        }
    }

    public static class RemovedKey {
        Object keyObj;
        String key;
        Set<String> keys;
        int times;

        RemovedKey(Object keyObj) {
            this.keyObj = keyObj;
            if (keyObj instanceof String) {
                key = String.valueOf(keyObj);
            }
            if (keyObj instanceof Set) {
                keys = (Set) keyObj;
            }
        }

        public Object getKeyObj() {
            return keyObj;
        }

        public void setKeyObj(Object keyObj) {
            this.keyObj = keyObj;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Set<String> getKeys() {
            return keys;
        }

        public void setKeys(Set<String> keys) {
            this.keys = keys;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        String getStringKey() {
            return key;
        }
        Set getSetKeys() {
            return keys;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(key)) {
                sb.append("key=").append(key);
            } else if (CollectionUtils.isNotEmpty(keys)) {
                sb.append("keys=").append(Arrays.toString(keys.toArray()));
            }
            return sb.toString();
        }
    }
}
