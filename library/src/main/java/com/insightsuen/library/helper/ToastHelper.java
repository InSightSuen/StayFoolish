package com.insightsuen.library.helper;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Toast helper class
 */
public class ToastHelper implements Handler.Callback {

    private static final String TAG = "ToastHelper";
    private static final boolean DEBUG = true;

    private static final int MSG_RECYCLE = 2;

    private static final int MIN_LIVE_TIME = 3 * 1000;
    private static final int DEFAULT_MAX_LIVE_TIME = 10 * 1000;

    private static final int PARAM_GRAVITY = 0;
    private static final int PARAM_X_OFFSET = 1;
    private static final int PARAM_Y_OFFSET = 2;

    private WeakReference<Context> mContextRef;
    private Handler mHandler = null;
    private Toast mToast = null;
    private int mMaxLiveTime;

    private boolean mParamDirty = true;

    private boolean mUseSystemGravity = true;
    private int mGravity;
    private int mXOffset;
    private int mYOffset;
    private int[] mDefaultGravityParams;

    private int mLayoutResId;
    private int mMessageTextViewId;

    public static ToastHelper getInstance() {
        return SingletonHolder.sSingleton;
    }

    private ToastHelper() { }

    /**
     * 初始化ToastHelper，保存Application Context WeakReference<br/>
     * Context 用于生成 Toast 实例以及获取 Sting resource
     *
     * @param application Application
     */
    public void init(@NonNull Application application) {
        init(application, DEFAULT_MAX_LIVE_TIME);
    }

    public void init(@NonNull Context context) {
        init(context.getApplicationContext(), DEFAULT_MAX_LIVE_TIME);
    }

    /**
     * 初始化ToastHelper，保存Application Context WeakReference<br/>
     * Context 用于生成 Toast 实例以及获取 Sting resource
     *
     * @param applicationContext Application context
     * @param maxLiveTime Toast 实例最大存活时间，超过最大时间未再次 show toast，会回收 Toast 实例
     */
    public void init(@NonNull Context applicationContext, int maxLiveTime) {
        mContextRef = new WeakReference<>(applicationContext);
        if (maxLiveTime >= MIN_LIVE_TIME) {
            mMaxLiveTime = maxLiveTime;
        } else {
            mMaxLiveTime = MIN_LIVE_TIME;
        }
        mHandler = new Handler(this);
        loadDefaultGravityParams(applicationContext);
    }

    public void setView(int layoutResId, int messageTextViewId) {
        mLayoutResId = layoutResId;
        mMessageTextViewId = messageTextViewId;

        invalidate();
    }

    public void useDeafultView() {
        mLayoutResId = 0;
        mMessageTextViewId = 0;

        invalidate();
    }

    public void setGravity(int gravity) {
        setGravity(gravity, 0, 0);
    }

    public void setGravity(int gravity, int offsetX, int offsetY) {
        mUseSystemGravity = false;
        mGravity = gravity;
        mXOffset = offsetX;
        mYOffset = offsetY;

        invalidate();
    }

    public void userDefaultGravity() {
        mUseSystemGravity = true;

        invalidate();
    }

    public int getGravity() {
        return mUseSystemGravity ? mDefaultGravityParams[PARAM_GRAVITY] : mGravity;
    }

    public int getXOffset() {
        return mUseSystemGravity ? mDefaultGravityParams[PARAM_X_OFFSET] : mXOffset;
    }

    public int getYOffset() {
        return mUseSystemGravity ? mDefaultGravityParams[PARAM_Y_OFFSET] : mYOffset;
    }

    public void show(@StringRes final int tips) {
        Context context = getContext();
        if (context != null) {
            show(context.getString(tips));
        }
    }

    public void show(final CharSequence tips) {
        show(tips, Toast.LENGTH_SHORT);
    }

    public void show(final CharSequence tips, final int duration) {
        if (TextUtils.isEmpty(tips)) {
            return;
        }

        Context context = getContext();
        if (context == null) {
            return;
        }

        mHandler.removeMessages(MSG_RECYCLE);
        Toast toast = getToast(context);
        if (mLayoutResId > 0) {
            TextView tvMsg = (TextView) toast.getView().findViewById(mMessageTextViewId);
            if (tvMsg == null) {
                throw new IllegalStateException("can not find TextView for set Message.");
            }
            tvMsg.setText(tips);
        } else {
            toast.setText(tips);
        }
        toast.setDuration(duration);
        toast.show();
        mHandler.sendEmptyMessageDelayed(MSG_RECYCLE, mMaxLiveTime);
    }

    private Toast getToast(Context context) {
        if (mToast == null || mParamDirty) {
            synchronized (ToastHelper.class) {
                if (mToast == null || mParamDirty) {
                    if (DEBUG) {
                        Log.d(TAG, "create toast");
                    }

                    Toast toast;
                    if (mLayoutResId > 0) {
                        toast = new Toast(context);
                        LayoutInflater inflate = (LayoutInflater)
                                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflate.inflate(mLayoutResId, null);
                        toast.setView(view);
                    } else {
                        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                    }
                    toast.setGravity(getGravity(), getXOffset(), getYOffset());

                    mToast = toast;
                    mParamDirty = false;
                }
            }
        }
        return mToast;
    }

    private void invalidate() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mParamDirty = true;
    }

    private void loadDefaultGravityParams(Context context) {
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mDefaultGravityParams = new int[3];
        mDefaultGravityParams[PARAM_GRAVITY] = toast.getGravity();
        mDefaultGravityParams[PARAM_X_OFFSET] = toast.getXOffset();
        mDefaultGravityParams[PARAM_Y_OFFSET] = toast.getYOffset();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_RECYCLE:
                if (mToast != null) {
                    if (DEBUG) {
                        Log.d(TAG, "recycle toast");
                    }
                    mToast = null;
                }
                return true;

            default:
                return false;
        }
    }

    private Context getContext() {
        if (mContextRef == null) {
            throw new RuntimeException("Must call init() method before show toast.");
        }
        return mContextRef.get();
    }

    private static class SingletonHolder {
        private static ToastHelper sSingleton = new ToastHelper();
    }
}