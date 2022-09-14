package org.easycachetest.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, 400, 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

}
