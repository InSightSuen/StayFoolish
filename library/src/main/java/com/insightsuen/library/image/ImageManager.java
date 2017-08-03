package com.insightsuen.library.image;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.widget.ImageView;

import com.insightsuen.library.cache.ImageCache;

import java.io.File;

/**
 * This class is focused on loading image to an ImageView, and on caching bitmaps for saving
 * memory and improving performance.
 */
public class ImageManager {

    private static final String TAG = "ImageManager";
    private static final String CACHE_DIR_NAME = "ImageManagerCache";

    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

    private Params mParams;

    private ImageCache mImageCache;

    private boolean mInitialized = false;
    private boolean mShutdown = false;
    private boolean mPaused = false;
    private final Object mPauseLock = new Object();

    Resources mResources;

    public static ImageManager getInstance(Context context) {
        ImageManager manager = SingletonHolder.sSingleton;
        if (!manager.mInitialized) {
            manager.init(context);
        }
        return SingletonHolder.sSingleton;
    }

    private static class SingletonHolder {
        private static ImageManager sSingleton = new ImageManager();
    }

    private ImageManager() { }

    private void init(Context context) {
        if (!mInitialized) {
            mResources = context.getResources();
            mImageCache = ImageCache.getInstance(new ImageCache.ImageCacheParams(context, CACHE_DIR_NAME));
            mInitialized = true;
        }
    }

    private void load(File file, ImageView target, int width, int height) {
        if (file == null || !file.exists()) {
            return;
        }

        Bitmap image = null;
        if (mImageCache != null) {
            image = mImageCache.getBitmapFromMemCache(file.getPath());
        }

        if (image != null) {
            target.setImageBitmap(image);
        }
    }

    public void cancelRequest(Target target) {

    }

    private void cancelReqestInternal(Object target) {

    }

    public Drawable getGolbalPlaceholder() {
        if (mParams.mUsePlacholder) {
            if (mParams.mPlaceholderResId > 0) {
                return mResources.getDrawable(mParams.mPlaceholderResId);
            } else {
                return mParams.mPlaceholderDrawable;
            }
        }
        return null;
    }

    public static class Params {

        private boolean mUsePlacholder = false;
        private int mPlaceholderResId;
        private Drawable mPlaceholderDrawable;
        private boolean mFadeIn = true;

        public void setPlaceholder(@DimenRes int placeholderResId) {
            if (placeholderResId <= 0) {
                throw new IllegalArgumentException("placeholder resource id must be positive.");
            }
            mUsePlacholder = true;
            mPlaceholderResId = placeholderResId;
            mPlaceholderDrawable = null;
        }

        public void setPlaceholder(Drawable placeholderDrawable) {
            mUsePlacholder = true;
            mPlaceholderDrawable = placeholderDrawable;
            mPlaceholderResId = 0;
        }

        public void noPlaceholder() {
            mUsePlacholder = false;
            mPlaceholderResId = 0;
            mPlaceholderDrawable = null;
        }

    }
}
