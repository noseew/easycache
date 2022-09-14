package org.easycachetest.funtest.api;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheLock;
import org.galileo.easycache.common.CacheProxy;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.constants.SubNamespace;
import org.galileo.easycache.core.core.AbsCombinationCache;
import org.galileo.easycache.core.core.JvmCacheLock;
import org.easycachetest.utils.ThreadUtils;
import org.galileo.easycache.springboot.utils.InnerSpringContextUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class CacheLockTest {

    @Autowired(required = false)
    @Qualifier(CacheConstants.DEFAULT_NAMESPACE)
    private CacheClient cacheClient;

    @Autowired(required = false)
    @Qualifier(SubNamespace.DFT_LOCAL)
    private CacheClient localCache;

    @Autowired(required = false)
    @Qualifier(SubNamespace.DFT_REMOTE)
    private CacheClient remoteCache;

    public void init() {
        cacheClient = InnerSpringContextUtils.getApplicationContext().getBean(CacheConstants.cacheBeanName(CacheConstants.DEFAULT_NAMESPACE), CacheClient.class);
        localCache = InnerSpringContextUtils.getApplicationContext().getBean(CacheConstants.cacheBeanName(SubNamespace.DFT_LOCAL), CacheClient.class);
        remoteCache = InnerSpringContextUtils.getApplicationContext().getBean(CacheConstants.cacheBeanName(SubNamespace.DFT_REMOTE), CacheClient.class);
    }

    public CacheProxy getCache(CacheClient cacheClient) {
        if (cacheClient instanceof CacheProxy) {
            return ((CacheProxy) cacheClient).unProxy();
        }
        if (cacheClient instanceof AbsCombinationCache) {
            return (AbsCombinationCache) cacheClient;
        }
        return (CacheProxy) cacheClient;
    }

    @Test
    public void test01_local() {
        init();
        CacheProxy cache = getCache(localCache);
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            String key = UUID.randomUUID().toString().substring(0, 3);
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getId() + " 获取锁 超时");
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 60);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(cacheLocker);
        assert ((JvmCacheLock) cacheLocker).getLockerCount() == 0;

    }

    @Test
    public void test02_local() {
        init();
        CacheProxy cache = getCache(localCache);
        int[] ints = new int[1000];
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            try {
                int finalI = i;
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = String.valueOf(finalI);
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            ints[finalI % 1000]++;
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 30);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(Arrays.toString(ints));
        for (int anInt : ints) {
            assert anInt == 5;
        }

    }

    @Test
    public void test01_remote() {
        init();
        CacheProxy cache = getCache(remoteCache);
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            String key = UUID.randomUUID().toString().substring(0, 3);
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getId() + " 获取锁 超时");
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 60);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(cacheLocker);

    }

    @Test
    public void test02_remote() {
        init();
        CacheProxy cache = getCache(remoteCache);
        int[] ints = new int[1000];
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            try {
                int finalI = i;
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = String.valueOf(finalI);
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            ints[finalI % 1000]++;
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 30);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(Arrays.toString(ints));
        for (int anInt : ints) {
            assert anInt == 5;
        }

    }

    @Test
    public void test01_common() {
        init();
        CacheProxy cache = getCache(cacheClient);
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            String key = UUID.randomUUID().toString().substring(0, 3);
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getId() + " 获取锁 超时");
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 60);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(cacheLocker);

    }

    @Test
    public void test02_common() {
        CacheProxy cache = getCache(cacheClient);
        int[] ints = new int[1000];
        CacheLock cacheLocker = cache.getCacheLocker(false);

        for (int i = 0; i < 5000; i++) {
            try {
                int finalI = i;
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = String.valueOf(finalI);
                    boolean lock = false;
                    try {
                        lock = cacheLocker.lock(key, 1000 * 10);
                    } catch (InterruptedException e) {
                    }
                    if (lock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " 获取到锁");
                            ints[finalI % 1000]++;
                        } finally {
                            cacheLocker.unlock(key);
                            System.out.println(Thread.currentThread().getId() + " 释放锁");
                        }
                    }
                });

            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 30);
        } catch (InterruptedException interruptedException) {
        }
        System.out.println(Arrays.toString(ints));
        for (int anInt : ints) {
            assert anInt == 5;
        }

    }


}
