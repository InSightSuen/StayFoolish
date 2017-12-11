package com.insightsuen.library.helper.permission;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Collections;
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
        PermissionRequest.Builder builder = new PermissionRequest.Builder(permissions);
        checkPermissions(activity, builder.build(), callback);
    }

    public void checkPermissions(Activity activity, PermissionRequest permissionRequest,
                                 OnPermissionRequestResult callback) {
        checkPermissions(activity, Collections.singletonList(permissionRequest), callback);
    }

    public void checkPermissions(Activity activity, List<PermissionRequest> permissionRequests,
                                 OnPermissionRequestResult callback) {
        mCallback = new WeakReference<>(callback);
        requestPermission(activity, permissionRequests);
    }

    private void requestPermission(Activity activity, List<PermissionRequest> permissionRequests) {
        PermissionActivity.start(activity, permissionRequests);
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
