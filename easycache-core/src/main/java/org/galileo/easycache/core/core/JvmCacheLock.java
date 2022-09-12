package org.galileo.easycache.core.core;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class JvmCacheLock extends AbsCacheLock {

    private ConcurrentHashMap<String, CacheLoaderLock> lockMap = new ConcurrentHashMap<>(128);

    public JvmCacheLock() {
        super(null);
    }

    @Override
    public boolean lock(String key, long mill) throws InterruptedException {
        return lock(key, mill, -1);
    }

    @Override
    public boolean lock(String key, long mill, int maxItem) throws InterruptedException {
        if (key == null) {
            return false;
        }
        return lockRetry(key, mill, 3, maxItem);
    }

    private boolean lockRetry(String key, long mill, int times, int maxItem) throws InterruptedException {
        if (times <= 0) {
            return false;
        }
        CacheLoaderLock loaderLock = lockMap.computeIfAbsent(key, k -> {
            CacheLoaderLock cacheLoaderLock = new CacheLoaderLock();
            cacheLoaderLock.canRemove.set(false);
            return cacheLoaderLock;
        });
        if (!loaderLock.canRemove.get()) {
            if (maxItem <= 0 || loaderLock.lock.getQueueLength() < maxItem) {
                return loaderLock.lock.tryLock(mill, TimeUnit.MILLISECONDS);
            }
        }
        return lockRetry(key, mill, times - 1, maxItem);
    }

    @Override
    public boolean unlock(String key) {
        if (key == null) {
            return false;
        }
        CacheLoaderLock loaderLock = lockMap.get(key);
        if (loaderLock == null) {
            return false;
        }
        ReentrantLock lock = loaderLock.lock;
        try {
            lock.unlock();
        } catch (Exception e) {
            return false;
        }
        if (!lock.hasQueuedThreads() && loaderLock.canRemove.compareAndSet(false, true)) {
            lockMap.remove(key);
        }
        return true;
    }

    public int getLockerCount() {
        return lockMap.size();
    }

    public static class CacheLoaderLock {

        private AtomicBoolean canRemove = new AtomicBoolean(true);

        private ReentrantLock lock = new ReentrantLock();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("等待线程数=").append(lock.getQueueLength());
            return sb.toString();
        }

        public AtomicBoolean getCanRemove() {
            return canRemove;
        }

        public void setCanRemove(AtomicBoolean canRemove) {
            this.canRemove = canRemove;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        public void setLock(ReentrantLock lock) {
            this.lock = lock;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("锁数量=").append(lockMap.size()).append(", 等待线程数");
        return sb.toString();
    }
}
