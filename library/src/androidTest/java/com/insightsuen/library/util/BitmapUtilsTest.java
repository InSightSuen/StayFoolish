package com.insightsuen.library.util;

import android.graphics.Bitmap;
import android.view.Gravity;

import org.junit.Test;

/**
 * Created by InSight Suen on 2017/8/7.
 */
public class BitmapUtilsTest {

    @Test
    public void crop() throws Exception {
        Bitmap original  = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Bitmap cropped = BitmapUtils.crop(original, 100, 50, Gravity.LEFT);

        original  = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        cropped = BitmapUtils.crop(original, 73, 81, Gravity.END);

        original  = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        cropped = BitmapUtils.crop(original, 300, 90, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

}