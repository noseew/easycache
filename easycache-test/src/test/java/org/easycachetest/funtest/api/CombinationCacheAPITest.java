package org.easycachetest.funtest.api;


import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.ValWrapper;
import org.galileo.easycache.core.utils.EasyCacheUtils;
import org.easycachetest.entity.UserDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class CombinationCacheAPITest {

    static final String cacheBean = "easycache_dft";

    @Autowired
    @Qualifier(cacheBean)
    private CacheClient cacheClient;

    @Test
    public void cacheTest01() {
        String key = "key";
        ValWrapper valWrapper = ValWrapper.createInstance(1000 * 60, key);

        cacheClient.put(key, valWrapper);

        ValWrapper val = cacheClient.get(key);
        System.out.println(val);
        val = cacheClient.get(key);
        System.out.println(val);
    }

    @Test
    public void cacheTest02() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1250; i++) {
            list.add(i);
        }

        cacheClient.removeAll(list.stream().map(Object::toString).collect(Collectors.toSet()));
    }

    @Test
    public void cacheTest03() {
        String namespace = "dft";
        String cacheName = "user";
        String key = String.valueOf(1);

        UserDO userDO = new UserDO();
        userDO.setName(UUID.randomUUID().toString().substring(0, 5));
        userDO.setId(1);
        userDO.setSalary(BigDecimal.valueOf(Math.random() * 1000));
        userDO.setAge((int) (Math.random() * 100));
        userDO.setCreateTime(new Date());
        EasyCacheUtils.put(namespace, cacheName, key, userDO, 100 * 1000);

        UserDO userDO1 = EasyCacheUtils.get(namespace, cacheName, key, UserDO.class);
        System.out.println(userDO1);
        assert userDO.equals(userDO1);
    }


}
