package com.insightsuen.library.image;

import android.net.Uri;

/**
 * Created by InSight Suen on 2017/7/21.
 */
public class Request {

    int mId;

    Uri mUri;
    int mResId;

    int mTargetWidth;
    int mTargetHeight;

    private Request(Uri uri, int resId, int targetWidth, int targetHeight) {
        mUri = uri;
        mResId = resId;
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
    }
}
