package com.insightsuen.stayfoolish.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;

/**
 * Created by Insight Suen on 2017/12/6.
 * Loader helper
 */

public final class ContentLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_QUERY_CONTENT = 35185211;

    private LoaderManager mLoaderManager;
    private WeakReference<Context> mHost;

    public ContentLoaderManager(Fragment fragment) {
        mLoaderManager = fragment.getLoaderManager();
        mHost = new WeakReference<>(fragment.getContext());
    }

    public ContentLoaderManager(FragmentActivity activity) {
        mLoaderManager = activity.getSupportLoaderManager();
        mHost = new WeakReference<>(((Context) activity));
    }

    public void init() {
        mLoaderManager.initLoader(ID_QUERY_CONTENT, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mHost.get();
        if (context == null) {
            return null;
        }
        Uri uri = MediaStore.Files.getContentUri("external");
        String[] projects = null;
        return new CursorLoader(context, uri, projects, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToFirst()) {

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
