package com.insightsuen.library.util;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * Created by InSight Suen on 2017/8/10.
 * Stopwatch timer, based on {@link android.os.CountDownTimer}
 */

public abstract class StopwatchTimer {

    private static final int MSG_HEARTBEAT = 1;
    private static final int MSG_PAUSE = 2;
    private static final int MSG_RESUME = 3;

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    /**
     * Mills when stop the timer.
     */
    private long mStopTimeInFuture;

    /**
     * Millis at pause.
     */
    private long mPausedTime;

    /**
     * Next tick millis
     */
    private long mNextTickTime;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    /**
     * boolean representing if the timer was paused
     */
    private boolean mPaused = true;

    /**
     * boolean representing if the timer is counting
     */
    private boolean mCounting = false;

    private Handler mHandler;

    public StopwatchTimer(long millisInFuture, long countdownInterval) {
        if (countdownInterval <= 0) {
            throw new IllegalArgumentException("countdownInterval must be positive");
        }
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countdownInterval;
        mHandler = new InternalHandler(this);
    }

    public synchronized StopwatchTimer start() {
        mCounting = true;
        mCancelled = false;
        mPaused = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG_HEARTBEAT));
        return this;
     }

    public synchronized StopwatchTimer pause() {
        if (!mPaused) {
            mPaused = true;
            mCounting = false;
            mHandler.removeMessages(MSG_HEARTBEAT);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_PAUSE));
        }
        return this;
    }

    public synchronized StopwatchTimer resume() {
        if (mPaused) {
            mPaused = false;
            mCounting = true;
            mHandler.sendMessage(mHandler.obtainMessage(MSG_RESUME));
        }
        return this;
    }

    public synchronized StopwatchTimer cancel() {
        mCancelled = true;
        mCounting = false;
        mPaused = true;
        mHandler.removeMessages(MSG_HEARTBEAT);
        return this;
    }

    public boolean isCounting() {
        return mCounting;
    }

    protected abstract void onTick(long millisUntilFinished);

    protected abstract void onFinish();

    private static class InternalHandler extends Handler {

        private WeakReference<StopwatchTimer> mTimer;

        private InternalHandler(StopwatchTimer timer) {
            mTimer = new WeakReference<>(timer);
        }

        @Override
        public void handleMessage(Message msg) {
            StopwatchTimer timer = mTimer.get();
            if (timer == null) {
                return;
            }
            if (timer.mCancelled) {
                return;
            }

            switch (msg.what) {
                case MSG_HEARTBEAT:
                    if (timer.mPaused) {
                        return;
                    }

                    final long millisLeft = timer.mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        timer.mCounting = false;
                        timer.onFinish();
                    } else if (millisLeft < timer.mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG_HEARTBEAT), millisLeft);
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        timer.onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + timer.mCountdownInterval - SystemClock.elapsedRealtime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += timer.mCountdownInterval;

                        timer.mNextTickTime = delay + SystemClock.elapsedRealtime();
                        sendMessageDelayed(obtainMessage(MSG_HEARTBEAT), delay);
                    }
                    break;

                case MSG_PAUSE:
                    timer.mPausedTime = SystemClock.elapsedRealtime();
                    break;

                case MSG_RESUME: {
                    final long pauseTime = SystemClock.elapsedRealtime() - timer.mPausedTime;
                    timer.mStopTimeInFuture += pauseTime;
                    long delay = timer.mNextTickTime - timer.mPausedTime;
                    timer.mNextTickTime = delay + SystemClock.elapsedRealtime();
                    sendMessageDelayed(obtainMessage(MSG_HEARTBEAT), delay);
                    break;
                }
            }
        }
    }
}
