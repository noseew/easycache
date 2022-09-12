package org.galileo.easycache.easycachetest.perftest;

import lombok.Data;
import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.constants.SubNamespace;
import org.galileo.easycache.easycachetest.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
public class CacheConcurrencyTests {

    @Autowired
    @Qualifier(CacheConstants.DEFAULT_NAMESPACE)
    private CacheClient autoCache;

    @Autowired
    @Qualifier(SubNamespace.DFT_LOCAL)
    private CacheClient localCache;

    @Autowired
    @Qualifier(SubNamespace.DFT_REMOTE)
    private CacheClient remoteCache;

    private long count = 1_0000_0000;

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test01() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(autoCache, key);
                    get(autoCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("autoCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test01_get() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    get(autoCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("autoCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test01_put() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(autoCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("autoCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test02() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(localCache, key);
                    get(localCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("localCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test02_get() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(localCache, key);
                    get(localCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("localCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test02_put() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(localCache, key);
                    get(localCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("localCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test03() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(remoteCache, key);
                    get(remoteCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("remoteCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test03_get() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    get(remoteCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("remoteCache end");
    }

    /**
     * -ea -Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
     */
    @Test
    public void test03_put() {
        for (int i = 0; i < count; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> {
                    String key = UUID.randomUUID().toString().substring(0, 5);
                    put(remoteCache, key);
                });
            } catch (Exception e) {
                System.out.println("full");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ex) {
                }
            }
        }
        while (!ThreadUtils.poolExecutor.getQueue().isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("remoteCache end");
    }

    public void put(CacheClient cacheProxy, String key) {
        User user = new User();
        ValWrapper valWrapper = ValWrapper.createInstance(1000 * 60, user);
        cacheProxy.put(key, valWrapper);
    }

    public void get(CacheClient cacheProxy, String key) {
        ValWrapper val = cacheProxy.get(key);
    }


    @Data
    static class User {
        int wight;
        int age;
        String name;
    }
}
