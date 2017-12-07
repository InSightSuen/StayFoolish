package com.insightsuen.library.helper.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/7.
 * 权限请求 helper
 */

public class PermissionHelper {

    private WeakReference<OnPermissionRequestResult> mCallback;

    public static PermissionHelper getInstance() {
        return Holder.sSingleton;
    }

    public void checkPermission(Activity activity, String permission, OnPermissionRequestResult callback) {
        checkPermissions(activity, new String[]{permission}, callback);
    }

    public void checkPermissions(Activity activity, String[] permissions, OnPermissionRequestResult callback) {
        List<String> requests = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requests.add(permission);
            }
        }
        mCallback = new WeakReference<OnPermissionRequestResult>(callback);
        requestPermission(activity, requests.toArray(new String[requests.size()]));
    }

    private void requestPermission(Activity activity, String[] permissions) {
        PermissionActivity.start(activity, permissions);
    }

    void onRequestResult(boolean allGranted, String[] grantedPermissions, String[] deniedPermissions) {
        OnPermissionRequestResult requestResult = mCallback.get();
        if (requestResult != null) {
            requestResult.onGrandResult(allGranted, grantedPermissions, deniedPermissions);
        }
    }

    private static class Holder {
        private static PermissionHelper sSingleton = new PermissionHelper();
    }

}
