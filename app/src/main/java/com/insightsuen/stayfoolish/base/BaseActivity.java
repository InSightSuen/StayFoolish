package com.insightsuen.stayfoolish.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.bindroid.component.BindActivity;
import com.insightsuen.bindroid.viewmodel.LifecycleViewModel;

/**
 * Created by InSight Suen on 2017/7/12.
 * Base Activity
 */
public abstract class BaseActivity<Binding extends ViewDataBinding> extends BindActivity<Binding> {

    @Nullable
    @Override
    protected LifecycleViewModel createOrFindViewModel(@Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LifecycleViewModel viewModel = createOrFindViewModel(null);
        if (viewModel != null) {
            viewModel.onStart(this);
        }
    }
}
