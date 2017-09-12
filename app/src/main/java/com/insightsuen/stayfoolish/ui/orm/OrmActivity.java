package com.insightsuen.stayfoolish.ui.orm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.bindroid.utils.ViewModelUtil;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/9/12.
 */

public class OrmActivity extends BaseActivity {

    private static final String EXTRA_VIEW_MODEL = "OrmViewModel";

    public static void start(Context context) {
        Intent starter = new Intent(context, OrmActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_orm;
    }

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        LifecycleViewModel viewModel = ViewModelUtil.findFromFragmentManger(
                getSupportFragmentManager(), EXTRA_VIEW_MODEL);
        if (viewModel == null) {
            viewModel = new OrmViewModel();
            ViewModelUtil.addToFragmentManager(getSupportFragmentManager(), viewModel, EXTRA_VIEW_MODEL);
        }
        return viewModel;
    }
}
