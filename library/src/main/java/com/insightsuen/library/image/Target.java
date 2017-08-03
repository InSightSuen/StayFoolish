package com.insightsuen.library.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by InSight Suen on 2017/7/21.
 */
public interface Target {

    void onPreload(Drawable placeholderDrawable);

    void onFinished(Bitmap bitmap);

    void onError(Drawable errorDrawable);

}
