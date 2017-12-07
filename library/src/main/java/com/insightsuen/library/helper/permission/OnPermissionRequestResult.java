package com.insightsuen.library.helper.permission;

/**
 * Created by Insight Suen on 2017/12/7.
 */

public interface OnPermissionRequestResult {

    void onGrandResult(boolean allGranted, String[] grantedPermissions, String[] deniedPermissions);

}
