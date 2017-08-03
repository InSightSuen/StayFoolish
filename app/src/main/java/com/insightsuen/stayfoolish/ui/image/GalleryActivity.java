package com.insightsuen.stayfoolish.ui.image;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.insightsuen.stayfoolish.R;
import com.insightsuen.stayfoolish.base.BaseActivity;
import com.insightsuen.stayfoolish.model.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Gallery Activity
 */
public class GalleryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int IMAGE_SPAN_COUNT = 2;

    private static final int ID_LOAD_IMAGES = 1;

    private static final int REQUEST_PERMISSION = 10;

    public static void start(Context context) {
        Intent starter = new Intent(context, GalleryActivity.class);
        context.startActivity(starter);
    }

    private GalleryAdapter mAdapter;

    private List<ImageInfo> mImageInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initData();
        initWidgets();
    }

    private void initData() {
        mImageInfo = new ArrayList<>();
        mAdapter = new GalleryAdapter(mImageInfo);

        if (checkPermission()) {
            startLoadImages();
        } else {
            requestPermission();
        }
    }

    private void initWidgets() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, IMAGE_SPAN_COUNT);
        RecyclerView rvImages = (RecyclerView) findViewById(R.id.rv_images);
        rvImages.setLayoutManager(layoutManager);
        rvImages.setAdapter(mAdapter);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (checkPermission()) {
                    startLoadImages();
                } else {
                    finish();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startLoadImages() {
        getLoaderManager().initLoader(ID_LOAD_IMAGES, null, this);
    }

    private void onImagesLoadFinished(List<ImageInfo> imageInfo) {
        mImageInfo.clear();
        mImageInfo.addAll(imageInfo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = ImageInfo.PROJECTION;
        String sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        return new CursorLoader(this, uri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ID_LOAD_IMAGES && data != null) {
            if (data.moveToFirst()) {
                List<ImageInfo> result = new ArrayList<>();
                do {
                    ImageInfo imageInfo = new ImageInfo(data);
                    result.add(imageInfo);
                } while (data.moveToNext());
                onImagesLoadFinished(result);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }
}
