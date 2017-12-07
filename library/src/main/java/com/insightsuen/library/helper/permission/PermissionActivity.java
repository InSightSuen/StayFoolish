package com.insightsuen.library.helper.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Insight Suen on 2017/12/7.
 */

public class PermissionActivity extends Activity {

    private static final String EXTRA_PERMISSIONS = "Permissions";
    private static final int CODE_REQUEST_PERMISSION = 3515;

    private String[] mPermissions;

    public static void start(Context context, String[] permissions) {
        Intent starter = new Intent(context, PermissionActivity.class);
        starter.putExtra(EXTRA_PERMISSIONS, permissions);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getIntent() != null) {
            mPermissions = getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
        }
        if (mPermissions != null && mPermissions.length > 0) {
            startRequest();
        } else {
            finish();
        }
    }

    private void startRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(mPermissions, CODE_REQUEST_PERMISSION);
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_REQUEST_PERMISSION:
                List<String> grantedPermissions = new ArrayList<>();
                List<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permissions[i]);
                    } else {
                        deniedPermissions.add(permissions[i]);
                    }
                }
                setRequestResult(grantedPermissions, deniedPermissions);
                finish();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setRequestResult(List<String> grantedPermissions, List<String> deniedPermissions) {
        PermissionHelper.getInstance().onRequestResult(mPermissions.length == grantedPermissions.size(),
                grantedPermissions.toArray(new String[grantedPermissions.size()]),
                deniedPermissions.toArray(new String[deniedPermissions.size()]));
    }
}
