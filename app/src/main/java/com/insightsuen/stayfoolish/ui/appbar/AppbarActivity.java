package com.insightsuen.stayfoolish.ui.appbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.insightsuen.library.helper.ToastHelper;
import com.insightsuen.library.helper.permission.OnPermissionRequestResult;
import com.insightsuen.library.helper.permission.PermissionHelper;
import com.insightsuen.library.widget.recyclerview.SimpleAdapter;
import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.loader.ContentLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/5.
 * Appbar practice
 */

public class AppbarActivity extends BaseActivity<AppbarBinding> {

    private ContentLoaderManager mManager;

    public static void start(Context context) {
        Intent starter = new Intent(context, AppbarActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        bindView();
    }

    @SuppressLint("InlinedApi")
    private void checkPermission() {
        PermissionHelper.getInstance().checkPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE, new OnPermissionRequestResult() {
            @Override
            public void onGrandResult(boolean allGranted, String[] grantedPermissions, String[] deniedPermissions) {
                if (allGranted) {
                    initData();
                } else {
                    ToastHelper.getInstance().show("未获取权限");
                    finish();
                }
            }
        });
    }

    private void initData() {
        if (mManager == null) {
            mManager = new ContentLoaderManager(AppbarActivity.this);
        }
        mManager.init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_appbar;
    }

    private void bindView() {
        setSupportActionBar(mBinding.toolbar);
        // noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        inflater.inflate(R.menu.app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setSubmitButtonEnabled(true);
                searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                        this, android.R.layout.simple_list_item_1, getQueryCursor(),
                        new String[]{"name"},
                        new int[]{android.R.id.text1}));
            }
        }

        MenuItem shareItem = menu.findItem(R.id.action_share);
        if (shareItem != null) {
            ActionProvider actionProvider = MenuItemCompat.getActionProvider(shareItem);
            if (actionProvider instanceof ShareActionProvider) {
                ShareActionProvider shareActionProvider = (ShareActionProvider) actionProvider;
                Intent myShareIntent = new Intent(Intent.ACTION_SEND);
                myShareIntent.setType("text/pain");
                myShareIntent.putExtra(Intent.EXTRA_TEXT, "StayFoolish");
                shareActionProvider.setShareIntent(myShareIntent);
            }
        }

        return true;
    }

    private Cursor getQueryCursor() {

        return null;
    }

}
