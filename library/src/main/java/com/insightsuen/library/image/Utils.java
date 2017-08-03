package com.insightsuen.library.image;

import com.insightsuen.library.util.ThreadUtils;

/**
 * Created by InSight Suen on 2017/7/21.
 */

public class Utils {

    static void checkMainThread() {
        if (ThreadUtils.isMainThread()) {
            throw new IllegalStateException("You should call on Main thread");
        }
    }
}
