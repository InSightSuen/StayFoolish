package com.insightsuen.stayfoolish.ui.widget;

import android.databinding.Bindable;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.util.StopwatchTimer;
import com.insightsuen.stayfoolish.BR;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by InSight Suen on 2017/8/9.
 */

public class WidgetViewModel extends LifecycleViewModel {

    private static final long TIMER_TOTAL_TIME = 60 * 1000; // 60s
    private static final long TIMER_INTERVAL =  16;
    private static final int PROGRESS_MAX = 1000;

    private StopwatchTimer mStopwatchTimer;
    private int mProgress;
    private boolean mPaused;
    private boolean mStarted;

    private StringBuilder mLog = new StringBuilder();

    @Bindable
    public int getProgress() {
        return mProgress;
    }

    @Bindable
    public int getMax() {
        return PROGRESS_MAX;
    }

    @Bindable
    public boolean isStarted() {
        return mStarted;
    }

    @Bindable
    public boolean isPaused() {
        return mPaused;
    }

    @Bindable
    public CharSequence getLog() {
        return mLog.toString();
    }

    public void onClickStart() {
        if (mStarted) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void onClickPause() {
        if (mPaused) {
            resumeTimer();
        } else {
            pauseTimer();
        }
    }

    private void startTimer() {
        if (!mStarted) {
            mStarted = true;
            if (mStopwatchTimer != null && mStopwatchTimer.isCounting()) {
                mStopwatchTimer.cancel();
            }
            mStopwatchTimer = new SimpleTimer(this, TIMER_TOTAL_TIME, TIMER_INTERVAL);
            mStopwatchTimer.start();
            mPaused = false;
            notifyPropertyChanged(BR.started);
            notifyPropertyChanged(BR.paused);
            log("Start");
        }
    }

    private void onRefreshProgress(long millisUntilFinished) {
        mProgress = (int) (PROGRESS_MAX * millisUntilFinished / TIMER_TOTAL_TIME);
        notifyPropertyChanged(BR.progress);
        log("Tick");
    }

    private void pauseTimer() {
        if (!mPaused) {
            mPaused = true;
            if (mStopwatchTimer != null) {
                mStopwatchTimer.pause();
            }
            notifyPropertyChanged(BR.paused);
            log("Pause");
        }
    }

    private void resumeTimer() {
        if (mPaused) {
            mPaused = false;
            if (mStopwatchTimer != null) {
                mStopwatchTimer.resume();
            }
            notifyPropertyChanged(BR.paused);
            log("Resume");
        }
    }


    private void stopTimer() {
        if (mStarted) {
            mStarted = false;
            if (mStopwatchTimer != null && mStopwatchTimer.isCounting()) {
                mStopwatchTimer.cancel();
            }
            mStopwatchTimer = null;
            mPaused = false;
            notifyPropertyChanged(BR.started);
            notifyPropertyChanged(BR.paused);
            log("Stop");
        }
    }

    private void log(String action) {
        long nowMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS", Locale.getDefault());
        CharSequence time = sdf.format(new Date(nowMillis));
        mLog.append(action).append(" ").append(time).append("\n");
        notifyPropertyChanged(BR.log);
    }

    private static class SimpleTimer extends StopwatchTimer {

        private WeakReference<WidgetViewModel> mViewModel;

        private SimpleTimer(WidgetViewModel viewModel, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            WidgetViewModel viewModel = mViewModel.get();
            if (viewModel == null) {
                cancel();
                return;
            }
            viewModel.onRefreshProgress(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            WidgetViewModel viewModel = mViewModel.get();
            if (viewModel != null) {
                viewModel.stopTimer();
            }
        }
    }
}
