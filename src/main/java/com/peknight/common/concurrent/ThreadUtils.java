package com.peknight.common.concurrent;

import java.util.Random;

/**
 * 线程工具类
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/6/12.
 */
public final class ThreadUtils {
    private ThreadUtils() {}

    public static void randomSleep(long millis, int bound) throws InterruptedException {
        millis -= bound - new Random(System.currentTimeMillis()).nextInt(bound * 2);
        Thread.sleep(millis);
    }
}
