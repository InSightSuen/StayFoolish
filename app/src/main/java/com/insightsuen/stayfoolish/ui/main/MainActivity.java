package com.insightsuen.stayfoolish.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.ui.blur.BlurActivity;
import com.insightsuen.stayfoolish.ui.drag.DragActivity;
import com.insightsuen.stayfoolish.ui.toast.ToastActivity;

/**
 * Created by InSight Suen on 2017/7/12.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickDragAndDrop(View view) {
        DragActivity.start(this);
    }

    public void onClickToast(View view) {
        ToastActivity.start(this);
    }

    public void onClickBlur(View view) {
        BlurActivity.start(this);
    }
}
