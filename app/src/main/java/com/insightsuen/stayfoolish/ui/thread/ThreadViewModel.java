package com.insightsuen.stayfoolish.ui.thread;

import android.databinding.Bindable;

import com.insightsuen.bindroid.BR;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.library.util.ThreadUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by InSight Suen on 2017/8/13.
 */

public class ThreadViewModel extends LifecycleViewModel {

    private StringBuilder mLog = new StringBuilder();

    @Bindable
    public String getLog() {
        return mLog.toString();
    }

    public void onClickStartMultiTaskByJoin() {
        startMultiTaskByJoin();
    }

    public void onClickStartMultiTaskByCountDownLatch() {
        startMultiTaskByCountDownLatch();
    }

    private void startMultiTaskByJoin() {
        new MultiTaskByJoin().start();
    }

    private void startMultiTaskByCountDownLatch() {
        new MultiTaskByCountDownLatch().start();
    }

    private void onMultiTaskStart() {
        mLog = new StringBuilder();
        mLog.append("Multi task start.\n");
        notifyPropertyChanged(BR.log);
    }

    private void onSingleTaskStart(String tag) {
        mLog.append("\t").append(tag).append(" start.\n");
        notifyPropertyChanged(BR.log);
    }

    private void onSingleTaskFinished(String tag) {
        mLog.append("\t").append(tag).append(" finished.\n");
        notifyPropertyChanged(BR.log);
    }

    private void onMultiTaskFinished() {
        mLog.append("Multi task finished.");
        notifyPropertyChanged(BR.log);
    }

    private class MultiTaskByJoin extends Thread {

        @Override
        public void run() {
            onMultiTaskStart();
            try {
                SingleTaskByJoin[] multiTask = {
                        new SingleTaskByJoin("Task1"),
                        new SingleTaskByJoin("Task2"),
                        new SingleTaskByJoin("Task3"),
                        new SingleTaskByJoin("Task4"),
                        new SingleTaskByJoin("Task5"),
                        new SingleTaskByJoin("Task6"),
                        new SingleTaskByJoin("Task7"),
                        new SingleTaskByJoin("Task8"),
                        new SingleTaskByJoin("Task9"),
                        new SingleTaskByJoin("Task10")};
                for (SingleTaskByJoin task : multiTask) {
                    task.start();
                }
                for (SingleTaskByJoin task : multiTask) {
                    task.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                onMultiTaskFinished();
            }
        }
    }

    private class SingleTaskByJoin extends Thread {

        private String mTag;

        SingleTaskByJoin(String tag) {
            mTag = tag;
        }

        @Override
        public void run() {
            onSingleTaskStart(mTag);
            ThreadUtils.sleep((long) (Math.random() * 3000 + 1000));
            onSingleTaskFinished(mTag);
        }
    }

    private class MultiTaskByCountDownLatch extends Thread {

        private CountDownLatch mLatch;

        @Override
        public void run() {
            onMultiTaskStart();
            mLatch = new CountDownLatch(10);
            SingleTaskByCountDownLatch[] multiTask = {
                    new SingleTaskByCountDownLatch(mLatch, "Task1"),
                    new SingleTaskByCountDownLatch(mLatch, "Task2"),
                    new SingleTaskByCountDownLatch(mLatch, "Task3"),
                    new SingleTaskByCountDownLatch(mLatch, "Task4"),
                    new SingleTaskByCountDownLatch(mLatch, "Task5"),
                    new SingleTaskByCountDownLatch(mLatch, "Task6"),
                    new SingleTaskByCountDownLatch(mLatch, "Task7"),
                    new SingleTaskByCountDownLatch(mLatch, "Task8"),
                    new SingleTaskByCountDownLatch(mLatch, "Task9"),
                    new SingleTaskByCountDownLatch(mLatch, "Task10")};

            for (SingleTaskByCountDownLatch task : multiTask) {
                task.start();
            }
            try {
                mLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                onMultiTaskFinished();
            }
        }

    }

    private class SingleTaskByCountDownLatch extends Thread {

        private CountDownLatch mCountDownLatch;
        private String mTag;

        SingleTaskByCountDownLatch(CountDownLatch latch, String tag) {
            mCountDownLatch = latch;
            mTag = tag;
        }

        @Override
        public void run() {
            onSingleTaskStart(mTag);
            ThreadUtils.sleep((long) (Math.random() * 3000 + 1000));
            mCountDownLatch.countDown();
            onSingleTaskFinished(mTag);
        }
    }

}
