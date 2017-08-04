package com.insightsuen.stayfoolish.ui.blur;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.insightsuen.library.util.BitmapUtils;
import com.insightsuen.library.util.ViewUtils;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/8/4.
 */

public class BlurActivity extends BaseActivity {

    // Number of bitmaps that is used for renderScript thread and UI thread synchronization.
    private final int NUM_BITMAPS = 2;

    private ImageView ivBg;

    private Bitmap mBitmapIn;
    private Bitmap[] mBitmapOut;
    private int mCurrentBitmap = 0;

    private Allocation mInAllocation;
    private Allocation[] mOutAllocations;

    private ScriptIntrinsicBlur mScriptBlur;
    private RenderScriptTask mLatestTask;
    private float mBlurRadius;
    private ValueAnimator mLatestAnimator;

    public static void start(Context context) {
        Intent starter = new Intent(context, BlurActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur);
        initData();
        initWidgets();
    }

    private void initData() {
        mBitmapIn = BitmapUtils.decodeSampledBitmapFromResource(getResources(),
                R.drawable.img_cat, 0, ViewUtils.dp2px(200), Bitmap.Config.ARGB_8888);
        mBitmapOut = new Bitmap[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; i++) {
            mBitmapOut[i] = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(), mBitmapIn.getConfig());
        }
        mCurrentBitmap += (mCurrentBitmap + 1) % NUM_BITMAPS;

        createScript();
    }

    private void createScript() {
        RenderScript RS = RenderScript.create(this);
        mInAllocation = Allocation.createFromBitmap(RS, mBitmapIn);
        mOutAllocations = new Allocation[NUM_BITMAPS];
        for (int i = 0; i < NUM_BITMAPS; i++) {
            mOutAllocations[i] = Allocation.createFromBitmap(RS, mBitmapOut[i]);
        }
        mScriptBlur = ScriptIntrinsicBlur.create(RS, Element.U8_4(RS));
        mBlurRadius = 1.f;
    }

    private void initWidgets() {
        ivBg = (ImageView) findViewById(R.id.iv_bg);
        TextView tvTap = (TextView) findViewById(R.id.tv_tap);
        tvTap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startBlurAnimator();
                        return true;

                    case MotionEvent.ACTION_UP:
                        clearBlurAnimator();
                        return true;
                }
                return false;
            }
        });

        startBlur(1.0f);
    }

    private void startBlurAnimator() {
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

    private void clearBlurAnimator() {
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

    private void startBlur(float radius) {
        if (mLatestTask != null) {
            mLatestTask.cancel(false);
        }
        mLatestTask = new RenderScriptTask();
        mLatestTask.execute(radius);
    }

    private void updateImage(Integer result) {
        if (result != -1) {
            ivBg.setImageBitmap(mBitmapOut[result]);
            ivBg.invalidate();
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
