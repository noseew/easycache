package org.galileo.easycache.easycachetest.funtest.api;


import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.CacheRedisClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
public class CacheRedisClientAPITest {

    static final String cacheBean = "easycache_dft";

    @Autowired
    @Qualifier(cacheBean)
    private CacheClient cacheClient;

    private Random r = new Random();

    @Test
    public void cacheTest01() {
        CacheRedisClient cacheRedisClient = cacheClient.getCacheRedisClient();
        String cacheName = "set";
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
        System.out.println("ok");
    }

    @Test
    public void cacheTest02() {
        CacheRedisClient cacheRedisClient = cacheClient.getCacheRedisClient();
        String cacheName = "zset";
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

        System.out.println(cacheRedisClient.zRank(cacheName, val4));

        cacheRedisClient.zRemoveByScore(cacheName, 0, 10);
        assert cacheRedisClient.zCount(cacheName, 0, 10) == 0;

        System.out.println("ok");

    }


}
