package com.insightsuen.stayfoolish.ui.thread;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/8/13.
 */

public class ThreadActivity extends BaseActivity<ThreadBinding> {

    private static final String EXTRA_VIEW_MODEL = "ThreadViewModel";

    public static void start(Context context) {
        Intent starter = new Intent(context, ThreadActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_thread;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        LifecycleViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new ThreadViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(),
                    viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }

}
