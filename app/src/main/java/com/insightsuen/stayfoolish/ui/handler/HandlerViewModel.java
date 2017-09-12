package com.insightsuen.stayfoolish.ui.handler;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by InSight Suen on 2017/8/17.
 */

public class HandlerViewModel extends LifecycleViewModel {

    private InternalHandler mHandler;

    public void onClickSendMsg() {
        if (mHandler != null) {
            Message.obtain(mHandler, 100).sendToTarget();
        }
    }

    public void onClickRemoveAllMessage() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        HandlerThread handlerThread = new HandlerThread("InternalHandler");
        handlerThread.start();
        mHandler = new InternalHandler(handlerThread.getLooper(), this);
    }

    @Override
    public void onStop() {
        mHandler = null;
        super.onStop();
    }

    private void showMsg() {
        Toast.makeText(getContext(), "Handler message", Toast.LENGTH_SHORT).show();
    }

    private static class InternalHandler extends Handler {

        private WeakReference<HandlerViewModel> mViewModel;

        InternalHandler(Looper looper, HandlerViewModel viewModel) {
            super(looper);
            mViewModel = new WeakReference<>(viewModel);
        }

        @Override
        public void handleMessage(Message msg) {
            HandlerViewModel viewModel = mViewModel.get();
            if (viewModel == null) {
                return;
            }
            viewModel.showMsg();
        }
    }
}
