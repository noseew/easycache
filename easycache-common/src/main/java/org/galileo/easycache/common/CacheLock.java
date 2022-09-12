package org.galileo.easycache.common;

/**
 * 缓存锁抽象
 * 用于缓存操作的各种锁能力
 * 默认提供不严谨的redis锁, 和基于ReentrantLock 的 JVM 锁
 */
public interface CacheLock {

    String LOCK_POSTFIX = "_uclock";

    /**
     *
     * @param key
     * @param mill 如果是 Redis 锁, 则是锁过期时间, 其他线程无等待; 如果是 JVM 锁, 则是其他线程等待时间
     * @return
     * @throws InterruptedException
     */
    boolean lock(String key, long mill) throws InterruptedException;

    /**
     * 限定锁成员数量的抢锁,
     * 如果锁等待线程数达到 maxItem, 则不在抢锁直接加锁失败
     *
     * @param key
     * @param mill
     * @param maxItem
     * @return
     * @throws InterruptedException
     */
    boolean lock(String key, long mill, int maxItem) throws InterruptedException;

    boolean unlock(String key);

    static String lockKey(String key) {
        return key + LOCK_POSTFIX;
    }

}
