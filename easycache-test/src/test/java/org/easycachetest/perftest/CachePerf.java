package org.easycachetest.perftest;

import lombok.Data;
import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.common.constants.SubNamespace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/*
-XX:+UnlockCommercialFeatures
-Xmx2048m -Xms2048m -XX:MetaspaceSize=1024m
 */
//@SpringBootTest
public class CachePerf {

    @Autowired
    @Qualifier(CacheConstants.DEFAULT_NAMESPACE)
    private CacheClient autoCache;

    @Autowired
    @Qualifier(SubNamespace.DFT_LOCAL)
    private CacheClient localCache;

    @Autowired
    @Qualifier(SubNamespace.DFT_REMOTE)
    private CacheClient remoteCache;



    /**
     * 测试结果, 序列化和反序列化 占用耗时比较高
     */
    @Test
    public void test01() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10_0000; i++) {
            put(autoCache);
            get(autoCache);
        }
        long l2 = System.currentTimeMillis();
        System.out.println("autoCache " + (l2 - l));// QPS 6000
    }

    /**
     * 测试结果, 序列化和反序列化 占用耗时比较高
     * UUID 占用耗时比较高
     */
    @Test
    public void test02() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100_0000; i++) {
            put(localCache);
            get(localCache);
        }
        long l2 = System.currentTimeMillis();
        System.out.println("localCache " + (l2 - l));
    }

    /**
     * 测试结果, 序列化和反序列化 占用耗时比较高
     */
    @Test
    public void test03() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 10_0000; i++) {
            put(remoteCache);
            get(remoteCache);
        }
        long l2 = System.currentTimeMillis();
        System.out.println("remoteCache " + (l2 - l));
    }

    public void put(CacheClient cacheProxy) {
        User user = new User();
        ValWrapper valWrapper = ValWrapper.createInstance(1000 * 60, user);
        cacheProxy.put("key1", valWrapper);
    }

    public void get(CacheClient cacheProxy) {
        ValWrapper val = cacheProxy.get("key1");
    }


    @Data
    static class User {
        int wight;
    }
}
