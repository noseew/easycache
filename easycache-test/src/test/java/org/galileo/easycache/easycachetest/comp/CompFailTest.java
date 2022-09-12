package org.galileo.easycache.easycachetest.comp;

import com.google.common.collect.Lists;
import org.galileo.easycache.easycachetest.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EnableDiscoveryClient
public class CompFailTest extends TestCommon {

    @Test
    public void test01() {
        for (int i = 0; i < 100; i++) {
            del1().run();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
        }
    }

    @Test
    public void test02() {
        long start = System.currentTimeMillis();
        int[] wight = runnableWight();
        for (int i = 0; i < 100_0000; i++) {
            try {
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

    public List<Runnable> runnableList() {
        return Lists.newArrayList(add1(), del1(), update1());
    }

    public int[] runnableWight() {
        return new int[]{5, 5, 20};
    }

}
