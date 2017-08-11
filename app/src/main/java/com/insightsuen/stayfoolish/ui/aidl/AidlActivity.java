package com.insightsuen.stayfoolish.ui.aidl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/8/11.
 */

public class AidlActivity extends BaseActivity<AidlBinding> {

    private static final String EXTRA_VIEW_MODEL = "AidlViewModel";

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_aidl;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        LifecycleViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new AidlViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(), viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AidlActivity.class);
        context.startActivity(starter);
    }
}
