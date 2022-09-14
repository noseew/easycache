package org.galileo.easycache.easycachetest.comp;

import org.galileo.easycache.common.CacheRedisClient;
import org.galileo.easycache.core.utils.EasyCacheUtils;
import org.galileo.easycache.easycachetest.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 综合性测试,
 * 用于模拟真实场景使用, 包含了大多数API使用方式,
 * 用于代码修改后的常规测试
 * 测试结果需要在控制台查看是否有异常信息
 */
@SpringBootTest
@EnableDiscoveryClient
public class CompAnnoTest extends TestCommon {

    @Test
    public void test01() {
        long start = System.currentTimeMillis();
        int[] wight = runnableWight();
        for (int i = 0; i < 1_0000; i++) {
            lb(wight).run();
            if (i % 10 == 0) redisClientTest();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void test02() {
        long start = System.currentTimeMillis();
        int[] wight = runnableWight();
        for (int i = 0; i < 10_0000; i++) {
            try {
                if (i % 50 == 0) redisClientTest();
                ThreadUtils.poolExecutor.execute(() -> lb(wight).run());
            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException interruptedException) {
                }
            }
        }
        if (ThreadUtils.poolExecutor.getActiveCount() > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000 * 5);
            } catch (InterruptedException interruptedException) {
            }
        }
        ThreadUtils.poolExecutor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }


    private Random r = new Random();

    public void redisClientTest() {
        setTest();
        zsetTest();
    }

    public void setTest() {
        CacheRedisClient cacheRedisClient = EasyCacheUtils.getCacheRedisClient("dft");
        String cacheName = "set" + UUID.randomUUID().toString();
        String val = "val1";
        cacheRedisClient.sAdd(cacheName, 60 * 1000, val);
        assert cacheRedisClient.sCard(cacheName) == 1;
        String sPop = cacheRedisClient.sPop(cacheName);
        assert val.equals(sPop);

        cacheRedisClient.sAdd(cacheName, 60 * 1000, val);
        cacheRedisClient.sRemove(cacheName, val);
        assert cacheRedisClient.sCard(cacheName) == 0;
        String val2 = "val12";
        String val3 = "val12";
        String val4 = "val13";
        cacheRedisClient.sAdd(cacheName, 60 * 1000, val2, val3, val4);
        assert cacheRedisClient.sCard(cacheName) == 2;
        cacheRedisClient.sRemove(cacheName, val3, val4);
    }

    public void zsetTest() {
        CacheRedisClient cacheRedisClient = EasyCacheUtils.getCacheRedisClient("dft");
        String cacheName = "zset" + UUID.randomUUID().toString();
        String val = "val1";

        cacheRedisClient.zAdd(cacheName, 60 * 1000, r.nextInt(10), val);
        assert cacheRedisClient.zCount(cacheName, 0, 10) == 1;
        assert cacheRedisClient.zRange(cacheName, 0, 10).contains(val);
        cacheRedisClient.zRemove(cacheName, val);
        assert cacheRedisClient.zCount(cacheName, 0, 10) == 0;

        String val2 = "val12";
        cacheRedisClient.zAdd(cacheName, 60 * 1000, r.nextInt(10), val2);
        String val3 = "val12";
        cacheRedisClient.zAdd(cacheName, 60 * 1000, r.nextInt(10), val3);
        String val4 = "val13";
        cacheRedisClient.zAdd(cacheName, 60 * 1000, r.nextInt(10), val4);

        assert cacheRedisClient.zCount(cacheName, 0, 10) == 2;
        assert cacheRedisClient.zCount(cacheName, 0, 10) == 2;

        cacheRedisClient.zRemoveByScore(cacheName, 0, 10);
        assert cacheRedisClient.zCount(cacheName, 0, 10) == 0;
    }

}
