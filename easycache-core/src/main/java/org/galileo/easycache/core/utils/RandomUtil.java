package org.galileo.easycache.core.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    private static final long mask = -1L >>> 42;

    /**
     * 效率比UUID高约7倍, 同时对重复性要求没那么高的随机数
     *
     * 实测, 单个key 10w并发重复一次
     *
     * @param prefix
     * @return
     */
    public static String random(String prefix) {
//        return prefix + ThreadLocalRandom.current().nextInt();

        int random = ThreadLocalRandom.current().nextInt(10000);
//         milli 取后22bit, 约1小时重复一次
        return prefix + Long.toHexString(mask & millsNano()) + random;
    }

    public static long millsNano() {
        // 取纳秒的后34bit, 约20s重复一次, 毫秒的后(64-30)bit, 约10天重复一次, 重复概率约 10天一次
        return  (-1L >>> 30 & System.nanoTime()) | (System.currentTimeMillis() << 34);
    }

}
