package com.insightsuen.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.insightsuen.library.R;
import com.insightsuen.library.util.ViewUtils;

/**
 * Created by InSight Suen on 2017/8/9.
 * Countdown timer
 */
public class PointProgress extends View {

    private static final String TAG = "PointProgress";
    private static final boolean DEBUG = true;

    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_PAINT_RADIUS = ViewUtils.dp2px(4);
    public static final int DEFAULT_STROKE_WIDTH = ViewUtils.dp2px(2);

    private float mPaintRadius;

    private int mMax;
    private int mProgress;
    private boolean mPaused = true;

    private Paint mPointPaint;
    private Paint mForegroundPaint;
    private Paint mBackgroundPaint;

    private RectF mRectF;

    public PointProgress(Context context) {
        this(context, null);
    }

    public PointProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.PointProgress);
        mProgress = ta.getInt(R.styleable.PointProgress_progress, 0);
        mMax = ta.getInt(R.styleable.PointProgress_max, DEFAULT_MAX_VALUE);
        if (mProgress < 0) {
            mProgress = 0;
        } else if (mProgress > mMax) {
            mProgress = mMax;
        }
        mPaintRadius = ta.getDimensionPixelSize(
                R.styleable.PointProgress_paint_radius, DEFAULT_PAINT_RADIUS);
        float strokeWidth = ta.getDimensionPixelSize(
                R.styleable.PointProgress_stroke_width, DEFAULT_STROKE_WIDTH);
        int backgroundColor = ta.getColor(R.styleable.PointProgress_background_color,
                Color.argb(0x0, 0x0, 0x0, 0x0));
        int primaryColor = ta.getColor(R.styleable.PointProgress_primary_color,
                Color.rgb(0x82, 0xb1, 0xff));
        ta.recycle();
        initPaint(primaryColor, backgroundColor, strokeWidth);
    }

    private void initPaint(int primaryColor, int backgroundColor, float strokeWidth) {
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(primaryColor);
        mPointPaint.setStyle(Paint.Style.FILL);

        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint.setColor(primaryColor);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeWidth(strokeWidth);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime;
        if (DEBUG) {
            startTime = SystemClock.elapsedRealtime();
        }
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getHeight() - getPaddingTop() -getPaddingBottom();

        float radius = (Math.min(contentWidth, contentHeight) - mPaintRadius * 2) / 2;
        float centerX = getPaddingLeft() + contentWidth / 2;
        float centerY = getPaddingTop() + contentHeight / 2;
        canvas.drawCircle(centerX, centerY, radius, mBackgroundPaint);


        if (mRectF == null) {
            mRectF = new RectF();
            int left = (int) (centerX - radius);
            int top = (int) (centerY - radius);
            int right = (int) (centerX + radius);
            int bottom = (int) (centerY + radius);
            mRectF.set(left, top, right, bottom);
        }
        float angle = (float) mProgress / mMax * 360;
        canvas.drawArc(mRectF, -90, angle, false, mForegroundPaint);

        if (mProgress > 0 && mProgress < mMax) {
            double radians = Math.toRadians(angle);
            canvas.drawCircle(centerX + (float) Math.sin(radians) * radius,
                    centerY - (float) Math.cos(radians) * radius,
                    mPaintRadius, mPointPaint);
        }
        if (DEBUG) {
            Log.d(TAG, "onDraw: time used=" + (SystemClock.elapsedRealtime() - startTime));
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setMax(int  max) {
        mMax = max;
        invalidate();
    }
}
