package com.insightsuen.stayfoolish.ui.identify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

public class IdentityActivity extends BaseActivity<IdentifyBinding> {

    private static final String EXTRA_VIEW_MODEL = "IdentifyViewModel";

    public static void start(Context context) {
        Intent starter = new Intent(context, IdentityActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_identify;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        LifecycleViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new IdentifyViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(), viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }

}
