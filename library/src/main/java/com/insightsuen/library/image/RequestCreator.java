package com.insightsuen.library.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Px;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by InSight Suen on 2017/7/21.
 */
public class RequestCreator {

    private static final AtomicInteger nextId = new AtomicInteger(0);

    private final ImageManager mManager;

    private Uri mUri;
    private int mResId;
    private int mTargetWidth;
    private int mTargetHeight;

    private boolean mFadeIn = true;
    private boolean mUsePlaceholder = true;
    private int mPlaceholderResId;
    private Drawable mPlaceholderDrawable;
    private int mErrorResId;
    private Drawable mErrorDrawable;
    private Bitmap.Config mConfig;

    private Object mTag;

    public RequestCreator(ImageManager manager, Uri uri, int resId) {
        mManager = manager;
        mUri = uri;
        mResId = resId;
    }

    public RequestCreator noPlaceholder() {
        mUsePlaceholder = false;
        return this;
    }

    public RequestCreator placeHolder(@DrawableRes int placeholderResId) {
        mPlaceholderResId = placeholderResId;
        mPlaceholderDrawable = null;
        mUsePlaceholder = true;
        return this;
    }

    public RequestCreator plachholder(Drawable placeholderDrawable) {
        mPlaceholderDrawable = placeholderDrawable;
        mPlaceholderResId = 0;
        mUsePlaceholder = true;
        return this;
    }

    public RequestCreator error(@DrawableRes int errorResId) {
        mErrorResId = errorResId;
        mErrorDrawable = null;
        return this;
    }

    public RequestCreator error(Drawable errorDrawable) {
        mErrorDrawable = errorDrawable;
        mErrorResId = 0;
        return this;
    }

    public RequestCreator tag(Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag may not be null.");
        }
        mTag = tag;
        return this;
    }

    public RequestCreator resize(@Px int targetWidth, @Px int targetHeight) {
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
        return this;
    }

    public RequestCreator resizeDimen(@DimenRes int targetWidthResId, @DimenRes int targetHeightResId) {
        Resources resources = mManager.mResources;
        return resize(resources.getDimensionPixelSize(targetWidthResId),
                resources.getDimensionPixelSize(targetHeightResId));
    }

    public RequestCreator noFadeIn() {
        mFadeIn = false;
        return this;
    }

    public void target(Target target) {
        long startTime = System.nanoTime();
        Utils.checkMainThread();
        if (target == null) {
            throw new IllegalArgumentException("Target may not ne null.");
        }
        if (hasData()) {
            mManager.cancelRequest(target);
            target.onPreload(mUsePlaceholder ? getPlaceholderDrawable() : null);
        } else {

        }
    }

    private boolean hasData() {
        return mUri != null || mResId > 0;
    }

    private Drawable getPlaceholderDrawable() {
        if (mPlaceholderDrawable != null) {
            return mPlaceholderDrawable;
        } else if (mPlaceholderResId >= 0) {
            return mManager.mResources.getDrawable(mPlaceholderResId);
        } else {
            return mManager.getGolbalPlaceholder();
        }
    }
}
