package com.insightsuen.stayfoolish.ui.style;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

/**
 * Created by InSight Suen on 2017/8/7.
 */

public class UnifyStyleActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, UnifyStyleActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unify_style);
    }
}
