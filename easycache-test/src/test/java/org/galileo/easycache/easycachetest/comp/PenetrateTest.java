package org.galileo.easycache.easycachetest.comp;

import com.google.common.collect.Lists;
import org.galileo.easycache.easycachetest.utils.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class PenetrateTest extends TestCommon {


    @Test
    public void breakdownTest() {

        long start = System.currentTimeMillis();
        int[] wight = runnableWight();
        for (int i = 0; i < 100; i++) {
            try {
                for (int j = 0; j < 10000; j++) {
                    ThreadUtils.poolExecutor.execute(() -> lb(wight).run());
                }
            } catch (Exception e) {
                System.out.println("reject");
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
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
        System.out.println((end - start) / (10 * 1000));
    }


    public List<Runnable> runnableList() {
        return Lists.newArrayList(del1(), update1(), cached1());
    }

    public int[] runnableWight() {
        return new int[]{5, 5, 20, 20, 80, 80};
    }


}
