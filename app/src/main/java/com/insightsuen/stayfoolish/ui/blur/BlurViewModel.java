package com.insightsuen.stayfoolish.ui.blur;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.library.util.BitmapUtils;
import com.insightsuen.library.util.ViewUtils;
import com.insightsuen.stayfoolish.BR;
import com.insightsuen.stayfoolish.R;

/**
 * Created by InSight Suen on 2017/8/8.
 */

public class BlurViewModel extends LifecycleViewModel {

    // Number of bitmaps that is used for renderScript thread and UI thread synchronization.
    private final static int NUM_BITMAPS = 2;

    private Bitmap mBitmapIn;
    private Bitmap[] mBitmapOut;
    private int mCurrentBitmap = 0;

    private Allocation mInAllocation;
    private Allocation[] mOutAllocations;

    private ScriptIntrinsicBlur mScriptBlur;
    private RenderScriptTask mLatestTask;
    private float mBlurRadius;

    private ValueAnimator mLatestAnimator;

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        ToastHelper.getInstance().init(context);

        mBitmapIn = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(),
                R.drawable.img_cat, 0, ViewUtils.dp2px(200), Bitmap.Config.ARGB_8888);
        mBitmapOut = new Bitmap[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; i++) {
            mBitmapOut[i] = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());
        }
        mCurrentBitmap += (mCurrentBitmap + 1) % NUM_BITMAPS;
        createScript(context);
        startBlur(1.0f);
    }

    @Bindable
    public Bitmap getBlurImage() {
        return mBitmapOut[(mCurrentBitmap + 1) % NUM_BITMAPS];
    }

    void startBlurAnimator() {
        if (mLatestAnimator != null && mLatestAnimator.isRunning()) {
            mLatestAnimator.cancel();
        }
        mLatestAnimator = ValueAnimator.ofFloat(mBlurRadius, 25.f);
        mLatestAnimator.setDuration(300);
        mLatestAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mLatestAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBlurRadius = (float) animation.getAnimatedValue();
                startBlur(mBlurRadius);
            }
        });
        mLatestAnimator.start();
    }

    void clearBlurAnimator() {
        if (mLatestAnimator != null && mLatestAnimator.isRunning()) {
            mLatestAnimator.cancel();
        }
        mLatestAnimator = ValueAnimator.ofFloat(mBlurRadius, 1.f);
        mLatestAnimator.setDuration(300);
        mLatestAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mLatestAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBlurRadius = (float) animation.getAnimatedValue();
                startBlur(mBlurRadius);
            }
        });
        mLatestAnimator.start();
    }

    private void createScript(Context context) {
        RenderScript RS = RenderScript.create(context);
        mInAllocation = Allocation.createFromBitmap(RS, mBitmapIn);
        mOutAllocations = new Allocation[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; i++) {
            mOutAllocations[i] = Allocation.createFromBitmap(RS, mBitmapOut[i]);
        }
        mScriptBlur = ScriptIntrinsicBlur.create(RS, Element.U8_4(RS));
        mBlurRadius = 1.f;
    }

    private void startBlur(float radius) {
        if (mLatestTask != null) {
            mLatestTask.cancel(false);
        }
        mLatestTask = new RenderScriptTask();
        mLatestTask.execute(radius);
    }

    private void updateImage(Integer result) {
        if (result != -1) {
            notifyPropertyChanged(BR.blurImage);
        }
    }

    private class RenderScriptTask extends AsyncTask<Float, Integer, Integer> {

        private boolean mIssued = false;

        @Override
        protected Integer doInBackground(Float... params) {
            int index = -1;
            if (!isCancelled()) {
                mIssued = true;
                index = mCurrentBitmap;
                performFilter(mInAllocation, mOutAllocations[index], mBitmapOut[index], params[0]);
                mCurrentBitmap = (mCurrentBitmap + 1) % NUM_BITMAPS;
            }
            return index;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private void performFilter(Allocation inAllocation, Allocation outAllocation,
                Bitmap bitmapOut, float value) {
            mScriptBlur.setRadius(value);
            mScriptBlur.setInput(inAllocation);
            mScriptBlur.forEach(outAllocation);
            outAllocation.copyTo(bitmapOut);
        }

        @Override
        protected void onPostExecute(Integer result) {
            updateImage(result);
        }

        @Override
        protected void onCancelled(Integer result) {
            if (mIssued) {
                updateImage(result);
            }
        }

    }
}
