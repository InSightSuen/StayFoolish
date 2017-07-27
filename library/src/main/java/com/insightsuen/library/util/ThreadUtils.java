package com.insightsuen.library.util;

import android.os.Looper;

/**
 * Created by InSight Suen on 2017/7/21.
 */

public class ThreadUtils {

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

    public static void sleep(long millis) {
        if (!isMainThread()) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
