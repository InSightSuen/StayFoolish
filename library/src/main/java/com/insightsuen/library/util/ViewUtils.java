package com.insightsuen.library.util;

import android.content.res.Resources;

/**
 * Static methods for View
 */
public class ViewUtils {

    private static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int dp2px(float dp) {
        return (int) (dp * getDensity() + 0.5f);
    }

    public static int dp2px(int dp) {
        return (int) (dp * getDensity() + 0.5f);
    }

    public static int sp2px(float sp) {
        return (int) (sp * getDensity() + 0.5f);
    }

    public static int sp2px(int sp) {
        return (int) (sp * getDensity() + 0.5f);
    }
}
