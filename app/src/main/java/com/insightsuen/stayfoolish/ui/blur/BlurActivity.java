package com.insightsuen.stayfoolish.ui.blur;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.util.ViewUtils;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/8/4.
 */

public class BlurActivity extends BaseActivity<BlurBinding> {

    private static final String TAG = "BlurActivity";
    private static final String EXTRA_VIEW_MODEL = "ViewModel";

    private static final int SWIPE_THRESHOLD_VELOCITY = ViewUtils.dp2px(30);
    private static final int MIN_VIEW_DRAG_HEIGHT = ViewUtils.dp2px(208);

    private GestureDetectorCompat mGestureDetector;

    private View mViewDrag;
    private View mGroupRoot;

    public static void start(Context context) {
        Intent starter = new Intent(context, BlurActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWidgets();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_blur;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        BlurViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new BlurViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(), viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }

    private void initWidgets() {
        TextView tvTap = (TextView) findViewById(R.id.tv_tap);
        tvTap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((BlurViewModel) createOrFindViewModel(null)).startBlurAnimator();
                        return true;

                    case MotionEvent.ACTION_UP:
                        ((BlurViewModel) createOrFindViewModel(null)).clearBlurAnimator();
                        return true;
                }
                return false;
            }
        });

        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int yDistance = (int) (e1.getY() - e2.getY());
                Log.d(TAG, "onFling: yDistance=" + yDistance + " velocityY=" + velocityY);

                if (velocityY > SWIPE_THRESHOLD_VELOCITY) {
                    // fling to bottom
                    finishDrag(mViewDrag, mGroupRoot.getHeight() - MIN_VIEW_DRAG_HEIGHT);
                    return true;
                } else if (-velocityY > SWIPE_THRESHOLD_VELOCITY) {
                    // fling to top
                    finishDrag(mViewDrag, 0);
                    return true;
                }
                return false;
            }
        });
        mViewDrag = findViewById(R.id.v_drag);
        mViewDrag.setOnTouchListener(new DragListener());
        mGroupRoot = findViewById(R.id.group_root);
    }

    private void finishDrag(final View view, int targetMarginTop) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(layoutParams.topMargin, targetMarginTop);
        animator.setDuration(150);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.topMargin = value;
                view.setLayoutParams(layoutParams);
                view.requestLayout();
            }
        });
        animator.start();
    }

    private class DragListener implements View.OnTouchListener {

        private int yDelta = 0;
        private int mMaxTopMargin = -1;
        private int mMinTopMargin = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMaxTopMargin < 0) {
                mMaxTopMargin = ((View) v.getParent()).getHeight() - MIN_VIEW_DRAG_HEIGHT;
            }
            if (mGestureDetector.onTouchEvent(event)) {
                return false;
            }
            int y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    yDelta = layoutParams.topMargin - y;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    int newTopMargin = y + yDelta;
                    if (newTopMargin < mMinTopMargin) {
                        newTopMargin = mMinTopMargin;
                    } else if (newTopMargin > mMaxTopMargin) {
                        newTopMargin = mMaxTopMargin;
                    }
                    layoutParams.topMargin = newTopMargin;
                    v.setLayoutParams(layoutParams);
                    v.requestLayout();
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    int newTopMargin = y + yDelta;
                    int midValue = (mMaxTopMargin - mMinTopMargin) / 2;
                    if (newTopMargin > midValue) {
                        newTopMargin = mMaxTopMargin;
                    } else {
                        newTopMargin = mMinTopMargin;
                    }
                    finishDrag(v, newTopMargin);
                    break;
                }
            }
            return true;
        }

    }
}
