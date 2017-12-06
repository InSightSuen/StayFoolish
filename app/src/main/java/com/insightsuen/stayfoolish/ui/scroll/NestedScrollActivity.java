package com.insightsuen.stayfoolish.ui.scroll;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import com.insightsuen.library.widget.recyclerview.SimpleAdapter;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/5.
 * NestedScroll widgets test
 */

public class NestedScrollActivity extends BaseActivity<NestedScrolledBinding> {

    public static void start(Context context) {
        Intent starter = new Intent(context, NestedScrollActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_nested_scroll;
    }

    private void bindView() {
        mBinding.toolbar.inflateMenu(R.menu.nested_scroll);
        setSupportActionBar(mBinding.toolbar);

        List<String> data = new ArrayList<>();
        final int dataSize = 20;
        for (int i = 0; i < dataSize; i++) {
            data.add("Content " + i);
        }
        RecyclerView.Adapter adapter = new SimpleAdapter(data);
        mBinding.rvData.setAdapter(adapter);
        mBinding.rvData.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nested_scroll, menu);
        return true;
    }
}
