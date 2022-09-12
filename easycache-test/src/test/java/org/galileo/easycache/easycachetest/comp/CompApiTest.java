package org.galileo.easycache.easycachetest.comp;

import org.galileo.easycache.common.CacheClient;
import org.galileo.easycache.common.constants.CacheConstants;
import org.galileo.easycache.easycachetest.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class CompApiTest extends TestCommon {

    @Autowired
    @Qualifier(CacheConstants.DEFAULT_NAMESPACE)
    private CacheClient cacheClient;

    @Test
    public void test01() {
        int[] wight = runnableWight();
        for (int i = 0; i < 10_0000; i++) {
            lb(wight).run();
        }
    }

    @Test
    public void test02() {
        int[] wight = runnableWight();
        for (int i = 0; i < 1_0000; i++) {
            try {
                ThreadUtils.poolExecutor.execute(() -> lb(wight).run());
            } catch (Exception e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
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
    }

    public Runnable cached1() {
        return () -> {
            int key = (int) (Math.random() * 1000);
            try {
                cacheClient.get(getUserKey(String.valueOf(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable cached2() {
        return () -> {
            int key = (int) (Math.random() * 1000);
            try {
                cacheClient.get(getUserDetailKey(String.valueOf(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable del1() {
        int key = (int) (Math.random() * 1000);
        return () -> {
            try {
                cacheClient.remove(getUserKey(String.valueOf(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable del2() {
        return () -> {
            int key = (int) (Math.random() * 1000);
            try {
                cacheClient.remove(getUserDetailKey(String.valueOf(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


    public static String getUserKey(String key) {
        return "cacheTest:dft:ec:user:" + key;
    }

    public static String getUserDetailKey(String key) {
        return "cacheTest:dft:ec:user_detail:" + key;
    }

}
